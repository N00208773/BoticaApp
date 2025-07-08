package com.example.boticaapp.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.boticaapp.R;
import com.example.boticaapp.api.ApiClient;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AdminTopPharmacyFragment extends Fragment {
    private TextView tvName, tvCount;
    private BarChart barChart;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup cont,
                             @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_admin_top_pharmacy, cont, false);
    }

    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle b) {
        super.onViewCreated(v,b);
        tvName   = v.findViewById(R.id.tvPharmacyName);
        tvCount  = v.findViewById(R.id.tvOrderCount);
        barChart = v.findViewById(R.id.barChart);

        // Llamada al endpoint que devuelve un ARRAY de { pharmacy_name, order_count }
        ApiClient.getTopPharmacy(getContext(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int status, Header[] headers, JSONArray response) {
                try {
                    List<BarEntry> entries = new ArrayList<>();
                    List<String> labels    = new ArrayList<>();

                    // Poblar barras y labels
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject o = response.getJSONObject(i);
                        String name = o.getString("pharmacy_name");
                        int count   = o.getInt("order_count");
                        entries.add(new BarEntry(i, count));
                        labels.add(name);

                        // Para el primer elemento, actualizamos también los TextView
                        if (i == 0) {
                            tvName.setText(name);
                            tvCount.setText("Pedidos: " + count);
                        }
                    }

                    // Dataset y configuración
                    BarDataSet ds = new BarDataSet(entries, "Pedidos");
                    BarData data = new BarData(ds);
                    data.setBarWidth(0.7f);

                    barChart.setData(data);
                    barChart.getDescription().setEnabled(false);

                    // Eje X con etiquetas de nombre de botica
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                    xAxis.setGranularity(1f);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                    barChart.animateY(800);
                    barChart.invalidate();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int status, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(),
                        "Error cargando estadísticas",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
