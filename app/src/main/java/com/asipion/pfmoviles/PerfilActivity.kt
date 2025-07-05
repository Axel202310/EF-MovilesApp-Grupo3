package com.asipion.pfmoviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.asipion.pfmoviles.databinding.ActivityPerfilBinding
import com.asipion.pfmoviles.model.UsuarioActualizarPassword
import com.asipion.pfmoviles.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbarYMenu()
        mostrarDatosUsuario()
        configurarListeners()
    }

    override fun onResume() {
        super.onResume()
        // Actualizamos el header por si algún dato (como el balance) cambió.
        actualizarHeaderMenuLateral()
    }

    private fun configurarToolbarYMenu() {
        setSupportActionBar(binding.topAppBarPerfil)
        val drawerLayout: DrawerLayout = binding.drawerLayoutPerfil
        drawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.topAppBarPerfil,
            R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.topAppBarPerfil.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navegacionLateralPerfil.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_inicio -> startActivity(Intent(this, InicioActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP })
                R.id.item_cuentas -> startActivity(Intent(this, CuentasActividad::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP })
                R.id.item_graficos -> startActivity(Intent(this, GraficosActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP })
                R.id.item_categorias -> startActivity(Intent(this, MenuCategoriasActividad::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP })
                R.id.item_ajustes -> startActivity(Intent(this, AjustesActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP })
                R.id.item_cerrar_sesion -> cerrarSesionCompleto()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun mostrarDatosUsuario() {
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val correo = prefs.getString("correo_usuario", "N/A")
        val idUsuario = prefs.getInt("id_usuario", -1)

        binding.emailValue.text = correo
        binding.userId.text = "ID: $idUsuario"
    }

    private fun configurarListeners() {
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("id_usuario", -1)

        binding.passwordModify.setOnClickListener {
            if (idUsuario != -1) {
                mostrarDialogoModificarContrasena(idUsuario)
            }
        }

        binding.deleteButton.setOnClickListener {
            if (idUsuario != -1) {
                mostrarDialogoEliminarPerfil(idUsuario)
            }
        }

        binding.logoutButton.setOnClickListener {
            cerrarSesionCompleto()
        }
    }

    private fun mostrarDialogoModificarContrasena(idUsuario: Int) {
        val vistaDialogo = LayoutInflater.from(this).inflate(R.layout.dialog_modificar_contrasena, null)
        val campoNuevaContrasena = vistaDialogo.findViewById<EditText>(R.id.edit_nueva_contrasena)

        val dialogo = AlertDialog.Builder(this)
            .setView(vistaDialogo)
            .create()

        vistaDialogo.findViewById<Button>(R.id.btn_cancelar).setOnClickListener {
            dialogo.dismiss()
        }

        vistaDialogo.findViewById<Button>(R.id.btn_guardar).setOnClickListener {
            val nuevaContrasena = campoNuevaContrasena.text.toString().trim()
            if (nuevaContrasena.length < 6) {
                campoNuevaContrasena.error = "La contraseña debe tener al menos 6 caracteres"
                return@setOnClickListener
            }
            dialogo.dismiss()
            actualizarContrasenaEnServidor(idUsuario, nuevaContrasena)
        }
        dialogo.show()
    }

    private fun actualizarContrasenaEnServidor(idUsuario: Int, nuevaContrasena: String) {
        val datosParaActualizar = UsuarioActualizarPassword(idUsuario = idUsuario, nuevaContrasena = nuevaContrasena)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respuesta = RetrofitClient.webService.modificarContrasena(datosParaActualizar)
                withContext(Dispatchers.Main) {
                    val mensaje = respuesta.body()?.mensaje ?: "Error desconocido"
                    Toast.makeText(this@PerfilActivity, mensaje, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarDialogoEliminarPerfil(idUsuario: Int) {
        val vistaDialogo = LayoutInflater.from(this).inflate(R.layout.dialog_eliminar_perfil, null)
        val dialogo = AlertDialog.Builder(this)
            .setView(vistaDialogo)
            .create()

        vistaDialogo.findViewById<Button>(R.id.btn_cancelar).setOnClickListener { dialogo.dismiss() }
        vistaDialogo.findViewById<Button>(R.id.btn_confirmar).setOnClickListener {
            dialogo.dismiss()
            eliminarPerfilDelServidor(idUsuario)
        }
        dialogo.show()
    }

    private fun eliminarPerfilDelServidor(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.eliminarUsuario(idUsuario)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilActivity, response.body()?.mensaje, Toast.LENGTH_LONG).show()
                    if (response.isSuccessful) {
                        cerrarSesionCompleto()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
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
                        val headerView = binding.navegacionLateralPerfil.getHeaderView(0)
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
                // No mostramos Toast para no interrumpir al usuario.
            }
        }
    }

    private fun cerrarSesionCompleto() {
        getSharedPreferences("mis_prefs", MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, BienvenidaActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}