package com.asipion.pfmoviles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.Categoria

class AdaptadorCategoria(
    private var categorias: List<Categoria>,
    private val onCategoryClick: (Categoria) -> Unit
) : RecyclerView.Adapter<AdaptadorCategoria.CategoriaViewHolder>() {

    class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icono: ImageView = itemView.findViewById(R.id.iv_categoria_icono)
        val nombre: TextView = itemView.findViewById(R.id.tv_categoria_nombre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria_gestion, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        val context = holder.itemView.context

        holder.nombre.text = categoria.nombreCategoria

        val nombreIcono = categoria.imgCategoria
        if (!nombreIcono.isNullOrEmpty()) {
            val resourceId = context.resources.getIdentifier(nombreIcono, "drawable", context.packageName)
            if (resourceId != 0) {
                holder.icono.setImageResource(resourceId)
            } else {
                holder.icono.setImageResource(R.drawable.ic_categoria_otros_gastos) // Usamos un default consistente
            }
        } else {
            holder.icono.setImageResource(R.drawable.ic_categoria_otros_gastos)
        }

        val colorResId = when (categoria.nombreCategoria) {
            "Educación" -> R.color.color_cat_educacion
            "Salud" -> R.color.color_cat_salud
            "Transporte" -> R.color.color_cat_transporte
            "Hogar" -> R.color.color_cat_hogar
            "Alimentos" -> R.color.color_cat_alimentos
            "Regalos" -> R.color.color_cat_regalos
            "Salario" -> R.color.color_cat_salario
            "Regalo" -> R.color.color_cat_regalo_recibido
            "Intereses" -> R.color.color_cat_intereses
            "Otros" -> R.color.color_cat_otros
            else -> R.color.color_cat_personalizada
        }

        val color = ContextCompat.getColor(context, colorResId)

        val fondoConTinte = DrawableCompat.wrap(holder.icono.background).mutate()
        DrawableCompat.setTint(fondoConTinte, color)

        holder.itemView.setOnClickListener {
            onCategoryClick(categoria)
        }
    }

    override fun getItemCount(): Int {
        return categorias.size
    }

    fun actualizarDatos(nuevasCategorias: List<Categoria>) {
        this.categorias = nuevasCategorias
        notifyDataSetChanged()
    }
}