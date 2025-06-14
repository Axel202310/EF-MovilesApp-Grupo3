// --- Archivo: MainActivity.kt (Explicado) ---
package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // private lateinit var etDate: EditText // Descomenta si tienes este EditText en tu layout final

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Habilita el modo "Edge-to-Edge": la app usa toda la pantalla.
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // etDate = findViewById(R.id.etDate) // Descomenta y asegúrate de que R.id.etDate exista

        // 2. Aplica "insets" como padding al layout principal (con id 'main').
        //    Esto evita que el contenido importante quede debajo de las barras del sistema.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. La lógica del DatePicker es correcta y modular.
        // etDate.setOnClickListener {
        //     showDatePickerDialog()
        // }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        // etDate.setText("$day / ${month + 1} / $year") // El mes está basado en 0, así que se le suma 1.
    }
}