package com.asipion.pfmoviles

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.Categoria
import com.asipion.pfmoviles.servicio.RetrofitClient
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FormularioCategoriaActivity : AppCompatActivity() {

    // --- Lista de Iconos Disponibles ---
    private val nombresDeIconosDisponibles = listOf(
        // Iconos de Gastos
        "ic_categoria_gasto_cine",
        "ic_categoria_gasto_compra",
        "ic_categoria_gasto_dentista",
        "ic_categoria_gasto_estadio",
        "ic_categoria_gasto_gasolineria",
        "ic_categoria_gasto_gym",
        "ic_categoria_gasto_juegos",
        "ic_categoria_gasto_netflix",
        "ic_categoria_gasto_veterinaria",

        // Iconos de Ingresos
        "ic_categoria_ingreso_finanza1",
        "ic_categoria_ingreso_finanza2",
        "ic_categoria_ingreso_finanza3",
        "ic_categoria_ingreso_finanza4",
        "ic_categoria_ingreso_minimoto",
        "ic_categoria_ingreso_taxi",
    )

    // Vistas
    private lateinit var toolbar: MaterialToolbar
    private lateinit var etNombre: EditText
    private lateinit var radioGroupTipo: RadioGroup
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button
    private lateinit var layoutIconoSeleccionado: View
    private lateinit var ivIconoActual: ImageView
    private lateinit var recyclerIconos: RecyclerView

    // Variables de estado
    private var categoriaId: Int = -1
    private var esModoEdicion = false
    private var iconoSeleccionado: String = "ic_categoria_otros_gastos" // Icono por defecto al crear una nueva

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_categoria)

        inicializarVistas()

        categoriaId = intent.getIntExtra("CATEGORIA_ID", -1)
        esModoEdicion = categoriaId != -1

        configurarUI()

        if (esModoEdicion) {
            cargarDatosCategoria()
        } else {
            // Si es una nueva categoría, mostramos el icono por defecto inicial.
            actualizarIconoMostrado(iconoSeleccionado)
        }
    }

    private fun inicializarVistas() {
        toolbar = findViewById(R.id.toolbar_formulario_categoria)
        etNombre = findViewById(R.id.et_nombre_categoria)
        radioGroupTipo = findViewById(R.id.radio_group_tipo)
        btnGuardar = findViewById(R.id.btn_guardar_categoria)
        btnEliminar = findViewById(R.id.btn_eliminar_categoria)
        layoutIconoSeleccionado = findViewById(R.id.layout_icono_seleccionado)
        ivIconoActual = findViewById(R.id.iv_icono_actual)
        recyclerIconos = findViewById(R.id.recycler_view_iconos)

        configurarSelectorIconos()
    }

    private fun configurarSelectorIconos() {
        // Usamos la lista 'nombresDeIconosDisponibles' definida al principio de esta clase.
        val adaptador = AdaptadorIcono(this, nombresDeIconosDisponibles) { nombreIcono ->
            iconoSeleccionado = nombreIcono
            actualizarIconoMostrado(nombreIcono)
            recyclerIconos.visibility = View.GONE // Ocultamos la grilla al seleccionar
        }

        recyclerIconos.adapter = adaptador
        recyclerIconos.layoutManager = GridLayoutManager(this, 5)

        layoutIconoSeleccionado.setOnClickListener {
            // Muestra/oculta la grilla de iconos
            recyclerIconos.visibility = if (recyclerIconos.visibility == View.GONE) View.VISIBLE else View.GONE
        }
    }

    private fun actualizarIconoMostrado(nombreIcono: String) {
        val resId = resources.getIdentifier(nombreIcono, "drawable", packageName)
        if (resId != 0) {
            ivIconoActual.setImageResource(resId)
        } else {
            // Si por alguna razón el icono no se encuentra, usamos uno por defecto.
            ivIconoActual.setImageResource(R.drawable.ic_categoria_otros_gastos)
        }
    }

    private fun configurarUI() {
        toolbar.setNavigationOnClickListener { finish() }

        if (esModoEdicion) {
            toolbar.title = "Editar Categoría"
            btnGuardar.text = "Guardar Cambios"
            btnEliminar.visibility = View.VISIBLE
            findViewById<RadioButton>(R.id.radio_gasto).isEnabled = false
            findViewById<RadioButton>(R.id.radio_ingreso).isEnabled = false
        } else {
            toolbar.title = "Añadir Nueva Categoría"
            btnGuardar.text = "Añadir Categoría"
            btnEliminar.visibility = View.GONE
        }

        btnGuardar.setOnClickListener { guardarCategoria() }
        btnEliminar.setOnClickListener { mostrarDialogoEliminacion() }
    }

    private fun cargarDatosCategoria() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerDetalleCategoria(categoriaId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { categoria ->
                            etNombre.setText(categoria.nombreCategoria)
                            if (categoria.tipoCategoria == "gasto") {
                                findViewById<RadioButton>(R.id.radio_gasto).isChecked = true
                            } else {
                                findViewById<RadioButton>(R.id.radio_ingreso).isChecked = true
                            }
                            iconoSeleccionado = categoria.imgCategoria ?: "ic_categoria_otros_gastos"
                            actualizarIconoMostrado(iconoSeleccionado)
                        }
                    } else {
                        Toast.makeText(this@FormularioCategoriaActivity, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FormularioCategoriaActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun guardarCategoria() {
        val nombre = etNombre.text.toString().trim()
        val tipoSeleccionado = if (findViewById<RadioButton>(R.id.radio_gasto).isChecked) "gasto" else "ingreso"
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre de la categoría es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }
        if (idUsuario == -1) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show()
            return
        }

        val categoriaData = Categoria(
            idCategoria = if (esModoEdicion) categoriaId else 0,
            idUsuario = idUsuario,
            nombreCategoria = nombre,
            tipoCategoria = tipoSeleccionado,
            imgCategoria = iconoSeleccionado
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if (esModoEdicion) {
                    RetrofitClient.webService.actualizarCategoria(categoriaId, categoriaData)
                } else {
                    RetrofitClient.webService.crearCategoria(categoriaData)
                }
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormularioCategoriaActivity, response.body()?.mensaje, Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorMsg = response.body()?.mensaje ?: "Error desconocido al guardar"
                        Toast.makeText(this@FormularioCategoriaActivity, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FormularioCategoriaActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarDialogoEliminacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Categoría")
            .setMessage("¿Estás seguro? Si hay transacciones usando esta categoría, no se podrá eliminar.")
            .setPositiveButton("Eliminar") { _, _ -> eliminarCategoria() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCategoria() {
        if (categoriaId == -1) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.eliminarCategoria(categoriaId)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FormularioCategoriaActivity, response.body()?.mensaje, Toast.LENGTH_LONG).show()
                    if (response.isSuccessful) {
                        finish() // Si se eliminó, cerramos y volvemos a la lista
                    }
                }
            } catch(e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FormularioCategoriaActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}