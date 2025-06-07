package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistroContrasenaActividad : AppCompatActivity() {

    private lateinit var campoContrasena: EditText
    private lateinit var botonSiguiente: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progreso: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_registro_contrasena)

        val botonAtras = findViewById<ImageView>(R.id.boton_atras)
        campoContrasena = findViewById(R.id.campo_contrasena)
        botonSiguiente = findViewById(R.id.boton_siguiente)
        progreso = ProgressBar(this).apply { visibility = ProgressBar.GONE }

        auth = FirebaseAuth.getInstance()
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
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            botonSiguiente.isEnabled = false
            progreso.visibility = ProgressBar.VISIBLE

            auth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener { task ->
                    botonSiguiente.isEnabled = true
                    progreso.visibility = ProgressBar.GONE

                    if (task.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()

                        val user = auth.currentUser
                        val uid = user?.uid ?: ""
                        val firestore = FirebaseFirestore.getInstance()
                        val datosUsuario = hashMapOf(
                            "uid" to uid,
                            "email" to correo,
                            "nombre" to correo.substringBefore("@"),
                            "profesion" to "Desconocida"
                        )
                        firestore.collection("users").document(uid).set(datosUsuario)

                        Toast.makeText(this, "Registro exitoso. Verifique su correo.", Toast.LENGTH_LONG).show()

                        // 🔄 Redirigir a RegistroActividad
                        val intent = Intent(this, RegistroActividad::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
