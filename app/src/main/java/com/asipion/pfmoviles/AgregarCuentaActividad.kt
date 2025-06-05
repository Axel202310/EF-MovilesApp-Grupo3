package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AgregarCuentaActividad : AppCompatActivity() {

    private lateinit var etNombreCuenta: EditText
    private lateinit var etSaldo: EditText
    private lateinit var btnAgregar: Button
    private lateinit var iconos: List<ImageView>
    private var iconoSeleccionado: Int? = null  // ID del recurso seleccionado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_agregar_cuenta)

        // Vincular vistas
        etNombreCuenta = findViewById(R.id.et_nombre_cuenta)
        etSaldo = findViewById(R.id.et_saldo)
        btnAgregar = findViewById(R.id.btn_agregar_cuenta)

        // Inicializar íconos
        iconos = listOf(
            findViewById(R.id.iv_yape),
            findViewById(R.id.iv_plin),
            findViewById(R.id.iv_izipay),
            findViewById(R.id.iv_agora),
            findViewById(R.id.iv_panda),
            findViewById(R.id.iv_paypal)
        )

        for (icono in iconos) {
            icono.setOnClickListener {
                iconoSeleccionado = icono.id
                resaltarIconoSeleccionado(icono.id)
            }
        }

        btnAgregar.setOnClickListener {
            val nombreCuenta = etNombreCuenta.text.toString().trim()
            val saldoInicial = etSaldo.text.toString().toDoubleOrNull()

            if (nombreCuenta.isEmpty() || saldoInicial == null || iconoSeleccionado == null) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Aquí puedes guardar la cuenta en la base de datos, lista temporal, etc.
            Toast.makeText(this, "Cuenta '$nombreCuenta' añadida correctamente", Toast.LENGTH_SHORT).show()

            finish() // Cerramos esta actividad
        }
    }

    private fun resaltarIconoSeleccionado(idSeleccionado: Int) {
        for (icono in iconos) {
            icono.setBackgroundResource(
                if (icono.id == idSeleccionado)
                    R.drawable.bg_icon_selector  // el fondo seleccionado
                else
                    android.R.color.transparent   // fondo transparente para los demás
            )
        }
    }
}
