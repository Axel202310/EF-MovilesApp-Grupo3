package com.asipion.pfmoviles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.Transaccion

class AdaptadorTransaccion: RecyclerView.Adapter<AdaptadorTransaccion.MiViewHolder>() {

    private var listaTransacciones = ArrayList<Transaccion>()
    private lateinit var context: Context

    fun setContext(context: Context){
        this.context = context
    }

    fun setTransacciones(transacciones:ArrayList<Transaccion>){
        this.listaTransacciones = transacciones
    }

    class MiViewHolder(var view : View):RecyclerView.ViewHolder(view) {
        val txtMonto: TextView = view.findViewById(R.id.txtMonto)
        val txtMoneda: TextView = view.findViewById(R.id.txtMoneda)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =MiViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.activity_inicio,parent,false)
    )

    override fun getItemCount(): Int {
        return listaTransacciones.size
    }

    override fun onBindViewHolder(holder: AdaptadorTransaccion.MiViewHolder, position: Int) {
        val item = listaTransacciones[position]
        holder.txtMoneda.text = item.moneda
        holder.txtMonto.text = item.monto_transaccion.toString()

    }
}