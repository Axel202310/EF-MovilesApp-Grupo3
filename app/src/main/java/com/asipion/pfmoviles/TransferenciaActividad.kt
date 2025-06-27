package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.model.Cuenta
import com.asipion.pfmoviles.model.Transferencia
import com.asipion.pfmoviles.servicio.RetrofitClient
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransferenciaActividad : AppCompatActivity() {

    // Vistas de la UI
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvDesdeCuenta: TextView
    private lateinit var tvHaciaCuenta: TextView
    private lateinit var etMonto: EditText
    private lateinit var tvDateValue: TextView
    private lateinit var etComentario: EditText
    private lateinit var btnAnadir: Button

    // Datos y estado
    private var listaDeCuentas: List<Cuenta> = emptyList()
    private var cuentaOrigen: Cuenta? = null
    private var cuentaDestino: Cuenta? = null
    private var fechaSeleccionada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_transferencia)

        inicializarVistas()
        configurarListeners()
        cargarCuentasDesdeApi()

        // Establecer la fecha actual por defecto al iniciar
        actualizarFecha(Calendar.getInstance())
    }

    private fun inicializarVistas() {
        toolbar = findViewById(R.id.toolbar_transferencia)
        tvDesdeCuenta = findViewById(R.id.tv_from_account_value)
        tvHaciaCuenta = findViewById(R.id.tv_to_account_value)
        etMonto = findViewById(R.id.et_monto)
        tvDateValue = findViewById(R.id.tv_date_value)
        etComentario = findViewById(R.id.et_comentario)
        btnAnadir = findViewById(R.id.btn_anadir)
    }

    private fun configurarListeners() {
        toolbar.setNavigationOnClickListener { finish() }

        // Hacemos que los TextViews de las cuentas sean clicables para abrir el selector
        tvDesdeCuenta.setOnClickListener { mostrarDialogoSeleccion("origen") }
        tvHaciaCuenta.setOnClickListener { mostrarDialogoSeleccion("destino") }

        btnAnadir.setOnClickListener { realizarTransferencia() }

        tvDateValue.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year ->
                val calendar = Calendar.getInstance().apply { set(year, month, day) }
                actualizarFecha(calendar)
            }
            datePicker.show(supportFragmentManager, "datePicker")
        }
    }

    private fun actualizarFecha(calendar: Calendar) {
        // Formato para enviar a la API
        val formatoApi = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        fechaSeleccionada = formatoApi.format(calendar.time)

        // Formato para mostrar al usuario
        val formatoVista = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        tvDateValue.text = formatoVista.format(calendar.time)
    }

    private fun cargarCuentasDesdeApi() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerCuentas(idUsuario)
                if (response.isSuccessful) {
                    listaDeCuentas = response.body()?.listaCuentas ?: emptyList()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { Toast.makeText(this@TransferenciaActividad, "Error de red", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun mostrarDialogoSeleccion(tipo: String) {
        if (listaDeCuentas.size < 2) {
            Toast.makeText(this, "Necesita al menos dos cuentas para realizar una transferencia", Toast.LENGTH_LONG).show()
            return
        }

        val nombresCuentas = listaDeCuentas.map { "${it.nombreCuenta} (S/ ${it.saldoActual})" }.toTypedArray()
        val titulo = if (tipo == "origen") "Transferir Desde" else "Transferir A"

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setItems(nombresCuentas) { _, which ->
                val cuentaSeleccionada = listaDeCuentas[which]
                if (tipo == "origen") {
                    if (cuentaSeleccionada.idCuenta == cuentaDestino?.idCuenta) {
                        Toast.makeText(this, "Las cuentas no pueden ser las mismas", Toast.LENGTH_SHORT).show()
                        return@setItems
                    }
                    cuentaOrigen = cuentaSeleccionada
                    tvDesdeCuenta.text = cuentaSeleccionada.nombreCuenta
                    tvDesdeCuenta.setTextColor(getColor(android.R.color.white))
                } else {
                    if (cuentaSeleccionada.idCuenta == cuentaOrigen?.idCuenta) {
                        Toast.makeText(this, "Las cuentas no pueden ser las mismas", Toast.LENGTH_SHORT).show()
                        return@setItems
                    }
                    cuentaDestino = cuentaSeleccionada
                    tvHaciaCuenta.text = cuentaSeleccionada.nombreCuenta
                    tvHaciaCuenta.setTextColor(getColor(android.R.color.white))
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun realizarTransferencia() {
        val montoStr = etMonto.text.toString()
        val monto = montoStr.toDoubleOrNull()
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        val comentario = etComentario.text.toString().trim()

        // Validaciones
        if (cuentaOrigen == null || cuentaDestino == null) {
            Toast.makeText(this, "Debe seleccionar ambas cuentas", Toast.LENGTH_SHORT).show()
            return
        }
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "Ingrese un monto válido y mayor a cero", Toast.LENGTH_SHORT).show()
            return
        }
        if (monto > cuentaOrigen!!.saldoActual) {
            Toast.makeText(this, "Saldo insuficiente en la cuenta '${cuentaOrigen!!.nombreCuenta}'", Toast.LENGTH_LONG).show()
            return
        }
        if (idUsuario == -1 || fechaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Error de sesión o fecha no seleccionada", Toast.LENGTH_SHORT).show()
            return
        }

        val transferencia = Transferencia(
            idUsuario = idUsuario,
            idCuentaOrigen = cuentaOrigen!!.idCuenta,
            idCuentaDestino = cuentaDestino!!.idCuenta,
            monto = monto,
            comentario = comentario,
            fechaTransferencia = fechaSeleccionada
        )

        // Llamada a la API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.realizarTransferencia(transferencia)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@TransferenciaActividad, response.body()?.mensaje, Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@TransferenciaActividad, "Error en la transferencia", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TransferenciaActividad, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}