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
import com.example.boticaapp.models.Medicine;
import com.example.boticaapp.ui.cliente.MedicineAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AdminProductsFragment extends Fragment {
    private RecyclerView rv;
    private MedicineAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_products, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        rv = v.findViewById(R.id.rvAdminProducts);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new MedicineAdapter(new ArrayList<>(), medicine -> { /* no-op */ });
        rv.setAdapter(adapter);

        loadAllMedicines();
    }

    private void loadAllMedicines() {
        ApiClient.getMedicines(  // ← use getMedicines, not getAllMedicines
                requireContext(),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Type listType = new TypeToken<List<Medicine>>(){}.getType();
                        List<Medicine> meds = new Gson().fromJson(response.toString(), listType);
                        adapter.setItems(meds);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        String err = errorResponse != null
                                ? errorResponse.optString("error", throwable.getMessage())
                                : throwable.getMessage();
                        Toast.makeText(requireContext(),
                                "Error cargando productos: " + err,
                                Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Toast.makeText(requireContext(),
                                "Error cargando productos",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
