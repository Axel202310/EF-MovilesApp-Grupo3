package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.card.MaterialCardView

class CuentasActividad : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_cuentas)

        // Inicialización de vistas
        val layoutNuevaTransferencia = findViewById<LinearLayout>(R.id.layout_nueva_transferencia)
        val fabAgregarCuenta = findViewById<FloatingActionButton>(R.id.fab_add_cuenta)
        val textTotal = findViewById<TextView>(R.id.tv_total_value)
        val cardCuentaPrincipal = findViewById<MaterialCardView>(R.id.card_cuenta_principal)

        // Eventos
        layoutNuevaTransferencia.setOnClickListener { irATransferencia() }
        fabAgregarCuenta.setOnClickListener { irAAgregarCuenta() }

        // Simulación de total
        textTotal.text = "3,708.00 S/." // Reemplazar luego por dato real
    }

    private fun irATransferencia() {
        startActivity(Intent(this, TransferenciaActividad::class.java))
    }

    private fun irAAgregarCuenta() {
        startActivity(Intent(this, AgregarCuentaActividad::class.java))
    }
}
