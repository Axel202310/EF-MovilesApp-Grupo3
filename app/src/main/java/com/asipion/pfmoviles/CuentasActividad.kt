package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.asipion.pfmoviles.databinding.ActividadCuentasBinding
import com.asipion.pfmoviles.model.Cuenta
import com.asipion.pfmoviles.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.Locale

class CuentasActividad : AppCompatActivity() {

    private lateinit var binding: ActividadCuentasBinding
    private lateinit var adaptadorCuenta: AdaptadorCuenta
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadCuentasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbarYMenu()
        configurarRecyclerView()
        configurarListeners()
    }

    override fun onResume() {
        super.onResume()
        cargarCuentasDesdeApi()
    }

    private fun configurarRecyclerView() {
        adaptadorCuenta = AdaptadorCuenta(emptyList())
        binding.recyclerViewCuentas.apply {
            adapter = adaptadorCuenta
            layoutManager = LinearLayoutManager(this@CuentasActividad)
        }
    }

    private fun configurarListeners() {
        binding.fabAddCuenta.setOnClickListener {
            startActivity(Intent(this, AgregarCuentaActividad::class.java))
        }
        binding.layoutNuevaTransferencia.setOnClickListener {
            startActivity(Intent(this, TransferenciaActividad::class.java))
        }
        binding.layoutHistorial.setOnClickListener {
            startActivity(Intent(this, HistorialTransferenciasActivity::class.java))
        }
    }

    private fun cargarCuentasDesdeApi() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) {
            cerrarSesion()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerCuentas(idUsuario)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val cuentas = response.body()?.listaCuentas ?: emptyList()
                        adaptadorCuenta.actualizarDatos(cuentas)
                        actualizarBalanceTotal(cuentas)
                        actualizarHeaderMenuLateral(cuentas)
                    } else {
                        Toast.makeText(this@CuentasActividad, "Error al cargar las cuentas", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CuentasActividad, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun actualizarBalanceTotal(cuentas: List<Cuenta>) {
        val balanceTotal = cuentas.sumOf { it.saldoActual }
        val monedaPrincipal = cuentas.firstOrNull()?.moneda ?: "PEN"

        val formato = DecimalFormat("#,##0.00")
        binding.tvTotalValue.text = "${formato.format(balanceTotal)} $monedaPrincipal"
    }

    private fun configurarToolbarYMenu() {
        setSupportActionBar(binding.topAppBar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.topAppBar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.topAppBar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navegacionLateral.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_inicio -> {
                    startActivity(Intent(this, InicioActivity::class.java))
                    finish()
                }
                R.id.item_cuentas -> Toast.makeText(this, "Ya estás en Cuentas", Toast.LENGTH_SHORT).show()
                R.id.item_graficos -> startActivity(Intent(this, GraficosActivity::class.java))
                R.id.item_categorias -> startActivity(Intent(this, MenuCategoriasActividad::class.java))
                R.id.item_ajustes -> startActivity(Intent(this, AjustesActivity::class.java))
                R.id.item_cerrar_sesion -> cerrarSesion()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun actualizarHeaderMenuLateral(cuentas: List<Cuenta>) {
        val headerView = binding.navegacionLateral.getHeaderView(0)
        val textCorreo = headerView.findViewById<TextView>(R.id.textViewCorreoUsuario)
        val textSaldo = headerView.findViewById<TextView>(R.id.textViewSaldoMenu)

        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val correoUsuario = prefs.getString("correo_usuario", "N/A")

        val balanceTotal = cuentas.sumOf { it.saldoActual }
        val monedaPrincipal = cuentas.firstOrNull()?.moneda ?: "PEN"

        textCorreo.text = correoUsuario
        textSaldo.text = "Balance: ${String.format(Locale.US, "%.2f", balanceTotal)} $monedaPrincipal"

        headerView.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
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