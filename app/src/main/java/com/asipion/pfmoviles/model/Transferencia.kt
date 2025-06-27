package com.asipion.pfmoviles.model
import com.google.gson.annotations.SerializedName

data class Transferencia(
    @SerializedName("id_usuario") val idUsuario: Int,
    @SerializedName("id_cuenta_origen") val idCuentaOrigen: Int,
    @SerializedName("id_cuenta_destino") val idCuentaDestino: Int,
    @SerializedName("monto") val monto: Double,
    @SerializedName("comentario") val comentario: String?,
    @SerializedName("fecha_transferencia") val fechaTransferencia: String
)