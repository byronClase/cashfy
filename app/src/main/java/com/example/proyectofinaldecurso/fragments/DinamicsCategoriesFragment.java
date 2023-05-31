package com.example.proyectofinaldecurso.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;

import com.example.proyectofinaldecurso.R;
import com.example.proyectofinaldecurso.fragments.adapter.CategoryAdapter;
import com.example.proyectofinaldecurso.fragments.data.Category;

import java.util.ArrayList;
import java.util.List;

public class DinamicsCategoriesFragment extends Fragment implements SearchView.OnQueryTextListener {

    private List<Category> categories;
    private CategoryAdapter adapter;
    private SearchView txtBuscar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dinamics_categories, container, false);

        categories = new ArrayList<>();
        // Aquí podrías obtener las categorías de tu base de datos y añadirlas a la lista "categories"

        adapter = new CategoryAdapter(categories);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        /*EditText categorySearchEditText = view.findViewById(R.id.editTextSearch);
        categorySearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Aquí puedes implementar un filtrado de las categorías en base a lo que el usuario escriba en el EditText
                // Por ejemplo, podrías mostrar solo las categorías cuyo nombre contenga el texto que el usuario escribió
                // Recuerda llamar a "adapter.notifyDataSetChanged()" después de modificar la lista de categorías
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        txtBuscar.setOnQueryTextListener(this);

        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    // Aquí puedes implementar otros métodos del fragmento, como por ejemplo para manejar la creación de nuevas categorías
}
