package com.asipion.pfmoviles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.Usuario

class Adaptador(private var listaUsuarios: List<Usuario>) : RecyclerView.Adapter<Adaptador.MiViewHolder>() {

    // El ViewHolder ahora tiene referencias a las vistas del item_usuario.xml
    class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textoId: TextView = view.findViewById(R.id.text_usuario_id)
        val textoCorreo: TextView = view.findViewById(R.id.text_usuario_correo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return MiViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
        val usuario = listaUsuarios[position]

        holder.textoId.text = "ID: ${usuario.idUsuario}"
        holder.textoCorreo.text = usuario.correoUsuario
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    fun actualizarDatos(nuevosUsuarios: List<Usuario>) {
        this.listaUsuarios = nuevosUsuarios
        notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
    }
}