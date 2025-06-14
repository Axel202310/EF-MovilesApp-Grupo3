package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class BienvenidaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Tu layout actividad_bienvenida.xml es perfecto, no necesita cambios.
        setContentView(R.layout.actividad_bienvenida)

        // Esperamos 2 segundos para mostrar el logo y luego decidimos a dónde ir.
        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
            val idUsuario = prefs.getInt("id_usuario", -1)

            val intent = if (idUsuario != -1) {
                // Si ya hay sesión, vamos directo al Dashboard.
                Intent(this, InicioActivity::class.java)
            } else {
                // Si no hay sesión, vamos al "centro de autenticación".
                Intent(this, AuthLandingActivity::class.java)
            }
            startActivity(intent)
            // Finalizamos esta actividad para que el usuario no pueda volver con el botón "atrás".
            finish()
        }, 2000) // 2000 milisegundos = 2 segundos
    }
}