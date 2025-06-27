// --- Archivo: model/PagoHabitual.kt (Versión Final y Simplificada) ---
package com.asipion.pfmoviles.model
import com.google.gson.annotations.SerializedName

data class PagoHabitual(
    @SerializedName("id_pago_habitual") val idPagoHabitual: Int,
    @SerializedName("id_usuario") val idUsuario: Int,
    @SerializedName("id_cuenta_origen") val idCuentaOrigen: Int,
    @SerializedName("id_categoria") val idCategoria: Int,
    @SerializedName("nombre_pago") val nombrePago: String,
    @SerializedName("monto_pago") val montoPago: Double,
    @SerializedName("frecuencia") val frecuencia: String,
    @SerializedName("fecha_inicio") val fechaInicio: String,

    // Campos extra del JOIN para mostrar en la UI
    @SerializedName("nombre_cuenta") val nombreCuenta: String,
    @SerializedName("nombre_categoria") val nombreCategoria: String
)