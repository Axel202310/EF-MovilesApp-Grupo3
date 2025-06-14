package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.model.Transaccion
import com.asipion.pfmoviles.servicio.RetrofitClient
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class DetalleTransaccionActivity : AppCompatActivity() {

    private var transaccionId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_transaccion)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_detalle)
        toolbar.setNavigationOnClickListener {
            finish() // Cierra esta actividad y vuelve a la anterior
        }

        transaccionId = intent.getIntExtra("TRANSACCION_ID", -1)
        if (transaccionId == -1) {
            Toast.makeText(this, "Error: No se recibió el ID de la transacción.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        cargarDetallesTransaccion(transaccionId)

        findViewById<Button>(R.id.btn_eliminar_transaccion).setOnClickListener {
            mostrarDialogoDeConfirmacion()
        }
    }

    private fun cargarDetallesTransaccion(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerDetalleTransaccion(id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { transaccion ->
                            poblarUI(transaccion)
                        }
                    } else {
                        Toast.makeText(this@DetalleTransaccionActivity, "Error al cargar los detalles", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleTransaccionActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun poblarUI(transaccion: Transaccion) {
        val textMonto: TextView = findViewById(R.id.text_detalle_monto)
        val textCategoria: TextView = findViewById(R.id.text_detalle_categoria)
        val textFecha: TextView = findViewById(R.id.text_detalle_fecha)

        val formatoMonto = DecimalFormat("#,##0.00")
        val simbolo = if (transaccion.tipoTransaccion == "gasto") "- S/" else "+ S/"
        val color = if (transaccion.tipoTransaccion == "gasto") getColor(R.color.rojo_gasto) else getColor(R.color.verde_ingreso)

        textMonto.text = "$simbolo ${formatoMonto.format(transaccion.montoTransaccion)}"
        textMonto.setTextColor(color)

        textCategoria.text = transaccion.nombreCategoria

        // Formatear la fecha para que sea más legible
        try {
            val formatoApi = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val formatoVista = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
            val fecha = formatoApi.parse(transaccion.fechaTransaccion)
            if (fecha != null) {
                textFecha.text = formatoVista.format(fecha)
            }
        } catch (e: Exception) {
            textFecha.text = "Fecha no válida"
        }
    }

    private fun mostrarDialogoDeConfirmacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Transacción")
            .setMessage("¿Estás seguro de que deseas eliminar esta transacción? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { dialog, which ->
                eliminarTransaccion()
            }
            .setNegativeButton("Cancelar", null)
            .setIcon(R.drawable.ic_delete)
            .show()
    }

    private fun eliminarTransaccion() {
        if (transaccionId == -1) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.eliminarTransaccion(transaccionId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DetalleTransaccionActivity, "Transacción eliminada con éxito", Toast.LENGTH_SHORT).show()
                        finish() // Cierra esta pantalla y vuelve a InicioActivity
                    } else {
                        Toast.makeText(this@DetalleTransaccionActivity, "Error al eliminar la transacción", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleTransaccionActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}