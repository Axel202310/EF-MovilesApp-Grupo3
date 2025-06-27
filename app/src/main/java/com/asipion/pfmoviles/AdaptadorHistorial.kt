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

    /**
     * ViewHolder que mantiene las referencias a las vistas de cada item del historial.
     * Esto evita hacer llamadas a findViewById() repetidamente, lo cual es muy eficiente.
     */
    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreOrigen: TextView = itemView.findViewById(R.id.tv_nombre_origen)
        val nombreDestino: TextView = itemView.findViewById(R.id.tv_nombre_destino)
        val montoTransferencia: TextView = itemView.findViewById(R.id.tv_transfer_monto)

        /**
         * Función para vincular los datos de un objeto HistorialTransferencia con las vistas.
         */
        fun bind(item: HistorialTransferencia) {
            nombreOrigen.text = item.nombreOrigen
            nombreDestino.text = item.nombreDestino

            val formato = DecimalFormat("#,##0.00")
            // Asumimos que la moneda es PEN.
            montoTransferencia.text = "S/ ${formato.format(item.monto)}"
        }
    }

    /**
     * Se llama cuando el RecyclerView necesita crear un nuevo ViewHolder.
     * Infla el layout del item y crea una instancia del ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial_transferencia, parent, false)
        return HistorialViewHolder(view)
    }

    /**
     * Se llama para mostrar los datos en una posición específica.
     * Aquí es donde vinculamos los datos y, más importante, el listener de clic.
     */
    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val item = listaHistorial[position]
        holder.bind(item)

        // --- INICIO DE LA LÓGICA DE NAVEGACIÓN ---
        // Hacemos que toda la fila (itemView) sea clicable.
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            // Creamos un Intent para abrir la pantalla de detalle de la transferencia.
            val intent = Intent(context, DetalleTransferenciaHistorialActivity::class.java).apply {
                // Pasamos el ID del registro del historial que se ha pulsado.
                // La pantalla de detalle usará este ID para obtener los datos de la API.
                putExtra("HISTORIAL_ID", item.idHistorial)
            }
            context.startActivity(intent)
        }
        // --- FIN DE LA LÓGICA DE NAVEGACIÓN ---
    }

    /**
     * Devuelve el número total de items en la lista.
     */
    override fun getItemCount(): Int {
        return listaHistorial.size
    }

    /**
     * Una función pública para actualizar la lista de datos del adaptador desde la Activity.
     * Después de actualizar, notifica al RecyclerView para que se redibuje.
     */
    fun actualizarDatos(nuevoHistorial: List<HistorialTransferencia>) {
        this.listaHistorial = nuevoHistorial
        notifyDataSetChanged() // Refresca toda la lista.
    }
}