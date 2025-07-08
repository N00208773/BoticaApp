package com.example.boticaapp.ui.admin;

import android.os.Bundle;
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
import com.example.boticaapp.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AdminUsersFragment extends Fragment {
    private RecyclerView rv;
    private AdminUsersAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup cont,
                             @Nullable Bundle savedInstanceState) {
        return inf.inflate(R.layout.fragment_admin_users, cont, false);
    }

    @Override public void onViewCreated(
            @NonNull View v,
            @Nullable Bundle s) {
        super.onViewCreated(v, s);
        rv = v.findViewById(R.id.rvUsers);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AdminUsersAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        // Llamada API
        ApiClient.getAllUsers(requireContext(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int status, Header[] h, JSONArray response) {
                Type t = new TypeToken<List<User>>(){}.getType();
                List<User> users = new Gson().fromJson(response.toString(), t);
                adapter.setItems(users);
            }
            @Override
            public void onFailure(int status, Header[] h, String resp, Throwable t) {
                Toast.makeText(requireContext(),
                        "Error cargando usuarios",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

