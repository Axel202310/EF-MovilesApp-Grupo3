package com.asipion.pfmoviles

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.HistorialTransferencia
import java.text.DecimalFormat
import java.util.Locale

class AdaptadorHistorial(private var listaHistorial: List<HistorialTransferencia>) :
    RecyclerView.Adapter<AdaptadorHistorial.HistorialViewHolder>() {

    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreOrigen: TextView = itemView.findViewById(R.id.tv_nombre_origen)
        val nombreDestino: TextView = itemView.findViewById(R.id.tv_nombre_destino)
        val montoTransferencia: TextView = itemView.findViewById(R.id.tv_transfer_monto)

        fun bind(item: HistorialTransferencia) {
            nombreOrigen.text = item.nombreOrigen
            nombreDestino.text = item.nombreDestino

            val formato = DecimalFormat("#,##0.00")
            montoTransferencia.text = "S/ ${formato.format(item.monto)}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial_transferencia, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val item = listaHistorial[position]
        holder.bind(item)


        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalleTransferenciaHistorialActivity::class.java).apply {
                putExtra("HISTORIAL_ID", item.idHistorial)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listaHistorial.size
    }

    fun actualizarDatos(nuevoHistorial: List<HistorialTransferencia>) {
        this.listaHistorial = nuevoHistorial
        notifyDataSetChanged() // Refresca toda la lista.
    }
}