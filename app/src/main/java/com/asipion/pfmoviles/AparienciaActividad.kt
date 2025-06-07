package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class AparienciaActividad : AppCompatActivity() {

    private lateinit var botonAtras: ImageView
    private lateinit var itemIdioma: LinearLayout
    private lateinit var itemTema: LinearLayout

    private lateinit var iconBilletera: ImageView
    private lateinit var iconBolsa: ImageView
    private lateinit var iconCerdito: ImageView
    private lateinit var iconGato: ImageView
    private lateinit var listaDeIconos: List<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_apariencia)

        inicializarVistas()
        configurarEventos()
    }

    private fun inicializarVistas() {
        botonAtras = findViewById(R.id.iv_back)
        itemIdioma = findViewById(R.id.item_idioma)
        itemTema = findViewById(R.id.item_tema)

        iconBilletera = findViewById(R.id.icon_billetera)
        iconBolsa = findViewById(R.id.icon_bolsa)
        iconCerdito = findViewById(R.id.icon_cerdito)
        iconGato = findViewById(R.id.icon_gato)

        listaDeIconos = listOf(iconBilletera, iconBolsa, iconCerdito, iconGato)
    }

    private fun configurarEventos() {
        botonAtras.setOnClickListener { finish() }

        itemIdioma.setOnClickListener {
            Toast.makeText(this, "Selector de idioma", Toast.LENGTH_SHORT).show()
        }

        itemTema.setOnClickListener {
            Toast.makeText(this, "Selector de tema", Toast.LENGTH_SHORT).show()
        }

        listaDeIconos.forEach { icono ->
            icono.setOnClickListener {
                seleccionarIcono(it as ImageView)
            }
        }
    }

    private fun seleccionarIcono(iconoSeleccionado: ImageView) {
        listaDeIconos.forEach { icono ->
            val fondo = if (icono.id == iconoSeleccionado.id)
                R.drawable.background_icon_app_selected
            else
                R.drawable.background_icon_app_default

            icono.background = ContextCompat.getDrawable(this, fondo)
        }

        guardarPreferenciaDeIcono(iconoSeleccionado.contentDescription.toString())
    }

    private fun guardarPreferenciaDeIcono(idIconoSeleccionado: String) {
        // Este método puede ser adaptado para guardar en SharedPreferences
        Toast.makeText(this, "Icono seleccionado: $idIconoSeleccionado", Toast.LENGTH_SHORT).show()
    }
}
