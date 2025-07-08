package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.asipion.pfmoviles.databinding.ActivityHistorialTransferenciasBinding
import com.asipion.pfmoviles.model.HistorialTransferencia
import com.asipion.pfmoviles.servicio.RetrofitClient
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistorialTransferenciasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialTransferenciasBinding
    private lateinit var adaptadorHistorial: AdaptadorHistorial
    private var todoElHistorial: List<HistorialTransferencia> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistorialTransferenciasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbar()
        configurarRecyclerView()
        configurarListeners()
        configurarTabs()
    }

    override fun onResume() {
        super.onResume()
        cargarHistorialDesdeApi()
    }

    private fun configurarToolbar() {
        binding.toolbarHistorial.setNavigationOnClickListener { finish() }
    }

    private fun configurarRecyclerView() {
        adaptadorHistorial = AdaptadorHistorial(emptyList())
        binding.recyclerViewHistorial.apply {
            adapter = adaptadorHistorial
            layoutManager = LinearLayoutManager(this@HistorialTransferenciasActivity)
        }
    }

    private fun configurarListeners() {
        binding.fabAddTransferencia.setOnClickListener {
            startActivity(Intent(this, TransferenciaActividad::class.java))
        }
    }

    private fun configurarTabs() {
        binding.tabLayoutPeriodoHistorial.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                filtrarYActualizarLista()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun cargarHistorialDesdeApi() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) { /* ... */ return }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerHistorialTransferencias(idUsuario)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        todoElHistorial = response.body()?.historial ?: emptyList()
                        // Seleccionamos la pestaña "Mes" por defecto para que coincida con tu prototipo.
                        binding.tabLayoutPeriodoHistorial.getTabAt(2)?.select()
                        filtrarYActualizarLista()
                    } else { /* ... */ }
                }
            } catch (e: Exception) { /* ... */ }
        }
    }

    private fun filtrarYActualizarLista() {
        val periodoSeleccionado = binding.tabLayoutPeriodoHistorial.selectedTabPosition
        // Usamos un calendario de referencia "ahora" para cada filtrado.
        val calReferencia = Calendar.getInstance()

        val historialFiltrado = todoElHistorial.filter { item ->
            if (item.fechaTransferencia.isNullOrEmpty()) return@filter false

            try {
                val formatoApi = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                val fechaTransferencia = formatoApi.parse(item.fechaTransferencia) ?: return@filter false
                val calTransferencia = Calendar.getInstance().apply { time = fechaTransferencia }

                when (periodoSeleccionado) {
                    0 -> esMismoDia(calTransferencia, calReferencia)
                    1 -> esMismaSemana(calTransferencia, calReferencia)
                    2 -> esMismoMes(calTransferencia, calReferencia)
                    3 -> esMismoAnio(calTransferencia, calReferencia)
                    else -> true
                }
            } catch (e: Exception) {
                // Si falla el primer formato, intentamos con el formato ISO
                try {
                    val formatoApiISO = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    val fechaTransferencia = formatoApiISO.parse(item.fechaTransferencia) ?: return@filter false
                    val calTransferencia = Calendar.getInstance().apply { time = fechaTransferencia }
                    // Volvemos a aplicar la lógica de filtrado
                    when (periodoSeleccionado) {
                        0 -> esMismoDia(calTransferencia, calReferencia)
                        1 -> esMismaSemana(calTransferencia, calReferencia)
                        2 -> esMismoMes(calTransferencia, calReferencia)
                        3 -> esMismoAnio(calTransferencia, calReferencia)
                        else -> true
                    }
                } catch (e2: Exception) {
                    Log.e("ParseoHistorial", "Formato de fecha no reconocido: ${item.fechaTransferencia}", e2)
                    false
                }
            }
        }

        adaptadorHistorial.actualizarDatos(historialFiltrado)
        mostrarEstadoVacio(historialFiltrado.isEmpty())
    }

    private fun mostrarEstadoVacio(estaVacio: Boolean) {
        binding.recyclerViewHistorial.visibility = if (estaVacio) View.GONE else View.VISIBLE
        binding.textViewEmptyHistorial.visibility = if (estaVacio) View.VISIBLE else View.GONE
    }

    private fun esMismoDia(cal1: Calendar, cal2: Calendar): Boolean = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    private fun esMismaSemana(cal1: Calendar, cal2: Calendar): Boolean = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
    private fun esMismoMes(cal1: Calendar, cal2: Calendar): Boolean = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    private fun esMismoAnio(cal1: Calendar, cal2: Calendar): Boolean = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
}