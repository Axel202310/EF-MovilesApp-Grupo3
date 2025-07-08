package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
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
import kotlinx.coroutines.async
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

    private var todasLasCuentas: List<Cuenta> = emptyList()
    private var todasLasTransacciones: List<Transaccion> = emptyList()
    private var cuentaSeleccionada: Cuenta? = null // null representa la vista "Balance Total"
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
        cargarDatosDelServidor()
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

    private fun configurarListeners() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AgregarTransaccionActividad::class.java)
            val idCuentaAUsar = cuentaSeleccionada?.idCuenta ?: todasLasCuentas.firstOrNull()?.idCuenta
            idCuentaAUsar?.let { intent.putExtra("ID_CUENTA", it) }
            startActivity(intent)
        }
        binding.buttonPreviousDate.setOnClickListener { cambiarFecha(-1) }
        binding.buttonNextDate.setOnClickListener { cambiarFecha(1) }

        binding.layoutBalance.setOnClickListener {
            mostrarDialogoSeleccionDeCuenta()
        }
    }

    private fun configurarTabs() {
        val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.parent == binding.tabLayoutPeriod) {
                    calendarioActual.time = Date()
                }
                actualizarVistaCompleta()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        }
        binding.tabLayoutType.addOnTabSelectedListener(tabSelectedListener)
        binding.tabLayoutPeriod.addOnTabSelectedListener(tabSelectedListener)
    }

    private fun mostrarDialogoSeleccionDeCuenta() {
        if (todasLasCuentas.isEmpty()) return

        val nombresCuentas = mutableListOf(getString(R.string.balance_total))
        nombresCuentas.addAll(todasLasCuentas.map { it.nombreCuenta })

        AlertDialog.Builder(this)
            .setTitle("Seleccionar Vista")
            .setItems(nombresCuentas.toTypedArray()) { _, which ->
                cuentaSeleccionada = if (which == 0) null else todasLasCuentas[which - 1]
                actualizarVistaCompleta()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cargarDatosDelServidor() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) {
            cerrarSesion()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cuentasDeferred = async { RetrofitClient.webService.obtenerCuentas(idUsuario) }
                val transaccionesDeferred = async { RetrofitClient.webService.obtenerTransaccionesDeUsuario(idUsuario) }

                val cuentasResponse = cuentasDeferred.await()
                val transaccionesResponse = transaccionesDeferred.await()

                withContext(Dispatchers.Main) {
                    if (cuentasResponse.isSuccessful && transaccionesResponse.isSuccessful) {
                        todasLasCuentas = cuentasResponse.body()?.listaCuentas ?: emptyList()
                        todasLasTransacciones = transaccionesResponse.body()?.listaTransacciones ?: emptyList()

                        aplicarPreferenciasGuardadas()

                    } else {
                        Toast.makeText(this@InicioActivity, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@InicioActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun aplicarPreferenciasGuardadas() {
        val prefs = getSharedPreferences(PersonalizacionActivity.PREFS_NAME, MODE_PRIVATE)

        val vistaGuardada = prefs.getString(PersonalizacionActivity.KEY_VISTA_DEFECTO, "Balance Total")
        cuentaSeleccionada = if (vistaGuardada == "Balance Total") {
            null
        } else {
            todasLasCuentas.find { it.nombreCuenta == vistaGuardada }
        }

        val periodoIndexGuardado = prefs.getInt(PersonalizacionActivity.KEY_PERIODO_DEFECTO_INDEX, 2) // 2 es "Mes"

        binding.tabLayoutPeriod.post {
            binding.tabLayoutPeriod.getTabAt(periodoIndexGuardado)?.select()
        }

        actualizarVistaCompleta()
    }

    private fun actualizarVistaCompleta() {
        actualizarTextoFecha()
        actualizarUIBalance()
        actualizarHeaderMenuLateral()

        val tipoSeleccionado = if (binding.tabLayoutType.selectedTabPosition == 0) "gasto" else "ingreso"
        val periodoSeleccionado = binding.tabLayoutPeriod.selectedTabPosition

        val transaccionesPorCuenta = if (cuentaSeleccionada == null) {
            todasLasTransacciones
        } else {
            todasLasTransacciones.filter { it.idCuenta == cuentaSeleccionada!!.idCuenta }
        }

        val transaccionesFiltradas = transaccionesPorCuenta
            .filter { it.tipoTransaccion == tipoSeleccionado }
            .filter { transaccion ->
                try {
                    val formatoApi1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    val formatoApi2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                    val fechaTransaccion = try { formatoApi1.parse(transaccion.fechaTransaccion) } catch (_: Exception) { formatoApi2.parse(transaccion.fechaTransaccion) } ?: return@filter false
                    val calTransaccion = Calendar.getInstance().apply { time = fechaTransaccion }
                    when (periodoSeleccionado) {
                        0 -> esMismoDia(calTransaccion, calendarioActual)
                        1 -> esMismaSemana(calTransaccion, calendarioActual)
                        2 -> esMismoMes(calTransaccion, calendarioActual)
                        3 -> esMismoAnio(calTransaccion, calendarioActual)
                        else -> true
                    }
                } catch (e: Exception) {
                    Log.e("FiltradoFecha", "Error al parsear fecha: ${transaccion.fechaTransaccion}", e)
                    false
                }
            }

        transaccionAdapter.actualizarDatos(transaccionesFiltradas)
        mostrarEstadoVacio(transaccionesFiltradas.isEmpty())
        actualizarGrafico(transaccionesFiltradas, tipoSeleccionado)
    }

    private fun actualizarUIBalance() {
        val formato = DecimalFormat("#,##0.00")
        if (cuentaSeleccionada == null) {
            val balanceTotal = todasLasCuentas.sumOf { it.saldoActual }
            val monedaPrincipal = todasLasCuentas.firstOrNull()?.moneda ?: "PEN"
            binding.txtMonto.text = formato.format(balanceTotal)
            binding.txtMoneda.text = monedaPrincipal
            binding.textViewTotalLabel.text = getString(R.string.balance_total)
        } else {
            cuentaSeleccionada?.let { cuenta ->
                binding.txtMonto.text = formato.format(cuenta.saldoActual)
                binding.txtMoneda.text = cuenta.moneda
                binding.textViewTotalLabel.text = cuenta.nombreCuenta
            }
        }
    }

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
        val colores = listOf(R.color.grafico_color_1, R.color.grafico_color_2, R.color.grafico_color_3, R.color.grafico_color_4, R.color.grafico_color_5)
        val datosGrafico = transaccionesPorCategoria.entries.mapIndexed { index, entry ->
            val porcentaje = (entry.value / total * 100).toFloat()
            val color = ContextCompat.getColor(this, colores[index % colores.size])
            Pair(porcentaje, color)
        }
        binding.donutChartView.setData(datosGrafico)
    }

    private fun cambiarFecha(cantidad: Int) {
        when (binding.tabLayoutPeriod.selectedTabPosition) {
            0 -> calendarioActual.add(Calendar.DAY_OF_YEAR, cantidad)
            1 -> calendarioActual.add(Calendar.WEEK_OF_YEAR, cantidad)
            2 -> calendarioActual.add(Calendar.MONTH, cantidad)
            3 -> calendarioActual.add(Calendar.YEAR, cantidad)
        }
        actualizarVistaCompleta()
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

    private fun esMismoDia(cal1: Calendar, cal2: Calendar): Boolean = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    private fun esMismaSemana(cal1: Calendar, cal2: Calendar): Boolean = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
    private fun esMismoMes(cal1: Calendar, cal2: Calendar): Boolean = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    private fun esMismoAnio(cal1: Calendar, cal2: Calendar): Boolean = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)

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
                R.id.item_graficos -> startActivity(Intent(this, GraficosActivity::class.java))
                R.id.item_categorias -> startActivity(Intent(this, MenuCategoriasActividad::class.java))
                R.id.item_ajustes -> startActivity(Intent(this, AjustesActivity::class.java))
                R.id.item_cerrar_sesion -> cerrarSesion()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun actualizarHeaderMenuLateral() {
        val headerView = binding.navegacionLateral.getHeaderView(0)
        val textCorreo = headerView.findViewById<TextView>(R.id.textViewCorreoUsuario)
        val textSaldo = headerView.findViewById<TextView>(R.id.textViewSaldoMenu)
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val correoUsuario = prefs.getString("correo_usuario", "N/A")
        val balanceTotal = todasLasCuentas.sumOf { it.saldoActual }
        val monedaPrincipal = todasLasCuentas.firstOrNull()?.moneda ?: "PEN"
        textCorreo.text = correoUsuario
        textSaldo.text = "Balance: ${String.format(Locale.US, "%.2f", balanceTotal)} $monedaPrincipal"
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