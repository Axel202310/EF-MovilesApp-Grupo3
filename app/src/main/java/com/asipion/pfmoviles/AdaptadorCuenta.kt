// --- Archivo: AdaptadorCuenta.kt (Final, Completo y con Navegación) ---
package com.asipion.pfmoviles

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.Cuenta
import java.text.DecimalFormat
import java.util.Locale

class AdaptadorCuenta(private var listaCuentas: List<Cuenta>) : RecyclerView.Adapter<AdaptadorCuenta.CuentaViewHolder>() {

    class CuentaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreCuenta: TextView = itemView.findViewById(R.id.tv_cuenta_nombre)
        val saldoCuenta: TextView = itemView.findViewById(R.id.tv_cuenta_saldo)
        val iconoCuenta: ImageView = itemView.findViewById(R.id.iv_cuenta_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuentaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cuenta, parent, false)
        return CuentaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CuentaViewHolder, position: Int) {
        val cuenta = listaCuentas[position]

        // Asignamos el nombre de la cuenta.
        holder.nombreCuenta.text = cuenta.nombreCuenta

        // Formateamos el saldo para que se vea bien (ej. 1,000.00).
        val formato = DecimalFormat("#,##0.00")
        holder.saldoCuenta.text = "${formato.format(cuenta.saldoActual)} ${cuenta.moneda}"

        // Asignamos un icono por defecto.
        // En el futuro, aquí podría ir la lógica para mostrar el icono específico de la cuenta.
        holder.iconoCuenta.setImageResource(R.drawable.ic_dollar_placeholder)

        // --- LÓGICA DE NAVEGACIÓN ---
        // Hacemos que toda la fila (itemView) sea clicable.
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            // Creamos un Intent para abrir la pantalla de formulario en modo "Edición".
            val intent = Intent(context, FormularioCuentaActivity::class.java).apply {
                putExtra("CUENTA_ID", cuenta.idCuenta)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listaCuentas.size
    }

    fun actualizarDatos(nuevasCuentas: List<Cuenta>) {
        this.listaCuentas = nuevasCuentas
        notifyDataSetChanged() // Refresca toda la lista.
    }
}