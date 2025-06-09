package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class BienvenidaActividad : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_bienvenida)
        siguiente()
    }
    private fun siguiente(){
        val botonComenzar = findViewById<Button>(R.id.boton_comenzar)

        botonComenzar.setOnClickListener {
            val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
            val idUsuario = prefs.getInt("id_usuario", -1)

            if (idUsuario != -1) {
                // Usuario ya está autenticado
                startActivity(Intent(this, InicioActivity::class.java))
            } else {
                // Usuario no autenticado, enviar a login o flujo inicial
                startActivity(Intent(this, RegistroActividad::class.java))
            }
            val intent = Intent(this, RegistroActividad::class.java)
            startActivity(intent)
        }
    }
}
