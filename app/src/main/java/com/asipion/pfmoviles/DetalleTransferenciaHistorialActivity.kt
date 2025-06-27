// --- Archivo: DetalleTransferenciaHistorialActivity.kt (NUEVO Y COMPLETO) ---
package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.model.HistorialTransferencia
import com.asipion.pfmoviles.servicio.RetrofitClient
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class DetalleTransferenciaHistorialActivity : AppCompatActivity() {

    private var historialId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_transferencia_historial)

        findViewById<MaterialToolbar>(R.id.toolbar_detalle_historial).setNavigationOnClickListener { finish() }

        historialId = intent.getIntExtra("HISTORIAL_ID", -1)
        if (historialId == -1) {
            Toast.makeText(this, "Error: No se recibió el ID del historial.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        cargarDetalles(historialId)

        findViewById<Button>(R.id.btn_eliminar_historial).setOnClickListener {
            mostrarDialogoConfirmacion()
        }
    }

    private fun cargarDetalles(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerDetalleTransferencia(id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { poblarUI(it) }
                    } else { Toast.makeText(this@DetalleTransferenciaHistorialActivity, "Error al cargar detalle", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) { withContext(Dispatchers.Main) { Toast.makeText(this@DetalleTransferenciaHistorialActivity, "Error de red", Toast.LENGTH_SHORT).show() } }
        }
    }

    private fun poblarUI(historial: HistorialTransferencia) {
        val formato = DecimalFormat("#,##0.00")
        findViewById<TextView>(R.id.tv_detalle_monto).text = "S/ ${formato.format(historial.monto)}"
        findViewById<TextView>(R.id.tv_detalle_info).text = "Desde '${historial.nombreOrigen}' hacia '${historial.nombreDestino}'"

        try {
            val formatoApi = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val formatoVista = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
            val fecha = formatoApi.parse(historial.fechaTransferencia)
            findViewById<TextView>(R.id.tv_detalle_fecha).text = fecha?.let { formatoVista.format(it) } ?: "Fecha inválida"
        } catch (e: Exception) {
            findViewById<TextView>(R.id.tv_detalle_fecha).text = historial.fechaTransferencia.substringBefore(" ")
        }
    }

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Transferencia")
            .setMessage("¿Estás seguro? El dinero será devuelto a la cuenta de origen.")
            .setPositiveButton("Eliminar") { _, _ -> eliminarHistorial() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarHistorial() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.eliminarHistorialTransferencia(historialId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DetalleTransferenciaHistorialActivity, "Transferencia eliminada y saldos restaurados", Toast.LENGTH_LONG).show()
                        finish()
                    } else { Toast.makeText(this@DetalleTransferenciaHistorialActivity, "Error al eliminar", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) { withContext(Dispatchers.Main) { Toast.makeText(this@DetalleTransferenciaHistorialActivity, "Error de red", Toast.LENGTH_SHORT).show() } }
        }
    }
}