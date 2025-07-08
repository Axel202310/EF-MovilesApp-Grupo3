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

    companion object {
        const val EXTRA_ID_USUARIO = "ID_USUARIO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_seleccionar_divisa)

        val idUsuario = intent.getIntExtra(EXTRA_ID_USUARIO, -1)

        Log.d("Depuracion", "SeleccionarDivisaActivity recibió idUsuario: $idUsuario")

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

                val intent = Intent(this, IngresarSaldoActivity::class.java).apply {
                    putExtra(EXTRA_ID_USUARIO, idUsuario)
                    putExtra("DIVISA_SELECCIONADA", divisaSeleccionada)
                }

                Log.d("Depuracion", "SeleccionarDivisaActivity está ENVIANDO idUsuario: $idUsuario")

                startActivity(intent)

            } else {
                Toast.makeText(this, "Por favor, seleccione una divisa", Toast.LENGTH_SHORT).show()
            }
        }
    }
}