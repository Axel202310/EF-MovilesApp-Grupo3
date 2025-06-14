package com.asipion.pfmoviles.model
import com.google.gson.annotations.SerializedName

// Modelo específico para ENVIAR datos al crear una cuenta nueva
data class CuentaParaCrear(
    @SerializedName("id_usuario")
    val idUsuario: Int,

    @SerializedName("nombre_cuenta")
    val nombreCuenta: String,

    @SerializedName("saldo_actual")
    val saldoActual: Double,

    @SerializedName("moneda")
    val moneda: String
)