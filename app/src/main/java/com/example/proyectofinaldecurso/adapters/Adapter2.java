package com.example.proyectofinaldecurso.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinaldecurso.R;
import com.example.proyectofinaldecurso.SharedPreferencesManager;
import com.example.proyectofinaldecurso.data.DatabaseHelper;
import com.example.proyectofinaldecurso.data.Record;
import com.example.proyectofinaldecurso.data.TransactionType;
import com.example.proyectofinaldecurso.fragments.DinamicsCategoriesFragment;
import com.example.proyectofinaldecurso.utils.DateTimeUtils;
import com.example.proyectofinaldecurso.utils.DecimalDigitsInputFilter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Adapter2 extends RecyclerView.Adapter<Adapter2.ViewHolder>{

    private List<Record> recordList; // Cambia el tipo de datos según tus necesidades
    private Context context;
    DatabaseHelper db;
    private SharedPreferencesManager sharedPreferencesManager;
    private int expandedPosition = -1; // Inicialmente no hay ningún ítem expandido


    public Adapter2(Context context) {
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context.getApplicationContext());
        this.context = context;
        this.recordList = new ArrayList<>();
        db = new DatabaseHelper(context);
        try {
            recordList.addAll(db.getAllRecordsSortedByDateInterleaved(context));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView dateTv;
        TextView amountTv;
        TextView categoryTv;
        TextView descriptionTv;
        TextView timeTv;
        ImageView arrowImageView;
        ImageButton deleteButton, editButton;
        View expandedContent;

        public ViewHolder(View itemView) {
            super(itemView);
            initViewsRecordAdapter(itemView);

        }

        public void initViewsRecordAdapter(View convertView) {
            dateTv = convertView.findViewById(R.id.dateTextView);
            amountTv = convertView.findViewById(R.id.amountTextView);
            categoryTv = convertView.findViewById(R.id.categoryTextView);
            descriptionTv = convertView.findViewById(R.id.textViewDescriptionRecord);
            timeTv = convertView.findViewById(R.id.textViewTimeRecord);
            arrowImageView = convertView.findViewById(R.id.arrowImageView);
            expandedContent = convertView.findViewById(R.id.expanded_content);
            deleteButton = convertView.findViewById(R.id.ImageButtonDeleteRecord);
            editButton = convertView.findViewById(R.id.ImageButtonEditRecord);
        }

        void bind(Record record, int position) {
            // Comprueba si este ítem es el que se supone que debe estar expandido
            if (position == expandedPosition) {
                expandedContent.setVisibility(View.VISIBLE);
            } else {
                expandedContent.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                // Si el ítem ya está expandido, lo contrae. Si no, lo expande.
                expandedPosition = position == expandedPosition ? -1 : position;
                notifyDataSetChanged();  // Esto hace que se llame nuevamente a bind() para todos los ítems, actualizando sus vistas
            });

            // Aquí puedes establecer el resto de tus vistas a partir de los datos en "record"
            deleteButton.setOnClickListener(v -> deleteRecordDialog(position, record));
            editButton.setOnClickListener(v -> updateRecordDialog(position, record));
        }


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false);
        return new ViewHolder(view);
    }

    private void updateRecordDialog(int position, Record record) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
        View dialogView = inflater.inflate(R.layout.dialogadapterupdate, null);

        EditText editTextAmount = dialogView.findViewById(R.id.editTextAmountDialog);
        EditText editTextCategory = dialogView.findViewById(R.id.editTextTextCategoryDialog);
        EditText editTextDescription = dialogView.findViewById(R.id.editTextTextDescriptionDialog);
        Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdateDialog);
        Button buttonUpdateExit = dialogView.findViewById(R.id.buttonExitUpdateDialog);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        editTextAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});

        buttonUpdate.setOnClickListener(v -> {
            String amountDialog = editTextAmount.getText().toString();
            if (amountDialog.isEmpty()) {
                editTextAmount.setError("La cantidad no puede estar vacía.");
            } else {
                Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
                Matcher matcher = pattern.matcher(amountDialog);
                if (!matcher.matches()) {
                    editTextAmount.setError("No se puede introducir esa cantidad.");
                } else {
                    // Si too es correcto, actualiza el registro y cierra el diálogo.
                    // Aquí puedes poner tu código de actualización del registro.

                    //editTextCantidad.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});

                    // Actualiza el registro con los nuevos valores
                    record.setAmount(Double.valueOf(amountDialog));
                    record.setDescription(editTextDescription.getText().toString());
                    record.setCategory(editTextCategory.getText().toString());

                    // Actualiza el registro en la base de datos


                    if (record.getType() == TransactionType.EXPENSE) {
                        db.updateRecordExpense(record);
                    } else {
                        db.updateRecordIncome(record);
                    }

                    //TODO
                    //COLOCAR el fragment de categoria dinamica
                    // Actualiza el registro en la lista y notifica al adaptador
                    recordList.set(position, record);
                    notifyItemChanged(position);
                    dialog.dismiss();
                }
            }
        });

        buttonUpdateExit.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    private void deleteRecordDialog(final int position, Record record) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Estás seguro de que quieres eliminar este registro?");

        builder.setPositiveButton("Sí", (dialog, which) -> {

            if (record.getType() == TransactionType.EXPENSE) {

                db.deleteSingleRecordExpense(String.valueOf(record.getId()));
            } else {

                db.deleteSingleRecordIncome(String.valueOf(record.getId()));
            }
            recordList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // Si el usuario hace clic en "No", simplemente cierra el diálogo
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Record record = recordList.get(position);
        renderComponentsItem(record, holder);
        holder.bind(record, position); // Asegúrate de llamar a bind() para ajustar la visibilidad de expandedContent
    }

    public void renderComponentsItem(Record record, ViewHolder holder) {
        // Pasamos el objeto Calendar a los métodos de formateo
        String formattedDateTime = DateTimeUtils.formatDateNewString(record.getDate());
        holder.dateTv.setText(formattedDateTime);
        String amountText = (record.getType() == TransactionType.EXPENSE ? "-" : "") + String.format(Locale.getDefault(), "%.2f", record.getAmount()) + sharedPreferencesManager.getMonedaSimbolo();
        holder.amountTv.setText(amountText);
        holder.categoryTv.setText(record.getCategory());
        holder.descriptionTv.setText(record.getDescription());
        // Pasamos el objeto Calendar al método de formateo del tiempo
        String formattedTime = DateTimeUtils.formatTimeNewString(record.getDate());
        holder.timeTv.setText(formattedTime);

        // Aplicar colores de fondo en función del tipo de transacción
        if (record.getType() == TransactionType.EXPENSE) {
            holder.arrowImageView.setImageResource(R.drawable.ic_arrow_expense);
        } else {
            holder.arrowImageView.setImageResource(R.drawable.ic_arrow_income);
        }
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }


}

