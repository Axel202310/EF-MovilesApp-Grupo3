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

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val posicionAnterior = selectedPosition
                    selectedPosition = adapterPosition

                    if (posicionAnterior != RecyclerView.NO_POSITION) {
                        notifyItemChanged(posicionAnterior)
                    }
                    notifyItemChanged(selectedPosition)

                    onCategoryClick(categorias[selectedPosition])
                }
            }
        }

        fun bind(categoria: Categoria) {
            nombre.text = categoria.nombreCategoria

            val nombreIcono = categoria.imgCategoria
            var resourceId = 0
            if (!nombreIcono.isNullOrEmpty()) {
                resourceId = itemView.context.resources.getIdentifier(
                    nombreIcono, "drawable", itemView.context.packageName
                )
            }

            if (resourceId != 0) {
                icono.setImageResource(resourceId)
            } else {
                icono.setImageResource(R.drawable.ic_categoria_otros_gastos)
            }

            if (adapterPosition == selectedPosition) {
                contenedor.setBackgroundResource(R.drawable.fondo_seleccionado_naranja)
            } else {
                contenedor.setBackgroundResource(android.R.color.transparent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria_selectable, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(categorias[position])
    }

    override fun getItemCount() = categorias.size

    fun actualizarDatos(nuevasCategorias: List<Categoria>) {
        this.categorias = nuevasCategorias
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}