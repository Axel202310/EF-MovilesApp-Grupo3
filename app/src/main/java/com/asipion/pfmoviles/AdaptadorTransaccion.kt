package com.asipion.pfmoviles

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.Transaccion
import java.text.DecimalFormat

// AÑADIMOS UN LISTENER AL CONSTRUCTOR:
// Es una función que la Activity nos pasará para que la llamemos cuando se haga clic.
class AdaptadorTransaccion(
    private var listaTransacciones: List<Transaccion>,
    private val onTransactionClick: (Transaccion) -> Unit
) : RecyclerView.Adapter<AdaptadorTransaccion.MiViewHolder>() {

    class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textoCategoria: TextView = view.findViewById(R.id.text_categoria_nombre)
        val textoDescripcion: TextView = view.findViewById(R.id.text_transaccion_descripcion)
        val textoMonto: TextView = view.findViewById(R.id.text_transaccion_monto)

        // Creamos una función 'bind' para encapsular la lógica de asignación.
        fun bind(transaccion: Transaccion, clickListener: (Transaccion) -> Unit) {
            textoCategoria.text = transaccion.nombreCategoria
            textoDescripcion.text = transaccion.descripcion ?: "Sin descripción"

            val formato = DecimalFormat("#,##0.00")
            if (transaccion.tipoTransaccion == "gasto") {
                textoMonto.text = "- S/ ${formato.format(transaccion.montoTransaccion)}"
                textoMonto.setTextColor(Color.parseColor("#FF5252")) // Rojo
            } else {
                textoMonto.text = "+ S/ ${formato.format(transaccion.montoTransaccion)}"
                textoMonto.setTextColor(Color.parseColor("#69F0AE")) // Verde
            }

            // Hacemos que toda la fila sea clicable.
            itemView.setOnClickListener { clickListener(transaccion) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_transaccion, parent, false)
        return MiViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
        // Llamamos a la función bind, pasándole la transacción y el listener.
        holder.bind(listaTransacciones[position], onTransactionClick)
    }

    override fun getItemCount(): Int {
        return listaTransacciones.size
    }

    fun actualizarDatos(nuevasTransacciones: List<Transaccion>) {
        this.listaTransacciones = nuevasTransacciones
        notifyDataSetChanged()
    }
}