package com.asipion.pfmoviles.model
import com.google.gson.annotations.SerializedName

data class HistorialTransferencia(
    @SerializedName("id_historial") val idHistorial: Int,
    @SerializedName("id_usuario") val idUsuario: Int,
    @SerializedName("id_cuenta_origen") val idCuentaOrigen: Int,
    @SerializedName("id_cuenta_destino") val idCuentaDestino: Int,
    @SerializedName("monto") val monto: Double,
    @SerializedName("fecha_transferencia_realizada")
    val fechaTransferencia: String,

    @SerializedName("nombre_origen") val nombreOrigen: String,
    @SerializedName("nombre_destino") val nombreDestino: String,

    // El campo comentario es opcional (nullable)
    @SerializedName("comentario") val comentario: String?
)