package com.example.boticaapp.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.boticaapp.R;
import com.example.boticaapp.api.ApiClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONObject;  // <- Import necesario
import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private Button btnRegisterClient, btnRegisterEmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName          = findViewById(R.id.etName);
        etEmail         = findViewById(R.id.etEmail);
        etPassword      = findViewById(R.id.etPassword);
        btnRegisterClient = findViewById(R.id.btnRegisterClient);
        btnRegisterEmp    = findViewById(R.id.btnRegisterEmp);

        btnRegisterClient.setOnClickListener(v -> register("cliente"));
        btnRegisterEmp   .setOnClickListener(v -> register("empleado"));
    }

    private void register(String role) {
        String name  = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass  = etPassword.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.register(
                this,
                email,
                pass,
                name,
                role,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        boolean ok = response.optBoolean("success", false);
                        if (ok) {
                            String msg = response.optString("message", "Registro exitoso");
                            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String err = response.optString("error", "Error desconocido");
                            Toast.makeText(RegisterActivity.this, "Error: " + err, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        String errMsg = (errorResponse != null)
                                ? errorResponse.optString("error", throwable.getMessage())
                                : throwable.getMessage();
                        Toast.makeText(
                                RegisterActivity.this,
                                "Falló registro (" + statusCode + "): " + errMsg,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}
