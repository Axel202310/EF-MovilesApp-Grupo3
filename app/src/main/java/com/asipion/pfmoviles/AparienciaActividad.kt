package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.asipion.pfmoviles.databinding.ActividadAparienciaBinding

class AparienciaActividad : AppCompatActivity() {

    private lateinit var binding: ActividadAparienciaBinding
    private lateinit var listaDeIconos: List<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadAparienciaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarVistas()
        configurarEventos()
    }

    private fun inicializarVistas() {
        listaDeIconos = listOf(
            binding.iconBilletera,
            binding.iconBolsa,
            binding.iconCerdito,
            binding.iconGato
        )
    }

    private fun configurarEventos() {
        binding.ivBack.setOnClickListener { finish() }

        binding.itemIdioma.setOnClickListener {
            Toast.makeText(this, "Selector de idioma", Toast.LENGTH_SHORT).show()
        }

        binding.itemTema.setOnClickListener {
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
        // Guardar en preferencias locales en el futuro
        Toast.makeText(this, "Icono seleccionado: $idIconoSeleccionado", Toast.LENGTH_SHORT).show()
    }
}
