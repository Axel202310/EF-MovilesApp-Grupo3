package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActividadAjustesBinding

class AjustesActividad : AppCompatActivity() {

    private lateinit var binding: ActividadAjustesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadAjustesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarEventos()
    }

    private fun configurarEventos() {
        binding.itemPin.setOnClickListener {
            startActivity(Intent(this, PinActividad::class.java))
        }

        binding.itemPersonalizacion.setOnClickListener {
            startActivity(Intent(this, PersonalizacionActividad::class.java))
        }

        binding.itemApariencia.setOnClickListener {
            startActivity(Intent(this, AparienciaActividad::class.java))
        }

        binding.ivMenu.setOnClickListener {
            Toast.makeText(this, "Menú presionado", Toast.LENGTH_SHORT).show()
        }
    }
}
