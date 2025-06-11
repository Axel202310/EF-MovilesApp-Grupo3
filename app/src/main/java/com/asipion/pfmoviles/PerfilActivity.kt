package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.model.Usuario
import com.asipion.pfmoviles.servicio.RetrofitClient
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilActivity : AppCompatActivity() {

    private lateinit var correoTextView: TextView
    private lateinit var idUsuarioTextView: TextView
    private lateinit var modificarContrasenaTextView: TextView
    private lateinit var cerrarSesionTextView: TextView
    private lateinit var botonEliminar: ImageButton
    private lateinit var iconoEditarCorreo: ImageView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_perfil)

        // Toolbar y menú lateral
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBarPerfil)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout_perfil)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navView = findViewById<NavigationView>(R.id.navegacion_lateral_perfil)
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_inicio -> {
                    startActivity(Intent(this, InicioActivity::class.java))
                    finish()
                }
                R.id.item_cerrar_sesion -> {
                    getSharedPreferences("mis_prefs", MODE_PRIVATE).edit().clear().apply()
                    startActivity(Intent(this, BienvenidaActividad::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Referencias UI
        correoTextView = findViewById(R.id.email_value)
        idUsuarioTextView = findViewById(R.id.user_id)
        modificarContrasenaTextView = findViewById(R.id.password_modify)
        cerrarSesionTextView = findViewById(R.id.logout_button)
        botonEliminar = findViewById(R.id.delete_button)


        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val correo = prefs.getString("correo_usuario", "usuario@example.com") ?: "usuario@example.com"
        val idUsuario = prefs.getInt("id_usuario", -1)

        correoTextView.text = correo
        idUsuarioTextView.text = "ID: $idUsuario"

        iconoEditarCorreo.setOnClickListener {
            Toast.makeText(this, "Editar correo (no implementado)", Toast.LENGTH_SHORT).show()
        }

        modificarContrasenaTextView.setOnClickListener {
            mostrarDialogoModificarContrasena(correo, idUsuario)
        }

        cerrarSesionTextView.setOnClickListener {
            prefs.edit().clear().apply()
            startActivity(Intent(this, IniciarSesionCorreoActividad::class.java))
            finish()
        }

        botonEliminar.setOnClickListener {
            mostrarDialogoEliminarPerfil(idUsuario)
        }
    }

    private fun mostrarDialogoEliminarPerfil(idUsuario: Int) {
        val vista = LayoutInflater.from(this).inflate(R.layout.dialog_eliminar_perfil, null)
        val dialogo = AlertDialog.Builder(this)
            .setView(vista)
            .setCancelable(false)
            .create()

        val btnCancelar = vista.findViewById<Button>(R.id.btn_cancelar)
        val btnConfirmar = vista.findViewById<Button>(R.id.btn_confirmar)

        btnCancelar.setOnClickListener { dialogo.dismiss() }

        btnConfirmar.setOnClickListener {
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
                    if (response.isSuccessful) {
                        Toast.makeText(this@PerfilActivity, "Perfil eliminado correctamente", Toast.LENGTH_LONG).show()
                        getSharedPreferences("mis_prefs", MODE_PRIVATE).edit().clear().apply()
                        startActivity(Intent(this@PerfilActivity, BienvenidaActividad::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@PerfilActivity, "Error al eliminar perfil", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarDialogoModificarContrasena(correo: String, idUsuario: Int) {
        val vista = LayoutInflater.from(this).inflate(R.layout.dialogo_modificar_contrasena, null)
        val campoNuevaContrasena = vista.findViewById<EditText>(R.id.edit_nueva_contrasena)
        val btnCancelar = vista.findViewById<Button>(R.id.btn_cancelar)
        val btnGuardar = vista.findViewById<Button>(R.id.btn_guardar)

        val dialogo = AlertDialog.Builder(this)
            .setView(vista)
            .setCancelable(false)
            .create()

        btnCancelar.setOnClickListener { dialogo.dismiss() }

        btnGuardar.setOnClickListener {
            val nuevaContrasena = campoNuevaContrasena.text.toString().trim()
            if (nuevaContrasena.isEmpty()) {
                campoNuevaContrasena.error = "La contraseña no puede estar vacía"
                return@setOnClickListener
            }

            dialogo.dismiss()
            actualizarContrasenaEnServidor(correo, idUsuario, nuevaContrasena)
        }

        dialogo.show()
    }

    private fun actualizarContrasenaEnServidor(correo: String, idUsuario: Int, nuevaContrasena: String) {
        val usuario = Usuario(id_usuario = idUsuario, correo_usuario = correo, contra_usuario = nuevaContrasena)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respuesta = RetrofitClient.webService.modificarUsuario(usuario)
                withContext(Dispatchers.Main) {
                    if (respuesta.isSuccessful) {
                        Toast.makeText(this@PerfilActivity, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@PerfilActivity, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
