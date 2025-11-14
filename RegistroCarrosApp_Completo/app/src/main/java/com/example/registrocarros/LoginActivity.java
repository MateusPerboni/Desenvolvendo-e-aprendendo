package com.example.registrocarros;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.registrocarros.model.Usuario;
import com.example.registrocarros.network.ApiClient;
import com.example.registrocarros.network.ApiInterface;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, senhaInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.inputEmail);
        senhaInput = findViewById(R.id.inputSenha);
        Button btnLogin = findViewById(R.id.btnLogin);

        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        btnLogin.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String senha = senhaInput.getText().toString();
            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Preencha email e senha", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario usuario = new Usuario(email, senha);
            Call<ResponseBody> call = api.loginUsuario(usuario);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String body = response.body().string();
                            if (body.contains("\"status\":\"ok\"")) {
                                // login ok -> abrir MainActivity
                                Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(it);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login falhou: " + body, Toast.LENGTH_LONG).show();
                            }
                        } catch (IOException e) {
                            Toast.makeText(LoginActivity.this, "Resposta inválida do servidor", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Erro no login", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
