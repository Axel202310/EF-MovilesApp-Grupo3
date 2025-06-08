package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActividadRecordatoriosBinding

class RecordatoriosActividad : AppCompatActivity() {

    private lateinit var binding: ActividadRecordatoriosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadRecordatoriosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarEventos()
    }

    private fun configurarEventos() {
        binding.layoutCrearRecordatorio.setOnClickListener {
            startActivity(Intent(this, CrearRecordatorioActividad::class.java))
        }

        binding.switchRecordatorios.setOnCheckedChangeListener { _, isChecked ->
            val mensaje = if (isChecked) "Recordatorios ACTIVADOS" else "Recordatorios DESACTIVADOS"
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            // Aquí puedes agregar la lógica real de activación/desactivación de recordatorios
        }

        binding.ivMenu.setOnClickListener {
            Toast.makeText(this, "Menú presionado", Toast.LENGTH_SHORT).show()
        }
    }
}
