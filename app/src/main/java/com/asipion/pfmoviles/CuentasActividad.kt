package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class CuentasActividad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_cuentas)

        val layoutNuevaTransferencia = findViewById<LinearLayout>(R.id.layout_nueva_transferencia)
        val fabAgregarCuenta = findViewById<FloatingActionButton>(R.id.fab_add_cuenta)
        val textTotal = findViewById<TextView>(R.id.tv_total_value)
        val cardCuentaPrincipal = findViewById<MaterialCardView>(R.id.card_cuenta_principal)

        // Navegar a TransferenciaActividad
        layoutNuevaTransferencia.setOnClickListener {
            val intent = Intent(this, TransferenciaActividad::class.java)
            startActivity(intent)
        }

        // Navegar a AgregarCuentaActividad
        fabAgregarCuenta.setOnClickListener {
            val intent = Intent(this, AgregarCuentaActividad::class.java)
            startActivity(intent)
        }

        // Valor total simulado por ahora
        textTotal.text = "3,708.00 S/."
    }
}
