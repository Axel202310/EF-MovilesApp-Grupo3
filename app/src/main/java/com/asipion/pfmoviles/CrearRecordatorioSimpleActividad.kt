package com.asipion.pfmoviles

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CrearRecordatorioSimpleActividad : AppCompatActivity() {

    // Referencias a las vistas
    private lateinit var ivBack: ImageView
    private lateinit var etNombre: EditText
    private lateinit var tvFrecuencia: TextView
    private lateinit var tvDia: TextView
    private lateinit var tvHora: TextView
    private lateinit var etComentario: EditText
    private lateinit var btnCrear: MaterialButton

    // Manejador global de fecha y hora
    private val calendario = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_crear_recordatorio_simple)

        inicializarVistas()
        configurarEventos()
        actualizarCamposFechaYHora()
    }

    private fun inicializarVistas() {
        ivBack = findViewById(R.id.iv_back)
        etNombre = findViewById(R.id.et_nombre)
        tvFrecuencia = findViewById(R.id.tv_frecuencia)
        tvDia = findViewById(R.id.tv_dia)
        tvHora = findViewById(R.id.tv_hora)
        etComentario = findViewById(R.id.et_comentario)
        btnCrear = findViewById(R.id.btn_crear)
    }

    private fun configurarEventos() {
        ivBack.setOnClickListener {
            finish()
        }

        tvDia.setOnClickListener {
            mostrarDialogoFecha()
        }

        tvHora.setOnClickListener {
            mostrarDialogoHora()
        }

        tvFrecuencia.setOnClickListener {
            Toast.makeText(this, "Selector de frecuencia presionado", Toast.LENGTH_SHORT).show()
        }

        btnCrear.setOnClickListener {
            crearRecordatorio()
        }
    }

    private fun mostrarDialogoFecha() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendario.set(Calendar.YEAR, year)
            calendario.set(Calendar.MONTH, month)
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            actualizarCamposFechaYHora()
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun mostrarDialogoHora() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendario.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendario.set(Calendar.MINUTE, minute)
            actualizarCamposFechaYHora()
        }

        TimePickerDialog(
            this,
            timeSetListener,
            calendario.get(Calendar.HOUR_OF_DAY),
            calendario.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun actualizarCamposFechaYHora() {
        val formatoFecha = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
        val fechaFormateada = formatoFecha.format(calendario.time)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        tvDia.text = fechaFormateada

        val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
        tvHora.text = formatoHora.format(calendario.time)
    }

    private fun crearRecordatorio() {
        val nombre = etNombre.text.toString().trim()

        if (nombre.isEmpty()) {
            etNombre.error = "Campo requerido"
            Toast.makeText(this, "Por favor, ingrese un nombre para el recordatorio", Toast.LENGTH_SHORT).show()
            return
        }

        val frecuencia = tvFrecuencia.text.toString()
        val comentario = etComentario.text.toString()
        val fechaHora = calendario.timeInMillis

        Toast.makeText(this, "Recordatorio '$nombre' creado!", Toast.LENGTH_LONG).show()

        // Aquí se puede guardar a una base de datos si se desea.
        // Ejemplo:
        // val recordatorio = Recordatorio(nombre, frecuencia, fechaHora, comentario)
        // viewModel.guardar(recordatorio)

        finish()
    }
}
