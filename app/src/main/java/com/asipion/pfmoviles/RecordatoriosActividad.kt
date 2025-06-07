package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch

// 1. Heredamos de AppCompatActivity para definir esta clase como una pantalla.
class RecordatoriosActividad : AppCompatActivity() {

    // 2. Declaramos las referencias a las vistas que necesitamos controlar.
    private lateinit var menuIcon: ImageView
    private lateinit var layoutCrearRecordatorio: LinearLayout
    private lateinit var switchRecordatorios: MaterialSwitch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 3. Conectamos el archivo XML con esta clase.
        setContentView(R.layout.actividad_recordatorios)

        // 4. Organizamos la inicialización y configuración en funciones separadas.
        inicializarVistas()
        configurarEventos()
    }

    /**
     * Encuentra las vistas en el layout por su ID y las asigna a nuestras variables.
     */
    private fun inicializarVistas() {
        menuIcon = findViewById(R.id.iv_menu)
        layoutCrearRecordatorio = findViewById(R.id.layout_crear_recordatorio)
        switchRecordatorios = findViewById(R.id.switch_recordatorios)
    }

    /**
     * Configura los listeners para los eventos de clic y cambio en las vistas.
     */
    private fun configurarEventos() {
        // Evento de clic para el botón "Crear".
        layoutCrearRecordatorio.setOnClickListener {
            abrirPantallaCrearRecordatorio()
        }

        // Evento para cuando el estado del Switch cambia (el usuario lo activa o desactiva).
        switchRecordatorios.setOnCheckedChangeListener { _, isChecked ->
            // 'isChecked' es un booleano: true si está activado, false si está desactivado.
            if (isChecked) {
                // Lógica para cuando se activan los recordatorios.
                Toast.makeText(this, "Recordatorios ACTIVADOS", Toast.LENGTH_SHORT).show()
                // Aquí podrías, por ejemplo, registrar las alarmas de los recordatorios en el sistema.
            } else {
                // Lógica para cuando se desactivan los recordatorios.
                Toast.makeText(this, "Recordatorios DESACTIVADOS", Toast.LENGTH_SHORT).show()
                // Aquí podrías cancelar todas las alarmas de recordatorios registradas.
            }
        }

        // Evento de clic para el icono del menú (opcional, para futuro).
        menuIcon.setOnClickListener {
            // Lógica para abrir el Navigation Drawer o un menú de opciones.
            Toast.makeText(this, "Menú presionado", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Crea un Intent y lanza la actividad para crear un nuevo recordatorio.
     */
    private fun abrirPantallaCrearRecordatorio() {
        // Usamos la misma lógica que en la pantalla anterior para navegar.
        val intent = Intent(this, CrearRecordatorioActividad::class.java)
        startActivity(intent)
    }
}