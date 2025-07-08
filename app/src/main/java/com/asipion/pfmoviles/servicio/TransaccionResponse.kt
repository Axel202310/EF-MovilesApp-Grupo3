package com.asipion.pfmoviles.servicio
import com.asipion.pfmoviles.model.Transaccion
import com.google.gson.annotations.SerializedName

data class TransaccionResponse(
    @SerializedName("listaTransacciones")
    val listaTransacciones: List<Transaccion>
)