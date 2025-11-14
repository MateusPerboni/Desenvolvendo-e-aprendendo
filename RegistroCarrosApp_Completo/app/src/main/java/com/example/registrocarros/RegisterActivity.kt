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

class RegisterActivity : AppCompatActivity() {

    private lateinit var inputNome: TextInputEditText
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputSenha: TextInputEditText
    private lateinit var inputConfirmaSenha: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        inputNome = findViewById(R.id.inputNome)
        inputEmail = findViewById(R.id.inputEmail)
        inputSenha = findViewById(R.id.inputSenha)
        inputConfirmaSenha = findViewById(R.id.inputConfirmaSenha)
        val btnRegistrar: Button = findViewById(R.id.btnRegistrar)
        val btnIrLogin: Button = findViewById(R.id.btnIrLogin)

        val api = ApiClient.getClient().create(ApiInterface::class.java)

        btnRegistrar.setOnClickListener {
            val nome = inputNome.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val senha = inputSenha.text.toString()
            val confirmaSenha = inputConfirmaSenha.text.toString()

            // Validações
            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmaSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha.length < 6) {
                Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!email.contains("@")) {
                Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chamada de API
            val body = mapOf("acao" to "criar", "nome" to nome, "email" to email, "senha" to senha)
            api.registrarUsuario(body).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful && response.body() != null) {
                        val json = response.body()!!
                        val status = json.get("status")?.asString ?: ""
                        if (status == "ok") {
                            Toast.makeText(this@RegisterActivity, "Registro realizado com sucesso!\nFaça login para continuar", Toast.LENGTH_LONG).show()
                            // Voltar para Login
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            val mensagem = json.get("mensagem")?.asString ?: "Falha no registro"
                            Toast.makeText(this@RegisterActivity, mensagem, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@RegisterActivity, "Erro ao registrar usuário", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        btnIrLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
