// --- Archivo: servicio/HistorialResponse.kt ---
package com.asipion.pfmoviles.servicio
import com.asipion.pfmoviles.model.HistorialTransferencia
import com.google.gson.annotations.SerializedName

data class HistorialResponse(
    @SerializedName("historial") val historial: List<HistorialTransferencia>
)