package com.asipion.pfmoviles.model
import com.google.gson.annotations.SerializedName

data class Transaccion(
    @SerializedName("id_transaccion")
    val idTransaccion: Int,
    @SerializedName("id_cuenta")
    val idCuenta: Int,
    @SerializedName("id_categoria")
    val idCategoria: Int,
    @SerializedName("monto_transaccion")
    val montoTransaccion: Double,
    @SerializedName("descripcion")
    val descripcion: String?,
    @SerializedName("fecha_transaccion")
    val fechaTransaccion: String,
    @SerializedName("tipo_categoria")
    val tipoTransaccion: String,
    @SerializedName("nombre_categoria")
    val nombreCategoria: String?,
    @SerializedName("img_categoria")
    val imgCategoria: String?
)