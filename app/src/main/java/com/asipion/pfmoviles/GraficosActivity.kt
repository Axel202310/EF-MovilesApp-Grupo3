package com.asipion.pfmoviles

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.asipion.pfmoviles.databinding.ActividadGraficosBinding




class GraficosActivity : AppCompatActivity() {

    private lateinit var binding: ActividadGraficosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadGraficosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupLegend()
        setupTabs()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            Toast.makeText(this, "Menú presionado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLegend() {
        setupLegendItem(findViewById(R.id.legend_ingresos), "ingresos", "#4DB6AC")
        setupLegendItem(findViewById(R.id.legend_gastos), "gastos", "#FFCA28")
        setupLegendItem(findViewById(R.id.legend_beneficio), "beneficio", "#4FC3F7")
        setupLegendItem(findViewById(R.id.legend_perdida), "pérdida", "#FF8A65")
    }


    private fun setupLegendItem(item: ViewGroup, label: String, color: String) {
        val legendText = item.findViewById<TextView>(R.id.legendText)
        val legendDot = item.findViewById<View>(R.id.legendDot)
        legendText.text = label

        val background = legendDot.background
        if (background is GradientDrawable) {
            background.setColor(Color.parseColor(color))
        }
    }

    private fun setupTabs() {
        binding.tabsPrincipal.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Toast.makeText(this@GraficosActivity, "Pestaña: ${tab?.text}", Toast.LENGTH_SHORT).show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.tabsPeriodo.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Toast.makeText(this@GraficosActivity, "Período: ${tab?.text}", Toast.LENGTH_SHORT).show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}
