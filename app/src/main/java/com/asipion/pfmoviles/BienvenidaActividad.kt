package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class BienvenidaActividad : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_bienvenida)

        auth = FirebaseAuth.getInstance()
        val botonComenzar = findViewById<Button>(R.id.boton_comenzar)

        botonComenzar.setOnClickListener {
            verificarSesionFirebase()
        }
    }

    private fun verificarSesionFirebase() {
        val usuario = auth.currentUser

        if (usuario != null) {
            // ⚠️ Verificamos con el servidor que el usuario sigue existiendo
            usuario.reload()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userRecargado = auth.currentUser
                        if (userRecargado != null && userRecargado.isEmailVerified) {
                            val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
                            val validado = prefs.getBoolean("validado_contrasena", false)

                            val destino = if (validado) {
                                InicioActivity::class.java
                            } else {
                                SeleccionarDivisaActividad::class.java
                            }

                            startActivity(Intent(this, destino))
                        } else {
                            Toast.makeText(this, "Verifica tu correo para continuar.", Toast.LENGTH_SHORT).show()
                            auth.signOut()
                            startActivity(Intent(this, RegistroActividad::class.java))
                        }
                    } else {
                        // ❌ Falló la recarga (el usuario no existe en Firebase)
                        Log.w("Bienvenida", "Usuario no válido. Redirigiendo al registro.")
                        auth.signOut()
                        startActivity(Intent(this, RegistroActividad::class.java))
                    }

                    finish()
                }
        } else {
            // No hay sesión activa
            startActivity(Intent(this, RegistroActividad::class.java))
            finish()
        }
    }
}
