// --- Archivo: model/UsuarioActualizarPassword.kt (NUEVO) ---
package com.asipion.pfmoviles.model
import com.google.gson.annotations.SerializedName

// Modelo específico para enviar los datos al cambiar la contraseña
data class UsuarioActualizarPassword(
    @SerializedName("id_usuario")
    val idUsuario: Int,
    @SerializedName("nueva_contrasena")
    val nuevaContrasena: String
)