package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.model.Transaccion
import com.asipion.pfmoviles.model.Usuario
import com.asipion.pfmoviles.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IngresarSaldoActividad : AppCompatActivity() {

    private lateinit var campoSaldo: EditText
    private lateinit var botonSiguiente: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_ingresar_saldo)

        val divisa = intent.getStringExtra("divisa") ?: "PEN"
        val textoDivisa = findViewById<TextView>(R.id.texto_divisa_seleccionada)
        textoDivisa.text = divisa // <-- Esto mostrará la divisa seleccionada

        campoSaldo = findViewById(R.id.campo_saldo)
        botonSiguiente = findViewById(R.id.boton_siguiente_saldo)

        desactivarBoton()

        campoSaldo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString()
                val numeroValido = texto.toDoubleOrNull()
                if (!texto.isNullOrBlank() && numeroValido != null && numeroValido > 0) {
                    activarBoton()
                } else {
                    desactivarBoton()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        botonSiguiente.setOnClickListener {
            val saldo = campoSaldo.text.toString().toDouble()
            val divisaCompleta = intent.getStringExtra("divisa") ?: "PEN"
            val divisa = divisaCompleta.takeLast(3)  // <-- Extrae solo las 3 letras finales
            val idUsuario = obtenerIdUsuario()

            if (idUsuario == -1) {
                Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 GUARDAR EN SharedPreferences para mostrarlo en InicioActivity
            val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString("moneda_$idUsuario", divisa)
            editor.putFloat("monto_$idUsuario", saldo.toFloat())
            editor.putBoolean("flujo_completo_$idUsuario", true)
            editor.apply()

            val transaccion = Transaccion(
                id_transaccion = null,  // null porque es autoincremental en la DB
                monto_transaccion = saldo,
                moneda = divisa,
                fecha = null,  // puedes agregar fecha en backend o aquí si quieres
                id_usuario = idUsuario,
                id_categoria = null,
                idcuenta = null
            )
            CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitClient.webService.agregarTransaccion(transaccion)
                if (response.isSuccessful) {
                    runOnUiThread {
                        startActivity(Intent(this@IngresarSaldoActividad, InicioActivity::class.java))
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@IngresarSaldoActividad, "Salgo o Tipo incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun obtenerIdUsuario(): Int {
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        return prefs.getInt("id_usuario", -1) // -1 si no existe
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
