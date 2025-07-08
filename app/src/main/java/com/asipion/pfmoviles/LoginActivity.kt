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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Usa el nuevo layout consolidado

        val campoCorreo = findViewById<EditText>(R.id.campo_correo)
        val campoContrasena = findViewById<EditText>(R.id.campo_contrasena)
        val botonLogin = findViewById<Button>(R.id.boton_iniciar_sesion)

        botonLogin.setOnClickListener {
            val correo = campoCorreo.text.toString().trim()
            val password = campoContrasena.text.toString().trim()

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuarioLogin = Usuario(idUsuario = 0, correoUsuario = correo, password = password)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.webService.iniciarSesion(usuarioLogin)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val mensajeResponse = response.body()
                            val usuario = mensajeResponse?.usuario
                            if (usuario != null) {
                                val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
                                prefs.edit()
                                    .putInt("id_usuario", usuario.idUsuario)
                                    .putString("correo_usuario", usuario.correoUsuario)
                                    .apply()

                                Toast.makeText(this@LoginActivity, "¡Bienvenido de nuevo!", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@LoginActivity, InicioActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}