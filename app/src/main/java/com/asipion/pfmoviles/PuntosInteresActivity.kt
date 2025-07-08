package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActivityPuntosInteresBinding

class PuntosInteresActivity : AppCompatActivity() {

    // Se declara la variable para el View Binding, que nos da acceso seguro a los botones del layout.
    private lateinit var binding: ActivityPuntosInteresBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se infla (crea) la vista a partir del layout XML y se establece como el contenido de la pantalla.
        binding = ActivityPuntosInteresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Llamamos a la función que configura los listeners para mantener el código organizado.
        configurarListenersDeBotones()
    }

    private fun configurarListenersDeBotones() {
        // Listener para el botón de "Cajeros BCP"
        binding.btnVerAtmsBcp.setOnClickListener {
            abrirMapa(
                latitud = -12.071093000039065,
                longitud = -77.06044971943385,
                titulo = "BCP Bolivar",
                idIcono = R.drawable.logo_bcp // Se pasa el ID del logo del BCP
            )
        }

        // Listener para el botón de "Sucursales Interbank"
        binding.btnVerSucursalesInterbank.setOnClickListener {
            abrirMapa(
                latitud = -12.066038756336305,
                longitud = -77.04820378774586,
                titulo = "ATM Interbank - Breña",
                idIcono = R.drawable.logo_interbank // Se pasa el ID del logo de Interbank
            )
        }

        // Listener para el botón de "Agentes Yape"
        binding.btnVerAgentesYape.setOnClickListener {
            abrirMapa(
                latitud = -12.061998587582847,
                longitud = -77.06546813250539,
                titulo = "Agente Yape - Breña",
                idIcono = R.drawable.ic_yape // Se pasa el ID del logo de Yape
            )
        }
    }

    private fun abrirMapa(latitud: Double, longitud: Double, titulo: String, idIcono: Int) {
        // Se crea el Intent para navegar a MapaActivity
        val intent = Intent(this, MapaActivity::class.java).apply {
            // Se añaden los datos como "extras" al Intent.
            // Cada extra tiene una clave única (ej. "EXTRA_LATITUD") para poder recuperarlo en la otra pantalla.
            putExtra("EXTRA_LATITUD", latitud)
            putExtra("EXTRA_LONGITUD", longitud)
            putExtra("EXTRA_TITULO", titulo)
            putExtra("EXTRA_ID_ICONO", idIcono) // Se añade el ID del ícono personalizado
        }
        // Inicia la MapaActivity
        startActivity(intent)
    }
}