// --- Archivo: Adaptador.kt (Corregido) ---
package com.asipion.pfmoviles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.Usuario

// Pasamos la lista en el constructor, es más limpio.
class Adaptador(private var listaUsuarios: List<Usuario>) : RecyclerView.Adapter<Adaptador.MiViewHolder>() {

    // El ViewHolder ahora tiene referencias a las vistas del item_usuario.xml
    class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textoId: TextView = view.findViewById(R.id.text_usuario_id)
        val textoCorreo: TextView = view.findViewById(R.id.text_usuario_correo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        // Inflamos el layout del ITEM, no de la actividad completa.
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return MiViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
        val usuario = listaUsuarios[position]

        // Asignamos los datos del usuario a las vistas del ViewHolder.
        holder.textoId.text = "ID: ${usuario.idUsuario}"
        holder.textoCorreo.text = usuario.correoUsuario
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    // Función para actualizar la lista de datos si es necesario
    fun actualizarDatos(nuevosUsuarios: List<Usuario>) {
        this.listaUsuarios = nuevosUsuarios
        notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
    }
}