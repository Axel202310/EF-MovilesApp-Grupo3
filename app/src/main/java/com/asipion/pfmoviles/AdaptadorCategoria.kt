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

    /**
     * ViewHolder que mantiene las referencias a las vistas de cada item.
     */
    class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icono: ImageView = itemView.findViewById(R.id.iv_categoria_icono)
        val nombre: TextView = itemView.findViewById(R.id.tv_categoria_nombre)
    }

    /**
     * Se llama cuando el RecyclerView necesita crear una nueva vista de item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria_gestion, parent, false)
        return CategoriaViewHolder(view)
    }

    /**
     * Se llama para vincular los datos de una categoría específica con una vista.
     */
    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        val context = holder.itemView.context

        // 1. Asignamos el nombre
        holder.nombre.text = categoria.nombreCategoria

        // 2. Lógica para cargar el icono (esta ya la tenías y estaba bien)
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

        // --- INICIO DE LA NUEVA LÓGICA DE COLORES ---
        // 3. Determinamos qué recurso de color usar basándonos en el nombre de la categoría
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
            // Si el nombre no coincide con ninguno, es una categoría creada por el usuario.
            else -> R.color.color_cat_personalizada
        }

        // 4. Obtenemos el valor del color desde los recursos.
        val color = ContextCompat.getColor(context, colorResId)

        // 5. "Teñimos" el fondo circular de la ImageView con el color obtenido.
        // DrawableCompat es la forma segura y recomendada de hacerlo.
        val fondoConTinte = DrawableCompat.wrap(holder.icono.background).mutate()
        DrawableCompat.setTint(fondoConTinte, color)
        // --- FIN DE LA NUEVA LÓGICA DE COLORES ---

        // 6. Asignamos el listener de clic.
        holder.itemView.setOnClickListener {
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
     * Actualiza la lista de datos del adaptador.
     */
    fun actualizarDatos(nuevasCategorias: List<Categoria>) {
        this.categorias = nuevasCategorias
        notifyDataSetChanged()
    }
}