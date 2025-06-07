package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// 1. Heredamos de AppCompatActivity.
class PersonalizacionActividad : AppCompatActivity() {

    // 2. Declaramos las referencias a todas las vistas interactivas.
    private lateinit var botonAtras: ImageView
    private lateinit var textoMostrarValor: TextView
    private lateinit var textoPeriodoValor: TextView
    private lateinit var textoCuentaValor: TextView
    // Nota: Podríamos hacer clickables los LinearLayout contenedores para un área táctil mayor,
    // pero para empezar, usar los TextView es directo y funcional.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 3. Conectamos el layout XML.
        setContentView(R.layout.actividad_personalizacion)

        // 4. Organizamos la lógica.
        inicializarVistas()
        configurarEventos()
    }

    /**
     * Encuentra las vistas en el layout por su ID.
     */
    private fun inicializarVistas() {
        botonAtras = findViewById(R.id.iv_back)
        textoMostrarValor = findViewById(R.id.tv_mostrar_valor)
        textoPeriodoValor = findViewById(R.id.tv_periodo_valor)
        textoCuentaValor = findViewById(R.id.tv_cuenta_valor)
    }

    /**
     * Configura los listeners de clic para cada opción de personalización.
     */
    private fun configurarEventos() {
        // Evento para el botón de retroceso.
        botonAtras.setOnClickListener {
            finish() // Cierra la pantalla actual.
        }

        // Evento para cambiar la opción "Mostrar por defecto".
        textoMostrarValor.setOnClickListener {
            // A futuro, aquí se abriría un diálogo con opciones como "Total", "Gastos", etc.
            Toast.makeText(this, "Cambiando vista por defecto...", Toast.LENGTH_SHORT).show()
        }

        // Evento para cambiar el "Periodo por defecto".
        textoPeriodoValor.setOnClickListener {
            // A futuro, aquí se abriría un diálogo con opciones como "Semana", "Mes", "Año".
            Toast.makeText(this, "Cambiando periodo por defecto...", Toast.LENGTH_SHORT).show()
        }

        // Evento para cambiar la "Cuenta por defecto".
        textoCuentaValor.setOnClickListener {
            // A futuro, aquí se abriría un diálogo con la lista de cuentas del usuario.
            Toast.makeText(this, "Seleccionando cuenta por defecto...", Toast.LENGTH_SHORT).show()
        }
    }
}