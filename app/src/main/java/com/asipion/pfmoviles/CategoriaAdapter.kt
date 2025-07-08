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

    /**
     * ViewHolder que mantiene las referencias a las vistas de un solo ítem.
     */
    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Obtenemos las vistas del layout 'item_categoria_selectable.xml'
        val icono: ImageView = itemView.findViewById(R.id.icono_categoria)
        val nombre: TextView = itemView.findViewById(R.id.nombre_categoria)
        val contenedor: View = itemView.findViewById(R.id.contenedor_categoria)

        init {
            // Configuramos el listener una sola vez aquí para mejorar el rendimiento.
            itemView.setOnClickListener {
                // Verificamos si la posición es válida
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Guardamos la posición anterior antes de cambiarla
                    val posicionAnterior = selectedPosition
                    // Actualizamos la nueva posición seleccionada
                    selectedPosition = adapterPosition

                    // Notificamos al adaptador para que actualice la vista del ítem anterior
                    if (posicionAnterior != RecyclerView.NO_POSITION) {
                        notifyItemChanged(posicionAnterior)
                    }
                    // Y para que actualice la vista del nuevo ítem seleccionado
                    notifyItemChanged(selectedPosition)

                    // Llamamos al callback para informar a la Activity de la selección
                    onCategoryClick(categorias[selectedPosition])
                }
            }
        }

        fun bind(categoria: Categoria) {
            nombre.text = categoria.nombreCategoria

            // --- ESTA ES LA LÓGICA CLAVE DE CORRECCIÓN ---
            val nombreIcono = categoria.imgCategoria
            var resourceId = 0
            if (!nombreIcono.isNullOrEmpty()) {
                // Usamos la función mágica para convertir el nombre del icono (String) a un ID de recurso.
                resourceId = itemView.context.resources.getIdentifier(
                    nombreIcono, "drawable", itemView.context.packageName
                )
            }

            // Si encontramos el recurso, lo usamos. Si no, ponemos uno por defecto.
            if (resourceId != 0) {
                icono.setImageResource(resourceId)
            } else {
                // Un ícono genérico para cuando no se encuentra el específico.
                // Asegúrate de tener 'ic_categoria_otros_gastos.png' en res/drawable.
                icono.setImageResource(R.drawable.ic_categoria_otros_gastos)
            }
            // --- FIN DE LA LÓGICA DE CORRECCIÓN ---

            // Lógica para resaltar el item seleccionado
            if (adapterPosition == selectedPosition) {
                contenedor.setBackgroundResource(R.drawable.fondo_seleccionado_naranja)
            } else {
                // Usamos un fondo transparente o el fondo por defecto del tema.
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
        // En lugar de pasar la posición, el ViewHolder ya la conoce a través de 'adapterPosition'.
        holder.bind(categorias[position])
    }

    override fun getItemCount() = categorias.size

    fun actualizarDatos(nuevasCategorias: List<Categoria>) {
        this.categorias = nuevasCategorias
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}