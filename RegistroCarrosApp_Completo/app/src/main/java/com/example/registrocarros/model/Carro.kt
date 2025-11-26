package com.example.registrocarros.model

import com.google.gson.annotations.SerializedName

class Carro(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("marca") var marca: String = "",
    @SerializedName("modelo") var modelo: String = "",
    @SerializedName("ano") var ano: Int = 0,
    @SerializedName("preco") var preco: Double = 0.0,
    @SerializedName("descricao") var descricao: String = "",
    // alguns campos do backend usam nomes diferentes (cor, observacoes), ajuste conforme necessário
    @SerializedName("cor") var cor: String = "",
    @SerializedName("observacoes") var observacoes: String? = null,
    @SerializedName("data_compra") var dataCompra: String? = null
)
