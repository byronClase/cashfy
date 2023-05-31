package com.example.proyectofinaldecurso.fragments.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinaldecurso.R;
import com.example.proyectofinaldecurso.fragments.data.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;

    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkboxCategory);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.checkBox.setChecked(category.isChecked());

        // Aquí puedes establecer un onClickListener en el CheckBox para cambiar el estado de "isSelected" de la categoría cuando el usuario haga clic en él
        // No olvides guardar este cambio en tu base de datos si es necesario
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
