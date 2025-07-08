package com.example.boticaapp.ui.cliente;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.boticaapp.R;
import com.example.boticaapp.models.Order;
import java.util.List;

public class OrdersAdapter
        extends RecyclerView.Adapter<OrdersAdapter.VH> {

    public interface OnStatusChangeListener {
        void onChangeStatus(int orderId);
    }

    private List<Order> items;
    private final OnStatusChangeListener listener;

    public OrdersAdapter(List<Order> items, OnStatusChangeListener listener) {
        this.items = items;
        this.listener = listener;  // null para clientes
    }

    public void setItems(List<Order> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Order o = items.get(position);
        holder.tvId.setText("Pedido #" + o.getId());
        holder.tvStatus.setText("Estado: " + o.getStatus());
        holder.tvTotal.setText(String.format("Total: $%.2f", o.getTotal()));
        holder.tvDate.setText(o.getCreatedAt());

        // Mostrar el botón sólo si:
        // A) Hay un listener (empleado)
        // B) El estado es "pendiente"
        if (listener != null && "pendiente".equalsIgnoreCase(o.getStatus())) {
            holder.btnMarkPaid.setVisibility(View.VISIBLE);
            holder.btnMarkPaid.setOnClickListener(v -> listener.onChangeStatus(o.getId()));
        } else {
            holder.btnMarkPaid.setVisibility(View.GONE);
            holder.btnMarkPaid.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvId, tvStatus, tvTotal, tvDate;
        Button btnMarkPaid;

        VH(View v) {
            super(v);
            tvId         = v.findViewById(R.id.tvOrderId);
            tvStatus     = v.findViewById(R.id.tvOrderStatus);
            tvTotal      = v.findViewById(R.id.tvOrderTotal);
            tvDate       = v.findViewById(R.id.tvOrderDate);
            btnMarkPaid  = v.findViewById(R.id.btnMarkPaid);
        }
    }
}
