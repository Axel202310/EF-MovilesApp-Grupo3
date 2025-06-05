package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CuentasActividad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_cuentas)

        val btnTransferencia = findViewById<LinearLayout>(R.id.layout_nueva_transferencia)
        val fabAddCuenta = findViewById<FloatingActionButton>(R.id.fab_add_cuenta)

        btnTransferencia.setOnClickListener {
            startActivity(Intent(this, TransferenciaActividad::class.java))
        }

        fabAddCuenta.setOnClickListener {
            startActivity(Intent(this, AgregarCuentaActividad::class.java))
        }
    }
}
