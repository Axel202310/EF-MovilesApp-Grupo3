package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class BienvenidaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_bienvenida)

        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
            val idUsuario = prefs.getInt("id_usuario", -1)

            val intent = if (idUsuario != -1) {
                Intent(this, InicioActivity::class.java)
            } else {
                Intent(this, AuthLandingActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}