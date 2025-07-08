package com.example.boticaapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.boticaapp.R;
import com.example.boticaapp.api.ApiClient;
import com.example.boticaapp.models.User;
import com.example.boticaapp.ui.admin.AdminActivity;
import com.example.boticaapp.ui.cliente.ClienteActivity;
import com.example.boticaapp.ui.empleado.EmpleadoActivity;
import com.example.boticaapp.utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Ingresa email y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiClient.login(
                    this,
                    email,
                    password,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // Si la API devuelve {"error":"..."}
                            if (response.has("error")) {
                                Toast.makeText(LoginActivity.this,
                                        response.optString("error"),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONObject ju = response.optJSONObject("user");
                            if (ju == null) {
                                Toast.makeText(LoginActivity.this,
                                        "Respuesta inválida",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Crear objeto User y guardar en sesión
                            User user = new User();
                            user.setId         (ju.optInt("id", -1));
                            user.setEmail      (ju.optString("email", ""));
                            user.setName       (ju.optString("name", ""));
                            user.setRole       (ju.optString("role", ""));
                            // Aquí nos aseguramos de leer pharmacy_id, incluso si no existe:
                            user.setPharmacyId(ju.optInt("pharmacy_id", -1));
// **LOG de depuración**
                            int ph = ju.optInt("pharmacy_id", -1);
                            Log.d("LoginActivity", "pharmacy_id recibido = " + ph);
                            // Guardamos en SessionManager
                            SessionManager.getInstance(LoginActivity.this)
                                    .saveUser(user);

                            // Preparamos intent según rol
                            Intent intent;
                            switch (user.getRole()) {
                                case "cliente":
                                    intent = new Intent(LoginActivity.this, ClienteActivity.class);
                                    break;
                                case "empleado":
                                    intent = new Intent(LoginActivity.this, EmpleadoActivity.class);
                                    // además pasamos pharmacy_id para el empleado
                                    intent.putExtra("pharmacy_id", user.getPharmacyId());
                                    break;
                                case "admin":
                                    intent = new Intent(LoginActivity.this, AdminActivity.class);
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this,
                                            "Rol desconocido",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                            }

                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(int statusCode,
                                              Header[] headers,
                                              Throwable throwable,
                                              JSONObject errorResponse) {
                            String msg = (errorResponse != null)
                                    ? errorResponse.optString("error", throwable.getMessage())
                                    : throwable.getMessage();
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Error de conexión (" + statusCode + "): " + msg,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );
        });
    }

    public void onRegisterClicked(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
