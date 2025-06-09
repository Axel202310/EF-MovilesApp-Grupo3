package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.model.*
import com.asipion.pfmoviles.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistroContrasenaActividad : AppCompatActivity() {

    private lateinit var campoContrasena: EditText
    private lateinit var botonSiguiente: Button
    private lateinit var progreso: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_registro_contrasena)

        val botonAtras = findViewById<ImageView>(R.id.boton_atras)
        campoContrasena = findViewById(R.id.campo_contrasena)
        botonSiguiente = findViewById(R.id.boton_siguiente)
        progreso = ProgressBar(this).apply { visibility = ProgressBar.GONE }


        val correo = intent.getStringExtra("correo") ?: ""

        botonAtras.setOnClickListener { finish() }

        campoContrasena.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val habilitar = !s.isNullOrBlank()
                botonSiguiente.isEnabled = habilitar
                botonSiguiente.alpha = if (habilitar) 1f else 0.5f
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        botonSiguiente.setOnClickListener {
            val contrasena = campoContrasena.text.toString().trim()

            if (contrasena.length < 6) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            botonSiguiente.isEnabled = false
            progreso.visibility = ProgressBar.VISIBLE


            //PARTE DE LA BASE DE DATOS
            val usuario = Usuario(0,correo, contrasena)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.webService.agregarUsuario(usuario)
                    if (response.isSuccessful) {
                        val mensajeRespuesta = response.body()?.mensaje ?: "Usuario agregado"
                        runOnUiThread {
                            Toast.makeText(this@RegistroContrasenaActividad, mensajeRespuesta, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RegistroContrasenaActividad, RegistroActividad::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        runOnUiThread {
                            Toast.makeText(this@RegistroContrasenaActividad, "Error backend: $errorBody", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this@RegistroContrasenaActividad, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}
