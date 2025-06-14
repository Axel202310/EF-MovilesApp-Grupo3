// Archivo: CategoriaAdapter.kt (NUEVO)
package com.asipion.pfmoviles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.Categoria

class CategoriaAdapter(
    private var categorias: List<Categoria>,
    private val onCategoryClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icono: ImageView = itemView.findViewById(R.id.icono_categoria)
        val nombre: TextView = itemView.findViewById(R.id.nombre_categoria)
        val contenedor: View = itemView.findViewById(R.id.contenedor_categoria)

        fun bind(categoria: Categoria, position: Int) {
            nombre.text = categoria.nombreCategoria
            // Aquí puedes añadir lógica para cargar el ícono dinámicamente si lo tienes en el modelo
            // ej: Glide.with(itemView.context).load(categoria.imgCategoria).into(icono)

            // Lógica para resaltar el item seleccionado
            if (position == selectedPosition) {
                contenedor.setBackgroundResource(R.drawable.fondo_seleccionado_naranja)
            } else {
                contenedor.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }

            itemView.setOnClickListener {
                notifyItemChanged(selectedPosition) // Des-selecciona el anterior
                selectedPosition = layoutPosition
                notifyItemChanged(selectedPosition) // Selecciona el nuevo
                onCategoryClick(categoria)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria_selectable, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(categorias[position], position)
    }

    override fun getItemCount() = categorias.size

    fun actualizarDatos(nuevasCategorias: List<Categoria>) {
        this.categorias = nuevasCategorias
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}