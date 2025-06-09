package com.asipion.pfmoviles.servicio

import com.asipion.pfmoviles.model.Usuario

data class MensajeResponse(
    val mensaje: String,
    val usuario: Usuario? = null
)
