package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActividadCuentasBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class CuentasActividad : AppCompatActivity() {

    private lateinit var binding: ActividadCuentasBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadCuentasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbarYMenu()
        actualizarHeaderMenuLateral()
        mostrarBalanceTotal()
        mostrarSaldoCuentaPrincipal()

        binding.layoutNuevaTransferencia.setOnClickListener {
            startActivity(Intent(this, TransferenciaActividad::class.java))
        }

        binding.fabAddCuenta.setOnClickListener {
            startActivity(Intent(this, AgregarCuentaActividad::class.java))
        }

    }

    private fun configurarToolbarYMenu() {
        setSupportActionBar(binding.topAppBar)

        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.topAppBar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }

        // NUEVO: Manejo del menú lateral
        binding.navegacionLateral.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_inicio -> {
                    Toast.makeText(this, "Ya estás en Inicio", Toast.LENGTH_SHORT).show()
                }
                R.id.item_cuentas -> {
                    Toast.makeText(this, "Ya estás en Cuentas", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, CuentasActividad::class.java))
                }
                R.id.item_graficos -> {
                    Toast.makeText(this, "Ya estás en Graficos", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, GraficosActivity::class.java))
                }
                R.id.item_categorias -> {
                    Toast.makeText(this, "Ya estás en Categorias", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MenuCategoriasActividad::class.java))
                }
                R.id.item_pagos_habituales -> {
                    Toast.makeText(this, "Ya estás en Pagos Habituales", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, PagosHabitualesActividad::class.java))
                }
                R.id.item_recordatorios -> {
                    startActivity(Intent(this, RecordatoriosActividad::class.java))
                }
                R.id.item_ajustes -> {
                    Toast.makeText(this, "Ya estás en Ajustes", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AjustesActividad::class.java))
                }
                R.id.item_cerrar_sesion -> {
                    Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, BienvenidaActividad::class.java))
                    finish()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun actualizarHeaderMenuLateral() {
        val headerView = binding.navegacionLateral.getHeaderView(0)
        val textCorreo = headerView.findViewById<TextView>(R.id.textViewCorreoUsuario)
        val textSaldo = headerView.findViewById<TextView>(R.id.textViewSaldoMenu)

        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("id_usuario", -1)
        val correoUsuario = prefs.getString("correo_usuario", "usuario@example.com") ?: "usuario@example.com"
        val moneda = prefs.getString("moneda_$idUsuario", "PEN") ?: "PEN"
        val monto = prefs.getFloat("monto_$idUsuario", 0.0f)

        textCorreo.text = correoUsuario
        textSaldo.text = "Balance: %.2f %s".format(monto, moneda)
    }

    private fun mostrarBalanceTotal() {
        val monto = obtenerMonto()
        val moneda = obtenerMoneda()
        val formato = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("es", "ES")))
        binding.tvTotalValue.text = "${formato.format(monto)} $moneda"
    }

    private fun mostrarSaldoCuentaPrincipal() {
        val monto = obtenerMonto()
        val moneda = obtenerMoneda()
        val formato = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("es", "ES")))
        binding.tvCuentaSaldo.text = "${formato.format(monto)} $moneda"
    }

    private fun obtenerIdUsuario(): Int {
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        return prefs.getInt("id_usuario", -1)
    }

    private fun obtenerMoneda(): String {
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val idUsuario = obtenerIdUsuario()
        return prefs.getString("moneda_$idUsuario", "PEN") ?: "PEN"
    }

    private fun obtenerMonto(): Float {
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val idUsuario = obtenerIdUsuario()
        return prefs.getFloat("monto_$idUsuario", 0.0f)
    }


}
