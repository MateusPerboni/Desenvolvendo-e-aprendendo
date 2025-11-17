package com.example.registrocarros

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
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

    // Propriedades de View (inicializadas no onCreate)
    private lateinit var listView: ListView
    private lateinit var inputBusca: TextInputEditText

    // Propriedades de lógica (inicializadas de forma preguiçosa para segurança e performance)
    private val api: ApiInterface by lazy {
        ApiClient.getClient().create(ApiInterface::class.java)
    }
    private val sessionManager: SessionManager by lazy {
        SessionManager(this)
    }
    private val adapter: ArrayAdapter<Carro> by lazy {
        object : ArrayAdapter<Carro>(this, android.R.layout.simple_list_item_1) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view: View
                val textView: TextView
                if (convertView == null) {
                    view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
                    textView = view.findViewById(android.R.id.text1)
                    view.tag = textView // Cache da view para reutilização
                } else {
                    view = convertView
                    textView = view.tag as TextView
                }
                getItem(position)?.let { carro ->
                    textView.text = "${carro.modelo} - ${carro.marca} (${carro.ano})"
                }
                return view
            }
        }
    }
    private val gson = Gson()
    private var allCarros: List<Carro> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userId = sessionManager.fetchAuthToken()
        if (userId == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Inicialização das Views
        listView = findViewById(R.id.listaCarros)
        inputBusca = findViewById(R.id.inputBusca)
        val btnAdicionar: Button = findViewById(R.id.btnAdicionar)
        val btnLogout: Button = findViewById(R.id.btnLogout)

        // Configuração do Adapter e Listeners
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            adapter.getItem(position)?.let {
                showCarroOptionsDialog(it)
            }
        }

        carregarCarros(userId)

        btnAdicionar.setOnClickListener { showCarroDialog(userId) }

        inputBusca.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarCarros(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Sair")
                .setMessage("Deseja realmente sair da sua conta?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Sair") { _, _ ->
                    sessionManager.clearAuthToken()
                    Toast.makeText(this@MainActivity, "Deslogado com sucesso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .show()
        }
    }

    private fun carregarCarros(userId: String) {
        api.listarCarros(mapOf("acao" to "listar", "usuario_id" to userId)).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    response.body()?.let { json ->
                        if (json.get("status")?.asString == "ok") {
                            val dados = json.getAsJsonArray("dados") ?: JsonArray()
                            allCarros = gson.fromJson(dados, Array<Carro>::class.java).toList()
                            exibirCarros(allCarros)
                        } else {
                            val mensagem = json.get("mensagem")?.asString ?: "Nenhum carro encontrado"
                            Toast.makeText(this@MainActivity, mensagem, Toast.LENGTH_SHORT).show()
                            allCarros = emptyList()
                            exibirCarros(allCarros)
                        }
                    } ?: run {
                        allCarros = emptyList()
                        exibirCarros(allCarros)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Falha ao carregar dados do servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun exibirCarros(carros: List<Carro>) {
        adapter.clear()
        adapter.addAll(carros)
    }

    private fun filtrarCarros(termo: String) {
        val filtrados = if (termo.isBlank()) allCarros else {
            allCarros.filter {
                it.modelo.contains(termo, ignoreCase = true) || it.marca.contains(termo, ignoreCase = true)
            }
        }
        exibirCarros(filtrados)
    }

    private fun showCarroOptionsDialog(carro: Carro) {
        val precoFormatado = String.format("R$ %.2f", carro.preco)
        AlertDialog.Builder(this)
            .setTitle("${carro.modelo} - ${carro.marca}")
            .setMessage("Ano: ${carro.ano}\nPreço: $precoFormatado")
            .setNeutralButton("Editar") { _, _ ->
                sessionManager.fetchAuthToken()?.let { userId ->
                    showCarroDialog(userId, carro)
                } ?: Toast.makeText(this, "Sessão inválida. Faça login novamente.", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Deletar") { _, _ -> confirmarDeletarCarro(carro) }
            .show()
    }

    private fun showCarroDialog(userId: String, carro: Carro? = null) {
        val isEditing = carro != null
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (isEditing) "Editar Carro" else "Adicionar Novo Carro")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val padding = (20 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding / 2, padding, padding / 2)
        }

        val inputMarca = EditText(this).apply { hint = "Marca"; if (isEditing) setText(carro?.marca) }
        val inputModelo = EditText(this).apply { hint = "Modelo"; if (isEditing) setText(carro?.modelo) }
        val inputAno = EditText(this).apply { hint = "Ano"; inputType = android.text.InputType.TYPE_CLASS_NUMBER; if (isEditing) setText(carro?.ano.toString()) }
        val inputPreco = EditText(this).apply { hint = "Preço"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL; if (isEditing) setText(carro?.preco.toString()) }
        val inputCor = EditText(this).apply { hint = "Cor (opcional)"; if (isEditing) setText(carro?.cor ?: "") }
        
        layout.addView(inputMarca)
        layout.addView(inputModelo)
        layout.addView(inputAno)
        layout.addView(inputPreco)
        layout.addView(inputCor)
        builder.setView(layout)

        builder.setPositiveButton(if (isEditing) "Atualizar" else "Adicionar") { _, _ ->
            val marca = inputMarca.text.toString().trim()
            val modelo = inputModelo.text.toString().trim()
            val anoStr = inputAno.text.toString().trim()
            val precoStr = inputPreco.text.toString().trim()
            val cor = inputCor.text.toString().trim()

            if (marca.isBlank() || modelo.isBlank() || anoStr.isBlank() || precoStr.isBlank()) {
                Toast.makeText(this, "Marca, Modelo, Ano e Preço são obrigatórios", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val ano = anoStr.toIntOrNull()
            val preco = precoStr.toDoubleOrNull()
            if (ano == null || preco == null) {
                Toast.makeText(this, "Ano e Preço devem ser números válidos", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val body = mutableMapOf<String, Any>(
                "marca" to marca, "modelo" to modelo, "ano" to ano, "preco" to preco, "cor" to cor, "usuario_id" to userId
            )
            
            val apiCall: Call<JsonObject>
            if (isEditing) {
                body["acao"] = "atualizar"
                if (carro != null) {
                    body["id"] = carro.id
                }
                apiCall = api.atualizarCarro(body)
            } else {
                body["acao"] = "criar"
                apiCall = api.criarCarro(body)
            }

            apiCall.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    val json = response.body()
                    if (response.isSuccessful && json != null && json.get("status")?.asString == "ok") {
                        val successMessage = if(isEditing) "Carro atualizado!" else "Carro adicionado!"
                        Toast.makeText(this@MainActivity, successMessage, Toast.LENGTH_SHORT).show()
                        carregarCarros(userId)
                    } else {
                        val errorMessage = json?.get("mensagem")?.asString ?: (if(isEditing) "Falha ao atualizar" else "Falha ao adicionar")
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        builder.setNegativeButton("Cancelar", null).show()
    }

    private fun confirmarDeletarCarro(carro: Carro) {
        val userId = sessionManager.fetchAuthToken() ?: return

        AlertDialog.Builder(this)
            .setTitle("Deletar Carro")
            .setMessage("Tem certeza que deseja deletar o carro ${carro.modelo}?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Deletar") { _, _ ->
                val body = mapOf("acao" to "deletar", "id" to carro.id, "usuario_id" to userId)
                api.deletarCarro(body).enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val json = response.body()
                        if (response.isSuccessful && json != null && json.get("status")?.asString == "ok") {
                            Toast.makeText(this@MainActivity, "Carro deletado!", Toast.LENGTH_SHORT).show()
                            carregarCarros(userId)
                        } else {
                            val errorMessage = json?.get("mensagem")?.asString ?: "Falha ao deletar o carro"
                            Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            }
            .show()
    }
}
