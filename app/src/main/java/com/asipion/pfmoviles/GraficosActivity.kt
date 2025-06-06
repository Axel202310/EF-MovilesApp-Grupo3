package com.asipion.pfmoviles

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.asipion.pfmoviles.R // Asegúrate de que este sea tu paquete correcto
import com.asipion.pfmoviles.databinding.ActividadGraficosBinding // Esta clase se genera automáticamente

class GraficosActivity : AppCompatActivity() {

    // Declaramos la variable para View Binding.
    // 'ActividadGraficosBinding' es el nombre generado a partir de 'actividad_graficos.xml'
    private lateinit var binding: ActividadGraficosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflamos el layout usando View Binding y lo establecemos como el contenido de la actividad
        binding = ActividadGraficosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración inicial de la UI
        setupToolbar()
        setupMainTabs()
        setupPeriodTabs()

        // Opcional: Cargar los datos iniciales para la primera pestaña seleccionada
        // Esto simula que el usuario acaba de entrar y ve la vista "GENERAL" y "por año"
        handleGeneralTabSelected()
        handlePeriodYearSelected()
    }

    /**
     * Configura la barra de herramientas (Toolbar) y el icono de navegación.
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        // Ocultamos el título por defecto de la toolbar porque ya tenemos "Graficos" en el XML
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // Mostramos el botón de navegación (icono de menú)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_24) // Asignamos el icono de menú
    }

    /**
     * Configura los listeners para las pestañas principales (GENERAL, GASTOS, INGRESOS).
     */
    private fun setupMainTabs() {
        binding.tabLayoutMain.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> handleGeneralTabSelected()
                    1 -> handleGastosTabSelected()
                    2 -> handleIngresosTabSelected()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // No se necesita acción aquí por ahora
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // No se necesita acción aquí por ahora
            }
        })
    }

    /**
     * Configura los listeners para las pestañas de periodo (por año, por mes, etc.).
     */
    private fun setupPeriodTabs() {
        binding.tabLayoutPeriod.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> handlePeriodYearSelected()
                    1 -> handlePeriodMonthSelected()
                    2 -> handlePeriodWeekSelected()
                    3 -> handlePeriodDaySelected()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    // --- Lógica para manejar la selección de pestañas ---
    // En una aplicación real, estas funciones actualizarían el gráfico con los datos correspondientes.

    private fun handleGeneralTabSelected() {
        // Lógica para mostrar el gráfico GENERAL
        // Por ejemplo: viewModel.loadGeneralData(currentPeriod)
        Toast.makeText(this, "Pestaña GENERAL seleccionada", Toast.LENGTH_SHORT).show()
    }

    private fun handleGastosTabSelected() {
        // Lógica para mostrar el gráfico de GASTOS
        Toast.makeText(this, "Pestaña GASTOS seleccionada", Toast.LENGTH_SHORT).show()
    }

    private fun handleIngresosTabSelected() {
        // Lógica para mostrar el gráfico de INGRESOS
        Toast.makeText(this, "Pestaña INGRESOS seleccionada", Toast.LENGTH_SHORT).show()
    }

    private fun handlePeriodYearSelected() {
        // Lógica para cargar datos ANUALES en el gráfico actual
        Toast.makeText(this, "Periodo: Por Año", Toast.LENGTH_SHORT).show()
    }

    private fun handlePeriodMonthSelected() {
        // Lógica para cargar datos MENSUALES
        Toast.makeText(this, "Periodo: Por Mes", Toast.LENGTH_SHORT).show()
    }

    private fun handlePeriodWeekSelected() {
        // Lógica para cargar datos SEMANALES
        Toast.makeText(this, "Periodo: Por Semana", Toast.LENGTH_SHORT).show()
    }

    private fun handlePeriodDaySelected() {
        // Lógica para cargar datos DIARIOS
        Toast.makeText(this, "Periodo: Por Día", Toast.LENGTH_SHORT).show()
    }


    /**
     * Maneja los clics en los ítems del menú de la toolbar, como el botón de navegación.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // El id 'android.R.id.home' corresponde al botón de navegación (flecha atrás o menú hamburguesa)
        if (item.itemId == android.R.id.home) {
            // Aquí iría la lógica para abrir el menú lateral (Navigation Drawer)
            Toast.makeText(this, "Menú presionado", Toast.LENGTH_SHORT).show()
            // Si no tienes un menú lateral, puedes hacer que cierre la actividad:
            // finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}