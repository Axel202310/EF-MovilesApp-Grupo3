package com.asipion.pfmoviles.servicio
import com.asipion.pfmoviles.model.Usuario

// Este archivo se mantiene sin cambios, es perfecto.
data class MensajeResponse(
    val mensaje: String,
    val usuario: Usuario? = null
)