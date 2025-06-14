package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.asipion.pfmoviles.model.Categoria
import com.asipion.pfmoviles.model.Cuenta
import com.asipion.pfmoviles.model.TransaccionParaCrear
import com.asipion.pfmoviles.servicio.RetrofitClient
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AgregarTransaccionActividad : AppCompatActivity() {

    // Vistas de la UI
    private lateinit var tabLayoutTipo: TabLayout
    private lateinit var montoEditText: EditText
    private lateinit var cuentasSpinner: Spinner
    private lateinit var categoriasRecyclerView: RecyclerView
    private lateinit var fechaEditText: EditText
    private lateinit var btnAnadir: Button
    private lateinit var btnAtras: ImageButton

    // Adaptadores y datos
    private lateinit var categoriaAdapter: CategoriaAdapter
    private var listaCuentas: List<Cuenta> = emptyList()

    // Estado de la selección
    private var tipoSeleccionado = "gasto" // Por defecto
    private var cuentaSeleccionada: Cuenta? = null
    private var categoriaSeleccionada: Categoria? = null
    private var fechaSeleccionada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregartransaccion)

        inicializarVistas()
        configurarListeners()

        // Inicialmente cargamos los datos para "gasto"
        cargarDatosIniciales()
    }

    private fun inicializarVistas() {
        tabLayoutTipo = findViewById(R.id.tab_layout_tipo)
        montoEditText = findViewById(R.id.txtMonto)
        cuentasSpinner = findViewById(R.id.spinner_cuentas)
        categoriasRecyclerView = findViewById(R.id.recycler_view_categorias)
        fechaEditText = findViewById(R.id.etDate)
        btnAnadir = findViewById(R.id.btnAnadir)
        btnAtras = findViewById(R.id.btnAtras)

        // Configurar el RecyclerView
        categoriaAdapter = CategoriaAdapter(emptyList()) { categoria ->
            categoriaSeleccionada = categoria
        }
        categoriasRecyclerView.adapter = categoriaAdapter
    }

    private fun configurarListeners() {
        btnAtras.setOnClickListener { finish() } // Manera correcta de volver atrás

        tabLayoutTipo.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tipoSeleccionado = if (tab?.position == 0) "gasto" else "ingreso"
                cargarCategorias()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        fechaEditText.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)

                val formatoApi = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                fechaSeleccionada = formatoApi.format(calendar.time)

                val formatoVista = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                fechaEditText.setText(formatoVista.format(calendar.time))
            }
            datePicker.show(supportFragmentManager, "selector_fecha")
        }

        btnAnadir.setOnClickListener {
            guardarTransaccion()
        }
    }

    private fun cargarDatosIniciales() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Cargamos cuentas y categorías en paralelo (más eficiente)
                val cuentasResponse = RetrofitClient.webService.obtenerCuentas(idUsuario)
                val categoriasResponse = RetrofitClient.webService.obtenerCategorias(idUsuario, tipoSeleccionado)

                withContext(Dispatchers.Main) {
                    // Manejar respuesta de cuentas
                    if (cuentasResponse.isSuccessful) {
                        listaCuentas = cuentasResponse.body()?.listaCuentas ?: emptyList()
                        val nombresCuentas = listaCuentas.map { it.nombreCuenta }
                        val spinnerAdapter = ArrayAdapter(this@AgregarTransaccionActividad, android.R.layout.simple_spinner_dropdown_item, nombresCuentas)
                        cuentasSpinner.adapter = spinnerAdapter
                    } else {
                        Toast.makeText(this@AgregarTransaccionActividad, "Error al cargar cuentas", Toast.LENGTH_SHORT).show()
                    }

                    // Manejar respuesta de categorías
                    if (categoriasResponse.isSuccessful) {
                        val categorias = categoriasResponse.body()?.listaCategorias ?: emptyList()
                        categoriaAdapter.actualizarDatos(categorias)
                    } else {
                        Toast.makeText(this@AgregarTransaccionActividad, "Error al cargar categorías", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AgregarTransaccionActividad, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cargarCategorias() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerCategorias(idUsuario, tipoSeleccionado)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val categorias = response.body()?.listaCategorias ?: emptyList()
                        categoriaAdapter.actualizarDatos(categorias)
                        categoriaSeleccionada = null // Resetear selección al cambiar de tipo
                    }
                }
            } catch (e: Exception) { /* ... */ }
        }
    }

    private fun guardarTransaccion() {
        // Validación de datos
        val monto = montoEditText.text.toString().toDoubleOrNull()
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "Ingrese un monto válido", Toast.LENGTH_SHORT).show()
            return
        }

        val cuentaSeleccionada = listaCuentas.getOrNull(cuentasSpinner.selectedItemPosition)
        if (cuentaSeleccionada == null) {
            Toast.makeText(this, "Seleccione una cuenta", Toast.LENGTH_SHORT).show()
            return
        }

        if (categoriaSeleccionada == null) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show()
            return
        }

        if (fechaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Seleccione una fecha", Toast.LENGTH_SHORT).show()
            return
        }

        // Creamos el objeto para enviar a la API
        val transaccionParaCrear = TransaccionParaCrear(
            idCuenta = cuentaSeleccionada.idCuenta,
            idCategoria = categoriaSeleccionada!!.idCategoria,
            montoTransaccion = monto,
            descripcion = null, // Puedes añadir un campo para esto
            fechaTransaccion = fechaSeleccionada
        )

        // Llamada a la API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.agregarTransaccion(transaccionParaCrear)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AgregarTransaccionActividad, "Transacción guardada", Toast.LENGTH_SHORT).show()
                        finish() // Volver a la pantalla anterior (InicioActivity)
                    } else {
                        Toast.makeText(this@AgregarTransaccionActividad, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AgregarTransaccionActividad, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}