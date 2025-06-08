package com.asipion.pfmoviles

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class CrearCategoriaActividad: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crear_categoria)
        atras()
        siguiente()
    }
    private fun atras(){
        val btnRetroceder = findViewById<Button>(R.id.btnRetroceder)

        btnRetroceder.setOnClickListener {
            val intent = Intent(this, AnadirCategoriaActividad::class.java)
            startActivity(intent)
        }
    }

    private fun siguiente(){
        val btnVerIconos = findViewById<Button>(R.id.btnVerIconos)

        btnVerIconos.setOnClickListener {
            val intent = Intent(this, CatalogoIconoActividad::class.java)
            startActivity(intent)
        }
    }
}