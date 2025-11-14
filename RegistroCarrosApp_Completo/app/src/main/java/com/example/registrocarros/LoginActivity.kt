package com.example.registrocarros

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.registrocarros.network.ApiClient
import com.example.registrocarros.network.ApiInterface
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var senhaInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.inputEmail)
        senhaInput = findViewById(R.id.inputSenha)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnIrRegistro: Button = findViewById(R.id.btnIrRegistro)

        val api = ApiClient.getClient().create(ApiInterface::class.java)

        btnLogin.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val senha = senhaInput.text.toString()
            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha email e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = mapOf("acao" to "login", "email" to email, "senha" to senha)
            api.loginUsuario(body).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful && response.body() != null) {
                        val json = response.body()!!
                        val status = json.get("status")?.asString ?: ""
                        if (status == "ok") {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            val mensagem = json.get("mensagem")?.asString ?: "Login falhou"
                            Toast.makeText(this@LoginActivity, mensagem, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Erro no login", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        btnIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}

