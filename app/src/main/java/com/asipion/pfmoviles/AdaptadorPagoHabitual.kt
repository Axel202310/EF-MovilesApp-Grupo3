// --- Archivo: AdaptadorPagoHabitual.kt (Final y Completo) ---
package com.asipion.pfmoviles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.PagoHabitual
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdaptadorPagoHabitual(
    private var listaPagos: List<PagoHabitual>,
    private val onEliminarClick: (PagoHabitual) -> Unit
) : RecyclerView.Adapter<AdaptadorPagoHabitual.PagoHabitualViewHolder>() {

    class PagoHabitualViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icono: ImageView = itemView.findViewById(R.id.iv_pago_icono)
        val nombrePago: TextView = itemView.findViewById(R.id.tv_pago_nombre)
        val cuentaCategoria: TextView = itemView.findViewById(R.id.tv_pago_cuenta_categoria)
        val montoPago: TextView = itemView.findViewById(R.id.tv_pago_monto)
        val frecuencia: TextView = itemView.findViewById(R.id.tv_frecuencia)
        val proximaFecha: TextView = itemView.findViewById(R.id.tv_proximo_pago_fecha)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btn_eliminar_pago)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagoHabitualViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pago_habitual, parent, false)
        return PagoHabitualViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagoHabitualViewHolder, position: Int) {
        val pago = listaPagos[position]
        val context = holder.itemView.context

        holder.nombrePago.text = pago.nombrePago
        holder.cuentaCategoria.text = "${pago.nombreCuenta} • ${pago.nombreCategoria}"
        holder.frecuencia.text = pago.frecuencia.replaceFirstChar { it.uppercase() }

        // Formatear monto y color
        val formatoMonto = DecimalFormat("S/ #,##0.00")
        // Necesitaríamos el tipo para colorear. Asumiremos gasto.
        holder.montoPago.text = "- ${formatoMonto.format(pago.montoPago)}"
        holder.montoPago.setTextColor(ContextCompat.getColor(context, R.color.rojo_gasto))

        // Calcular y formatear la próxima fecha de pago
        try {
            val formatoApi = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val fechaInicio = formatoApi.parse(pago.fechaInicio)
            if (fechaInicio != null) {
                val proximoPagoCal = calcularProximoPago(fechaInicio, pago.frecuencia)
                val formatoVista = SimpleDateFormat("dd 'de' MMM, yyyy", Locale("es", "ES"))
                holder.proximaFecha.text = "Próximo: ${formatoVista.format(proximoPagoCal.time)}"
            }
        } catch (e: Exception) {
            holder.proximaFecha.text = "Fecha inválida"
        }

        // Asignar listener de clic para eliminar
        holder.btnEliminar.setOnClickListener {
            onEliminarClick(pago)
        }
    }

    override fun getItemCount(): Int = listaPagos.size

    fun actualizarDatos(nuevosPagos: List<PagoHabitual>) {
        this.listaPagos = nuevosPagos
        notifyDataSetChanged()
    }

    // Función para calcular la próxima fecha de pago
    private fun calcularProximoPago(fechaInicio: java.util.Date, frecuencia: String): Calendar {
        val ahora = Calendar.getInstance()
        val proximoPago = Calendar.getInstance().apply { time = fechaInicio }

        while (proximoPago.before(ahora)) {
            when (frecuencia.lowercase()) {
                "diaria" -> proximoPago.add(Calendar.DAY_OF_MONTH, 1)
                "semanal" -> proximoPago.add(Calendar.WEEK_OF_YEAR, 1)
                "quincenal" -> proximoPago.add(Calendar.DAY_OF_MONTH, 15)
                "mensual" -> proximoPago.add(Calendar.MONTH, 1)
            }
        }
        return proximoPago
    }
}