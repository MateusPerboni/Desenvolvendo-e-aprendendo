package com.example.registrocarros.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object ApiClient {
    // ajuste a URL base conforme o local do backend; 10.0.2.2 aponta para host Windows quando usar em emulador
    private const val BASE_URL = "http://10.0.2.2/RegistroCarrosWeb/backend/api/"
    private var retrofit: Retrofit? = null
    private const val TIMEOUT = 30L

    fun getClient(): Retrofit {
        if (retrofit == null) {
            val client = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun resetClient() {
        retrofit = null
    }
}
