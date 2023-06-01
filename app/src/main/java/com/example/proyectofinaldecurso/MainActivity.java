package com.example.proyectofinaldecurso;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.proyectofinaldecurso.activities.HistoryActivity;
import com.example.proyectofinaldecurso.activities.NewRecordActivity;
import com.example.proyectofinaldecurso.activities.SettingCashfyActivity;
import com.example.proyectofinaldecurso.data.DatabaseHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mensaje_inicial, balanceTv;
    private Button boton_ingreso, boton_gasto;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DatabaseHelper db;


    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onResume() {
        super.onResume();
        actualBalance();
    }//solo se llama cuando la actividad esta en primer plano


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        setTheme(sharedPreferencesManager.getTheme());
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        initViews();
        initListeners();
        setupNavigationView();
        setupActioBarToggle();
    }

    public void actualBalance() {
        balanceTv.setText(String.format(Locale.getDefault(), "%,.2f " + sharedPreferencesManager.getMonedaSimbolo(), db.getBalance()));
    }

    private void initListeners() {
        boton_ingreso.setOnClickListener(this);
        boton_gasto.setOnClickListener(this);
        mensaje_inicial.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
       dialogClose();
    }

    public void dialogClose(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.cerrar_aplicaci_n_dialogo)
                .setMessage(R.string.pregunta_seguridad_dialogo)
                .setPositiveButton(R.string.si_dialogo, (dialog, which) -> finish())
                .setNegativeButton(R.string.no_dialogo, null)
                .show();
    }


    private void initViews() {

        balanceTv = findViewById(R.id.balanceTv);
        mensaje_inicial = findViewById(R.id.mensaje_inicial);
        boton_ingreso = findViewById(R.id.boton_ingreso_id);
        boton_gasto = findViewById(R.id.boton_gasto_id);
        mDrawerLayout = findViewById(R.id.Drawer_id);
        navigationView = findViewById(R.id.Navigation_view);
        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);//para mostrar un item menu

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingCashfyActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Ha pulsado ajustes", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void goToHistoryActivity() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent = new Intent(this, NewRecordActivity.class);
        if (id == boton_ingreso.getId()) {
            int modo = 1; // 1 igual a ingreso
            intent.putExtra("codigo", modo);
            startActivity(intent);
        } else if (id == boton_gasto.getId()) {
            int modo = 2; // 2 igual a gasto
            intent.putExtra("codigo", modo);
            actualBalance();
            startActivity(intent);
            System.out.println("se supone vale salir en la app");
        }
    }

    private void setupNavigationView() {

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.historial_menu) {
                goToHistoryActivity();
                return true;
            } else if (id == R.id.perfil_menu) {
                //TODO
                return true;
            }  else if (id == R.id.Acerca_de_mi_menu) {
                //TODO
                return true;
            } else if (id == R.id.Salir_menu) {
                //TODO
                dialogClose();
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void setupActioBarToggle() {
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, 0, 0);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Configurar un Listener para el estado del DrawerLayout
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                mDrawerLayout.setElevation(16);
            }
            @Override
            public void onDrawerOpened(View drawerView) {

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                // Restablecer la elevación cuando se cierra el menú
                mDrawerLayout.setElevation(0);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // No se necesita implementar
            }
        });
    }


    private void showCoinSelectionDialog() {
        Resources res = getResources();
        String[] opciones = res.getStringArray(R.array.nombres_de_monedas);
        String[] moneda = res.getStringArray(R.array.tipos_de_monedas);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, sharedPreferencesManager.getThemeDialog());
        builder.setTitle("Elige una opción")
                .setSingleChoiceItems(opciones, -1, (dialog, which) -> {
                    sharedPreferencesManager.setMonedaSimbolo(moneda[which]);
                    actualBalance();
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showThemeSelectionDialog() {
        Resources res = getResources();
        String[] opciones = res.getStringArray(R.array.temas_de_la_app);
        int[] estilos = {R.style.Theme_Light, R.style.Theme_Dark};
        int[] estilosDialogo = {R.style.LightDialog, R.style.DarkDialog};


        AlertDialog.Builder builder = new AlertDialog.Builder(this, sharedPreferencesManager.getThemeDialog());
        builder.setTitle("Elige una opción")
                .setSingleChoiceItems(opciones, -1, (dialog, which) -> {
                    sharedPreferencesManager.setTheme(estilos[which]);
                    sharedPreferencesManager.setThemeDialog(estilosDialogo[which]);
                    setTheme(sharedPreferencesManager.getTheme());

                    setContentView(R.layout.activity_main);
                    recreate();
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}