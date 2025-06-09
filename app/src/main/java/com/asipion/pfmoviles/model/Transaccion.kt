package com.asipion.pfmoviles.model
import java.util.Date

class Transaccion (
    var id_transaccion: Int? = null,
    var monto_transaccion: Double,
    var moneda: String,
    var fecha: Date? = null,
    var id_usuario: Int,
    var id_categoria: Int? = null,
    var idcuenta: Int? = null,
    )