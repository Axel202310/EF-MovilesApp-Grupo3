package com.asipion.pfmoviles

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
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
import java.util.Calendar
import java.util.Locale

class AgregarTransaccionActividad : AppCompatActivity() {

    private lateinit var tabLayoutTipo: TabLayout
    private lateinit var montoEditText: EditText
    private lateinit var cuentasSpinner: Spinner
    private lateinit var categoriasRecyclerView: RecyclerView
    private lateinit var fechaEditText: EditText
    private lateinit var btnAnadir: Button
    private lateinit var btnAtras: ImageButton

    private lateinit var categoriaAdapter: CategoriaAdapter
    private var listaCuentas: List<Cuenta> = emptyList()

    private var tipoSeleccionado = "gasto"
    private var cuentaSeleccionada: Cuenta? = null
    private var categoriaSeleccionada: Categoria? = null
    private var fechaSeleccionada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregartransaccion)

        inicializarVistas()
        configurarListeners()
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

        categoriaAdapter = CategoriaAdapter(emptyList()) { categoria ->
            categoriaSeleccionada = categoria
        }
        categoriasRecyclerView.adapter = categoriaAdapter
    }

    private fun configurarListeners() {
        btnAtras.setOnClickListener { finish() }

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
                val calendar = Calendar.getInstance().apply { set(year, month, day) }
                val formatoApi = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                fechaSeleccionada = formatoApi.format(calendar.time)
                val formatoVista = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
                fechaEditText.setText(formatoVista.format(calendar.time))
            }
            datePicker.show(supportFragmentManager, "selector_fecha")
        }

        btnAnadir.setOnClickListener {
            guardarTransaccion()
        }

        cuentasSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cuentaSeleccionada = listaCuentas.getOrNull(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                cuentaSeleccionada = null
            }
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
                val cuentasResponse = RetrofitClient.webService.obtenerCuentas(idUsuario)
                withContext(Dispatchers.Main) {
                    if (cuentasResponse.isSuccessful) {
                        listaCuentas = cuentasResponse.body()?.listaCuentas ?: emptyList()
                        val nombresCuentas = listaCuentas.map { it.nombreCuenta }

                        val spinnerAdapter = ArrayAdapter(
                            this@AgregarTransaccionActividad,
                            R.layout.spinner_item_cuenta,
                            nombresCuentas
                        )
                        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_cuenta)
                        cuentasSpinner.adapter = spinnerAdapter

                        val idCuentaDesdeInicio = intent.getIntExtra("ID_CUENTA", -1)
                        var posicionASeleccionar = -1

                        if (idCuentaDesdeInicio != -1) {
                            posicionASeleccionar = listaCuentas.indexOfFirst { it.idCuenta == idCuentaDesdeInicio }
                        } else {
                            val prefsAjustes = getSharedPreferences(PersonalizacionActivity.PREFS_NAME,
                                Context.MODE_PRIVATE)
                            val idCuentaDefecto = prefsAjustes.getInt(PersonalizacionActivity.KEY_CUENTA_DEFECTO_ID, -1)
                            if (idCuentaDefecto != -1) {
                                posicionASeleccionar = listaCuentas.indexOfFirst { it.idCuenta == idCuentaDefecto }
                            }
                        }

                        if (posicionASeleccionar != -1) {
                            cuentasSpinner.setSelection(posicionASeleccionar)
                        }

                    } else {
                        Toast.makeText(this@AgregarTransaccionActividad, "Error al cargar cuentas", Toast.LENGTH_SHORT).show()
                    }
                }
                cargarCategorias()
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
                        categoriaSeleccionada = null
                    }
                }
            } catch (e: Exception) { /* ... */ }
        }
    }

    private fun guardarTransaccion() {
        val monto = montoEditText.text.toString().toDoubleOrNull()
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "Ingrese un monto válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Ahora usamos la variable de la clase que se actualiza con el listener.
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

        val transaccionParaCrear = TransaccionParaCrear(
            idCuenta = cuentaSeleccionada!!.idCuenta,
            idCategoria = categoriaSeleccionada!!.idCategoria,
            montoTransaccion = monto,
            descripcion = null,
            fechaTransaccion = fechaSeleccionada
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.agregarTransaccion(transaccionParaCrear)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AgregarTransaccionActividad, "Transacción guardada", Toast.LENGTH_SHORT).show()
                        finish()
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