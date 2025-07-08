package com.example.boticaapp.ui.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.boticaapp.R;
import com.example.boticaapp.api.ApiClient;
import com.example.boticaapp.models.User;
import com.example.boticaapp.ui.auth.LoginActivity;
import com.example.boticaapp.utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileFragment extends Fragment {

    private EditText etName, etEmail, etRole, etPharmacy;
    private Button btnEdit, btnSave, btnLogout;
    private ImageView imgAvatar;
    private User user;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias UI
        imgAvatar  = view.findViewById(R.id.imgAvatar);
        etName     = view.findViewById(R.id.etName);
        etEmail    = view.findViewById(R.id.etEmail);
        etRole     = view.findViewById(R.id.etRole);
        etPharmacy = view.findViewById(R.id.etPharmacy);
        btnEdit    = view.findViewById(R.id.btnEdit);
        btnSave    = view.findViewById(R.id.btnSave);
        btnLogout  = view.findViewById(R.id.btnLogout);

        // Carga datos de sesión
        user = SessionManager.getInstance(requireContext()).getUser();
        if (user == null) return;

        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        etRole.setText(user.getRole());
        // Si es empleado, mostramos la farmacia
        if ("empleado".equals(user.getRole())) {
            etPharmacy.setText(String.valueOf(user.getPharmacyId()));
            view.findViewById(R.id.labelPharmacy).setVisibility(View.VISIBLE);
            etPharmacy.setVisibility(View.VISIBLE);
        }

        // Editar: habilita edición de nombre y email (no rol)
        btnEdit.setOnClickListener(v -> {
            etName.setEnabled(true);
            etEmail.setEnabled(true);
            btnSave.setEnabled(true);
            btnEdit.setEnabled(false);
        });

        // Guardar: validar y enviar al servidor
        btnSave.setOnClickListener(v -> {
            String newName  = etName.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newEmail)) {
                Toast.makeText(requireContext(), "Nombre y correo obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiClient.updateUser(  // Necesitas exponer este método en tu ApiClient
                    requireContext(),
                    user.getId(),
                    newName,
                    newEmail,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(requireContext(),
                                    response.optString("message","Datos actualizados"),
                                    Toast.LENGTH_SHORT).show();
                            // Guardamos en sesión y deshabilitamos edición
                            user.setName(newName);
                            user.setEmail(newEmail);
                            SessionManager.getInstance(requireContext()).saveUser(user);

                            etName.setEnabled(false);
                            etEmail.setEnabled(false);
                            btnSave.setEnabled(false);
                            btnEdit.setEnabled(true);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            String err = errorResponse!=null
                                    ? errorResponse.optString("error", throwable.getMessage())
                                    : throwable.getMessage();
                            Toast.makeText(requireContext(),
                                    "Error actualizando: "+err, Toast.LENGTH_LONG).show();
                        }
                    }
            );
        });

        // Cerrar sesión
        btnLogout.setOnClickListener(v -> {
            SessionManager.getInstance(requireContext()).clearSession();
            // Volver al login
            startActivity(new Intent(requireContext(), LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            requireActivity().finish();
        });
    }
}
