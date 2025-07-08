package com.example.boticaapp.ui.empleado;

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
import com.example.boticaapp.ui.cliente.OrdersAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EmployeeOrdersFragment extends Fragment {
    private RecyclerView rv;
    private OrdersAdapter adapter;
    private int pharmacyId;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employee_orders, container, false);
    }

    @Override public void onViewCreated(@NonNull View view,
                                        @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.rvEmployeeOrders);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 1) Instanciamos el adapter con el listener
        adapter = new OrdersAdapter(new ArrayList<>(), orderId -> {
            // Al pulsar el botón “Marcar como pagado”
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
                            String err = (errorResponse != null)
                                    ? errorResponse.optString("error", throwable.getMessage())
                                    : throwable.getMessage();
                            Toast.makeText(requireContext(),
                                    "Error JSON: " + err,
                                    Toast.LENGTH_LONG).show();
                        }

                        public void onFailure(int statusCode, Header[] headers, JSONArray errorResponse) {
                            Toast.makeText(requireContext(),
                                    "Error JSONArray al actualizar estado",
                                    Toast.LENGTH_LONG).show();
                        }

                        // <---- esta es la clave:
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            // Aquí puedes loguear la cadena que recibes y mostrar un toast
                            Log.e("EmpOrders", "Error texto al actualizar: " + responseString, throwable);
                            Toast.makeText(requireContext(),
                                    "Error al actualizar: " + responseString,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            );

        });
        rv.setAdapter(adapter);

        // 2) Leemos pharmacy_id que guardaste en la sesión al login
        pharmacyId = requireActivity().getIntent()
                .getIntExtra("pharmacy_id", -1);

        // 3) Cargamos los pedidos
        loadOrders();
    }

    private void loadOrders() {
        if (pharmacyId < 0) {
            Toast.makeText(requireContext(),
                    "Pharmacy ID inválido",
                    Toast.LENGTH_LONG).show();
            return;
        }

        ApiClient.getOrdersByPharmacy(
                requireContext(),
                pharmacyId,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Type listType = new TypeToken<List<Order>>(){}.getType();
                        List<Order> orders = new Gson()
                                .fromJson(response.toString(), listType);
                        adapter.setItems(orders);
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
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Toast.makeText(requireContext(),
                                "Error cargando pedidos",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
