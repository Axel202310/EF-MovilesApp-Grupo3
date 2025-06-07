package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
// No necesitamos importar View para ProgressBar.GONE/VISIBLE si no hay ProgressBar
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class IniciarSesionContrasenaActividad : AppCompatActivity() {

    private lateinit var campoContrasena: EditText
    private lateinit var botonIniciarSesion: Button
    private lateinit var auth: FirebaseAuth
    // Eliminamos la referencia a ProgressBar ya que no está en el layout proporcionado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_iniciar_sesion_contrasena)

        // Inicializa tus vistas del layout usando los IDs de tu XML
        val botonAtras = findViewById<ImageView>(R.id.boton_atras)
        campoContrasena = findViewById(R.id.campo_contrasena)
        botonIniciarSesion = findViewById(R.id.boton_siguiente)
        // No hay ProgressBar con el id 'progreso_login' en el layout, así que no la inicializamos

        // Inicializa Firebase Auth
        auth = FirebaseAuth.getInstance()

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

            // Llamada a Firebase Authentication para iniciar sesión con correo y contraseña
            auth.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this) { task ->
                    // Volver a habilitar el botón al completar la operación, sin importar el resultado
                    botonIniciarSesion.isEnabled = true
                    // No hay ProgressBar para ocultar.

                    if (task.isSuccessful) {
                        // El inicio de sesión con Firebase fue exitoso
                        val usuario = auth.currentUser
                        if (usuario != null && usuario.isEmailVerified) {
                            // El usuario está autenticado Y su correo está verificado. ¡Perfecto!
                            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            Log.d("IniciarSesion", "Usuario ${usuario.uid} ha iniciado sesión y correo verificado.")

                            // Navegar a la siguiente pantalla principal (SeleccionarDivisaActividad)
                            // Usamos FLAG_ACTIVITY_NEW_TASK y FLAG_ACTIVITY_CLEAR_TASK
                            // para asegurar que la pila de actividades se limpia y el usuario
                            // no pueda volver a las pantallas de login con el botón físico atrás.
                            val intent = Intent(this, SeleccionarDivisaActividad::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            // No necesitas finish() aquí porque CLEAR_TASK ya se encarga de las actividades anteriores.

                        } else if (usuario != null && !usuario.isEmailVerified){
                            // El usuario está autenticado PERO su correo NO está verificado.
                            Toast.makeText(this, "Verifica tu correo electrónico primero.", Toast.LENGTH_LONG).show()
                            Log.w("IniciarSesion", "Usuario ${usuario.uid} inició sesión pero correo no verificado.")

                            // Opcional: enviar el correo de verificación de nuevo para recordarle al usuario
                            usuario.sendEmailVerification()
                                .addOnCompleteListener { sendTask ->
                                    if (sendTask.isSuccessful) {
                                        Log.d("IniciarSesion", "Correo de verificación enviado.")
                                        // Puedes comentar el Toast de envío si no quieres dos Toasts seguidos
                                        Toast.makeText(this, "Correo de verificación enviado.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Log.e("IniciarSesion", "Error al enviar correo de verificación.", sendTask.exception)
                                        // Opcional: mostrar un Toast si no se pudo enviar el correo de verificación
                                    }
                                }

                            // Cerrar la sesión del usuario que no ha verificado su correo
                            auth.signOut()
                            Log.d("IniciarSesion", "Sesión cerrada por correo no verificado.")

                            // *** Acción Importante: Cerrar esta actividad para regresar a la pantalla de correo
                            // Esto guía al usuario de vuelta al inicio del flujo de login si necesita verificar.
                            finish()

                        } else {
                            // Este bloque maneja un caso inesperado: task.isSuccessful es true, pero auth.currentUser es nulo.
                            // Esto no debería ocurrir normalmente justo después de un inicio de sesión exitoso.
                            Toast.makeText(this, "Error inesperado: No se pudo obtener el usuario autenticado.", Toast.LENGTH_LONG).show()
                            Log.e("IniciarSesion", "Error inesperado: usuario nulo después de task.isSuccessful.")

                            // *** Acción Importante: Cerrar esta actividad en caso de estado inesperado
                            finish()
                        }
                    } else {
                        // El inicio de sesión con Firebase Falló (correo/contraseña incorrecta, etc.)
                        val exception = task.exception
                        // Preparamos un mensaje de error más amigable basado en el tipo de excepción
                        val mensajeError = when (exception) {
                            is FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta. Por favor, inténtalo de nuevo."
                            is FirebaseAuthInvalidUserException -> "No existe una cuenta con este correo. Considera registrarte."
                            // Puedes añadir más casos aquí para otros errores comunes si lo deseas:
                            // is FirebaseAuthUserDisabledException -> "Tu cuenta ha sido deshabilitada."
                            // is FirebaseAuthTooManyRequestsException -> "Demasiados intentos fallidos. Intenta más tarde."
                            else -> task.exception?.message ?: "Error desconocido al iniciar sesión." // Mensaje genérico si no es una excepción conocida
                        }

                        // Mostramos el mensaje de error al usuario
                        Toast.makeText(this, "Error: $mensajeError", Toast.LENGTH_LONG).show()
                        // Registramos el error completo en Logcat para depuración
                        Log.e("IniciarSesion", "Error al iniciar sesión: $mensajeError", exception)

                        // *** Acción Importante: Cerrar esta actividad si el inicio de sesión falla.
                        // Al cerrar esta actividad, el sistema regresa automáticamente a la actividad anterior,
                        // que es IniciarSesionCorreoActividad, replicando el comportamiento que viste.
                        // Puedes decidir si quieres limpiar el campo de contraseña antes de llamar a finish() si lo prefieres.
                        // campoContrasena.setText("") // Descomenta si quieres limpiar el campo
                        finish()
                    }
                }
        }
    }
}
