package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuPrincipalActividad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_principal)
        ircategorias()

    }
    private fun ircategorias(){
        val btnCategoria = findViewById<Button>(R.id.btnCategoria)

        btnCategoria.setOnClickListener {
            val intent = Intent(this, MenuCategoriasActividad::class.java)
            startActivity(intent)
        }
    }

}