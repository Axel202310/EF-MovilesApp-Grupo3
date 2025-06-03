package com.asipion.pfmoviles

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.R

class IngresarCorreoActividad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_ingresar_correo)

        val botonAtras = findViewById<ImageView>(R.id.boton_atras)
        val botonSiguiente = findViewById<Button>(R.id.boton_siguiente)
        val entradaCorreo = findViewById<EditText>(R.id.entrada_correo)

        botonAtras.setOnClickListener {
            finish()
        }

        entradaCorreo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val tieneTexto = !s.isNullOrBlank()
                botonSiguiente.isEnabled = tieneTexto
                botonSiguiente.alpha = if (tieneTexto) 1f else 0.5f
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        botonSiguiente.setOnClickListener {
            val correo = entradaCorreo.text.toString()
            // Aquí va la lógica para continuar el registro
        }
    }
}
