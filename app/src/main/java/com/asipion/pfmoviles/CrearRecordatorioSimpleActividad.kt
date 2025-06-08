package com.asipion.pfmoviles

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActividadCrearRecordatorioSimpleBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CrearRecordatorioSimpleActividad : AppCompatActivity() {

    private lateinit var binding: ActividadCrearRecordatorioSimpleBinding
    private val calendario = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadCrearRecordatorioSimpleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarEventos()
        actualizarCamposFechaYHora()
    }

    private fun configurarEventos() {
        binding.ivBack.setOnClickListener { finish() }

        binding.tvDia.setOnClickListener { mostrarDialogoFecha() }

        binding.tvHora.setOnClickListener { mostrarDialogoHora() }

        binding.tvFrecuencia.setOnClickListener {
            Toast.makeText(this, "Selector de frecuencia presionado", Toast.LENGTH_SHORT).show()
        }

        binding.btnCrear.setOnClickListener {
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
        binding.tvDia.text = formatoFecha.format(calendario.time)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

        val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.tvHora.text = formatoHora.format(calendario.time)
    }

    private fun crearRecordatorio() {
        val nombre = binding.etNombre.text.toString().trim()

        if (nombre.isEmpty()) {
            binding.etNombre.error = "Campo requerido"
            Toast.makeText(this, "Por favor, ingrese un nombre para el recordatorio", Toast.LENGTH_SHORT).show()
            return
        }

        val frecuencia = binding.tvFrecuencia.text.toString()
        val comentario = binding.etComentario.text.toString()
        val fechaHora = calendario.timeInMillis

        Toast.makeText(this, "Recordatorio '$nombre' creado!", Toast.LENGTH_LONG).show()

        // Aquí podrías guardar el recordatorio en base de datos
        finish()
    }
}
