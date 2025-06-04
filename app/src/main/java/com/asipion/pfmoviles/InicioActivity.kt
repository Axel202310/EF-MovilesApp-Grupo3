package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActivityInicioBinding // IMPORTANTE: Cambia esto al binding correcto
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Asegúrate que el nombre de la clase coincida con tu Activity
class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding // CAMBIA ActivityInicioBinding al nombre correcto
    private var currentCalendar: Calendar = Calendar.getInstance()
    // Formato español para "Hoy, 21 de Abril" o "Vie, 21 de Abril"
    private val dayMonthFormat = SimpleDateFormat("dd 'de' MMMM", Locale("es", "ES"))
    private val dayNameFormat = SimpleDateFormat("EEE", Locale("es", "ES"))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater) // CAMBIA ActivityInicioBinding
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        // Para que el icono de navegación (menú) sea clickeable:
        binding.topAppBar.setNavigationOnClickListener {
            // Lógica para abrir el drawer o menú
            Toast.makeText(this, "Menú presionado", Toast.LENGTH_SHORT).show()
        }

        setupTabs()
        setupDateNavigation()
        updateDateDisplay() // Mostrar fecha inicial

        binding.fabAdd.setOnClickListener {
            Toast.makeText(this, "Agregar transacción", Toast.LENGTH_SHORT).show()
            // Aquí iría la lógica para abrir una nueva pantalla o diálogo para agregar gasto/ingreso
        }

        // Seleccionar "GASTOS" y "Día" por defecto al iniciar
        binding.tabLayoutType.getTabAt(0)?.select()
        binding.tabLayoutPeriod.getTabAt(0)?.select()
    }

    private fun setupTabs() {
        binding.tabLayoutType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> { // GASTOS
                        binding.textViewDonutCenterText.text = "No hubo\ngastos ${getCurrentPeriodText().lowercase()}"
                    }
                    1 -> { // INGRESOS
                        binding.textViewDonutCenterText.text = "No hubo\ningresos ${getCurrentPeriodText().lowercase()}"
                    }
                }
                // Lógica para recargar datos basados en GASTOS/INGRESOS
                Toast.makeText(this@InicioActivity, "${tab?.text} seleccionado", Toast.LENGTH_SHORT).show()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.tabLayoutPeriod.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentCalendar = Calendar.getInstance() // Resetear a hoy al cambiar tipo de periodo
                updateDateDisplay()
                updateDonutTextWithPeriod()
                // Lógica para recargar datos basados en Día, Semana, Mes, Año
                Toast.makeText(this@InicioActivity, "${tab?.text} seleccionado", Toast.LENGTH_SHORT).show()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupDateNavigation() {
        binding.buttonPreviousDate.setOnClickListener {
            changeDate(-1)
        }
        binding.buttonNextDate.setOnClickListener {
            changeDate(1)
        }
    }

    private fun changeDate(amount: Int) {
        val selectedPeriodTab = binding.tabLayoutPeriod.selectedTabPosition
        when (selectedPeriodTab) {
            0 -> currentCalendar.add(Calendar.DAY_OF_YEAR, amount) // Día
            1 -> currentCalendar.add(Calendar.WEEK_OF_YEAR, amount) // Semana
            2 -> currentCalendar.add(Calendar.MONTH, amount)        // Mes
            3 -> currentCalendar.add(Calendar.YEAR, amount)         // Año
        }
        updateDateDisplay()
        updateDonutTextWithPeriod() // Actualizar texto de la dona también
        // Aquí también deberías recargar los datos para la nueva fecha/periodo
    }

    private fun updateDateDisplay() {
        val todayCalendar = Calendar.getInstance()
        val selectedPeriodTab = binding.tabLayoutPeriod.selectedTabPosition
        var dateText = ""

        // Capitalizar primera letra de formatos
        val currentDayName = dayNameFormat.format(currentCalendar.time).replaceFirstChar { it.titlecase(Locale("es", "ES")) }
        val currentDayMonth = dayMonthFormat.format(currentCalendar.time)


        when (selectedPeriodTab) {
            0 -> { // Día
                dateText = if (isSameDay(currentCalendar, todayCalendar)) {
                    "Hoy, ${currentDayMonth.substringAfter("de ")}" // Ej: Hoy, 21 de Abril
                } else {
                    "$currentDayName, $currentDayMonth" // Ej: Vie, 21 de Abril
                }
            }
            1 -> { // Semana
                val startOfWeek = currentCalendar.clone() as Calendar
                startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek) // Cuidado: firstDayOfWeek depende de Locale
                val endOfWeek = startOfWeek.clone() as Calendar
                endOfWeek.add(Calendar.DAY_OF_YEAR, 6)

                val startStr = SimpleDateFormat("dd MMM", Locale("es", "ES")).format(startOfWeek.time)
                val endStr = SimpleDateFormat("dd MMM", Locale("es", "ES")).format(endOfWeek.time)
                dateText = "Semana: $startStr - $endStr"
            }
            2 -> { // Mes
                dateText = SimpleDateFormat("MMMM 'de' yyyy", Locale("es", "ES")).format(currentCalendar.time)
                dateText = dateText.replaceFirstChar { it.titlecase(Locale("es", "ES")) }
            }
            3 -> { // Año
                dateText = SimpleDateFormat("yyyy", Locale("es", "ES")).format(currentCalendar.time)
            }
        }
        binding.textViewCurrentDate.text = dateText
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun getCurrentPeriodText(): String {
        return when (binding.tabLayoutPeriod.selectedTabPosition) {
            0 -> "hoy"
            1 -> "esta semana"
            2 -> "este mes"
            3 -> "este año"
            else -> ""
        }
    }
    private fun updateDonutTextWithPeriod() {
        val typeText = if (binding.tabLayoutType.selectedTabPosition == 0) "gastos" else "ingresos"
        binding.textViewDonutCenterText.text = "No hubo\n$typeText ${getCurrentPeriodText()}"
    }
}