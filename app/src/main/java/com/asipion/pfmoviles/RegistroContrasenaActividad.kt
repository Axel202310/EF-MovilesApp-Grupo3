package com.asipion.pfmoviles

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class RegistroContrasenaActividad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_registro_contrasena)

        val botonAtras = findViewById<ImageView>(R.id.boton_atras)
        val campoContrasena = findViewById<EditText>(R.id.campo_contrasena)
        val botonSiguiente = findViewById<Button>(R.id.boton_siguiente)

        botonAtras.setOnClickListener {
            finish()
        }

        campoContrasena.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val habilitar = !s.isNullOrBlank()
                botonSiguiente.isEnabled = habilitar
                botonSiguiente.alpha = if (habilitar) 1f else 0.5f
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
