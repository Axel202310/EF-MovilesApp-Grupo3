package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActividadAgregarCuentaBinding

class AgregarCuentaActividad : AppCompatActivity() {

    private lateinit var binding: ActividadAgregarCuentaBinding
    private lateinit var iconos: List<ImageView>
    private var iconoSeleccionado: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadAgregarCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarIconos()
        configurarEventos()
    }

    private fun inicializarIconos() {
        iconos = listOf(
            binding.ivYape,
            binding.ivPlin,
            binding.ivIzipay,
            binding.ivAgora,
            binding.ivPanda,
            binding.ivPaypal
        )

        for (icono in iconos) {
            icono.setOnClickListener {
                iconoSeleccionado = icono.id
                resaltarIconoSeleccionado(icono.id)
            }
        }
    }

    private fun configurarEventos() {
        binding.btnAgregarCuenta.setOnClickListener {
            val nombreCuenta = binding.etNombreCuenta.text.toString().trim()
            val saldoInicial = binding.etSaldo.text.toString().toDoubleOrNull()

            if (nombreCuenta.isEmpty() || saldoInicial == null || iconoSeleccionado == null) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Cuenta '$nombreCuenta' añadida correctamente", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun resaltarIconoSeleccionado(idSeleccionado: Int) {
        for (icono in iconos) {
            icono.setBackgroundResource(
                if (icono.id == idSeleccionado)
                    R.drawable.bg_icon_selector
                else
                    android.R.color.transparent
            )
        }
    }
}
