package com.example.proyectofinaldecurso.activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinaldecurso.R;
import com.example.proyectofinaldecurso.SharedPreferencesManager;
import com.example.proyectofinaldecurso.adapters.Adapter2;
import com.example.proyectofinaldecurso.data.Record;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ImageButton imageButtonExitActivity;
    private FloatingActionButton fab;
    RecyclerView recyclerView;
    SharedPreferencesManager sharedPreferencesManager;
    Adapter2 adapter;
    private boolean isFabVisible = false;

    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        setTheme(sharedPreferencesManager.getTheme());
        setContentView(R.layout.recyclerviewdemo);
        initViewHistoryActivity();
        initListenerHistoryActivity();
    }

    private void setFabVisible(boolean isVisible) {
        if (isVisible && !isFabVisible) {
            fab.setVisibility(View.VISIBLE);
            isFabVisible = true;
        } else if (!isVisible && isFabVisible) {
            fab.setVisibility(View.INVISIBLE);
            isFabVisible = false;
        }
    }


    private void initListenerHistoryActivity() {
        imageButtonExitActivity.setOnClickListener(v -> finish());

        // Agrega un OnScrollListener al RecyclerView
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (firstVisibleItemPosition == 0) {
                    // Si el usuario está en la parte superior de la lista,
                    // mostrar el FAB y configurarlo para que lleve al usuario al final de la lista
                    fab.setImageResource(R.drawable.ic_down_arrow);
                    setFabVisible(true);
                    fab.setOnClickListener(v -> recyclerView.smoothScrollToPosition(totalItemCount - 1));
                } else if (lastVisibleItemPosition == totalItemCount - 1) {
                    // Si el usuario está en la parte inferior de la lista,
                    // mostrar el FAB y configurarlo para que lleve al usuario de vuelta al principio
                    fab.setImageResource(R.drawable.ic_up_arrow);
                    setFabVisible(true);
                    fab.setOnClickListener(v -> recyclerView.smoothScrollToPosition(0));
                } else if(totalItemCount==0){
                    setFabVisible(false);
                }else {
                    // Si el usuario no está ni en la parte superior ni en la inferior de la lista, ocultar el FAB
                    setFabVisible(false);
                }
            }
        });
    }
                /* {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (firstVisibleItemPosition > 0) {
                        // Si el usuario ha desplazado la lista hacia abajo (no está en la parte superior),
                        // mostrar el FAB y configurarlo para que lleve al usuario de vuelta al principio
                        fab.setImageResource(R.drawable.ic_up_arrow);
                        setFabVisible(true);
                        fab.setOnClickListener(v -> recyclerView.smoothScrollToPosition(0));
                    } else if (totalItemCount > 0) {
                        // Si el usuario está en la parte superior pero hay elementos en la lista,
                        // mostrar el FAB y configurarlo para que lleve al usuario al final de la lista
                        fab.setImageResource(R.drawable.ic_down_arrow);
                        setFabVisible(true);
                        fab.setOnClickListener(v -> recyclerView.smoothScrollToPosition(totalItemCount - 1));
                    } else {
                        // Si no hay elementos en la lista, ocultar el FAB
                        setFabVisible(false);
                    }
                }
            });*/


    private void initViewHistoryActivity() {
        fab = findViewById(R.id.floatingActionButtonRec);
        imageButtonExitActivity = findViewById(R.id.imageButtonExitHistory);
        recyclerView = findViewById(R.id.recyclerViewDemo1);

        adapter = new Adapter2(this);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }
}
