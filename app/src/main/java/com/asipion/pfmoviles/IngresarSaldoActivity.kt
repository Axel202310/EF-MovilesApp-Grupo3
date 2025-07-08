package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.model.CuentaParaCrear
import com.asipion.pfmoviles.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IngresarSaldoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_ingresar_saldo)

        val idUsuario = intent.getIntExtra(SeleccionarDivisaActivity.EXTRA_ID_USUARIO, -1)
        val divisaSeleccionada = intent.getStringExtra("DIVISA_SELECCIONADA") ?: "PEN"

        if (idUsuario <= 0) {
            Toast.makeText(this, "Error de sesión (ID inválido: $idUsuario). Por favor, inicie de nuevo.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, AuthLandingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return
        }

        val campoSaldo = findViewById<EditText>(R.id.campo_saldo)
        val textoDivisa = findViewById<TextView>(R.id.texto_divisa_seleccionada)
        val botonSiguiente = findViewById<Button>(R.id.boton_siguiente_saldo)

        textoDivisa.text = divisaSeleccionada
        botonSiguiente.isEnabled = true
        botonSiguiente.alpha = 1.0f

        botonSiguiente.setOnClickListener {
            val saldoTexto = campoSaldo.text.toString()
            if (saldoTexto.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese un saldo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val saldoInicial = saldoTexto.toDoubleOrNull() ?: 0.0

            val cuentaParaCrear = CuentaParaCrear(
                idUsuario = idUsuario,
                nombreCuenta = "Principal",
                saldoActual = saldoInicial,
                moneda = divisaSeleccionada,
                imgCuenta = null
            )
            crearPrimeraCuenta(cuentaParaCrear)
        }
    }

    private fun crearPrimeraCuenta(cuenta: CuentaParaCrear) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.crearCuenta(cuenta)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@IngresarSaldoActivity, "¡Tu cuenta está lista!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@IngresarSaldoActivity, InicioActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        val errorCode = response.code()
                        val errorBody = response.errorBody()?.string()
                        val errorMsg = "Código: $errorCode, Mensaje: $errorBody"
                        Toast.makeText(this@IngresarSaldoActivity, "Error al crear la cuenta: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@IngresarSaldoActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}