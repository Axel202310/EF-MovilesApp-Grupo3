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

        holder.nombreCuenta.text = cuenta.nombreCuenta

        val formato = DecimalFormat("#,##0.00")
        holder.saldoCuenta.text = "${formato.format(cuenta.saldoActual)} ${cuenta.moneda}"

        holder.iconoCuenta.setImageResource(R.drawable.ic_dollar_placeholder)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
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
        notifyDataSetChanged()
    }
}