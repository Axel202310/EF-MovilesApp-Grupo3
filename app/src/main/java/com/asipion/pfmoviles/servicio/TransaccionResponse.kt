package com.asipion.pfmoviles.servicio
import com.asipion.pfmoviles.model.Transaccion
import com.google.gson.annotations.SerializedName

data class TransaccionResponse(
    // La clave debe coincidir con la del index.js
    @SerializedName("listaTransacciones")
    val listaTransacciones: List<Transaccion>
)