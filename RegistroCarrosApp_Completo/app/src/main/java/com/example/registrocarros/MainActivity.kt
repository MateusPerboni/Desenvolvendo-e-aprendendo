package com.example.registrocarros

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.registrocarros.model.Carro
import com.example.registrocarros.network.ApiClient
import com.example.registrocarros.network.ApiInterface
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var inputBusca: TextInputEditText
    private lateinit var api: ApiInterface
    private var allCarros: List<Carro> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listaCarros)
        inputBusca = findViewById(R.id.inputBusca)
        val btnAdicionar: Button = findViewById(R.id.btnAdicionar)
        val btnLogout: Button? = findViewById(R.id.btnLogout)

        api = ApiClient.getClient().create(ApiInterface::class.java)

        carregarCarros()

        btnAdicionar.setOnClickListener { showAdicionarCarroDialog() }

        // Busca em tempo real
        inputBusca.setOnKeyListener { _, _, _ ->
            filtrarCarros(inputBusca.text.toString())
            false
        }

        btnLogout?.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Sair")
                .setMessage("Deseja realmente sair da sua conta?")
                .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
                .setPositiveButton("Sair") { _, _ ->
                    val bodyLogout = mapOf("acao" to "logout")
                    api.logout(bodyLogout).enqueue(object : Callback<JsonObject> {
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            if (response.isSuccessful && response.body() != null) {
                                getSharedPreferences("app_prefs", MODE_PRIVATE).edit().clear().apply()
                                Toast.makeText(this@MainActivity, "Deslogado com sucesso", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            Toast.makeText(this@MainActivity, "Erro de rede ao deslogar", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .show()
        }
    }

    private fun carregarCarros() {
        val body = mapOf("acao" to "listar")
        api.listarCarros(body).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    val json = response.body()!!
                    val status = json.get("status")?.asString ?: ""
                    if (status == "ok") {
                        val dados = json.getAsJsonArray("dados") ?: JsonArray()
                        val gson = Gson()
                        allCarros = dados.map { gson.fromJson(it, Carro::class.java) }
                        exibirCarros(allCarros)
                    } else {
                        Toast.makeText(this@MainActivity, "Erro ao listar carros", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Falha ao carregar carros", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun exibirCarros(carros: List<Carro>) {
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1
        )
        for (c in carros) {
            adapter.add("${c.modelo} - ${c.marca} (${c.ano})")
        }
        listView.adapter = adapter

        // Click listener para selecionar um carro
        listView.setOnItemClickListener { _, _, position, _ ->
            val carro = carros[position]
            showCarroOptionsDialog(carro)
        }
    }

    private fun filtrarCarros(termo: String) {
        val filtrados = if (termo.isEmpty()) {
            allCarros
        } else {
            allCarros.filter {
                it.modelo.contains(termo, ignoreCase = true) ||
                it.marca.contains(termo, ignoreCase = true) ||
                it.cor?.contains(termo, ignoreCase = true) ?: false
            }
        }
        exibirCarros(filtrados)
    }

    private fun showCarroOptionsDialog(carro: Carro) {
        AlertDialog.Builder(this)
            .setTitle("${carro.modelo} - ${carro.marca}")
            .setMessage("Preço: R$ ${carro.preco}\nAno: ${carro.ano}")
            .setNeutralButton("Editar") { _, _ ->
                showEditarCarroDialog(carro)
            }
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .setPositiveButton("Deletar") { _, _ ->
                confirmarDeletarCarro(carro)
            }
            .show()
    }

    private fun showAdicionarCarroDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Adicionar Novo Carro")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        val padding = (20 * resources.displayMetrics.density).toInt()
        layout.setPadding(padding, padding / 2, padding, padding / 2)

        val inputMarca = EditText(this)
        inputMarca.hint = "Marca"
        layout.addView(inputMarca, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val inputModelo = EditText(this)
        inputModelo.hint = "Modelo"
        layout.addView(inputModelo, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val inputAno = EditText(this)
        inputAno.hint = "Ano"
        inputAno.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        layout.addView(inputAno, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val inputPreco = EditText(this)
        inputPreco.hint = "Preço"
        inputPreco.inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(inputPreco, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val inputCor = EditText(this)
        inputCor.hint = "Cor"
        layout.addView(inputCor, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        builder.setView(layout)

        builder.setPositiveButton("Adicionar") { _, _ ->
            val marca = inputMarca.text.toString().trim()
            val modelo = inputModelo.text.toString().trim()
            val ano = inputAno.text.toString().trim()
            val preco = inputPreco.text.toString().trim()
            val cor = inputCor.text.toString().trim()

            if (marca.isEmpty() || modelo.isEmpty() || ano.isEmpty() || preco.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val body = mapOf(
                "acao" to "criar",
                "marca" to marca,
                "modelo" to modelo,
                "ano" to ano.toInt(),
                "preco" to preco.toDouble(),
                "cor" to cor
            )

            api.criarCarro(body).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful && response.body() != null) {
                        val status = response.body()!!.get("status")?.asString ?: ""
                        if (status == "ok") {
                            Toast.makeText(this@MainActivity, "Carro adicionado com sucesso", Toast.LENGTH_LONG).show()
                            inputBusca.setText("")
                            carregarCarros()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun showEditarCarroDialog(carro: Carro) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Carro")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        val padding = (20 * resources.displayMetrics.density).toInt()
        layout.setPadding(padding, padding / 2, padding, padding / 2)

        val inputMarca = EditText(this)
        inputMarca.setText(carro.marca)
        layout.addView(inputMarca, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val inputModelo = EditText(this)
        inputModelo.setText(carro.modelo)
        layout.addView(inputModelo, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val inputAno = EditText(this)
        inputAno.setText(carro.ano.toString())
        layout.addView(inputAno, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val inputPreco = EditText(this)
        inputPreco.setText(carro.preco.toString())
        layout.addView(inputPreco, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        builder.setView(layout)

        builder.setPositiveButton("Atualizar") { _, _ ->
            val marca = inputMarca.text.toString().trim()
            val modelo = inputModelo.text.toString().trim()
            val ano = inputAno.text.toString().trim()
            val preco = inputPreco.text.toString().trim()

            val body = mapOf(
                "acao" to "atualizar",
                "id" to carro.id,
                "marca" to marca,
                "modelo" to modelo,
                "ano" to ano.toInt(),
                "preco" to preco.toDouble()
            )

            api.atualizarCarro(body).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful && response.body() != null) {
                        val status = response.body()!!.get("status")?.asString ?: ""
                        if (status == "ok") {
                            Toast.makeText(this@MainActivity, "Carro atualizado com sucesso", Toast.LENGTH_LONG).show()
                            inputBusca.setText("")
                            carregarCarros()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun confirmarDeletarCarro(carro: Carro) {
        AlertDialog.Builder(this)
            .setTitle("Deletar Carro")
            .setMessage("Deseja realmente deletar este carro?")
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .setPositiveButton("Deletar") { _, _ ->
                val body = mapOf("acao" to "deletar", "id" to carro.id)
                api.deletarCarro(body).enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful && response.body() != null) {
                            val status = response.body()!!.get("status")?.asString ?: ""
                            if (status == "ok") {
                                Toast.makeText(this@MainActivity, "Carro deletado com sucesso", Toast.LENGTH_LONG).show()
                                inputBusca.setText("")
                                carregarCarros()
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            }
            .show()
    }
}


