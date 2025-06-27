package com.asipion.pfmoviles

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActividadAgregarCuentaBinding
import com.asipion.pfmoviles.model.CuentaParaCrear
import com.asipion.pfmoviles.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AgregarCuentaActividad : AppCompatActivity() {

    private lateinit var binding: ActividadAgregarCuentaBinding
    private lateinit var iconos: List<ImageView>
    // Guardaremos el nombre del recurso drawable como un String.
    private var nombreIconoSeleccionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadAgregarCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarIconos()
        configurarListeners()
    }

    private fun inicializarIconos() {
        iconos = listOf(
            binding.ivYape, binding.ivPlin, binding.ivIzipay,
            binding.ivAgora, binding.ivPanda, binding.ivPaypal
        )

        for (icono in iconos) {
            icono.setOnClickListener {
                // Obtenemos el nombre del recurso drawable directamente de la vista.
                val resourceName = resources.getResourceEntryName(it.id)
                // Para los íconos, usaremos el nombre del drawable que está asignado en el XML.
                // Es más robusto si lo asociamos a un tag en el XML, pero esto funciona.
                // Por ejemplo, para iv_yape, guardaremos "ic_yape".
                nombreIconoSeleccionado = obtenerNombreDrawable(it as ImageView)
                resaltarIconoSeleccionado(it)
            }
        }
    }

    // Función auxiliar para obtener el nombre del drawable de un ImageView
    private fun obtenerNombreDrawable(imageView: ImageView): String {
        return when (imageView.id) {
            R.id.iv_yape -> "ic_yape"
            R.id.iv_plin -> "ic_plin"
            R.id.iv_izipay -> "ic_izipayya"
            R.id.iv_agora -> "ic_agorapay"
            R.id.iv_panda -> "ic_panda"
            R.id.iv_paypal -> "ic_paypal"
            else -> ""
        }
    }

    private fun configurarListeners() {
        binding.ivBackArrow.setOnClickListener { finish() }
        binding.btnAgregarCuenta.setOnClickListener { guardarNuevaCuenta() }
    }

    private fun guardarNuevaCuenta() {
        val nombreCuenta = binding.etNombreCuenta.text.toString().trim()
        val saldoInicialStr = binding.etSaldo.text.toString().trim()
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)

        if (nombreCuenta.isEmpty() || saldoInicialStr.isEmpty()) {
            Toast.makeText(this, "Por favor, complete el nombre y el saldo", Toast.LENGTH_SHORT).show()
            return
        }

        if (idUsuario == -1) {
            Toast.makeText(this, "Error de sesión.", Toast.LENGTH_SHORT).show()
            return
        }

        val saldoInicial = saldoInicialStr.toDoubleOrNull()
        if (saldoInicial == null) {
            Toast.makeText(this, "Por favor, ingrese un saldo válido", Toast.LENGTH_SHORT).show()
            return
        }

        val iconoSeleccionado = nombreIconoSeleccionado ?: "ic_dollar_placeholder"
        val nuevaCuenta = CuentaParaCrear(
            idUsuario = idUsuario,
            nombreCuenta = nombreCuenta,
            saldoActual = saldoInicial,
            moneda = "PEN",
            imgCuenta = iconoSeleccionado
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.crearCuenta(nuevaCuenta)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AgregarCuentaActividad, "Cuenta añadida correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AgregarCuentaActividad, "Error al crear la cuenta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AgregarCuentaActividad, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun resaltarIconoSeleccionado(iconoSeleccionado: View) {
        for (icono in iconos) {
            // Ponemos un fondo de borde si es el seleccionado, si no, lo quitamos.
            if (icono.id == iconoSeleccionado.id) {
                icono.setBackgroundResource(R.drawable.bg_icon_selector) // bg_icon_selector es un borde.
            } else {
                icono.background = null // Quitamos el fondo
            }
        }
    }
}