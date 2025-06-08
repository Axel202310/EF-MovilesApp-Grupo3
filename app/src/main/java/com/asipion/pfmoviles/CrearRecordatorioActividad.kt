package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActividadCrearRecordatorioBinding
import com.google.android.material.tabs.TabLayout

class CrearRecordatorioActividad : AppCompatActivity() {

    private lateinit var binding: ActividadCrearRecordatorioBinding
    private var esModoGasto: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadCrearRecordatorioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarEventos()
        actualizarVistaParaGastos() // Por defecto
    }

    private fun configurarEventos() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                esModoGasto = (tab?.position == 0)
                if (esModoGasto) {
                    actualizarVistaParaGastos()
                } else {
                    actualizarVistaParaIngresos()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun actualizarVistaParaGastos() {
        Toast.makeText(this, "Modo: Gastos", Toast.LENGTH_SHORT).show()
        // Aquí puedes actualizar visualmente los elementos del layout.
    }

    private fun actualizarVistaParaIngresos() {
        Toast.makeText(this, "Modo: Ingresos", Toast.LENGTH_SHORT).show()
        // Aquí puedes actualizar visualmente los elementos del layout.
    }
}
