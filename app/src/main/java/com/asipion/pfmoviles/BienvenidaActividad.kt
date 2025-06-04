package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BienvenidaActividad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_bienvenida)

        val botonComenzar = findViewById<Button>(R.id.boton_comenzar)
        botonComenzar.setOnClickListener {
            startActivity(Intent(this, RegistroActividad::class.java))
        }
    }
}
