package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout

class CrearRecordatorioActividad : AppCompatActivity() {

    // Estado para saber si estamos en modo Gasto (true) o Ingreso (false)
    private var esModoGasto: Boolean = true

    // Referencias a los componentes de la vista
    private lateinit var tabLayout: TabLayout
    private lateinit var botonAtras: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_crear_recordatorio)

        inicializarVista()
        configurarEventos()

        // Por defecto iniciar en el modo Gastos
        actualizarVistaParaGastos()
    }

    private fun inicializarVista() {
        tabLayout = findViewById(R.id.tab_layout)
        botonAtras = findViewById(R.id.iv_back)
    }

    private fun configurarEventos() {
        botonAtras.setOnClickListener {
            finish() // Volver a la pantalla anterior
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                esModoGasto = (tab?.position == 0)
                if (esModoGasto) {
                    actualizarVistaParaGastos()
                } else {
                    actualizarVistaParaIngresos()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun actualizarVistaParaGastos() {
        // Aquí puedes cargar categorías de gastos, cambiar colores, etc.
        Toast.makeText(this, "Modo: Gastos", Toast.LENGTH_SHORT).show()

        // Ejemplo futuro:
        // val campoCategoria = findViewById<TextView>(R.id.tv_category_value)
        // campoCategoria.setOnClickListener { abrirDialogoCategorias("gastos") }
    }

    private fun actualizarVistaParaIngresos() {
        Toast.makeText(this, "Modo: Ingresos", Toast.LENGTH_SHORT).show()

        // Ejemplo futuro:
        // val campoCategoria = findViewById<TextView>(R.id.tv_category_value)
        // campoCategoria.setOnClickListener { abrirDialogoCategorias("ingresos") }
    }
}
