package com.asipion.pfmoviles.servicio

import com.google.gson.annotations.SerializedName
import com.asipion.pfmoviles.model.Usuario

data class UsuarioResponse(
    @SerializedName("listaUsuarios") var listaUsuarios: List<Usuario>
)