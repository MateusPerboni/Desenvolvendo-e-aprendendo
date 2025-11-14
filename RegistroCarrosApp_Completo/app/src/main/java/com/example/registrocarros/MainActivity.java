package com.example.registrocarros;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.example.registrocarros.model.Carro;
import com.example.registrocarros.model.Usuario;
import com.example.registrocarros.network.ApiClient;
import com.example.registrocarros.network.ApiInterface;
import java.io.IOException;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listaCarros);
        Button btnRegister = findViewById(R.id.btnRegister);

        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        api.listarCarros().enqueue(new Callback<List<Carro>>() {
            @Override
            public void onResponse(Call<List<Carro>> call, Response<List<Carro>> response) {
                if (response.isSuccessful()) {
                    List<Carro> carros = response.body();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_list_item_1);
                    for (Carro c : carros) {
                        adapter.add(c.getModelo() + " - " + c.getAno());
                    }
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Carro>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erro ao carregar carros", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(v -> showRegisterDialog(api));
        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // confirmação antes de deslogar
                new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Sair")
                        .setMessage("Deseja realmente sair da sua conta?")
                        .setNegativeButton("Cancelar", (d, w) -> d.dismiss())
                        .setPositiveButton("Sair", (d, w) -> {
                            Call<ResponseBody> logoutCall = api.logout();
                            logoutCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        // limpar dados locais
                                        getSharedPreferences("app_prefs", MODE_PRIVATE).edit().clear().apply();
                                        Toast.makeText(MainActivity.this, "Deslogado com sucesso", Toast.LENGTH_SHORT).show();
                                        // abrir LoginActivity
                                        android.content.Intent it = new android.content.Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(it);
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Falha ao deslogar", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(MainActivity.this, "Erro de rede ao deslogar", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).show();
            });
        }
    }

    private void showRegisterDialog(ApiInterface api) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registrar Usuário");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);

        final EditText inputNome = new EditText(this);
        inputNome.setHint("Nome");
        layout.addView(inputNome, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final EditText inputEmail = new EditText(this);
        inputEmail.setHint("Email");
        layout.addView(inputEmail, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final EditText inputSenha = new EditText(this);
        inputSenha.setHint("Senha");
        inputSenha.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputSenha, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        builder.setView(layout);

        builder.setPositiveButton("Registrar", (dialog, which) -> {
            String nome = inputNome.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String senha = inputSenha.getText().toString();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(MainActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario usuario = new Usuario(nome, email, senha);
            registerUsuario(api, usuario);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void registerUsuario(ApiInterface api, Usuario usuario) {
        Call<ResponseBody> call = api.registerUsuario(usuario);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String body = response.body().string();
                        // Espera um JSON com status e mensagem
                        if (body.contains("\"status\":\"ok\"")) {
                            Toast.makeText(MainActivity.this, "Registro realizado com sucesso", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Falha no registro: " + body, Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "Resposta inválida do servidor", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Erro ao registrar usuário", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
