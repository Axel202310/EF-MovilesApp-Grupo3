package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.model.Usuario
import com.asipion.pfmoviles.servicio.ConfiguracionResponse
import com.asipion.pfmoviles.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IniciarSesionContrasenaActividad : AppCompatActivity() {

    private lateinit var campoContrasena: EditText
    private lateinit var botonIniciarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_iniciar_sesion_contrasena)

        validarUsuarioGuardado()

        val botonAtras = findViewById<ImageView>(R.id.boton_atras)
        campoContrasena = findViewById(R.id.campo_contrasena)
        botonIniciarSesion = findViewById(R.id.boton_siguiente)

        val correo = intent.getStringExtra("correo") ?: ""
        Log.d("IniciarSesion", "Correo recibido: $correo")

        botonAtras.setOnClickListener { finish() }

        campoContrasena.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val habilitar = !s.isNullOrBlank()
                botonIniciarSesion.isEnabled = habilitar
                botonIniciarSesion.alpha = if (habilitar) 1f else 0.5f
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        botonIniciarSesion.setOnClickListener {
            val contrasena = campoContrasena.text.toString().trim()

            if (contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese su contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            botonIniciarSesion.isEnabled = false

            val usuario = Usuario(0, correo, contrasena)
            CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitClient.webService.iniciarSesion(usuario)
                if (response.isSuccessful && response.body()?.mensaje == "Login exitoso") {
                    val usuarioRespuesta = response.body()?.usuario
                    usuarioRespuesta?.let { usuario ->
                        val idUsuario = usuario.id_usuario
                        if (idUsuario != null) {
                            val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
                            prefs.edit()
                                .putInt("id_usuario", idUsuario)
                                .putString("correo_usuario", usuario.correo_usuario)
                                .apply()

                            // NUEVO: Obtener monto y moneda desde la base de datos
                            val configCall = RetrofitClient.webService.obtenerConfiguracionInicial(idUsuario)
                            configCall.enqueue(object : Callback<ConfiguracionResponse> {
                                override fun onResponse(
                                    call: Call<ConfiguracionResponse>,
                                    response: Response<ConfiguracionResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        val config = response.body()?.configuracion
                                        if (config != null) {
                                            prefs.edit()
                                                .putFloat("monto_$idUsuario", config.monto.toFloat())
                                                .putString("moneda_$idUsuario", config.moneda)
                                                .apply()
                                        }
                                    }

                                    // Continuar el flujo normal
                                    verificarTransaccionesYRedirigir(idUsuario)
                                }

                                override fun onFailure(call: Call<ConfiguracionResponse>, t: Throwable) {
                                    Log.e("ConfigLoad", "Error al obtener configuración: ${t.message}")
                                    verificarTransaccionesYRedirigir(idUsuario)
                                }
                            })
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@IniciarSesionContrasenaActividad, "El ID de usuario es nulo", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@IniciarSesionContrasenaActividad, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        botonIniciarSesion.isEnabled = true
                    }
                }
            }
        }
    }

    private fun verificarTransaccionesYRedirigir(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val transaccionesResponse = RetrofitClient.webService.obtenerTransacciones(idUsuario)
            if (transaccionesResponse.isSuccessful) {
                val transacciones = transaccionesResponse.body()?.listaTransacciones
                runOnUiThread {
                    if (!transacciones.isNullOrEmpty() || usuarioTieneSaldoRegistrado(idUsuario)) {
                        startActivity(Intent(this@IniciarSesionContrasenaActividad, InicioActivity::class.java))
                    } else {
                        val intent = Intent(this@IniciarSesionContrasenaActividad, SeleccionarDivisaActividad::class.java)
                        intent.putExtra("divisa", "PEN")
                        startActivity(intent)
                    }
                    finish()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@IniciarSesionContrasenaActividad, "Error al verificar transacciones", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@IniciarSesionContrasenaActividad, InicioActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun validarUsuarioGuardado() {
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        val idGuardado = prefs.getInt("id_usuario", -1)

        if (idGuardado != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.webService.cargarUsuarios()
                    if (response.isSuccessful) {
                        val usuarios = response.body()?.listaUsuarios ?: emptyList()
                        val usuarioExiste = usuarios.any { it.id_usuario == idGuardado }

                        if (!usuarioExiste) {
                            prefs.edit().clear().apply()
                            runOnUiThread {
                                Toast.makeText(this@IniciarSesionContrasenaActividad, "Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@IniciarSesionContrasenaActividad, IniciarSesionCorreoActividad::class.java))
                                finish()
                            }
                        }
                    } else {
                        Log.e("ValidarUsuario", "Error al cargar usuarios: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("ValidarUsuario", "Excepción al validar usuario guardado", e)
                }
            }
        }
    }

    private fun usuarioTieneSaldoRegistrado(idUsuario: Int): Boolean {
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        return prefs.getBoolean("flujo_completo_$idUsuario", false)
    }
}
