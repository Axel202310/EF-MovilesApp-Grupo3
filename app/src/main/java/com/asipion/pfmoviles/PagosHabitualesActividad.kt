package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class PagosHabitualesActividad : AppCompatActivity() {

    private lateinit var layoutCrearPago: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_pagos_habituales)

        inicializarVista()
        configurarEventos()
    }

    private fun inicializarVista() {
        layoutCrearPago = findViewById(R.id.layout_crear_pago)
    }

    private fun configurarEventos() {
        layoutCrearPago.setOnClickListener {
            abrirPantallaCrearRecordatorio()
        }
    }

    private fun abrirPantallaCrearRecordatorio() {
        startActivity(Intent(this, CrearRecordatorioActividad::class.java))
    }
}
