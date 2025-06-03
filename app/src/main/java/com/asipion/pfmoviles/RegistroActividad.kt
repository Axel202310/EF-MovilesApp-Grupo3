package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegistroActividad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_registro)

        val botonRegistrarse = findViewById<Button>(R.id.boton_registrarse)
        botonRegistrarse.setOnClickListener {
            startActivity(Intent(this, IngresarCorreoActividad::class.java))
        }
    }
}
