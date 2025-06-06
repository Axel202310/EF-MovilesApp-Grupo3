package com.asipion.pfmoviles


import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class PerfilActivity : AppCompatActivity() {

    private lateinit var emailTextView: TextView
    private lateinit var userIdTextView: TextView
    private lateinit var passwordModifyTextView: TextView
    private lateinit var logoutButton: TextView
    private lateinit var deleteButton: ImageButton
    private lateinit var editEmailIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_perfil)

        // Toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Referencias
        emailTextView = findViewById(R.id.email_value)
        userIdTextView = findViewById(R.id.user_id)
        passwordModifyTextView = findViewById(R.id.password_modify)
        logoutButton = findViewById(R.id.logout_button)
        deleteButton = findViewById(R.id.delete_button)
        editEmailIcon = findViewById(R.id.edit_email_icon)

        // Simulación de datos dinámicos
        val email = "usuario1@gmail.com"
        val userId = "ID: 10983593"

        emailTextView.text = email
        userIdTextView.text = userId

        editEmailIcon.setOnClickListener {
            Toast.makeText(this, "Editar correo (no implementado)", Toast.LENGTH_SHORT).show()
            // Aquí podrías lanzar un diálogo
        }

        passwordModifyTextView.setOnClickListener {
            Toast.makeText(this, "Modificar contraseña (no implementado)", Toast.LENGTH_SHORT).show()
        }

        deleteButton.setOnClickListener {
            Toast.makeText(this, "Cuenta eliminada (simulado)", Toast.LENGTH_SHORT).show()
        }

        logoutButton.setOnClickListener {
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, IniciarSesionCorreoActividad::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
