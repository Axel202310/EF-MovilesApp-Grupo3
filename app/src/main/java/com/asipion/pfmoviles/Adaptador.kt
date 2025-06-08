package com.asipion.pfmoviles

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.Usuario

class Adaptador: RecyclerView.Adapter<Adaptador.MiViewHolder>(){
    private var listaUsuarios = ArrayList<Usuario>()
    private lateinit var context: Context

    fun setContext(context: Context){
        this.context = context
    }

    fun setUsuarios(usuarios:ArrayList<Usuario>){
        this.listaUsuarios = usuarios
    }

    class MiViewHolder(var view :View):RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MiViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.actividad_registro_contrasena,parent,false)
    )

    override fun onBindViewHolder(holder: Adaptador.MiViewHolder, position: Int) {
        val item = listaUsuarios[position]

    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }
}