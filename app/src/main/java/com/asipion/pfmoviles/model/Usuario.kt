package com.asipion.pfmoviles.model
import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id_usuario")
    val idUsuario: Int,
    @SerializedName("correo_usuario")
    val correoUsuario: String,
    var password: String? = null
)