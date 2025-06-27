// --- Archivo: PagosHabitualesActividad.kt (Final y Completo con Eliminación) ---
package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.asipion.pfmoviles.databinding.ActividadPagosHabitualesBinding
import com.asipion.pfmoviles.model.PagoHabitual
import com.asipion.pfmoviles.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PagosHabitualesActividad : AppCompatActivity() {

    private lateinit var binding: ActividadPagosHabitualesBinding
    private lateinit var adaptador: AdaptadorPagoHabitual
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadPagosHabitualesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbarYMenu()
        configurarRecyclerView()
        configurarListeners()
    }

    override fun onResume() {
        super.onResume()
        cargarPagosHabituales()
        actualizarHeaderMenuLateral()
    }

    private fun configurarToolbarYMenu() {
        setSupportActionBar(binding.toolbarPagos)
        val drawerLayout: DrawerLayout = binding.drawerLayoutPagos
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolbarPagos,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.toolbarPagos.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navegacionLateralPagos.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_inicio -> {
                    startActivity(Intent(this, InicioActivity::class.java))
                    finish()
                }
                R.id.item_cuentas -> {
                    startActivity(Intent(this, CuentasActividad::class.java))
                    finish()
                }
                R.id.item_graficos -> {
                    startActivity(Intent(this, GraficosActivity::class.java))
                    finish()
                }
                R.id.item_categorias -> {
                    startActivity(Intent(this, MenuCategoriasActividad::class.java))
                    finish()
                }
                R.id.item_pagos_habituales -> {
                    Toast.makeText(this, "Ya estás en Pagos Habituales", Toast.LENGTH_SHORT).show()
                }
                R.id.item_ajustes -> {
                    Toast.makeText(this, "Ajustes (próximamente)", Toast.LENGTH_SHORT).show()
                }
                R.id.item_cerrar_sesion -> {
                    cerrarSesion()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    // --- CAMBIO CLAVE: Se añade la lógica de eliminación al adaptador ---
    private fun configurarRecyclerView() {
        adaptador = AdaptadorPagoHabitual(emptyList()) { pagoHabitual ->
            // El 'it' aquí es la función para eliminar que definimos en el adaptador
            mostrarDialogoEliminacion(pagoHabitual)
        }

        binding.recyclerViewPagos.adapter = adaptador
        binding.recyclerViewPagos.layoutManager = LinearLayoutManager(this)
    }

    private fun configurarListeners() {
        binding.fabAddPago.setOnClickListener {
            // Renombramos la Activity para más claridad en el futuro.
            startActivity(Intent(this, FormularioPagoHabitualActivity::class.java))
        }
    }

    private fun cargarPagosHabituales() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) {
            cerrarSesion()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerPagosHabituales(idUsuario)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val pagos = response.body()?.pagosHabituales ?: emptyList()
                        adaptador.actualizarDatos(pagos)
                        binding.textEmptyPagos.visibility = if (pagos.isEmpty()) View.VISIBLE else View.GONE
                    } else {
                        Toast.makeText(this@PagosHabitualesActividad, "Error al cargar los pagos habituales", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PagosHabitualesActividad, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // --- NUEVA FUNCIÓN: Muestra el diálogo de confirmación para eliminar ---
    private fun mostrarDialogoEliminacion(pago: PagoHabitual) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Pago Habitual")
            .setMessage("¿Estás seguro de que deseas eliminar el pago '${pago.nombrePago}'? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarPagoEnServidor(pago.idPagoHabitual)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // --- NUEVA FUNCIÓN: Llama a la API para eliminar el registro ---
    private fun eliminarPagoEnServidor(idPago: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.eliminarPagoHabitual(idPago)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PagosHabitualesActividad, response.body()?.mensaje, Toast.LENGTH_SHORT).show()
                    if (response.isSuccessful) {
                        // Si se eliminó con éxito, recargamos la lista para que desaparezca.
                        cargarPagosHabituales()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PagosHabitualesActividad, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // --- FUNCIÓN AÑADIDA Y COMPLETADA ---
    private fun actualizarHeaderMenuLateral() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerCuentas(idUsuario)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val cuentas = response.body()?.listaCuentas ?: emptyList()
                        val headerView = binding.navegacionLateralPagos.getHeaderView(0)
                        val textCorreo = headerView.findViewById<TextView>(R.id.textViewCorreoUsuario)
                        val textSaldo = headerView.findViewById<TextView>(R.id.textViewSaldoMenu)

                        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
                        val correoUsuario = prefs.getString("correo_usuario", "N/A")
                        val balanceTotal = cuentas.sumOf { it.saldoActual }
                        val monedaPrincipal = cuentas.firstOrNull()?.moneda ?: "PEN"

                        textCorreo.text = correoUsuario
                        textSaldo.text = "Balance: ${String.format(Locale.US, "%.2f", balanceTotal)} $monedaPrincipal"
                    }
                }
            } catch (e: Exception) {
                // No mostramos un Toast para no ser intrusivos
            }
        }
    }

    private fun cerrarSesion() {
        getSharedPreferences("mis_prefs", MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, BienvenidaActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}