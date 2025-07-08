// --- Archivo: AdaptadorIcono.kt---
package com.asipion.pfmoviles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class AdaptadorIcono(
    private val context: Context,
    private val nombresIconos: List<String>,
    private val onIconoClick: (String) -> Unit
) : RecyclerView.Adapter<AdaptadorIcono.IconoViewHolder>() {

    class IconoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icono: ImageView = itemView.findViewById(R.id.iv_icono_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_icono, parent, false)
        return IconoViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconoViewHolder, position: Int) {
        val nombreIcono = nombresIconos[position]
        // Obtenemos el ID del drawable a partir de su nombre como String
        val resourceId = context.resources.getIdentifier(nombreIcono, "drawable", context.packageName)

        if (resourceId != 0) {
            holder.icono.setImageResource(resourceId)
        }

        holder.itemView.setOnClickListener {
            onIconoClick(nombreIcono)
        }
    }

    override fun getItemCount() = nombresIconos.size
}