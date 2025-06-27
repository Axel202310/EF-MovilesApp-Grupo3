package com.asipion.pfmoviles.model
import com.google.gson.annotations.SerializedName

data class UsuarioActualizarPassword(
    @SerializedName("id_usuario")
    val idUsuario: Int,
    @SerializedName("nueva_contrasena")
    val nuevaContrasena: String
)