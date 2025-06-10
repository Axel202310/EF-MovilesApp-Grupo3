package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.asipion.pfmoviles.databinding.ActivityInicioBinding
import com.google.android.material.tabs.TabLayout
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private val currentCalendar: Calendar = Calendar.getInstance()

    private val formatoFechaDiaMes = SimpleDateFormat("dd 'de' MMMM", Locale("es", "ES"))
    private val formatoNombreDia = SimpleDateFormat("EEE", Locale("es", "ES"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbarYMenu()
        actualizarHeaderMenuLateral()
        configurarTabs()
        configurarNavegacionFechas()
        mostrarFechaActual()

        // Aquí solo muestras un dato: moneda y monto
        val txtMoneda = binding.txtMoneda
        val txtMonto = binding.txtMonto

        val moneda = obtenerMoneda()
        val monto = obtenerMonto()           // lo que el usuario ingresó

        txtMoneda.text = moneda
        txtMonto.text = monto.toString()

        // Redirigir al hacer clic en el botón flotante "+"
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AgregarTransaccionActividad::class.java)
            startActivity(intent)
        }

        binding.tabLayoutType.getTabAt(0)?.select()
        binding.tabLayoutPeriod.getTabAt(0)?.select()
    }

    override fun onResume() {
        super.onResume()
        val id = obtenerIdUsuario()
        Log.d("Inicio", "Usuario ID: $id - Moneda: ${obtenerMoneda()}, Monto: ${obtenerMonto()}")
        actualizarMonedaYMonto()
        actualizarHeaderMenuLateral()
    }
    private fun actualizarMonedaYMonto() {
        binding.txtMoneda.text = obtenerMoneda()
        binding.txtMonto.text = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("es", "ES")))
            .format(obtenerMonto())
    }

    private fun configurarToolbarYMenu() {
        setSupportActionBar(binding.topAppBar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, binding.topAppBar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
        }

        // NUEVO: Manejo del menú lateral
        binding.navegacionLateral.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_inicio -> {
                    Toast.makeText(this, "Ya estás en Inicio", Toast.LENGTH_SHORT).show()
                }
                R.id.item_cuentas -> {
                    startActivity(Intent(this, CuentasActividad::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
                R.id.item_graficos -> {
                    startActivity(Intent(this, GraficosActivity::class.java))
                }
                R.id.item_categorias -> {
                    startActivity(Intent(this, MenuCategoriasActividad::class.java))
                }
                R.id.item_pagos_habituales -> {
                    startActivity(Intent(this, PagosHabitualesActividad::class.java))
                }
                R.id.item_recordatorios -> {
                    startActivity(Intent(this, RecordatoriosActividad::class.java))
                }
                R.id.item_ajustes -> {
                    startActivity(Intent(this, AjustesActividad::class.java))
                }
                R.id.item_cerrar_sesion -> {
                    Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, BienvenidaActividad::class.java))
                    finish()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }


    private fun configurarTabs() {
        binding.tabLayoutType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val texto = if (tab?.position == 0) "gastos" else "ingresos"
                binding.textViewDonutCenterText.text = "No hubo\n$texto ${obtenerTextoPeriodoActual()}"
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.tabLayoutPeriod.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentCalendar.time = Date()
                mostrarFechaActual()
                actualizarTextoDonut()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun configurarNavegacionFechas() {
        binding.buttonPreviousDate.setOnClickListener { cambiarFecha(-1) }
        binding.buttonNextDate.setOnClickListener { cambiarFecha(1) }
    }

    private fun cambiarFecha(cantidad: Int) {
        when (binding.tabLayoutPeriod.selectedTabPosition) {
            0 -> currentCalendar.add(Calendar.DAY_OF_YEAR, cantidad)
            1 -> currentCalendar.add(Calendar.WEEK_OF_YEAR, cantidad)
            2 -> currentCalendar.add(Calendar.MONTH, cantidad)
            3 -> currentCalendar.add(Calendar.YEAR, cantidad)
        }
        mostrarFechaActual()
        actualizarTextoDonut()
    }

    private fun mostrarFechaActual() {
        val hoy = Calendar.getInstance()
        val posicion = binding.tabLayoutPeriod.selectedTabPosition
        val diaNombre = formatoNombreDia.format(currentCalendar.time).replaceFirstChar { it.uppercase() }
        val fecha = formatoFechaDiaMes.format(currentCalendar.time)

        val textoFecha = when (posicion) {
            0 -> if (esMismoDia(currentCalendar, hoy)) "Hoy, ${fecha.substringAfter("de ")}"
            else "$diaNombre, $fecha"

            1 -> {
                val inicio = (currentCalendar.clone() as Calendar).apply {
                    set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                }
                val fin = (inicio.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, 6)
                }
                val formato = SimpleDateFormat("dd MMM", Locale("es", "ES"))
                "Semana: ${formato.format(inicio.time)} - ${formato.format(fin.time)}"
            }

            2 -> SimpleDateFormat("MMMM 'de' yyyy", Locale("es", "ES"))
                .format(currentCalendar.time).replaceFirstChar { it.uppercase() }

            3 -> SimpleDateFormat("yyyy", Locale("es", "ES")).format(currentCalendar.time)
            else -> ""
        }

        binding.textViewCurrentDate.text = textoFecha
    }

    private fun esMismoDia(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun obtenerTextoPeriodoActual(): String {
        return when (binding.tabLayoutPeriod.selectedTabPosition) {
            0 -> "hoy"
            1 -> "esta semana"
            2 -> "este mes"
            3 -> "este año"
            else -> ""
        }
    }

    private fun actualizarTextoDonut() {
        val tipo = if (binding.tabLayoutType.selectedTabPosition == 0) "gastos" else "ingresos"
        binding.textViewDonutCenterText.text = "No hubo\n$tipo ${obtenerTextoPeriodoActual()}"
    }

    private fun obtenerIdUsuario(): Int {
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        return prefs.getInt("id_usuario", -1)
    }

    private fun obtenerMoneda(): String {
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val idUsuario = obtenerIdUsuario()
        return prefs.getString("moneda_$idUsuario", "USD") ?: "USD"
    }

    private fun obtenerMonto(): Float {
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val idUsuario = obtenerIdUsuario()
        return prefs.getFloat("monto_$idUsuario", 0.0f)
    }

    private fun actualizarHeaderMenuLateral() {
        val headerView = binding.navegacionLateral.getHeaderView(0)
        val textCorreo = headerView.findViewById<TextView>(R.id.textViewCorreoUsuario)
        val textSaldo = headerView.findViewById<TextView>(R.id.textViewSaldoMenu)

        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("id_usuario", -1)
        val correoUsuario = prefs.getString("correo_usuario", "usuario@example.com") ?: "usuario@example.com"
        val moneda = prefs.getString("moneda_$idUsuario", "PEN") ?: "PEN"
        val monto = prefs.getFloat("monto_$idUsuario", 0.0f)

        textCorreo.text = correoUsuario
        textSaldo.text = "Balance: ${String.format("%.2f", monto)} $moneda"
    }


}
