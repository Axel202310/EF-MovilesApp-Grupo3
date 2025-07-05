// --- Archivo: GraficosActivity.kt (Final, Corregido para tu Layout Específico) ---
package com.asipion.pfmoviles

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.asipion.pfmoviles.databinding.ActividadGraficosBinding
import com.asipion.pfmoviles.model.ResumenPeriodo
import com.asipion.pfmoviles.servicio.RetrofitClient
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.Locale

class GraficosActivity : AppCompatActivity() {

    private lateinit var binding: ActividadGraficosBinding
    private lateinit var barChart: BarChart
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // El binding se genera a partir del nombre del layout XML (actividad_graficos.xml -> ActividadGraficosBinding)
        binding = ActividadGraficosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Usamos el ID correcto del BarChart definido en tu layout
        barChart = binding.barChart

        configurarToolbarYMenu()
        configurarTabsDePeriodo()

        // Seleccionamos "Por Mes" por defecto y cargamos los datos iniciales.
        binding.tabsPeriodo.getTabAt(1)?.select()
    }

    override fun onResume() {
        super.onResume()
        actualizarHeaderMenuLateral()
    }

    // --- FUNCIÓN COMPLETA PARA LA TOOLBAR Y EL MENÚ LATERAL ---
    private fun configurarToolbarYMenu() {
        setSupportActionBar(binding.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayoutGraficos
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navegacionLateralGraficos.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_inicio -> {
                    startActivity(Intent(this, InicioActivity::class.java))
                    finish()
                }
                R.id.item_cuentas -> {
                    startActivity(Intent(this, CuentasActividad::class.java))
                    finish()
                }
                R.id.item_graficos -> {
                    Toast.makeText(this, "Ya estás en Gráficos", Toast.LENGTH_SHORT).show()
                }
                R.id.item_categorias -> {
                    startActivity(Intent(this, MenuCategoriasActividad::class.java))
                    finish()
                }
                R.id.item_ajustes -> {
                    startActivity(Intent(this, AjustesActivity::class.java))
                    finish()
                }
                R.id.item_cerrar_sesion -> cerrarSesion()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun configurarTabsDePeriodo() {
        binding.tabsPeriodo.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val periodo = when (tab?.position) {
                    0 -> "año"
                    1 -> "mes"
                    2 -> "semana"
                    3 -> "dia"
                    else -> "mes"
                }
                cargarDatosGrafico(periodo)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun cargarDatosGrafico(periodo: String) {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) {
            // Podríamos cerrar sesión, pero un Toast es suficiente para esta pantalla.
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerResumenPorPeriodo(idUsuario, periodo)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val resumen = response.body()?.resumen ?: emptyList()
                        if (resumen.isNotEmpty()) {
                            setupBarChart(resumen)
                        } else {
                            barChart.clear()
                            binding.textBalancePeriodo.text = ""
                            barChart.invalidate()
                            Toast.makeText(this@GraficosActivity, "No hay datos para este período", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@GraficosActivity, "Error al cargar datos del gráfico", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GraficosActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupBarChart(data: List<ResumenPeriodo>) {
        val etiquetas = data.map { it.periodo }.distinct().sorted()
        val ingresosEntries = ArrayList<BarEntry>()
        val gastosEntries = ArrayList<BarEntry>()

        var ingresoTotalPeriodo = 0.0
        var gastoTotalPeriodo = 0.0

        etiquetas.forEachIndexed { index, periodo ->
            val ingreso = data.find { it.periodo == periodo && it.tipo == "ingreso" }?.total ?: 0.0
            val gasto = data.find { it.periodo == periodo && it.tipo == "gasto" }?.total ?: 0.0
            ingresosEntries.add(BarEntry(index.toFloat(), ingreso.toFloat()))
            gastosEntries.add(BarEntry(index.toFloat(), gasto.toFloat()))

            ingresoTotalPeriodo += ingreso
            gastoTotalPeriodo += gasto
        }

        val balance = ingresoTotalPeriodo - gastoTotalPeriodo
        val formato = DecimalFormat("S/ #,##0.00")
        if (balance >= 0) {
            binding.textBalancePeriodo.text = "Balance: +${formato.format(balance)}"
            binding.textBalancePeriodo.setTextColor(ContextCompat.getColor(this, R.color.verde_ingreso))
        } else {
            binding.textBalancePeriodo.text = "Balance: ${formato.format(balance)}"
            binding.textBalancePeriodo.setTextColor(ContextCompat.getColor(this, R.color.rojo_gasto))
        }

        val ingresosDataSet = BarDataSet(ingresosEntries, "Ingresos").apply { color = ContextCompat.getColor(this@GraficosActivity, R.color.verde_ingreso) }
        val gastosDataSet = BarDataSet(gastosEntries, "Gastos").apply { color = ContextCompat.getColor(this@GraficosActivity, R.color.rojo_gasto) }

        val barData = BarData(ingresosDataSet, gastosDataSet)
        val groupSpace = 0.4f
        val barSpace = 0.05f
        val barWidth = 0.25f
        barData.barWidth = barWidth

        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(etiquetas.map { it.substringAfter("-") })
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setCenterAxisLabels(true)
        xAxis.granularity = 1f
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = etiquetas.size.toFloat()

        barChart.groupBars(0f, groupSpace, barSpace)

        barChart.description.isEnabled = false
        barChart.legend.textColor = Color.WHITE
        barChart.xAxis.textColor = Color.WHITE
        barChart.axisLeft.textColor = Color.WHITE
        barChart.axisRight.isEnabled = false
        barChart.animateY(1000)
        barChart.invalidate()
    }

    private fun actualizarHeaderMenuLateral() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerCuentas(idUsuario)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val cuentas = response.body()?.listaCuentas ?: emptyList()
                        val headerView = binding.navegacionLateralGraficos.getHeaderView(0)
                        val textCorreo = headerView.findViewById<TextView>(R.id.textViewCorreoUsuario)
                        val textSaldo = headerView.findViewById<TextView>(R.id.textViewSaldoMenu)

                        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
                        val correoUsuario = prefs.getString("correo_usuario", "N/A")
                        val balanceTotal = cuentas.sumOf { it.saldoActual }
                        val monedaPrincipal = cuentas.firstOrNull()?.moneda ?: "PEN"

                        textCorreo.text = correoUsuario
                        textSaldo.text = "Balance: ${String.format(Locale.US, "%.2f", balanceTotal)} $monedaPrincipal"
                    }

                }
            } catch (e: Exception) {
                // No mostramos Toast aquí para no ser intrusivos
            }
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