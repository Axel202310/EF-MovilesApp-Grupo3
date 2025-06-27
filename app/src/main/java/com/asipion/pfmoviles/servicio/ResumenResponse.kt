package com.asipion.pfmoviles.servicio

import com.asipion.pfmoviles.model.ResumenPeriodo
import com.google.gson.annotations.SerializedName

data class ResumenResponse(
    @SerializedName("resumen") val resumen: List<ResumenPeriodo>
)