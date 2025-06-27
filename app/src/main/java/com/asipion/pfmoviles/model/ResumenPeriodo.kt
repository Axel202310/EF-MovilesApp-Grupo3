package com.asipion.pfmoviles.model

import com.google.gson.annotations.SerializedName

data class ResumenPeriodo(
    @SerializedName("periodo") val periodo: String,
    @SerializedName("tipo") val tipo: String, // "ingreso" o "gasto"
    @SerializedName("total") val total: Double
)