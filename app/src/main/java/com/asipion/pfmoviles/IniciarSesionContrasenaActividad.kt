package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
// No necesitamos importar View para ProgressBar.GONE/VISIBLE si no hay ProgressBar
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.model.Usuario
import com.asipion.pfmoviles.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IniciarSesionContrasenaActividad : AppCompatActivity() {

    private lateinit var campoContrasena: EditText
    private lateinit var botonIniciarSesion: Button
    // Eliminamos la referencia a ProgressBar ya que no está en el layout proporcionado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_iniciar_sesion_contrasena)

        // Inicializa tus vistas del layout usando los IDs de tu XML
        val botonAtras = findViewById<ImageView>(R.id.boton_atras)
        campoContrasena = findViewById(R.id.campo_contrasena)
        botonIniciarSesion = findViewById(R.id.boton_siguiente)
        // No hay ProgressBar con el id 'progreso_login' en el layout, así que no la inicializamos


        // Obtener el correo pasado desde la actividad anterior
        // Usamos un valor por defecto vacío si no se encuentra el extra "correo"
        val correo = intent.getStringExtra("correo") ?: ""
        // Opcional: Log para verificar que el correo llegó correctamente
        Log.d("IniciarSesion", "Correo recibido: $correo")


        // Configurar el botón de atrás para simplemente cerrar esta actividad
        botonAtras.setOnClickListener { finish() }

        // Configurar el TextWatcher para habilitar/deshabilitar el botón "Siguiente"
        campoContrasena.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Habilitar el botón si hay texto (no en blanco o nulo) en el campo de contraseña
                val habilitar = !s.isNullOrBlank()
                botonIniciarSesion.isEnabled = habilitar
                // Cambiar la opacidad para indicar si está habilitado o no
                botonIniciarSesion.alpha = if (habilitar) 1f else 0.5f
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Configurar el listener para el botón de iniciar sesión ("Siguiente")
        botonIniciarSesion.setOnClickListener {
            val contrasena = campoContrasena.text.toString().trim()



            // Validación simple: verificar que el campo de contraseña no esté vacío
            if (contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese su contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Salir del listener si está vacío
            }

            // Deshabilitar botón durante la operación para evitar clics múltiples
            botonIniciarSesion.isEnabled = false
            // Como no hay ProgressBar, no mostramos progreso visual aquí.


            val usuario = Usuario(0, correo, contrasena)
            CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitClient.webService.iniciarSesion(usuario)
                if (response.isSuccessful && response.body()?.mensaje == "Login exitoso") {
                    val usuarioRespuesta = response.body()?.usuario
                    usuarioRespuesta?.let { usuario ->
                        val idUsuario = usuario.id_usuario
                        if (idUsuario != null) {
                            guardarIdUsuario(idUsuario)

                            val transaccionesResponse = RetrofitClient.webService.obtenerTransacciones(idUsuario)
                            if (transaccionesResponse.isSuccessful) {
                                val transacciones = transaccionesResponse.body()?.listaTransacciones
                                runOnUiThread {
                                    if (!transacciones.isNullOrEmpty() || usuarioTieneSaldoRegistrado(idUsuario)) {
                                        // Ya tiene transacciones o flujo completo guardado → ir al inicio
                                        startActivity(Intent(this@IniciarSesionContrasenaActividad, InicioActivity::class.java))
                                    } else {
                                        // No tiene nada → ir a seleccionar divisa
                                        val intent = Intent(this@IniciarSesionContrasenaActividad, SeleccionarDivisaActividad::class.java)
                                        intent.putExtra("divisa", "PEN") // o ajusta si manejas otra lógica de divisa
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
    private fun usuarioTieneSaldoRegistrado(idUsuario: Int): Boolean {
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        return prefs.getBoolean("flujo_completo_$idUsuario", false)
    }
    private fun guardarIdUsuario(idUsuario: Int) {
        val prefs = getSharedPreferences("mis_prefs", MODE_PRIVATE)
        // Limpiamos los datos anteriores
        prefs.edit().clear().apply()

        // Guardamos el nuevo ID
        prefs.edit().putInt("id_usuario", idUsuario).apply()
    }

}
