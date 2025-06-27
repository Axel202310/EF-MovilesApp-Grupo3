package com.asipion.pfmoviles.model
import com.google.gson.annotations.SerializedName

data class CuentaParaCrear(
    @SerializedName("id_usuario")
    val idUsuario: Int,
    @SerializedName("nombre_cuenta")
    val nombreCuenta: String,
    @SerializedName("saldo_actual")
    val saldoActual: Double,
    @SerializedName("moneda")
    val moneda: String,
    @SerializedName("img_cuenta")
    val imgCuenta: String?
)