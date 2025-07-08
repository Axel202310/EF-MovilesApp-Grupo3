// --- Archivo: PersonalizacionActivity.kt  ---
package com.asipion.pfmoviles

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActivityPersonalizacionBinding
import com.asipion.pfmoviles.model.Cuenta
import com.asipion.pfmoviles.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonalizacionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalizacionBinding
    private lateinit var prefs: SharedPreferences
    private var listaCuentas: List<Cuenta> = emptyList()

    companion object {
        const val PREFS_NAME = "ajustes_prefs"
        const val KEY_VISTA_DEFECTO = "vista_defecto"
        const val KEY_PERIODO_DEFECTO_INDEX = "periodo_defecto_index" // Guardaremos el índice
        const val KEY_CUENTA_DEFECTO_ID = "cuenta_defecto_id"
        const val KEY_CUENTA_DEFECTO_NOMBRE = "cuenta_defecto_nombre"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalizacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        configurarListeners()
        cargarDatosDeCuentas()
    }

    private fun configurarListeners() {
        binding.toolbarPersonalizacion.setNavigationOnClickListener { finish() }

        binding.layoutMostrarPorDefecto.setOnClickListener { mostrarDialogoVistaDefecto() }
        binding.layoutPeriodoPorDefecto.setOnClickListener { mostrarDialogoPeriodoDefecto() }
        binding.layoutCuentaPorDefecto.setOnClickListener { mostrarDialogoCuentaDefecto() }
    }

    private fun cargarDatosDeCuentas() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerCuentas(idUsuario)
                if(response.isSuccessful) {
                    listaCuentas = response.body()?.listaCuentas ?: emptyList()
                    withContext(Dispatchers.Main) {
                        cargarPreferenciasGuardadas()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PersonalizacionActivity, "Error al cargar cuentas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cargarPreferenciasGuardadas() {
        // Vista por defecto
        val vistaGuardada = prefs.getString(KEY_VISTA_DEFECTO, "Balance Total")
        binding.tvMostrarValor.text = vistaGuardada

        // Período por defecto
        val periodos = arrayOf("Día", "Semana", "Mes", "Año")
        val periodoIndexGuardado = prefs.getInt(KEY_PERIODO_DEFECTO_INDEX, 2) // Por defecto "Mes" (índice 2)
        binding.tvPeriodoValor.text = periodos.getOrElse(periodoIndexGuardado) { "Mes" }

        // Cuenta por defecto
        val nombreCuentaGuardada = prefs.getString(KEY_CUENTA_DEFECTO_NOMBRE, "No seleccionada")
        binding.tvCuentaValor.text = nombreCuentaGuardada
        if(nombreCuentaGuardada == "No seleccionada") {
            binding.tvCuentaValor.setTextColor(getColor(R.color.rojo_gasto))
        } else {
            binding.tvCuentaValor.setTextColor(getColor(R.color.accent_orange))
        }
    }

    private fun mostrarDialogoVistaDefecto() {
        val opciones = mutableListOf("Balance Total")
        opciones.addAll(listaCuentas.map { it.nombreCuenta })

        AlertDialog.Builder(this)
            .setTitle("Mostrar por defecto en Inicio")
            .setItems(opciones.toTypedArray()) { _, which ->
                val seleccion = opciones[which]
                // Guardamos la preferencia y actualizamos la UI
                prefs.edit().putString(KEY_VISTA_DEFECTO, seleccion).apply()
                binding.tvMostrarValor.text = seleccion
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoPeriodoDefecto() {
        val opciones = arrayOf("Día", "Semana", "Mes", "Año")
        val indiceActual = prefs.getInt(KEY_PERIODO_DEFECTO_INDEX, 2)

        AlertDialog.Builder(this)
            .setTitle("Período por defecto en Inicio")
            .setSingleChoiceItems(opciones, indiceActual) { dialog, which ->
                val seleccion = opciones[which]
                // Guardamos el índice seleccionado
                prefs.edit().putInt(KEY_PERIODO_DEFECTO_INDEX, which).apply()
                binding.tvPeriodoValor.text = seleccion
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoCuentaDefecto() {
        if (listaCuentas.isEmpty()) {
            Toast.makeText(this, "No hay cuentas para seleccionar", Toast.LENGTH_SHORT).show()
            return
        }
        val nombresCuentas = listaCuentas.map { it.nombreCuenta }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Cuenta por defecto para añadir transacciones")
            .setItems(nombresCuentas) { _, which ->
                val cuentaSeleccionada = listaCuentas[which]
                // Guardamos tanto el ID como el nombre para facilidad
                prefs.edit().apply {
                    putInt(KEY_CUENTA_DEFECTO_ID, cuentaSeleccionada.idCuenta)
                    putString(KEY_CUENTA_DEFECTO_NOMBRE, cuentaSeleccionada.nombreCuenta)
                    apply()
                }
                // Actualizamos la UI
                binding.tvCuentaValor.text = cuentaSeleccionada.nombreCuenta
                binding.tvCuentaValor.setTextColor(getColor(R.color.accent_orange))
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}