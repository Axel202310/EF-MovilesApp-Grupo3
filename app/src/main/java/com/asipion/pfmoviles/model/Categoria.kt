package com.asipion.pfmoviles.model
import com.google.gson.annotations.SerializedName

data class Categoria(
    @SerializedName("id_categoria")
    val idCategoria: Int,
    @SerializedName("id_usuario")
    val idUsuario: Int?,
    @SerializedName("nombre_categoria")
    val nombreCategoria: String,
    @SerializedName("tipo_categoria")
    val tipoCategoria: String, // "ingreso" o "gasto"
    @SerializedName("img_categoria")
    val imgCategoria: String?
)