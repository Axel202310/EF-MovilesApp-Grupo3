// --- Archivo: PerfilActivity.kt (Corregido y Final) ---
package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.model.UsuarioActualizarPassword // Usamos el nuevo modelo
import com.asipion.pfmoviles.servicio.RetrofitClient
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
    // Eliminamos la referencia a iconoEditarCorreo, ya que no existe en el XML.

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_perfil)

        inicializarVistas()

        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val correo = prefs.getString("correo_usuario", "N/A")
        val idUsuario = prefs.getInt("id_usuario", -1)

        if (idUsuario == -1) {
            cerrarSesionCompleto()
            return
        }

        correoTextView.text = correo
        idUsuarioTextView.text = "ID: $idUsuario"

        configurarListeners(idUsuario)
    }

    private fun inicializarVistas() {
        // ... (Tu código para la Toolbar y el DrawerLayout es correcto y se mantiene) ...
        correoTextView = findViewById(R.id.email_value)
        idUsuarioTextView = findViewById(R.id.user_id)
        modificarContrasenaTextView = findViewById(R.id.password_modify)
        cerrarSesionTextView = findViewById(R.id.logout_button)
        botonEliminar = findViewById(R.id.delete_button)
    }

    private fun configurarListeners(idUsuario: Int) {
        modificarContrasenaTextView.setOnClickListener {
            mostrarDialogoModificarContrasena(idUsuario)
        }

        cerrarSesionTextView.setOnClickListener {
            cerrarSesionCompleto()
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
                // Llamamos al endpoint que acabamos de crear en el backend
                val response = RetrofitClient.webService.eliminarUsuario(idUsuario)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PerfilActivity, "Perfil eliminado correctamente", Toast.LENGTH_LONG).show()
                        cerrarSesionCompleto()
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

    private fun mostrarDialogoModificarContrasena(idUsuario: Int) {
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
            if (nuevaContrasena.length < 6) { // Es una buena práctica validar la longitud
                campoNuevaContrasena.error = "La contraseña debe tener al menos 6 caracteres"
                return@setOnClickListener
            }

            dialogo.dismiss()
            actualizarContrasenaEnServidor(idUsuario, nuevaContrasena)
        }
        dialogo.show()
    }

    private fun actualizarContrasenaEnServidor(idUsuario: Int, nuevaContrasena: String) {
        // Usamos nuestro nuevo modelo específico
        val datosParaActualizar = UsuarioActualizarPassword(idUsuario = idUsuario, nuevaContrasena = nuevaContrasena)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Llamamos a la nueva función del WebService
                val respuesta = RetrofitClient.webService.modificarContrasena(datosParaActualizar)
                withContext(Dispatchers.Main) {
                    if (respuesta.isSuccessful) {
                        Toast.makeText(this@PerfilActivity, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorMsg = respuesta.body()?.mensaje ?: "Error desconocido"
                        Toast.makeText(this@PerfilActivity, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
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