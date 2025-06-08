package com.asipion.pfmoviles

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActividadGraficosBinding
import com.google.android.material.tabs.TabLayout

class GraficosActivity : AppCompatActivity() {

    private lateinit var binding: ActividadGraficosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadGraficosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbar()
        configurarLeyenda()
        configurarTabs()
    }

    private fun configurarToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            Toast.makeText(this, "Menú presionado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarLeyenda() {
        configurarItemLeyenda(binding.legendIngresos, "ingresos", "#4DB6AC")
        configurarItemLeyenda(binding.legendGastos, "gastos", "#FFCA28")
        configurarItemLeyenda(binding.legendBeneficio, "beneficio", "#4FC3F7")
        configurarItemLeyenda(binding.legendPerdida, "pérdida", "#FF8A65")
    }

    private fun configurarItemLeyenda(item: ViewGroup, etiqueta: String, colorHex: String) {
        val texto = item.findViewById<TextView>(R.id.legendText)
        val punto = item.findViewById<View>(R.id.legendDot)

        texto.text = etiqueta
        (punto.background as? GradientDrawable)?.setColor(Color.parseColor(colorHex))
    }

    private fun configurarTabs() {
        binding.tabsPrincipal.addOnTabSelectedListener(tabListener("Pestaña"))
        binding.tabsPeriodo.addOnTabSelectedListener(tabListener("Período"))
    }

    private fun tabListener(tipo: String): TabLayout.OnTabSelectedListener {
        return object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Toast.makeText(this@GraficosActivity, "$tipo: ${tab?.text}", Toast.LENGTH_SHORT).show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        }
    }
}
