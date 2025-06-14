package com.asipion.pfmoviles.servicio
import com.asipion.pfmoviles.model.Categoria
import com.google.gson.annotations.SerializedName

data class CategoriaResponse(
    @SerializedName("listaCategorias")
    val listaCategorias: List<Categoria>
)