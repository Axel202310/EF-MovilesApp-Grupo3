package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.asipion.pfmoviles.databinding.ActivityAjustesBinding
import com.asipion.pfmoviles.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class AjustesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAjustesBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAjustesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbarYMenu()
        configurarListeners()
    }

    override fun onResume() {
        super.onResume()
        actualizarHeaderMenuLateral()
    }

    private fun configurarToolbarYMenu() {
        setSupportActionBar(binding.toolbarAjustes)
        val drawerLayout: DrawerLayout = binding.drawerLayoutAjustes
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolbarAjustes,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.toolbarAjustes.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navegacionLateralAjustes.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_inicio -> startActivity(Intent(this, InicioActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP })
                R.id.item_cuentas -> startActivity(Intent(this, CuentasActividad::class.java))
                R.id.item_graficos -> startActivity(Intent(this, GraficosActivity::class.java))
                R.id.item_categorias -> startActivity(Intent(this, MenuCategoriasActividad::class.java))
                R.id.item_ajustes -> Toast.makeText(this, "Ya estás en Ajustes", Toast.LENGTH_SHORT).show()
                R.id.item_cerrar_sesion -> cerrarSesion()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun configurarListeners() {
        binding.itemPersonalizacion.setOnClickListener {
            startActivity(Intent(this, PersonalizacionActivity::class.java))
        }

        binding.itemMapaSucursales.setOnClickListener {
            val intent = Intent(this, PuntosInteresActivity::class.java)
            startActivity(intent)
        }
    }

    private fun actualizarHeaderMenuLateral() {
        val idUsuario = getSharedPreferences("mis_prefs", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario == -1) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.obtenerCuentas(idUsuario)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val cuentas = response.body()?.listaCuentas ?: emptyList()
                        val headerView = binding.navegacionLateralAjustes.getHeaderView(0)
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