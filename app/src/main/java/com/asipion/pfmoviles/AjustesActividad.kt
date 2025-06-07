package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AjustesActividad : AppCompatActivity() {

    private lateinit var itemPin: LinearLayout
    private lateinit var itemPersonalizacion: LinearLayout
    private lateinit var itemApariencia: LinearLayout
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_ajustes)
        inicializarVistas()
        configurarEventos()
    }

    private fun inicializarVistas() {
        itemPin = findViewById(R.id.item_pin)
        itemPersonalizacion = findViewById(R.id.item_personalizacion)
        itemApariencia = findViewById(R.id.item_apariencia)
        menuIcon = findViewById(R.id.iv_menu)
    }

    private fun configurarEventos() {
        itemPin.setOnClickListener {
            startActivity(Intent(this, PinActividad::class.java))
        }
        itemPersonalizacion.setOnClickListener {
            startActivity(Intent(this, PersonalizacionActividad::class.java))
        }
        itemApariencia.setOnClickListener {
            startActivity(Intent(this, AparienciaActividad::class.java))
        }
        menuIcon.setOnClickListener {
            Toast.makeText(this, "Menú presionado", Toast.LENGTH_SHORT).show()
        }
    }
}
