package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class IngresarSaldoActividad : AppCompatActivity() {

    private lateinit var campoSaldo: EditText
    private lateinit var botonSiguiente: Button
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_ingresar_saldo)

        val divisa = intent.getStringExtra("divisa") ?: "PEN"
        val textoDivisa = findViewById<TextView>(R.id.texto_divisa_seleccionada)
        textoDivisa.text = divisa // <-- Esto mostrará la divisa seleccionada

        campoSaldo = findViewById(R.id.campo_saldo)
        botonSiguiente = findViewById(R.id.boton_siguiente_saldo)

        desactivarBoton()

        campoSaldo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString()
                val numeroValido = texto.toDoubleOrNull()
                if (!texto.isNullOrBlank() && numeroValido != null && numeroValido > 0) {
                    activarBoton()
                } else {
                    desactivarBoton()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        botonSiguiente.setOnClickListener {
            val saldo = campoSaldo.text.toString().toDoubleOrNull()
            val divisa = intent.getStringExtra("divisa") ?: "PEN"

            val usuario = auth.currentUser
            if (usuario != null && saldo != null && saldo > 0) {
                val datosUsuario = hashMapOf(
                    "correo" to usuario.email,
                    "divisa" to divisa,
                    "saldo" to saldo
                )

                firestore.collection("usuarios").document(usuario.uid)
                    .set(datosUsuario)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, InicioActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al guardar en Firestore: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Ingrese un saldo válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun activarBoton() {
        botonSiguiente.isEnabled = true
        botonSiguiente.alpha = 1.0f
    }

    private fun desactivarBoton() {
        botonSiguiente.isEnabled = false
        botonSiguiente.alpha = 0.5f
    }
}
