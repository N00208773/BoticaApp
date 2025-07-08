package com.example.boticaapp.ui.cliente;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;    // <--- import
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.boticaapp.R;
import com.example.boticaapp.api.ApiClient;
import com.example.boticaapp.models.CartItem;
import com.example.boticaapp.models.Medicine;
import com.example.boticaapp.utils.CartManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView rv;
    private MedicineAdapter adapter;
    private ProgressBar progress;
    private SwipeRefreshLayout swipe;
    private SearchView svSearch;           // nueva

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        rv       = view.findViewById(R.id.rvMedicines);
        progress = view.findViewById(R.id.progressBar);
        swipe    = view.findViewById(R.id.swipeRefresh);
        svSearch = view.findViewById(R.id.svSearch);     // buscar referencia

        adapter = new MedicineAdapter(new ArrayList<>(), medicine -> {
            CartManager.getInstance().addItem(new CartItem(medicine, 1));
            Toast.makeText(getContext(),
                    medicine.getName() + " añadido al carrito",
                    Toast.LENGTH_SHORT).show();
        });

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        swipe.setOnRefreshListener(this::loadMedicines);
        setupSearch();                   // configurar SearchView
        loadMedicines();
    }

    private void setupSearch() {
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                // Evitamos recargar teclado
                svSearch.clearFocus();
                return true;
            }
            @Override public boolean onQueryTextChange(String newText) {
                // Si está vacío, cargamos todo; si no, buscamos
                if (newText == null || newText.trim().isEmpty()) {
                    loadMedicines();
                } else {
                    searchMedicines(newText.trim());
                }
                return true;
            }
        });
    }

    private void searchMedicines(String name) {
        progress.setVisibility(View.VISIBLE);
        ApiClient.searchMedicines(getContext(), name, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                handleMedicinesArray(response);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.has("data")) {
                        handleMedicinesArray(response.getJSONArray("data"));
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
                finally {
                    progress.setVisibility(View.GONE);
                    swipe.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                Log.e("HomeFragment","Error búsqueda JSON Obj", e);
                progress.setVisibility(View.GONE);
                swipe.setRefreshing(false);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String resp, Throwable t) {
                Log.e("HomeFragment","Error búsqueda texto: "+resp, t);
                progress.setVisibility(View.GONE);
                swipe.setRefreshing(false);
            }
            private void handleMedicinesArray(JSONArray arr) {
                progress.setVisibility(View.GONE);
                swipe.setRefreshing(false);
                Type listType = new TypeToken<List<Medicine>>(){}.getType();
                List<Medicine> meds = new Gson().fromJson(arr.toString(), listType);
                adapter.setItems(meds);
            }
        });
    }

    private void loadMedicines() {
        progress.setVisibility(View.VISIBLE);
        ApiClient.getMedicines(getContext(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                handleMedicinesArray(response);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.has("data")) {
                        handleMedicinesArray(response.getJSONArray("data"));
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
                finally {
                    progress.setVisibility(View.GONE);
                    swipe.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                Log.e("HomeFragment","Error JSON Obj: "+statusCode, e);
                progress.setVisibility(View.GONE);
                swipe.setRefreshing(false);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String resp, Throwable t) {
                Log.e("HomeFragment","Error texto: "+resp, t);
                progress.setVisibility(View.GONE);
                swipe.setRefreshing(false);
            }
            private void handleMedicinesArray(JSONArray arr) {
                progress.setVisibility(View.GONE);
                swipe.setRefreshing(false);
                Type listType = new TypeToken<List<Medicine>>(){}.getType();
                List<Medicine> meds = new Gson().fromJson(arr.toString(), listType);
                adapter.setItems(meds);
            }
        });
    }
}
