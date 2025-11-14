package com.example.registrocarros.network;

import com.example.registrocarros.model.Carro;
import com.example.registrocarros.model.Usuario;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import okhttp3.ResponseBody;

public interface ApiInterface {
    // Carros
    @GET("carros.php")
    Call<List<Carro>> listarCarros(@Query("acao") String acao);

    @FormUrlEncoded
    @POST("carros.php")
    Call<JsonObject> criarCarro(
        @Field("modelo") String modelo,
        @Field("marca") String marca,
        @Field("ano") int ano,
        @Field("preco") double preco,
        @Field("descricao") String descricao,
        @Field("acao") String acao
    );

    @FormUrlEncoded
    @POST("carros.php")
    Call<JsonObject> atualizarCarro(
        @Field("id") int id,
        @Field("modelo") String modelo,
        @Field("marca") String marca,
        @Field("ano") int ano,
        @Field("preco") double preco,
        @Field("descricao") String descricao,
        @Field("acao") String acao
    );

    @FormUrlEncoded
    @POST("carros.php")
    Call<JsonObject> deletarCarro(
        @Field("id") int id,
        @Field("acao") String acao
    );

    // Usuários
    @FormUrlEncoded
    @POST("usuarios.php")
    Call<JsonObject> loginUsuario(
        @Field("email") String email,
        @Field("senha") String senha,
        @Field("acao") String acao
    );

    @FormUrlEncoded
    @POST("usuarios.php")
    Call<JsonObject> registrarUsuario(
        @Field("nome") String nome,
        @Field("email") String email,
        @Field("senha") String senha,
        @Field("acao") String acao
    );

    @FormUrlEncoded
    @POST("usuarios.php")
    Call<JsonObject> logout(@Field("acao") String acao);
}
