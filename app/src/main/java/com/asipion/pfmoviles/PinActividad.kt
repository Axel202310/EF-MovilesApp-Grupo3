package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActividadPinBinding

class PinActividad : AppCompatActivity() {

    private lateinit var binding: ActividadPinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarEventos()
    }

    private fun configurarEventos() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnEstablecerPin.setOnClickListener {
            iniciarProcesoDePin()
        }
    }

    private fun iniciarProcesoDePin() {
        Toast.makeText(this, "Abriendo pantalla para establecer PIN...", Toast.LENGTH_SHORT).show()
        // A futuro: abrir la pantalla real de ingreso de PIN
        // startActivity(Intent(this, IngresarPinActividad::class.java))
    }
}
