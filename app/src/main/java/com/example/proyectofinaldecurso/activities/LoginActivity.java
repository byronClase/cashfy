package com.example.proyectofinaldecurso.activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.proyectofinaldecurso.MainActivity;
import com.example.proyectofinaldecurso.R;
import com.example.proyectofinaldecurso.SharedPreferencesManager;
import com.example.proyectofinaldecurso.utils.BiometricAuthCallback;
import com.example.proyectofinaldecurso.utils.BiometricUtils;
import com.google.android.material.tabs.TabLayout;

public class LoginActivity extends AppCompatActivity implements BiometricAuthCallback {

    Button loginButton;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        setTheme(sharedPreferencesManager.getTheme());
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(view -> {
            showBiometricPrompt();
            checkBiometricCapability();
        });
    }

    @Override
    public void onSuccess() {
        sharedPreferencesManager.setAuthentication(true);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError() {
        finish();
    }

    @Override
    public void onNotRecognized() {
        Toast.makeText(this, "Huella no reconocida", Toast.LENGTH_LONG).show();
    }

    private void checkBiometricCapability() {
        if (!BiometricUtils.isDeviceReady(this)) {
            finish();
        } else {
            Toast.makeText(this, "Biometria disponible", Toast.LENGTH_LONG).show();
        }
    }

    private void showBiometricPrompt() {
        BiometricUtils.showPrompt("Autenticación Biométrica", "Introduce tus credenciales,", "para acceder a Cashfy.", "Cancelar", this, this);
    }
}