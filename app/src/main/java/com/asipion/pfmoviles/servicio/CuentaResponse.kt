package com.asipion.pfmoviles.servicio
import com.asipion.pfmoviles.model.Cuenta
import com.google.gson.annotations.SerializedName

data class CuentaResponse(
    @SerializedName("listaCuentas")
    val listaCuentas: List<Cuenta>
)