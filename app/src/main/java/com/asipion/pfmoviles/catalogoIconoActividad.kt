package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class catalogoIconoActividad : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.catalogo_icono)
        atras()
    }

    private fun atras(){
        val btnRetroceder = findViewById<Button>(R.id.btnRetroceder)

        btnRetroceder.setOnClickListener {
            val intent = Intent(this, CrearCategoriaActividad::class.java)
            startActivity(intent)
        }
    }
}