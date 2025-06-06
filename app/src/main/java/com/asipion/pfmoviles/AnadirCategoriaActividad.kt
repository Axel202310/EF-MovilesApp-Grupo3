package com.asipion.pfmoviles

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AnadirCategoriaActividad: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anadir_categoria)
        seleccionCategorias()
        retroceder()
        mostrarseleccion()
        clickSiguiente()
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

    private fun mostrarseleccion(){
        val tipoCategoria = intent.getStringExtra("tipoCategoria") ?: "gasto"

        val textosCategoria = listOf(
            findViewById<TextView>(R.id.txtCat1),
            findViewById<TextView>(R.id.txtCat2),
            findViewById<TextView>(R.id.txtCat3),
            findViewById<TextView>(R.id.txtCat4),
            findViewById<TextView>(R.id.txtCat5),
            findViewById<TextView>(R.id.txtCat6),
            findViewById<TextView>(R.id.txtCat7)
        )

        val nombresGasto = listOf("Educación", "Salud", "Transporte", "Hogar", "Regalos", "Alimentos", "Otros")
        val nombresIngreso = listOf("Sueldo", "Ventas", "Inversiones", "Premios", "Ahorros", "Bonos", "Otros")

        val nombresSeleccionados = if (tipoCategoria == "gasto") nombresGasto else nombresIngreso

        textosCategoria.forEachIndexed { index, textView ->
            textView.text = nombresSeleccionados[index]
        }
    }

    private fun retroceder(){
        val btnAtras1 = findViewById<Button>(R.id.btnAtras1)

        btnAtras1.setOnClickListener {
            val intent = Intent(this, AgregarTransaccionActividad::class.java)
            startActivity(intent)
        }
    }

    private fun clickSiguiente(){
        val btnCrear = findViewById<Button>(R.id.btnCrear)

        btnCrear.setOnClickListener {
            val intent = Intent(this, CrearCategoriaActividad::class.java)
            startActivity(intent)
        }
    }
}