package com.asipion.pfmoviles.model
import com.google.gson.annotations.SerializedName

data class TransaccionParaCrear(
    @SerializedName("id_cuenta")
    val idCuenta: Int,
    @SerializedName("id_categoria")
    val idCategoria: Int,
    @SerializedName("monto_transaccion")
    val montoTransaccion: Double,
    @SerializedName("descripcion")
    val descripcion: String?,
    @SerializedName("fecha_transaccion")
    val fechaTransaccion: String
)