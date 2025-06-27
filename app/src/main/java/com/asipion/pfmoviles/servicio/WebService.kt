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

    // Obtener los detalles de una sola cuenta
    @GET("cuentas/detalle/{id}") // Necesitaremos esta ruta para precargar los datos en la pantalla de edición
    suspend fun obtenerDetalleCuenta(@Path("id") idCuenta: Int): Response<Cuenta>

    // Actualizar una cuenta
    @PUT("cuenta/actualizar/{id}")
    suspend fun actualizarCuenta(@Path("id") idCuenta: Int, @Body cuenta: CuentaParaCrear): Response<MensajeResponse>

    // Eliminar una cuenta
    @DELETE("cuenta/eliminar/{id}")
    suspend fun eliminarCuenta(@Path("id") idCuenta: Int): Response<MensajeResponse>

    @POST("cuenta/transferencia")
    suspend fun realizarTransferencia(@Body transferencia: Transferencia): Response<MensajeResponse>

    // NUEVAS FUNCIONES PARA EL HISTORIAL TRANSFERENCIAS
    @GET("transferencia/detalle/{id}")
    suspend fun obtenerDetalleTransferencia(@Path("id") idHistorial: Int): Response<HistorialTransferencia>

    @DELETE("transferencia/eliminar/{id}")
    suspend fun eliminarHistorialTransferencia(@Path("id") idHistorial: Int): Response<MensajeResponse>

    @GET("transferencias/historial/{idUsuario}")
    suspend fun obtenerHistorialTransferencias(@Path("idUsuario") idUsuario: Int): Response<HistorialResponse>


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

    @GET("transacciones/usuario/{idUsuario}")
    suspend fun obtenerTransaccionesDeUsuario(@Path("idUsuario") idUsuario: Int): Response<TransaccionResponse>

    // --- CATEGORÍAS ---
    @GET("categorias/{idUsuario}")
    suspend fun obtenerCategorias(
        @Path("idUsuario") idUsuario: Int,
        @Query("tipo") tipo: String
    ): Response<CategoriaResponse>

    @GET("categorias/detalle/{id}")
    suspend fun obtenerDetalleCategoria(@Path("id") idCategoria: Int): Response<Categoria>

    @POST("categoria/agregar")
    suspend fun crearCategoria(@Body categoria: Categoria): Response<MensajeResponse>

    @PUT("categoria/actualizar/{id}")
    suspend fun actualizarCategoria(@Path("id") idCategoria: Int, @Body categoria: Categoria): Response<MensajeResponse>

    @DELETE("categoria/eliminar/{id}")
    suspend fun eliminarCategoria(@Path("id") idCategoria: Int): Response<MensajeResponse>

    // --- GESTIÓN DE PERFIL ---
    @POST("usuario/modificar_contrasena")
    suspend fun modificarContrasena(
        @Body datos: UsuarioActualizarPassword
    ): Response<MensajeResponse>

    @DELETE("usuario/eliminar/{id}")
    suspend fun eliminarUsuario(
        @Path("id") idUsuario: Int
    ): Response<MensajeResponse>

    // --- Graficos ---
    @GET("estadisticas/resumen_periodo/{idUsuario}")
    suspend fun obtenerResumenPorPeriodo(
        @Path("idUsuario") idUsuario: Int,
        @Query("periodo") periodo: String // "dia", "semana", "mes", "año"
    ): Response<ResumenResponse>

    // --- Pagos Habituales ---

    @GET("pagos_habituales/{idUsuario}")
    suspend fun obtenerPagosHabituales(@Path("idUsuario") idUsuario: Int): Response<PagoHabitualResponse>

    @POST("pagos_habituales/agregar")
    suspend fun agregarPagoHabitual(@Body pagoHabitual: PagoHabitual): Response<MensajeResponse>

    @DELETE("pagos_habituales/eliminar/{id}")
    suspend fun eliminarPagoHabitual(@Path("id") idPago: Int): Response<MensajeResponse>
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