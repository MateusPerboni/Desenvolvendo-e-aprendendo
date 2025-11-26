package com.example.registrocarros.network

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("carros.php")
    fun listarCarros(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<JsonElement>

    @POST("carros.php")
    fun criarCarro(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<JsonElement>

    @POST("carros.php")
    fun atualizarCarro(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<JsonElement>

    @POST("carros.php")
    fun deletarCarro(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<JsonElement>

    // Usuários
    @POST("usuarios.php")
    fun loginUsuario(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<JsonElement>

    @POST("usuarios.php")
    fun registrarUsuario(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<JsonElement>

    @POST("usuarios.php")
    fun logout(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<JsonElement>
}
