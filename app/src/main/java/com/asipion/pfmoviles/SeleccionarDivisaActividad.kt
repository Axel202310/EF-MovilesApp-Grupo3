package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SeleccionarDivisaActividad : AppCompatActivity() {

    private lateinit var campoDivisa: AutoCompleteTextView
    private lateinit var botonSiguiente: Button

    private val divisasDisponibles = listOf(
        "Sol Peruano - PEN",
        "Dólar Estadounidense - USD",
        "Peso Colombiano - COP",
        "Peso Argentino - ARS",
        "Real Brasileño - BRL",
        "Peso Mexicano - MXN",
        "Euro - EUR"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_seleccionar_divisa)

        campoDivisa = findViewById(R.id.campo_divisa)
        botonSiguiente = findViewById(R.id.boton_siguiente)

        val adaptador = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, divisasDisponibles)
        campoDivisa.setAdapter(adaptador)

        desactivarBoton()

        // Mostrar la lista al tocar el campo
        campoDivisa.setOnClickListener {
            campoDivisa.showDropDown()
        }

        campoDivisa.setOnItemClickListener { _, _, posicion, _ ->
            val seleccion = divisasDisponibles[posicion]
            campoDivisa.setText(seleccion, false)
            activarBoton()
        }

        campoDivisa.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (divisasDisponibles.contains(s.toString())) {
                    activarBoton()
                } else {
                    desactivarBoton()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        botonSiguiente.setOnClickListener {
            val seleccion = campoDivisa.text.toString()
            val intent = Intent(this, IngresarSaldoActividad::class.java)
            intent.putExtra("divisa", seleccion)
            startActivity(intent)
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
