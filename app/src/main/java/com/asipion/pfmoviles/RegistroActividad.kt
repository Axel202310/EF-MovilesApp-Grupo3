package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegistroActividad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_registro)

        val botonRegistrarse = findViewById<Button>(R.id.boton_registrarse)
        val textoIniciarSesion = findViewById<TextView>(R.id.texto_iniciar_sesion)

        botonRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegistroCorreoActividad::class.java))
        }

        textoIniciarSesion.setOnClickListener {
            startActivity(Intent(this, IniciarSesionCorreoActividad::class.java))
        }
    }
}
