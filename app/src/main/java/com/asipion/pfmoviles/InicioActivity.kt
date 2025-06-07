package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActivityInicioBinding
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding
    private var currentCalendar: Calendar = Calendar.getInstance()

    private val dayMonthFormat = SimpleDateFormat("dd 'de' MMMM", Locale("es", "ES"))
    private val dayNameFormat = SimpleDateFormat("EEE", Locale("es", "ES"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        binding.topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Menú presionado", Toast.LENGTH_SHORT).show()
        }

        setupTabs()
        setupDateNavigation()
        updateDateDisplay()

        // Al hacer clic en el botón flotante, abrir la actividad para agregar transacción
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AgregarTransaccionActividad::class.java)
            startActivity(intent)
        }

        binding.tabLayoutType.getTabAt(0)?.select()
        binding.tabLayoutPeriod.getTabAt(0)?.select()
    }

    private fun setupTabs() {
        binding.tabLayoutType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.textViewDonutCenterText.text = "No hubo\ngastos ${getCurrentPeriodText().lowercase()}"
                    }
                    1 -> {
                        binding.textViewDonutCenterText.text = "No hubo\ningresos ${getCurrentPeriodText().lowercase()}"
                    }
                }
                Toast.makeText(this@InicioActivity, "${tab?.text} seleccionado", Toast.LENGTH_SHORT).show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.tabLayoutPeriod.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentCalendar = Calendar.getInstance()
                updateDateDisplay()
                updateDonutTextWithPeriod()
                Toast.makeText(this@InicioActivity, "${tab?.text} seleccionado", Toast.LENGTH_SHORT).show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupDateNavigation() {
        binding.buttonPreviousDate.setOnClickListener { changeDate(-1) }
        binding.buttonNextDate.setOnClickListener { changeDate(1) }
    }

    private fun changeDate(amount: Int) {
        when (binding.tabLayoutPeriod.selectedTabPosition) {
            0 -> currentCalendar.add(Calendar.DAY_OF_YEAR, amount)
            1 -> currentCalendar.add(Calendar.WEEK_OF_YEAR, amount)
            2 -> currentCalendar.add(Calendar.MONTH, amount)
            3 -> currentCalendar.add(Calendar.YEAR, amount)
        }
        updateDateDisplay()
        updateDonutTextWithPeriod()
    }

    private fun updateDateDisplay() {
        val todayCalendar = Calendar.getInstance()
        val selectedPeriodTab = binding.tabLayoutPeriod.selectedTabPosition
        var dateText = ""

        val currentDayName = dayNameFormat.format(currentCalendar.time).replaceFirstChar { it.titlecase(Locale("es", "ES")) }
        val currentDayMonth = dayMonthFormat.format(currentCalendar.time)

        when (selectedPeriodTab) {
            0 -> {
                dateText = if (isSameDay(currentCalendar, todayCalendar)) {
                    "Hoy, ${currentDayMonth.substringAfter("de ")}"
                } else {
                    "$currentDayName, $currentDayMonth"
                }
            }
            1 -> {
                val startOfWeek = currentCalendar.clone() as Calendar
                startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)
                val endOfWeek = startOfWeek.clone() as Calendar
                endOfWeek.add(Calendar.DAY_OF_YEAR, 6)
                val startStr = SimpleDateFormat("dd MMM", Locale("es", "ES")).format(startOfWeek.time)
                val endStr = SimpleDateFormat("dd MMM", Locale("es", "ES")).format(endOfWeek.time)
                dateText = "Semana: $startStr - $endStr"
            }
            2 -> {
                dateText = SimpleDateFormat("MMMM 'de' yyyy", Locale("es", "ES")).format(currentCalendar.time)
                    .replaceFirstChar { it.titlecase(Locale("es", "ES")) }
            }
            3 -> {
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
        val tipo = if (binding.tabLayoutType.selectedTabPosition == 0) "gastos" else "ingresos"
        binding.textViewDonutCenterText.text = "No hubo\n$tipo ${getCurrentPeriodText()}"
    }
}
