package com.asipion.pfmoviles.servicio

import com.asipion.pfmoviles.model.Usuario

import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

object AppConstantes {
    const val BASE_URL= "http://169.254.3.34:3000"
}

interface WebService {
    @GET("/usuarios")
    suspend fun cargarUsuarios(): Response<UsuarioResponse>

    @POST("/usuario/agregar")
    suspend fun agregarUsuario(@Body usuario: Usuario): Response<MensajeResponse>
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