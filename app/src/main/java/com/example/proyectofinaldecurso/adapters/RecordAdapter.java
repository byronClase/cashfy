package com.example.proyectofinaldecurso.adapters;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.proyectofinaldecurso.R;
import com.example.proyectofinaldecurso.SharedPreferencesManager;
import com.example.proyectofinaldecurso.data.DatabaseHelper;
import com.example.proyectofinaldecurso.data.Record;
import com.example.proyectofinaldecurso.data.TransactionType;
import com.example.proyectofinaldecurso.utils.DateTimeUtils;
import com.example.proyectofinaldecurso.utils.LocaleUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecordAdapter extends BaseAdapter {
    private Context context;
    private List<Record> records;
    private int expandedPosition = ListView.INVALID_POSITION;
    DatabaseHelper db;
    private SharedPreferencesManager sharedPreferencesManager;
    TextView dateTv;
    TextView amountTv;
    TextView categoryTv;
    TextView descriptionTv;
    TextView timeTv;
    ImageView arrowImageView;

    public RecordAdapter(Context context) throws ParseException {
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context.getApplicationContext());
        this.context = context;
        this.records = new ArrayList<>();
        db = new DatabaseHelper(context);
        records.addAll(db.getAllRecordsSortedByDateInterleaved(context));
        LocaleUtils.setLocale(context, "es", "ES");
    }


    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        return records.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    public void initViewsRecordAdapter(View convertView) {
        dateTv = convertView.findViewById(R.id.dateTextView);
        amountTv = convertView.findViewById(R.id.amountTextView);
        categoryTv = convertView.findViewById(R.id.categoryTextView);
        descriptionTv = convertView.findViewById(R.id.textViewDescriptionRecord);
        timeTv = convertView.findViewById(R.id.textViewTimeRecord);
        arrowImageView = convertView.findViewById(R.id.arrowImageView);
    }

    void initListenerRecorAdapter(int position, View convertView, View expandedContent, View deleteItemEc, View editItemEc, Record record) {
        // Comprueba si este ítem es el que se supone que debe estar expandido.

        if (position == expandedPosition) {
            Log.d(TAG, "************************ Pos visible: " + position);
            expandedContent.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "************************ Pos no visible: " + position);
            expandedContent.setVisibility(View.GONE);
        }

        // Configura el oyente de clics para expandir/contraer el ítem cuando se le haga clic.
        convertView.setOnClickListener(v -> {
            // Si este ítem está actualmente expandido, contrae este ítem.
            if (position == expandedPosition) {
                expandedContent.setVisibility(View.GONE);
                expandedPosition = ListView.INVALID_POSITION;

            }
            // Si otro ítem está expandido, contrae ese ítem y expande este.
            else {
                if (expandedPosition != ListView.INVALID_POSITION) {
                    // Esto causará que se llame a getView en la posición previamente expandida,
                    // lo que contraerá ese ítem.
                    notifyDataSetChanged();
                }
                expandedContent.setVisibility(View.VISIBLE);
                expandedPosition = position;
            }
        });

        deleteItemEc.setOnClickListener(v -> {
            deleteRecordDialog(position, record);
        });

        editItemEc.setOnClickListener(v -> {
            editRecordDialog(position, record);
        });

    }

    public void editRecordDialog(final int position, final Record record) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Editar registro");

        // Crea un layout para el diálogo
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Crea un campo de entrada para la cantidad
        final EditText amountField = new EditText(context);
        amountField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        amountField.setText(String.valueOf(record.getAmount()));
        layout.addView(amountField);

        // Crea un campo de entrada para la descripción
        final EditText descriptionField = new EditText(context);
        descriptionField.setText(record.getDescription());
        layout.addView(descriptionField);

        // Crea un campo de entrada para la categoría
        final EditText categoryField = new EditText(context);
        categoryField.setText(record.getCategory());
        layout.addView(categoryField);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Actualiza el registro con los nuevos valores
                record.setAmount(Double.valueOf(amountField.getText().toString()));
                record.setDescription(descriptionField.getText().toString());
                record.setCategory(categoryField.getText().toString());

                // Actualiza el registro en la base de datos


                if (record.getType() == TransactionType.EXPENSE) {
                    db.updateRecordExpense(record);
                } else {
                    db.updateRecordIncome(record);
                }


                // Actualiza el registro en la lista y notifica al adaptador
                records.set(position, record);
              //  notifyItemChanged(position);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario hace clic en "Cancelar", simplemente cierra el diálogo
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void deleteRecordDialog(final int position, Record record) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Estás seguro de que quieres eliminar este registro?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (record.getType() == TransactionType.EXPENSE) {
                    records.remove(position);
                    db.deleteSingleRecordExpense(String.valueOf(record.getId()));
                } else {
                    records.remove(position);
                    db.deleteSingleRecordIncome(String.valueOf(record.getId()));
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario hace clic en "No", simplemente cierra el diálogo
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.record_item, parent, false);
        }
        initViewsRecordAdapter(convertView);
        Record record = (Record) getItem(position);
        final View expandedContent = convertView.findViewById(R.id.expanded_content);
        final View editItemEc = convertView.findViewById(R.id.ImageButtonEditRecord);
        final View deleteItemEc = convertView.findViewById(R.id.ImageButtonDeleteRecord);
        initListenerRecorAdapter(position, convertView, expandedContent, deleteItemEc, editItemEc, record);
        renderComponentsItem(record);
        return convertView;
    }


    public void renderComponentsItem(Record record) {
        // Pasamos el objeto Calendar a los métodos de formateo
        String formattedDateTime = DateTimeUtils.formatDateNewString(record.getDate());
        dateTv.setText(formattedDateTime);
        String amountText = (record.getType() == TransactionType.EXPENSE ? "-" : "") + String.format(Locale.getDefault(), "%.2f", record.getAmount()) + sharedPreferencesManager.getMonedaSimbolo();
        amountTv.setText(amountText);
        categoryTv.setText(record.getCategory());
        descriptionTv.setText(record.getDescription());
        // Pasamos el objeto Calendar al método de formateo del tiempo
        String formattedTime = DateTimeUtils.formatTimeNewString(record.getDate());
        timeTv.setText(formattedTime);

        // Aplicar colores de fondo en función del tipo de transacción
        if (record.getType() == TransactionType.EXPENSE) {
            arrowImageView.setImageResource(R.drawable.ic_arrow_expense);
        } else {
            arrowImageView.setImageResource(R.drawable.ic_arrow_income);
        }
    }

    public List<Record> getRecords() {
        return records;
    }
}
