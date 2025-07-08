package com.example.boticaapp.ui.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.boticaapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        NavHostFragment navHost = (NavHostFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_admin);  // debe coincidir con tu XML

        if (navHost == null) {
            throw new IllegalStateException(
                    "No encontré <fragment android:id=\"@+id/nav_host_admin\" /> en activity_admin.xml"
            );
        }

        NavController navController = navHost.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_admin);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}

