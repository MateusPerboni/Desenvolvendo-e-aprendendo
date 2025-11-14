package com.example.registrocarros.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    // URL base correta para o backend PHP
    private static final String BASE_URL = "http://10.0.2.2/P2/registro_carros_atualizado/backend/api/";
    private static Retrofit retrofit;
    private static final int TIMEOUT = 30; // segundos

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Configurar OkHttpClient com timeout
            OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();

            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit;
    }

    // Método para resetar o cliente (útil para trocar URLs em tempo de execução)
    public static void resetClient() {
        retrofit = null;
    }
}
