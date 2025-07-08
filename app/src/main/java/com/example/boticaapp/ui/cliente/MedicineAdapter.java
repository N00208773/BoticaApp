package com.example.boticaapp.ui.cliente;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.boticaapp.R;
import com.example.boticaapp.models.Medicine;
import java.util.List;

public class MedicineAdapter
        extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    private List<Medicine> items;
    public MedicineAdapter(List<Medicine> items) {
        this.items = items;
    }

    public void setItems(List<Medicine> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
    public interface OnAddClickListener {
        void onAddClick(Medicine medicine);
    }

    private OnAddClickListener listener;
    public MedicineAdapter(List<Medicine> items, OnAddClickListener l) {
        this.items = items;
        this.listener = l;
    }


    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Medicine m = items.get(pos);
        h.tvName.setText(m.getName());
        h.tvPrice.setText(String.format("$%.2f", m.getPrice()));
        h.btnAdd.setOnClickListener(v -> {
            if (listener != null) listener.onAddClick(m);
        });
    }

    @Override
    public int getItemCount() { return items != null ? items.size() : 0; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        Button btnAdd;
        ViewHolder(View itemView) {
            super(itemView);
            tvName  = itemView.findViewById(R.id.tvMedicineName);
            tvPrice = itemView.findViewById(R.id.tvMedicinePrice);
            btnAdd  = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
