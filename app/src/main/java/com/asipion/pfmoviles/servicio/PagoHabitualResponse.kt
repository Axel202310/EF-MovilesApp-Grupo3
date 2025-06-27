// --- Archivo: servicio/PagoHabitualResponse.kt (Confirmado) ---
package com.asipion.pfmoviles.servicio
import com.asipion.pfmoviles.model.PagoHabitual
import com.google.gson.annotations.SerializedName

data class PagoHabitualResponse(
    @SerializedName("pagosHabituales") val pagosHabituales: List<PagoHabitual>
)