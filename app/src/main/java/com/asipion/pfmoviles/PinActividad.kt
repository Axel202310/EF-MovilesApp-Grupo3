package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

// 1. Heredamos de AppCompatActivity para definir esta clase como una pantalla.
class PinActividad : AppCompatActivity() {

    // 2. Declaramos las referencias a las vistas interactivas.
    private lateinit var ivBack: ImageView
    private lateinit var btnEstablecerPin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_pin)

        // 3. Separación lógica del flujo.
        inicializarVistas()
        configurarEventos()
    }

    /**
     * Encuentra y vincula las vistas del layout a las variables.
     */
    private fun inicializarVistas() {
        ivBack = findViewById(R.id.iv_back)
        btnEstablecerPin = findViewById(R.id.btn_establecer_pin)
    }

    /**
     * Define los eventos de clic para los elementos de la interfaz.
     */
    private fun configurarEventos() {
        ivBack.setOnClickListener {
            finish() // Cierra esta pantalla y vuelve a la anterior.
        }

        btnEstablecerPin.setOnClickListener {
            iniciarProcesoDePin()
        }
    }

    /**
     * Simula el inicio del proceso de establecer un PIN.
     * A futuro, debería mostrar un diálogo seguro o abrir otra actividad.
     */
    private fun iniciarProcesoDePin() {
        Toast.makeText(this, "Abriendo pantalla para establecer PIN...", Toast.LENGTH_SHORT).show()

        // A FUTURO:
        // startActivity(Intent(this, IngresarPinActividad::class.java))
    }
}
