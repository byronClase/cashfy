package com.example.proyectofinaldecurso.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.proyectofinaldecurso.R;
import com.example.proyectofinaldecurso.fragments.SettingAboutUsFragment;
import com.example.proyectofinaldecurso.fragments.SettingUserFragment;
import com.example.proyectofinaldecurso.fragments.SettingsHistorialFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;

public class SettingCashfyActivity extends AppCompatActivity {

    ImageButton backButton;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_cashfy);
        initView();
        initListener();
    }

    private void initView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        backButton = findViewById(R.id.imageButtonBackSettings);
    }

    /*
    TODO HABRIA QUE ARREGLAR EL MAIN ACTIVITY PARA QUE LLAME AL LOGIN
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class); // Reemplaza MenuLateralActivity con tu clase de actividad correcta
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Esta bandera limpia todas las actividades en la pila por encima de MenuLateralActivity
        startActivity(intent);
        finish();
    }*/


    private void initListener() {
        loadDefaultFragment();
        backButton.setOnClickListener(v -> finish());
        bottomNavigationView.setOnItemSelectedListener((OnNavigationItemSelectedListener) item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;
            if (itemId == R.id.settings_historial) {
                Log.d("TAG", "Estoy en ajustes de base de datos");
                selectedFragment = new SettingsHistorialFragment();
            } else if (itemId == R.id.settings_nosotros) {
                Log.d("TAG", "Estoy en about us");
                selectedFragment = new SettingAboutUsFragment();
            } else if (itemId == R.id.settings_name) {
                Log.d("TAG", "Estoy en personal");
                selectedFragment = new SettingUserFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_settings, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    private void loadDefaultFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_settings, new SettingsHistorialFragment())
                .commit();
    }

}