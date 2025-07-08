package com.asipion.pfmoviles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.Transaccion
import java.text.DecimalFormat

class AdaptadorTransaccion(
    private var listaTransacciones: List<Transaccion>,
    private val onTransactionClick: (Transaccion) -> Unit
) : RecyclerView.Adapter<AdaptadorTransaccion.MiViewHolder>() {

    inner class MiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val iconoCategoria: ImageView = itemView.findViewById(R.id.iv_categoria_icono)
        val textoCategoria: TextView = itemView.findViewById(R.id.tv_categoria_nombre)
        val textoDescripcion: TextView = itemView.findViewById(R.id.tv_descripcion)
        val textoMonto: TextView = itemView.findViewById(R.id.tv_monto)

        fun bind(transaccion: Transaccion) {
            textoCategoria.text = transaccion.nombreCategoria ?: "Sin Categoría"
            textoDescripcion.text = transaccion.descripcion ?: "Sin descripción"

            val nombreIcono = transaccion.imgCategoria
            var resourceId = 0
            if (!nombreIcono.isNullOrEmpty()) {
                resourceId = itemView.context.resources.getIdentifier(
                    nombreIcono, "drawable", itemView.context.packageName
                )
            }

            if (resourceId != 0) {
                iconoCategoria.setImageResource(resourceId)
            } else {
                iconoCategoria.setImageResource(R.drawable.ic_categoria_otros_gastos)
            }

            val formato = DecimalFormat("S/ #,##0.00")
            val context = itemView.context

            if (transaccion.tipoTransaccion == "gasto") {
                textoMonto.text = "- ${formato.format(transaccion.montoTransaccion)}"
                textoMonto.setTextColor(ContextCompat.getColor(context, R.color.rojo_gasto))
            } else {
                textoMonto.text = "+ ${formato.format(transaccion.montoTransaccion)}"
                textoMonto.setTextColor(ContextCompat.getColor(context, R.color.verde_ingreso))
            }

            // Asignamos el listener al ítem.
            itemView.setOnClickListener { onTransactionClick(transaccion) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_transaccion, parent, false)
        return MiViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
        holder.bind(listaTransacciones[position])
    }

    override fun getItemCount(): Int {
        return listaTransacciones.size
    }

    fun actualizarDatos(nuevasTransacciones: List<Transaccion>) {
        this.listaTransacciones = nuevasTransacciones
        notifyDataSetChanged()
    }
}