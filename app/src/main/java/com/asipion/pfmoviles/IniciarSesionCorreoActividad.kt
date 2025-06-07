package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class IniciarSesionCorreoActividad : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_iniciar_sesion_correo)

        val botonAtras = findViewById<ImageView>(R.id.boton_atras)
        val campoCorreo = findViewById<EditText>(R.id.campo_correo)
        val botonSiguiente = findViewById<Button>(R.id.boton_siguiente)

        botonAtras.setOnClickListener { finish() }

        campoCorreo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val habilitar = !s.isNullOrBlank()
                botonSiguiente.isEnabled = habilitar
                botonSiguiente.alpha = if (habilitar) 1f else 0.5f
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        botonSiguiente.setOnClickListener {
            val correo = campoCorreo.text.toString().trim()
            val intent = Intent(this, IniciarSesionContrasenaActividad::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)
        }
    }
}
