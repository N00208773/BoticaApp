package com.example.boticaapp.ui.cliente;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.boticaapp.R;
import com.example.boticaapp.api.ApiClient;
import com.example.boticaapp.models.CartItem;
import com.example.boticaapp.utils.CartManager;
import com.example.boticaapp.utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;
import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView rvCart;
    private CartAdapter adapter;
    private TextView tvTotal;
    private Button btnPay;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override public void onViewCreated(@NonNull View view,
                                        @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvCart  = view.findViewById(R.id.rvCart);
        tvTotal = view.findViewById(R.id.tvCartTotal);
        btnPay  = view.findViewById(R.id.btnCheckout);

        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(CartManager.getInstance().getItems());
        rvCart.setAdapter(adapter);

        btnPay.setOnClickListener(v -> confirmAndPay());
    }

    @Override public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        double total = CartManager.getInstance().getTotal();
        tvTotal.setText(String.format("Total: $%.2f", total));
    }

    private void confirmAndPay() {
        List<CartItem> items = CartManager.getInstance().getItems();
        if (items.isEmpty()) {
            Toast.makeText(getContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }
        // Confirmación simple
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirmar pago")
                .setMessage("¿Deseas pagar $" + String.format("%.2f", CartManager.getInstance().getTotal()) + "?")
                .setPositiveButton("Sí", (d, i) -> performPayment(items))
                .setNegativeButton("No", null)
                .show();
    }

    private void performPayment(List<CartItem> items) {
        btnPay.setEnabled(false);
        Context ctx = getContext();
        if (ctx == null) {
            Toast.makeText(requireActivity(), "Error: contexto inválido", Toast.LENGTH_SHORT).show();
            btnPay.setEnabled(true);
            return;
        }

        // Montar array de ítems
        JSONArray arr = new JSONArray();
        for (CartItem ci : items) {
            JSONObject o = new JSONObject();
            try {
                o.put("medicine_id", ci.getMedicine().getId());
                o.put("quantity",    ci.getQuantity());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            arr.put(o);
        }

        int userId     = SessionManager.getInstance(ctx).getUser().getId();
        int pharmacyId = items.get(0).getMedicine().getPharmacyId();

        ApiClient.createOrder(
                ctx,
                userId,
                pharmacyId,
                arr,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        String msg = response.optString("message", "Pedido recibido");
                        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();

                        // Limpiar carrito y actualizar UI
                        CartManager.getInstance().clear();
                        adapter.notifyDataSetChanged();
                        tvTotal.setText("Total: $0.00");
                        btnPay.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        String err = errorResponse != null
                                ? errorResponse.optString("error", throwable.getMessage())
                                : throwable.getMessage();
                        Toast.makeText(ctx, "Error al pagar: " + err, Toast.LENGTH_LONG).show();
                        btnPay.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Toast.makeText(ctx, "Error al pagar", Toast.LENGTH_LONG).show();
                        btnPay.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(ctx, "Error al pagar: " + responseString, Toast.LENGTH_LONG).show();
                        btnPay.setEnabled(true);
                    }
                }
        );
    }
}
