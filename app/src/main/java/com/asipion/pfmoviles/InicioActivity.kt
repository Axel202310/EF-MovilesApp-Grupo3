package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.asipion.pfmoviles.databinding.ActivityInicioBinding
import com.asipion.pfmoviles.model.Cuenta
import com.asipion.pfmoviles.model.Transaccion
import com.asipion.pfmoviles.servicio.RetrofitClient
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding
    private lateinit var transaccionAdapter: AdaptadorTransaccion
    private var cuentaActual: Cuenta? = null
    private var todasLasTransacciones: List<Transaccion> = emptyList()
    private val calendarioActual = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        configurarToolbarYMenu()
        configurarListeners()
        configurarTabs()
    }

    override fun onResume() {
        super.onResume()
        cargarDatosDeCuentas()
    }

    private fun configurarRecyclerView() {
        transaccionAdapter = AdaptadorTransaccion(emptyList()) { transaccion ->
            val intent = Intent(this, DetalleTransaccionActivity::class.java).apply {
                putExtra("TRANSACCION_ID", transaccion.idTransaccion)
            }
            startActivity(intent)
        }
        binding.recyclerViewTransacciones.apply {
            adapter = transaccionAdapter
            layoutManager = LinearLayoutManager(this@InicioActivity)
        }
    }

    // MODIFICADO: Se han añadido los listeners para los botones de fecha.
    private fun configurarListeners() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AgregarTransaccionActividad::class.java)
            cuentaActual?.let { intent.putExtra("ID_CUENTA", it.idCuenta) }
            startActivity(intent)
        }
        binding.buttonPreviousDate.setOnClickListener { cambiarFecha(-1) }
        binding.buttonNextDate.setOnClickListener { cambiarFecha(1) }
    }

    private fun configurarTabs() {
        // --- CORRECCIÓN CLAVE ---
        // Añadimos un listener para ambos TabLayouts que llama a la misma función.
        // Esto simplifica la lógica y asegura que la vista se actualice siempre.
        val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Al cambiar CUALQUIER pestaña, reseteamos la fecha si es una de período, y filtramos.
                if (tab?.parent == binding.tabLayoutPeriod) {
                    calendarioActual.time = Date()
                }
                filtrarYActualizarVistaCompleta()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        }

        binding.tabLayoutType.addOnTabSelectedListener(tabSelectedListener)
        binding.tabLayoutPeriod.addOnTabSelectedListener(tabSelectedListener)
    }

    private fun cargarDatosDeCuentas() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) {
            cerrarSesion()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val responseCuentas = RetrofitClient.webService.obtenerCuentas(idUsuario)
                withContext(Dispatchers.Main) {
                    if (responseCuentas.isSuccessful) {
                        val cuentas = responseCuentas.body()?.listaCuentas
                        if (!cuentas.isNullOrEmpty()) {
                            cuentaActual = cuentas[0]
                            actualizarUIConDatosDeCuenta()
                            cuentaActual?.let { cargarTransacciones(it.idCuenta) }
                        } else {
                            startActivity(Intent(this@InicioActivity, SeleccionarDivisaActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this@InicioActivity, "Error al cargar datos de cuenta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@InicioActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cargarTransacciones(idCuenta: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerTransaccionesDeCuenta(idCuenta)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        todasLasTransacciones = response.body()?.listaTransacciones ?: emptyList()
                        // --- CORRECCIÓN CLAVE ---
                        // Después de cargar los datos, siempre filtramos. No asumimos nada.
                        filtrarYActualizarVistaCompleta()
                    } else {
                        Toast.makeText(this@InicioActivity, "Error al cargar transacciones", Toast.LENGTH_SHORT).show()
                        mostrarEstadoVacio(true)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@InicioActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                    mostrarEstadoVacio(true)
                }
            }
        }
    }

    // --- CAMBIO CLAVE ---
    private fun filtrarYActualizarVistaCompleta() {
        actualizarTextoFecha()

        val tipoSeleccionado = if (binding.tabLayoutType.selectedTabPosition == 0) "gasto" else "ingreso"
        val periodoSeleccionado = binding.tabLayoutPeriod.selectedTabPosition

        val transaccionesFiltradas = todasLasTransacciones
            .filter { it.tipoTransaccion == tipoSeleccionado }
            .filter { transaccion ->
                try {
                    // --- CORRECCIÓN CLAVE ---
                    // El formato que viene de la API es ISO 8601 (con T y Z).
                    // El que nosotros enviamos es "yyyy-MM-dd HH:mm:ss".
                    // Necesitamos poder parsear ambos. Un try-catch es perfecto para esto.
                    val formatoApi1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    val formatoApi2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

                    val fechaTransaccion = try {
                        formatoApi1.parse(transaccion.fechaTransaccion)
                    } catch (e: Exception) {
                        formatoApi2.parse(transaccion.fechaTransaccion)
                    } ?: return@filter false

                    val calTransaccion = Calendar.getInstance().apply { time = fechaTransaccion }

                    when (periodoSeleccionado) {
                        0 -> esMismoDia(calTransaccion, calendarioActual)
                        1 -> esMismaSemana(calTransaccion, calendarioActual)
                        2 -> esMismoMes(calTransaccion, calendarioActual)
                        3 -> esMismoAnio(calTransaccion, calendarioActual)
                        else -> true
                    }
                } catch (e: Exception) {
                    Log.e("FiltradoFecha", "No se pudo parsear la fecha: ${transaccion.fechaTransaccion}", e)
                    false
                }
            }

        transaccionAdapter.actualizarDatos(transaccionesFiltradas)
        mostrarEstadoVacio(transaccionesFiltradas.isEmpty())
        actualizarGrafico(transaccionesFiltradas, tipoSeleccionado)
    }

    // --- CAMBIO CLAVE ---
    private fun actualizarGrafico(transacciones: List<Transaccion>, tipo: String) {
        if (transacciones.isEmpty()) {
            binding.donutChartView.setData(emptyList())
            binding.textViewDonutCenterText.text = "No hubo ${tipo}s\n${obtenerTextoPeriodo()}"
            return
        }

        val transaccionesPorCategoria = transacciones
            .groupBy { it.nombreCategoria ?: "Sin Categoría" }
            .mapValues { entry -> entry.value.sumOf { it.montoTransaccion } }

        val total = transaccionesPorCategoria.values.sum()
        val formatoDecimal = DecimalFormat("#,##0.00")
        binding.textViewDonutCenterText.text = "Total:\nS/ ${formatoDecimal.format(total)}"

        val colores = listOf(
            R.color.grafico_color_1, R.color.grafico_color_2, R.color.grafico_color_3,
            R.color.grafico_color_4, R.color.grafico_color_5
        )

        val datosGrafico = transaccionesPorCategoria.entries.mapIndexed { index, entry ->
            val porcentaje = (entry.value / total * 100).toFloat()
            val color = ContextCompat.getColor(this, colores[index % colores.size])
            Pair(porcentaje, color)
        }

        binding.donutChartView.setData(datosGrafico)
    }

    // --- NUEVAS FUNCIONES AUXILIARES ---
    private fun cambiarFecha(cantidad: Int) {
        when (binding.tabLayoutPeriod.selectedTabPosition) {
            0 -> calendarioActual.add(Calendar.DAY_OF_YEAR, cantidad)
            1 -> calendarioActual.add(Calendar.WEEK_OF_YEAR, cantidad)
            2 -> calendarioActual.add(Calendar.MONTH, cantidad)
            3 -> calendarioActual.add(Calendar.YEAR, cantidad)
        }
        filtrarYActualizarVistaCompleta()
    }

    private fun actualizarTextoFecha() {
        val formato = when (binding.tabLayoutPeriod.selectedTabPosition) {
            0 -> if (esMismoDia(calendarioActual, Calendar.getInstance())) "'Hoy,' dd 'de' MMMM" else "dd 'de' MMMM, yyyy"
            1 -> "'Semana del' dd 'de' MMMM"
            2 -> "MMMM yyyy"
            3 -> "yyyy"
            else -> "dd/MM/yyyy"
        }
        binding.textViewCurrentDate.text = SimpleDateFormat(formato, Locale("es", "ES")).format(calendarioActual.time).replaceFirstChar { it.uppercase() }
    }

    // --- FUNCIONES DE COMPARACIÓN DE FECHA CORREGIDAS Y ROBUSTAS ---
    private fun esMismoDia(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    private fun esMismaSemana(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
    }
    private fun esMismoMes(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    }
    private fun esMismoAnio(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
    }

    private fun obtenerTextoPeriodo(): String {
        return when (binding.tabLayoutPeriod.selectedTabPosition) {
            0 -> "hoy"
            1 -> "esta semana"
            2 -> "este mes"
            3 -> "este año"
            else -> ""
        }
    }

    private fun mostrarEstadoVacio(estaVacio: Boolean) {
        binding.recyclerViewTransacciones.visibility = if (estaVacio) View.GONE else View.VISIBLE
        binding.textViewEmptyState.visibility = if (estaVacio) View.VISIBLE else View.GONE
    }

    private fun actualizarUIConDatosDeCuenta() {
        cuentaActual?.let { cuenta ->
            val formato = DecimalFormat("#,##0.00")
            binding.txtMonto.text = formato.format(cuenta.saldoActual)
            binding.txtMoneda.text = cuenta.moneda
            actualizarHeaderMenuLateral(cuenta)
        }
    }

    private fun configurarToolbarYMenu() {
        setSupportActionBar(binding.topAppBar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val drawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.topAppBar,
            R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.topAppBar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navegacionLateral.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_inicio -> Toast.makeText(this, "Ya estás en Inicio", Toast.LENGTH_SHORT).show()
                R.id.item_cuentas -> startActivity(Intent(this, CuentasActividad::class.java))
                R.id.item_graficos -> Toast.makeText(this, "Gráficos (próximamente)", Toast.LENGTH_SHORT).show()
                R.id.item_categorias -> startActivity(Intent(this, MenuCategoriasActividad::class.java))
                R.id.item_pagos_habituales -> Toast.makeText(this, "Pagos Habituales (próximamente)", Toast.LENGTH_SHORT).show()
                R.id.item_recordatorios -> Toast.makeText(this, "Recordatorios (próximamente)", Toast.LENGTH_SHORT).show()
                R.id.item_ajustes -> Toast.makeText(this, "Ajustes (próximamente)", Toast.LENGTH_SHORT).show()
                R.id.item_cerrar_sesion -> cerrarSesion()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun actualizarHeaderMenuLateral(cuenta: Cuenta) {
        val headerView = binding.navegacionLateral.getHeaderView(0)
        val textCorreo = headerView.findViewById<TextView>(R.id.textViewCorreoUsuario)
        val textSaldo = headerView.findViewById<TextView>(R.id.textViewSaldoMenu)
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val correoUsuario = prefs.getString("correo_usuario", "N/A")
        textCorreo.text = correoUsuario
        textSaldo.text = "Balance: ${String.format(Locale.US, "%.2f", cuenta.saldoActual)} ${cuenta.moneda}"
        headerView.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun cerrarSesion() {
        getSharedPreferences("mis_prefs", MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, BienvenidaActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}