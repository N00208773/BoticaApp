package com.example.boticaapp.ui.cliente;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.boticaapp.R;
import com.example.boticaapp.models.CartItem;
import java.util.List;

public class CartAdapter
        extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final List<CartItem> items;

    public CartAdapter(List<CartItem> items) {
        this.items = items;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder h, int pos) {
        CartItem ci = items.get(pos);
        h.tvName.setText(ci.getMedicine().getName());
        h.tvQty .setText("x" + ci.getQuantity());
        h.tvSub .setText(String.format("$%.2f", ci.getSubtotal()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvSub;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCartMedicineName);
            tvQty  = itemView.findViewById(R.id.tvCartQuantity);
            tvSub  = itemView.findViewById(R.id.tvCartSubtotal);
        }
    }
}
