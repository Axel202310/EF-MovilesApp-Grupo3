package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActividadPagosHabitualesBinding

class PagosHabitualesActividad : AppCompatActivity() {

    private lateinit var binding: ActividadPagosHabitualesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadPagosHabitualesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutCrearPago.setOnClickListener {
            startActivity(Intent(this, CrearRecordatorioActividad::class.java))
        }
    }
}
