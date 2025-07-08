package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.model.Usuario
import com.asipion.pfmoviles.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val campoCorreo = findViewById<EditText>(R.id.campo_correo)
        val campoContrasena = findViewById<EditText>(R.id.campo_contrasena)
        val campoConfirmar = findViewById<EditText>(R.id.campo_confirmar_contrasena)
        val botonRegistro = findViewById<Button>(R.id.boton_registrarse)

        botonRegistro.setOnClickListener {
            val correo = campoCorreo.text.toString().trim()
            val password = campoContrasena.text.toString().trim()
            val confirmarPassword = campoConfirmar.text.toString().trim()

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmarPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuarioRegistro = Usuario(idUsuario = 0, correoUsuario = correo, password = password)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.webService.registrarUsuario(usuarioRegistro)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val usuarioRespuesta = response.body()?.usuario
                            val idNuevoUsuario = usuarioRespuesta?.idUsuario ?: -1
                            val correoNuevoUsuario = usuarioRespuesta?.correoUsuario ?: ""

                            if (idNuevoUsuario != -1) {
                                // Guardamos la sesión
                                val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
                                prefs.edit()
                                    .putInt("id_usuario", idNuevoUsuario)
                                    .putString("correo_usuario", correoNuevoUsuario)
                                    .apply()

                                Toast.makeText(this@RegistroActivity, "Registro exitoso. Ahora configura tu cuenta.", Toast.LENGTH_LONG).show()

                                // Navegamos al flujo de configuración inicial
                                val intent = Intent(this@RegistroActivity,
                                    SeleccionarDivisaActivity::class.java).apply {
                                    // Pasamos los datos del nuevo usuario a la siguiente pantalla
                                    putExtra(SeleccionarDivisaActivity.EXTRA_ID_USUARIO, idNuevoUsuario)
                                }
                                // Limpiamos la pila de actividades para que no se pueda volver
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)

                            } else {
                                Toast.makeText(this@RegistroActivity, "Error: No se recibió un ID de usuario válido.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            val errorMsg = response.body()?.mensaje ?: "El correo podría ya estar en uso."
                            Toast.makeText(this@RegistroActivity, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegistroActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}