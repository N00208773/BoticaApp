package com.example.boticaapp.ui.empleado;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.boticaapp.R;
import com.example.boticaapp.api.ApiClient;
import com.example.boticaapp.utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import java.io.UnsupportedEncodingException;

public class EmployeeHomeFragment extends Fragment {
    private EditText etName, etDesc, etPrice, etStock;
    private Button btnAdd;
    private int pharmacyId;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employee_home, container, false);
    }

    @Override public void onViewCreated(@NonNull View v,
                                        @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        etName  = v.findViewById(R.id.etName);
        etDesc  = v.findViewById(R.id.etDesc);
        etPrice = v.findViewById(R.id.etPrice);
        etStock = v.findViewById(R.id.etStock);
        btnAdd  = v.findViewById(R.id.btnAdd);

        Context ctx = requireContext();
        pharmacyId = SessionManager
                .getInstance(ctx)
                .getUser()
                .getPharmacyId();

        btnAdd.setOnClickListener(view -> addProduct());
    }

    private void addProduct() {
        Context ctx = requireContext();
        String name  = etName.getText().toString().trim();
        String desc  = etDesc.getText().toString().trim();
        String sp    = etPrice.getText().toString().trim();
        String ss    = etStock.getText().toString().trim();

        if (name.isEmpty() || sp.isEmpty() || ss.isEmpty()) {
            Toast.makeText(ctx, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }
        double price;
        int stock;
        try {
            price = Double.parseDouble(sp);
            stock = Integer.parseInt(ss);
        } catch (NumberFormatException e) {
            Toast.makeText(ctx, "Precio o stock inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAdd.setEnabled(false);

        // 1) Construye el JSON
        JSONObject body = new JSONObject();
        try {
            body.put("pharmacy_id", pharmacyId);
            body.put("name",        name);
            body.put("description", desc);
            body.put("price",       price);
            body.put("stock",       stock);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 2) Empaqueta como StringEntity
        StringEntity entity;
        entity = new StringEntity(body.toString(), "UTF-8");

        // 3) URL con action=create
        String url = ApiClient.MEDICINES_URL + "?action=create";
        Log.d("EmployeeHome", "POST JSON " + url + " → " + body);

        // 4) Petición
        ApiClient.client.post(
                ctx,
                url,
                entity,
                "application/json",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject resp) {
                        String msg = resp.optString("message", "Producto añadido");
                        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                        // limpiar formulario
                        etName.setText("");
                        etDesc.setText("");
                        etPrice.setText("");
                        etStock.setText("");
                        btnAdd.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        String err = errorResponse != null
                                ? errorResponse.optString("error", throwable.getMessage())
                                : throwable.getMessage();
                        Log.e("EmployeeHome", "Error JSON (" + statusCode + "): " + err, throwable);
                        Toast.makeText(ctx, "Error al añadir producto: " + err, Toast.LENGTH_LONG).show();
                        btnAdd.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("EmployeeHome", "Error texto (" + statusCode + "): " + responseString, throwable);
                        Toast.makeText(ctx, "Error al añadir producto: " + responseString, Toast.LENGTH_LONG).show();
                        btnAdd.setEnabled(true);
                    }
                }
        );
    }
}
