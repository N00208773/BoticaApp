package com.example.boticaapp.ui.cliente;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boticaapp.R;
import com.example.boticaapp.api.ApiClient;
import com.example.boticaapp.models.Order;
import com.example.boticaapp.ui.cliente.OrdersAdapter.OnStatusChangeListener;
import com.example.boticaapp.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class OrdersFragment extends Fragment {
    private RecyclerView rv;
    private OrdersAdapter adapter;
    private String role;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override public void onViewCreated(@NonNull View view,
                                        @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.rvOrders);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 1) Detectar rol desde la sesión
        role = SessionManager.getInstance(requireContext())
                .getUser()
                .getRole();  // "cliente" o "empleado"
        Log.d("OrdersFragment", "ROL ACTUAL = " + role);
        // 2) Crear listener solo si es empleado
        OnStatusChangeListener listener = null;
        if ("empleado".equals(role)) {
            listener = orderId -> {
                ApiClient.updateOrderStatus(
                        requireContext(),
                        orderId,
                        "pagado",
                        new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                String msg = response.optString("message", "Estado actualizado");
                                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                                loadOrders();
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                String err = errorResponse != null
                                        ? errorResponse.optString("error", throwable.getMessage())
                                        : throwable.getMessage();
                                Toast.makeText(requireContext(),
                                        "Error actualizando: " + err,
                                        Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                Toast.makeText(requireContext(),
                                        "Error actualizando: " + responseString,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                );
            };
        }

        // 3) Instanciar Adapter con listener (null para cliente)
        adapter = new OrdersAdapter(new ArrayList<>(), listener);
        rv.setAdapter(adapter);

        // 4) Cargar pedidos según rol
        loadOrders();
    }

    private void loadOrders() {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Type listType = new TypeToken<List<Order>>(){}.getType();
                List<Order> orders = new Gson().fromJson(response.toString(), listType);
                adapter.setItems(orders);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(requireContext(),
                        "Error cargando pedidos",
                        Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String err = errorResponse != null
                        ? errorResponse.optString("error", throwable.getMessage())
                        : throwable.getMessage();
                Toast.makeText(requireContext(),
                        "Error cargando pedidos: " + err,
                        Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(requireContext(),
                        "Error cargando pedidos: " + responseString,
                        Toast.LENGTH_LONG).show();
            }
        };

        if ("empleado".equals(role)) {
            int pharmacyId = SessionManager.getInstance(requireContext())
                    .getUser()
                    .getPharmacyId();
            ApiClient.getOrdersByPharmacy(requireContext(), pharmacyId, handler);
        } else {
            int userId = SessionManager.getInstance(requireContext())
                    .getUser()
                    .getId();
            ApiClient.getOrdersByUser(requireContext(), userId, handler);
        }
    }
}
