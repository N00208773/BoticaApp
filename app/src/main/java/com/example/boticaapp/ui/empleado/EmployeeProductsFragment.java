package com.example.boticaapp.ui.empleado;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.boticaapp.R;
import com.example.boticaapp.api.ApiClient;
import com.example.boticaapp.models.Medicine;
import com.example.boticaapp.ui.cliente.MedicineAdapter;
import com.example.boticaapp.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EmployeeProductsFragment extends Fragment {
    private RecyclerView rv;
    private MedicineAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employee_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        rv = v.findViewById(R.id.rvProducts);
        adapter = new MedicineAdapter(new ArrayList<>());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        int pharmacyId = SessionManager.getInstance(getContext())
                .getUser()
                .getPharmacyId();

        ApiClient.getMedicinesByPharmacy(
                getContext(),
                pharmacyId,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Type listType = new TypeToken<List<Medicine>>(){}.getType();
                        List<Medicine> meds = new Gson().fromJson(response.toString(), listType);
                        adapter.setItems(meds);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        // manejar error
                    }
                }
        );
    }
}