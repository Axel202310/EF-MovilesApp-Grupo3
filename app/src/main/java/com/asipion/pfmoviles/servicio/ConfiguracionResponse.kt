package com.asipion.pfmoviles.servicio

data class ConfiguracionResponse(
    val configuracion: ConfiguracionInicial
)

data class ConfiguracionInicial(
    val monto: Double,
    val moneda: String
)
