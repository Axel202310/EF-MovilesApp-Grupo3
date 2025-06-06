package com.asipion.pfmoviles

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class AgregarTransaccionActividad : AppCompatActivity() {


    private lateinit var etDate: EditText
    private lateinit var btnAtras: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregartransaccion)
        etDate = findViewById(R.id.etDate)
        btnAtras = findViewById(R.id.btnAtras)

        // Mostrar selector de fecha al hacer click
        etDate.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year ->
                val fechaFormateada = "%02d/%02d/%04d".format(day, month + 1, year)
                etDate.setText(fechaFormateada)
            }
            datePicker.show(supportFragmentManager, "selector_fecha")
        }

        // Cerrar esta Activity con el botón de retroceso
        btnAtras.setOnClickListener {
            //finish()
        }
        seleccion()
        seleccionCategorias()

        pasarVistaAñadir()
    }

    private fun seleccion() {
        val btnGasto = findViewById<Button>(R.id.btnGasto)
        val btnIngreso = findViewById<Button>(R.id.btnIngreso)
        val underlineGasto = findViewById<View>(R.id.underlineGasto)
        val underlineIngreso = findViewById<View>(R.id.underlineIngreso)

        val textosCategoria = listOf(
            findViewById<TextView>(R.id.txtcat1),
            findViewById<TextView>(R.id.txtCat2),
            findViewById<TextView>(R.id.txtCat3),
            findViewById<TextView>(R.id.txtCat4),
            findViewById<TextView>(R.id.txtCat5),
            findViewById<TextView>(R.id.txtCat6),
            findViewById<TextView>(R.id.txtCat7)
        )

        val nombresGasto = listOf("Educacion", "Salud", "Transporte", "Hogar", "Regalos", "Alimentos", "Otros")
        val nombresIngreso = listOf("Sueldo", "Ventas", "Inversiones", "Premios", "Ahorros", "Bonos", "Otros")

        btnGasto.setOnClickListener {
            underlineGasto.visibility = View.VISIBLE
            underlineIngreso.visibility = View.GONE

            textosCategoria.forEachIndexed { index, textView ->
                textView.text = nombresGasto[index]
            }
        }

        btnIngreso.setOnClickListener {
            underlineIngreso.visibility = View.VISIBLE
            underlineGasto.visibility = View.GONE

            textosCategoria.forEachIndexed { index, textView ->
                textView.text = nombresIngreso[index]
            }
        }
    }

    private fun seleccionCategorias() {
        val categorias = listOf(
            Triple(
                findViewById<LinearLayout>(R.id.contenedorCat1),
                findViewById<View>(R.id.vistaFondo1),
                R.drawable.fondo_seleccionado_rojo to R.drawable.circulo_rojo
            ),
            Triple(
                findViewById<LinearLayout>(R.id.contenedorCat2),
                findViewById<View>(R.id.vistaFondo2),
                R.drawable.fondo_seleccionado_verdeagua to R.drawable.circulo_verdeagua
            ),
            Triple(
                findViewById<LinearLayout>(R.id.contenedorCat3),
                findViewById<View>(R.id.vistaFondo3),
                R.drawable.fondo_seleccionado_naranja to R.drawable.circulo_naranja
            ),
            Triple(
                findViewById<LinearLayout>(R.id.contenedorCat4),
                findViewById<View>(R.id.vistaFondo4),
                R.drawable.fondo_seleccionado_morado to R.drawable.circulo_morado
            ),
            Triple(
                findViewById<LinearLayout>(R.id.contenedorCat5),
                findViewById<View>(R.id.vistaFondo5),
                R.drawable.fondo_seleccionado_celeste to R.drawable.circulo_celeste
            ),
            Triple(
                findViewById<LinearLayout>(R.id.contenedorCat6),
                findViewById<View>(R.id.vistaFondo6),
                R.drawable.fondo_seleccionado_rosado to R.drawable.circulo_rosado
            ),
            Triple(
                findViewById<LinearLayout>(R.id.contenedorCat7),
                findViewById<View>(R.id.vistaFondo7),
                R.drawable.fondo_seleccionado_verde to R.drawable.circulo_verde
            )
        )

        val botones = listOf(
            findViewById<Button>(R.id.btncat1),
            findViewById<Button>(R.id.btncat2),
            findViewById<Button>(R.id.btncat3),
            findViewById<Button>(R.id.btncat4),
            findViewById<Button>(R.id.btncat5),
            findViewById<Button>(R.id.btncat6),
            findViewById<Button>(R.id.btncat7)
        )

        var categoriaSeleccionada = -1

        botones.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (categoriaSeleccionada != index) {
                    // Limpiar todas las categorías
                    categorias.forEachIndexed { i, triple ->
                        triple.first.setBackgroundColor(Color.TRANSPARENT)
                        triple.second.setBackgroundResource(triple.third.second)
                    }

                    // Seleccionar la actual
                    val (contenedor, vista, recursos) = categorias[index]
                    contenedor.setBackgroundResource(recursos.first)
                    vista.setBackgroundColor(Color.TRANSPARENT)

                    categoriaSeleccionada = index
                } else {
                    // Deseleccionar si se vuelve a tocar
                    categorias[index].first.setBackgroundColor(Color.TRANSPARENT)
                    categorias[index].second.setBackgroundResource(categorias[index].third.second)
                    categoriaSeleccionada = -1
                }
            }
        }
    }

    private fun pasarVistaAñadir(){
        val btnMas = findViewById<Button>(R.id.btnMas)

        btnMas.setOnClickListener {
            val intent = Intent(this, AnadirCategoriaActividad::class.java)
            startActivity(intent)
        }
    }
}