// --- Archivo: FormularioPagoHabitualActivity.kt ---
package com.asipion.pfmoviles

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActividadCrearPagoHabitualBinding
import com.asipion.pfmoviles.model.Categoria
import com.asipion.pfmoviles.model.Cuenta
import com.asipion.pfmoviles.model.PagoHabitual
import com.asipion.pfmoviles.servicio.RetrofitClient
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// NOTA: El nombre de la clase es el que tú definiste, aunque el layout sea 'actividad_crear_recordatorio'
class FormularioPagoHabitualActivity : AppCompatActivity() {

    private lateinit var binding: ActividadCrearPagoHabitualBinding

    // Listas para los datos de la API
    private var listaCuentas: List<Cuenta> = emptyList()
    private var listaCategorias: List<Categoria> = emptyList()

    // Estado del formulario
    private var cuentaSeleccionada: Cuenta? = null
    private var categoriaSeleccionada: Categoria? = null
    private var frecuenciaSeleccionada: String = "mensual"
    private var fechaInicioSeleccionada: String = ""
    private var tipoTransaccion = "gasto" // Por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadCrearPagoHabitualBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Asignamos el título correcto a la Toolbar
        binding.toolbarFormularioPago.title = "Nuevo Pago Habitual"

        configurarListeners()
        cargarDatosIniciales()

        // Establecemos la fecha de inicio por defecto a hoy
        actualizarFechaInicio(Calendar.getInstance())
    }

    private fun configurarListeners() {
        binding.toolbarFormularioPago.setNavigationOnClickListener { finish() }

        binding.tabLayoutTipo.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tipoTransaccion = if (tab?.position == 0) "gasto" else "ingreso"
                cargarCategorias()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.tvFrecuenciaValue.setOnClickListener { mostrarDialogoFrecuencia() }
        binding.tvStartDateValue.setOnClickListener { mostrarDialogoFechaInicio() }
        binding.tvAccountValue.setOnClickListener { mostrarDialogoSeleccion(listaCuentas.map { it.nombreCuenta }, "cuenta") }
        binding.tvCategoryValue.setOnClickListener { mostrarDialogoSeleccion(listaCategorias.map { it.nombreCategoria }, "categoria") }

        binding.btnCrear.setOnClickListener { guardarPagoHabitual() }
    }

    private fun cargarDatosIniciales() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) { return }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val responseCuentas = RetrofitClient.webService.obtenerCuentas(idUsuario)
                if (responseCuentas.isSuccessful) {
                    listaCuentas = responseCuentas.body()?.listaCuentas ?: emptyList()
                }
                // Cargamos las categorías de gasto por defecto después de obtener las cuentas
                withContext(Dispatchers.Main) {
                    cargarCategorias()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { Toast.makeText(this@FormularioPagoHabitualActivity, "Error de red", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun cargarCategorias() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) { return }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val responseCategorias = RetrofitClient.webService.obtenerCategorias(idUsuario, tipoTransaccion)
                withContext(Dispatchers.Main) {
                    if (responseCategorias.isSuccessful) {
                        listaCategorias = responseCategorias.body()?.listaCategorias ?: emptyList()
                        categoriaSeleccionada = null // Reseteamos la selección
                        binding.tvCategoryValue.text = "Seleccionar categoría"
                        binding.tvCategoryValue.setTextColor(getColor(R.color.rojo_gasto))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { Toast.makeText(this@FormularioPagoHabitualActivity, "Error al cargar categorías", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun mostrarDialogoFrecuencia() {
        val frecuencias = arrayOf("diaria", "semanal", "quincenal", "mensual")
        AlertDialog.Builder(this)
            .setTitle("Seleccionar Frecuencia")
            .setItems(frecuencias) { _, which ->
                frecuenciaSeleccionada = frecuencias[which]
                binding.tvFrecuenciaValue.text = frecuenciaSeleccionada.replaceFirstChar { it.uppercase() }
            }
            .show()
    }

    private fun mostrarDialogoFechaInicio() {
        val datePicker = DatePickerFragment { day, month, year ->
            val calendar = Calendar.getInstance().apply { set(year, month, day) }
            actualizarFechaInicio(calendar)
        }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun actualizarFechaInicio(calendar: Calendar) {
        val formatoApi = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        fechaInicioSeleccionada = formatoApi.format(calendar.time)
        val formatoVista = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
        binding.tvStartDateValue.text = formatoVista.format(calendar.time)
    }

    private fun mostrarDialogoSeleccion(items: List<String>, tipo: String) {
        val titulo = "Seleccionar ${tipo.replaceFirstChar { it.uppercase() }}"
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setItems(items.toTypedArray()) { _, which ->
                if (tipo == "cuenta") {
                    cuentaSeleccionada = listaCuentas[which]
                    binding.tvAccountValue.text = cuentaSeleccionada?.nombreCuenta
                    binding.tvAccountValue.setTextColor(getColor(android.R.color.white))
                } else {
                    categoriaSeleccionada = listaCategorias[which]
                    binding.tvCategoryValue.text = categoriaSeleccionada?.nombreCategoria
                    binding.tvCategoryValue.setTextColor(getColor(android.R.color.white))
                }
            }
            .show()
    }

    private fun guardarPagoHabitual() {
        val nombre = binding.etNombrePago.text.toString().trim()
        val monto = binding.etCantidad.text.toString().toDoubleOrNull()
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)

        // Validaciones
        if (nombre.isEmpty() || monto == null || monto <= 0 || cuentaSeleccionada == null || categoriaSeleccionada == null || fechaInicioSeleccionada.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos requeridos", Toast.LENGTH_LONG).show()
            return
        }

        val pagoHabitual = PagoHabitual(
            idPagoHabitual = 0,
            idUsuario = idUsuario,
            idCuentaOrigen = cuentaSeleccionada!!.idCuenta,
            idCategoria = categoriaSeleccionada!!.idCategoria,
            nombrePago = nombre,
            montoPago = monto,
            frecuencia = frecuenciaSeleccionada,
            fechaInicio = fechaInicioSeleccionada,
            nombreCuenta = "",
            nombreCategoria = ""
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.agregarPagoHabitual(pagoHabitual)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormularioPagoHabitualActivity, "Pago habitual guardado", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@FormularioPagoHabitualActivity, "Error al guardar el pago", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FormularioPagoHabitualActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}