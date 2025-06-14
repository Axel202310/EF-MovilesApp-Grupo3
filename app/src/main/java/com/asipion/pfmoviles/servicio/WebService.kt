package com.asipion.pfmoviles.servicio

import com.asipion.pfmoviles.model.*
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object AppConstantes {
    const val BASE_URL = "http://192.168.18.158:3000"
}

interface WebService {

    // --- AUTENTICACIÓN ---
    @POST("usuario/registrar")
    suspend fun registrarUsuario(@Body usuario: Usuario): Response<MensajeResponse>

    @POST("usuario/login")
    suspend fun iniciarSesion(@Body usuario: Usuario): Response<MensajeResponse>

    // --- CUENTAS ---
    @POST("cuenta/crear")
    suspend fun crearCuenta(@Body cuenta: CuentaParaCrear): Response<MensajeResponse>

    @GET("cuentas/{idUsuario}")
    suspend fun obtenerCuentas(@Path("idUsuario") idUsuario: Int): Response<CuentaResponse>

    // --- CATEGORÍAS ---
    @POST("categoria/agregar")
    suspend fun crearCategoria(@Body categoria: Categoria): Response<MensajeResponse>

    @GET("categorias/{idUsuario}")
    suspend fun obtenerCategorias(
        @Path("idUsuario") idUsuario: Int,
        @Query("tipo") tipo: String // "ingreso" o "gasto"
    ): Response<CategoriaResponse>

    // --- TRANSACCIONES ---
    @POST("transaccion/agregar")
    suspend fun agregarTransaccion(@Body transaccion: TransaccionParaCrear): Response<MensajeResponse>

    @GET("transacciones/{idCuenta}")
    suspend fun obtenerTransaccionesDeCuenta(@Path("idCuenta") idCuenta: Int): Response<TransaccionResponse>

    // OBTENER EL DETALLE DE UNA TRANSACCIÓN
    @GET("transaccion/{id}")
    suspend fun obtenerDetalleTransaccion(@Path("id") idTransaccion: Int): Response<Transaccion>

    // ELIMINAR UNA TRANSACCIÓN
    @DELETE("transaccion/eliminar/{id}")
    suspend fun eliminarTransaccion(@Path("id") idTransaccion: Int): Response<MensajeResponse>

    // --- GESTIÓN DE PERFIL (NUEVO) ---
    @POST("usuario/modificar_contrasena")
    suspend fun modificarContrasena(
        @Body datos: UsuarioActualizarPassword
    ): Response<MensajeResponse>

    @DELETE("usuario/eliminar/{id}")
    suspend fun eliminarUsuario(
        @Path("id") idUsuario: Int
    ): Response<MensajeResponse>
}

object RetrofitClient {
    val webService: WebService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstantes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(WebService::class.java)
    }
}