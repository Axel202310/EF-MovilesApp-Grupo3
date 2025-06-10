package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuPrincipalActividad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_principal)
        ircategorias()
        ircuentas()
        irgraficos()
        irpagoshabituales()
        irrecordatorios()
        irajustes()

    }
    private fun ircategorias(){
        val btnCategoria = findViewById<Button>(R.id.btnCategoria)

        btnCategoria.setOnClickListener {
            val intent = Intent(this, MenuCategoriasActividad::class.java)
            startActivity(intent)
        }
    }

    private fun ircuentas(){
        val btnCuenta = findViewById<Button>(R.id.btnCuenta)

        btnCuenta.setOnClickListener {
            val intent = Intent(this, CuentasActividad::class.java)
            startActivity(intent)
        }
    }

    private fun irgraficos(){
        val btnGrafico = findViewById<Button>(R.id.btnGrafico)

        btnGrafico.setOnClickListener {
            val intent = Intent(this, GraficosActivity::class.java)
            startActivity(intent)
        }
    }

    private fun irpagoshabituales(){
        val btnPagos = findViewById<Button>(R.id.btnPagos)

        btnPagos.setOnClickListener {
            val intent = Intent(this, PagosHabitualesActividad::class.java)
            startActivity(intent)
        }
    }

    private fun irrecordatorios(){
        val btnRecordatorio = findViewById<Button>(R.id.btnRecordatorio)

        btnRecordatorio.setOnClickListener {
            val intent = Intent(this, RecordatoriosActividad::class.java)
            startActivity(intent)
        }
    }

    private fun irajustes(){
        val btnAjuste = findViewById<Button>(R.id.btnAjuste)

        btnAjuste.setOnClickListener {
            val intent = Intent(this, AjustesActividad::class.java)
            startActivity(intent)
        }
    }


}