package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.asipion.pfmoviles.databinding.MenuCategoriasBinding
import com.asipion.pfmoviles.model.Categoria
import com.asipion.pfmoviles.servicio.RetrofitClient
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MenuCategoriasActividad : AppCompatActivity() {

    private lateinit var binding: MenuCategoriasBinding
    private lateinit var adaptadorGastos: AdaptadorCategoria
    private lateinit var adaptadorIngresos: AdaptadorCategoria
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuCategoriasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbarYMenu()
        configurarRecyclerViews()
        configurarListeners()
        configurarTabs()
    }

    override fun onResume() {
        super.onResume()
        cargarCategorias()
        actualizarHeaderMenuLateral()
    }

    private fun configurarRecyclerViews() {
        // Adaptador para la lista de categorías de GASTO
        adaptadorGastos = AdaptadorCategoria(emptyList()) { categoria ->
            manejarClicCategoria(categoria)
        }
        binding.recyclerViewGastos.apply {
            adapter = adaptadorGastos
            layoutManager = GridLayoutManager(this@MenuCategoriasActividad, 4)
        }

        // Adaptador para la lista de categorías de INGRESO
        adaptadorIngresos = AdaptadorCategoria(emptyList()) { categoria ->
            manejarClicCategoria(categoria)
        }
        binding.recyclerViewIngresos.apply {
            adapter = adaptadorIngresos
            layoutManager = GridLayoutManager(this@MenuCategoriasActividad, 4)
        }
    }

    private fun configurarListeners() {

        binding.fabAddCategoria.setOnClickListener {
            // Lanzamos el formulario sin pasarle ningún extra.
            // FormularioCategoriaActivity sabrá que está en modo "Creación".
            startActivity(Intent(this, FormularioCategoriaActivity::class.java))
        }
    }

    private fun configurarTabs() {
        binding.tabLayoutTipoCategoria.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Mostramos un RecyclerView y ocultamos el otro según la pestaña seleccionada.
                if (tab?.position == 0) {
                    binding.recyclerViewGastos.visibility = View.VISIBLE
                    binding.recyclerViewIngresos.visibility = View.GONE
                } else {
                    binding.recyclerViewGastos.visibility = View.GONE
                    binding.recyclerViewIngresos.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun cargarCategorias() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) {
            cerrarSesion()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Hacemos ambas llamadas a la API en paralelo para ser más eficientes.
                val responseGastos = RetrofitClient.webService.obtenerCategorias(idUsuario, "gasto")
                val responseIngresos = RetrofitClient.webService.obtenerCategorias(idUsuario, "ingreso")

                withContext(Dispatchers.Main) {
                    if (responseGastos.isSuccessful) {
                        val categoriasGastos = responseGastos.body()?.listaCategorias ?: emptyList()
                        adaptadorGastos.actualizarDatos(categoriasGastos)
                    } else {
                        Toast.makeText(this@MenuCategoriasActividad, "Error al cargar categorías de gasto", Toast.LENGTH_SHORT).show()
                    }

                    if (responseIngresos.isSuccessful) {
                        val categoriasIngresos = responseIngresos.body()?.listaCategorias ?: emptyList()
                        adaptadorIngresos.actualizarDatos(categoriasIngresos)
                    } else {
                        Toast.makeText(this@MenuCategoriasActividad, "Error al cargar categorías de ingreso", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MenuCategoriasActividad, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // --- En MenuCategoriasActividad.kt ---
    private fun manejarClicCategoria(categoria: Categoria) {
        // Solo las categorías con un idUsuario (las personalizadas) se pueden editar.
        if (categoria.idUsuario != null) {
            // Creamos el Intent para abrir el formulario en modo EDICIÓN.
            val intent = Intent(this, FormularioCategoriaActivity::class.java)
            // Pasamos el ID para que la siguiente pantalla sepa qué categoría cargar.
            intent.putExtra("CATEGORIA_ID", categoria.idCategoria)
            startActivity(intent)
        } else {
            // Las categorías por defecto no son editables.
            Toast.makeText(this, "Las categorías por defecto no se pueden modificar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarToolbarYMenu() {
        setSupportActionBar(binding.toolbarCategorias)
        val drawerLayout: DrawerLayout = binding.drawerLayoutCategorias
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolbarCategorias,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.toolbarCategorias.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navegacionLateralCategorias.setNavigationItemSelectedListener { item ->
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
                }
                R.id.item_categorias -> {
                    Toast.makeText(this, "Ya estás en Categorías", Toast.LENGTH_SHORT).show()
                }
                R.id.item_pagos_habituales -> {
                    startActivity(Intent(this, PagosHabitualesActividad::class.java))
                    finish()
                }
                R.id.item_ajustes -> {
                    startActivity(Intent(this, AjustesActivity::class.java))
                    finish()
                }
                R.id.item_cerrar_sesion -> {
                    cerrarSesion()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    // --- FUNCIÓN AÑADIDA ---
    // Esta función se encarga de obtener los datos y actualizar el header del menú.
    private fun actualizarHeaderMenuLateral() {
        val headerView = binding.navegacionLateralCategorias.getHeaderView(0)
        val textCorreo = headerView.findViewById<TextView>(R.id.textViewCorreoUsuario)
        val textSaldo = headerView.findViewById<TextView>(R.id.textViewSaldoMenu)

        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val correoUsuario = prefs.getString("correo_usuario", "N/A")
        val idUsuario = prefs.getInt("id_usuario", -1)

        textCorreo.text = correoUsuario

        if (idUsuario != -1) {
            // Hacemos una llamada a la API para obtener el balance total actualizado.
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.webService.obtenerCuentas(idUsuario)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val cuentas = response.body()?.listaCuentas ?: emptyList()
                            val balanceTotal = cuentas.sumOf { it.saldoActual }
                            val monedaPrincipal = cuentas.firstOrNull()?.moneda ?: "PEN"
                            textSaldo.text = "Balance: ${String.format(Locale.US, "%.2f", balanceTotal)} $monedaPrincipal"
                        } else {
                            textSaldo.text = "Balance: N/A"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        textSaldo.text = "Balance: Error"
                    }
                }
            }
        } else {
            textSaldo.text = "Balance: N/A"
        }

        headerView.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            binding.drawerLayoutCategorias.closeDrawer(GravityCompat.START)
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