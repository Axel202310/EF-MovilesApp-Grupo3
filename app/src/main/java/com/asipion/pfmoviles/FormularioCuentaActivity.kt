package com.asipion.pfmoviles

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.model.CuentaParaCrear
import com.asipion.pfmoviles.servicio.RetrofitClient
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FormularioCuentaActivity : AppCompatActivity() {

    // Vistas
    private lateinit var toolbar: MaterialToolbar
    private lateinit var etNombre: EditText
    private lateinit var etSaldo: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button

    private var cuentaId: Int = -1
    private var esModoEdicion = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_cuenta)

        inicializarVistas()

        cuentaId = intent.getIntExtra("CUENTA_ID", -1)
        esModoEdicion = cuentaId != -1

        configurarUI()

        if (esModoEdicion) {
            cargarDatosDeLaCuenta()
        }
    }

    private fun inicializarVistas() {
        toolbar = findViewById(R.id.toolbar_formulario_cuenta)
        etNombre = findViewById(R.id.et_nombre_cuenta)
        etSaldo = findViewById(R.id.et_saldo_cuenta)
        btnGuardar = findViewById(R.id.btn_guardar_cambios)
        btnEliminar = findViewById(R.id.btn_eliminar_cuenta)
    }

    private fun configurarUI() {
        toolbar.setNavigationOnClickListener { finish() }

        if (esModoEdicion) {
            toolbar.title = "Editar Cuenta"
            btnGuardar.text = "Guardar Cambios"
            btnEliminar.visibility = View.VISIBLE
        } else {
            toolbar.title = "Añadir Nueva Cuenta"
            btnGuardar.text = "Añadir Cuenta"
            btnEliminar.visibility = View.GONE
        }

        btnGuardar.setOnClickListener { guardarCuenta() }
        btnEliminar.setOnClickListener { mostrarDialogoEliminacion() }
    }

    private fun cargarDatosDeLaCuenta() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerDetalleCuenta(cuentaId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { cuenta ->
                            etNombre.setText(cuenta.nombreCuenta)
                            etSaldo.setText(cuenta.saldoActual.toString())
                        }
                    } else {
                        Toast.makeText(this@FormularioCuentaActivity, "Error al cargar los datos de la cuenta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FormularioCuentaActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun guardarCuenta() {
        val nombre = etNombre.text.toString().trim()
        val saldoStr = etSaldo.text.toString().trim()
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)

        if (nombre.isEmpty() || saldoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val saldo = saldoStr.toDoubleOrNull()
        if (saldo == null) {
            Toast.makeText(this, "Por favor, ingrese un saldo numérico válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (idUsuario == -1) {
            Toast.makeText(this, "Error de sesión. Por favor, inicie sesión de nuevo.", Toast.LENGTH_SHORT).show()
            return
        }

        val cuentaData = CuentaParaCrear(
            idUsuario = idUsuario,
            nombreCuenta = nombre,
            saldoActual = saldo,
            moneda = "PEN",
            imgCuenta = "ic_dollar_placeholder"
        )

        if (esModoEdicion) {
            actualizarCuentaEnServidor(cuentaData)
        } else {
            crearCuentaEnServidor(cuentaData)
        }
    }

    private fun crearCuentaEnServidor(cuenta: CuentaParaCrear) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.crearCuenta(cuenta)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormularioCuentaActivity, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
                        finish() // Cierra la pantalla y vuelve a la lista de cuentas
                    } else {
                        Toast.makeText(this@FormularioCuentaActivity, "Error al crear la cuenta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FormularioCuentaActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun actualizarCuentaEnServidor(cuenta: CuentaParaCrear) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.actualizarCuenta(cuentaId, cuenta)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormularioCuentaActivity, "Cuenta actualizada con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@FormularioCuentaActivity, "Error al actualizar la cuenta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FormularioCuentaActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarDialogoEliminacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Cuenta")
            .setMessage("¿Estás seguro? Se eliminarán también todas las transacciones asociadas a esta cuenta. Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarCuentaEnServidor()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCuentaEnServidor() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.eliminarCuenta(cuentaId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormularioCuentaActivity, "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@FormularioCuentaActivity, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FormularioCuentaActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}