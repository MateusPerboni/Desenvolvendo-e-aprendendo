package com.example.registrocarros.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("nome")
    var nome: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("senha")
    var senha: String = ""
)
