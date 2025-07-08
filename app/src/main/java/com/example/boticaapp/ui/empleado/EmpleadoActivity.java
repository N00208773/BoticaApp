package com.example.boticaapp.ui.empleado;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.boticaapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmpleadoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empleado);

        // Encuentra el NavHostFragment y configura la navegación con el BottomNavigationView
        NavHostFragment navHost = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_empleado);
        NavController navController = navHost.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_empleado);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}