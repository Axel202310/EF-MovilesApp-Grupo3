package com.asipion.pfmoviles.servicio

import com.asipion.pfmoviles.model.*
import com.google.gson.annotations.SerializedName

data class TransaccionResponse(
    @SerializedName("listatransacciones") var listaTransacciones: List<Transaccion>
)