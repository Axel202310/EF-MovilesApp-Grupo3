// --- Archivo: FormularioCategoriaActivity.kt (Final, Completo y Detallado) ---
package com.asipion.pfmoviles

import android.os.Bundle
import android.util.Log
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
    private var iconoSeleccionado: String = "ic_default_category" // Icono por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_categoria)

        inicializarVistas()

        categoriaId = intent.getIntExtra("CATEGORIA_ID", -1)
        esModoEdicion = categoriaId != -1

        configurarUI()

        if (esModoEdicion) {
            cargarDatosCategoria()
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
        // Define aquí los nombres de tus archivos drawable para los iconos.
        val listaNombresIconos = listOf(
            "educacion", "salud", "transporte", "hogar",
            "alimentos", "regalos", "otros", "ic_yape", "ic_dollar_placeholder",
            "ic_default_category" // Añade todos los que necesites
        )

        val adaptador = AdaptadorIcono(this, listaNombresIconos) { nombreIcono ->
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

    // Nueva función para actualizar el ImageView del icono
    private fun actualizarIconoMostrado(nombreIcono: String) {
        val resId = resources.getIdentifier(nombreIcono, "drawable", packageName)
        if (resId != 0) {
            ivIconoActual.setImageResource(resId)
        } else {
            // Si el icono no se encuentra, usamos el por defecto.
            ivIconoActual.setImageResource(R.drawable.ic_default_category)
        }
    }

    private fun configurarUI() {
        toolbar.setNavigationOnClickListener { finish() }

        if (esModoEdicion) {
            toolbar.title = "Editar Categoría"
            btnGuardar.text = "Guardar Cambios"
            btnEliminar.visibility = View.VISIBLE
            // Deshabilitamos el cambio de tipo para una categoría existente
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
                            iconoSeleccionado = categoria.imgCategoria ?: "ic_default_category"
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