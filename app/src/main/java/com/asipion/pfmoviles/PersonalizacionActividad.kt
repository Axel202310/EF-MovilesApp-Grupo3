package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActividadPersonalizacionBinding

class PersonalizacionActividad : AppCompatActivity() {

    private lateinit var binding: ActividadPersonalizacionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadPersonalizacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarEventos()
    }

    private fun configurarEventos() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvMostrarValor.setOnClickListener {
            Toast.makeText(this, "Cambiando vista por defecto...", Toast.LENGTH_SHORT).show()
        }

        binding.tvPeriodoValor.setOnClickListener {
            Toast.makeText(this, "Cambiando periodo por defecto...", Toast.LENGTH_SHORT).show()
        }

        binding.tvCuentaValor.setOnClickListener {
            Toast.makeText(this, "Seleccionando cuenta por defecto...", Toast.LENGTH_SHORT).show()
        }
    }
}
