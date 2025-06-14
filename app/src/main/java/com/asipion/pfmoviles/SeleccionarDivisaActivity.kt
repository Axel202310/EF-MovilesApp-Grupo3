// --- Archivo: SeleccionarDivisaActivity.kt (VERSIÓN FINAL Y CORRECTA) ---
package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.util.Log // Importante para depurar
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SeleccionarDivisaActivity : AppCompatActivity() {

    // Definimos la clave del Intent como una constante para evitar errores de tipeo.
    // Esta es una excelente práctica de programación.
    companion object {
        const val EXTRA_ID_USUARIO = "ID_USUARIO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_seleccionar_divisa)

        // 1. RECIBIMOS el idUsuario que nos pasó RegistroActivity.
        // Usamos la constante para asegurar que la clave sea la correcta.
        val idUsuario = intent.getIntExtra(EXTRA_ID_USUARIO, -1)

        // ---- PASO DE DEPURACIÓN (MUY IMPORTANTE) ----
        // Esto imprimirá en tu Logcat el ID que esta Activity ha recibido.
        // Si aquí ves "-1", el problema viene de RegistroActivity.
        // Si aquí ves un número válido (ej. 1, 2, 3), el problema está en cómo se envía más abajo.
        Log.d("Depuracion", "SeleccionarDivisaActivity recibió idUsuario: $idUsuario")

        // Si el ID es inválido, no deberíamos estar aquí. Volvemos al inicio.
        if (idUsuario == -1) {
            Toast.makeText(this, "Error crítico: ID de usuario no recibido.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, AuthLandingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return
        }

        val campoDivisa = findViewById<AutoCompleteTextView>(R.id.campo_divisa)
        val botonSiguiente = findViewById<Button>(R.id.boton_siguiente)

        val divisas = arrayOf("PEN", "USD", "EUR")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, divisas)
        campoDivisa.setAdapter(adapter)
        campoDivisa.setOnClickListener { campoDivisa.showDropDown() }

        botonSiguiente.setOnClickListener {
            val divisaSeleccionada = campoDivisa.text.toString()
            if (divisaSeleccionada.isNotEmpty()) {

                // 2. CREAMOS el nuevo Intent para la siguiente pantalla.
                val intent = Intent(this, IngresarSaldoActivity::class.java).apply {
                    // 3. PASAMOS el idUsuario que recibimos al principio.
                    // Usamos la constante de nuevo para garantizar la consistencia.
                    putExtra(EXTRA_ID_USUARIO, idUsuario)
                    putExtra("DIVISA_SELECCIONADA", divisaSeleccionada)
                }

                // ---- PASO DE DEPURACIÓN (MUY IMPORTANTE) ----
                // Esto te confirmará que el Intent que estás a punto de enviar SÍ CONTIENE el ID correcto.
                Log.d("Depuracion", "SeleccionarDivisaActivity está ENVIANDO idUsuario: $idUsuario")

                startActivity(intent)

            } else {
                Toast.makeText(this, "Por favor, seleccione una divisa", Toast.LENGTH_SHORT).show()
            }
        }
    }
}