package com.example.boticaapp.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.boticaapp.R;
import com.example.boticaapp.models.User;
import java.util.List;

public class AdminUsersAdapter
        extends RecyclerView.Adapter<AdminUsersAdapter.VH> {

    private List<User> items;
    public AdminUsersAdapter(List<User> items) {
        this.items = items;
    }
    public void setItems(List<User> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_admin_user, p, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        User u = items.get(pos);
        h.tvId.setText("ID: " + u.getId());
        h.tvName.setText(u.getName());
        h.tvEmail.setText(u.getEmail());
        h.tvRole.setText("Rol: " + u.getRole());
        if ("empleado".equals(u.getRole())) {
            h.tvPharmacy.setVisibility(View.VISIBLE);
            h.tvPharmacy.setText("Farmacia: " + u.getPharmacyId());
        } else {
            h.tvPharmacy.setVisibility(View.GONE);
        }
    }

    @Override public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvId, tvName, tvEmail, tvRole, tvPharmacy;
        VH(View v) {
            super(v);
            tvId       = v.findViewById(R.id.tvUserId);
            tvName     = v.findViewById(R.id.tvUserName);
            tvEmail    = v.findViewById(R.id.tvUserEmail);
            tvRole     = v.findViewById(R.id.tvUserRole);
            tvPharmacy = v.findViewById(R.id.tvUserPharmacy);
        }
    }
}

