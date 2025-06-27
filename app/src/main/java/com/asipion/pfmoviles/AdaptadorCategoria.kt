package com.asipion.pfmoviles

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.Categoria

class AdaptadorCategoria(
    private var categorias: List<Categoria>,
    private val onCategoryClick: (Categoria) -> Unit
) : RecyclerView.Adapter<AdaptadorCategoria.CategoriaViewHolder>() {

    /**
     * ViewHolder que mantiene las referencias a las vistas de cada item.
     * Esto es crucial para un rendimiento eficiente.
     */
    class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icono: ImageView = itemView.findViewById(R.id.iv_categoria_icono)
        val nombre: TextView = itemView.findViewById(R.id.tv_categoria_nombre)
    }

    /**
     * Se llama cuando el RecyclerView necesita crear una nueva vista de item.
     * Infla el layout XML para una sola fila.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria_gestion, parent, false)
        return CategoriaViewHolder(view)
    }

    /**
     * Se llama para vincular los datos de una categoría específica con una vista.
     * Aquí es donde toda la magia ocurre.
     */
    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]

        // 1. Asignamos el nombre de la categoría al TextView.
        holder.nombre.text = categoria.nombreCategoria

        // --- INICIO DE LA LÓGICA DE ICONOS ---
        // 2. Cargamos el icono dinámicamente.
        val context = holder.itemView.context
        // Obtenemos el nombre del icono del objeto Categoria (ej. "educacion", "ic_yape").
        val nombreIcono = categoria.imgCategoria

        if (!nombreIcono.isNullOrEmpty()) {
            // Buscamos el ID del recurso drawable que coincida con el nombre del icono.
            val resourceId = context.resources.getIdentifier(nombreIcono, "drawable", context.packageName)

            // Si encontramos un recurso válido (resourceId no es 0), lo asignamos al ImageView.
            if (resourceId != 0) {
                holder.icono.setImageResource(resourceId)
            } else {
                // Si no se encuentra, ponemos un icono por defecto para evitar errores.
                holder.icono.setImageResource(R.drawable.ic_default_category)
            }
        } else {
            // Si el nombre del icono es nulo o vacío, también ponemos el icono por defecto.
            holder.icono.setImageResource(R.drawable.ic_default_category)
        }
        // --- FIN DE LA LÓGICA DE ICONOS ---

        // 3. Asignamos el listener de clic a toda la fila (itemView).
        holder.itemView.setOnClickListener {
            // Llamamos a la función lambda que nos pasó la Activity.
            onCategoryClick(categoria)
        }
    }

    /**
     * Devuelve el número total de items en la lista.
     */
    override fun getItemCount(): Int {
        return categorias.size
    }

    /**
     * Actualiza la lista de datos del adaptador y notifica al RecyclerView para que se redibuje.
     */
    fun actualizarDatos(nuevasCategorias: List<Categoria>) {
        this.categorias = nuevasCategorias
        notifyDataSetChanged()
    }
}