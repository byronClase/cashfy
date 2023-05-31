package com.example.proyectofinaldecurso.activities;

import static android.content.ContentValues.TAG;

import static com.example.proyectofinaldecurso.utils.DateTimeUtils.createCalendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyectofinaldecurso.R;
import com.example.proyectofinaldecurso.SharedPreferencesManager;
import com.example.proyectofinaldecurso.data.DatabaseHelper;
import com.example.proyectofinaldecurso.data.Record;
import com.example.proyectofinaldecurso.data.TransactionType;
import com.example.proyectofinaldecurso.fragments.DinamicsCategoriesFragment;
import com.example.proyectofinaldecurso.utils.DateTimeUtils;
import com.example.proyectofinaldecurso.utils.DecimalDigitsInputFilter;
import com.example.proyectofinaldecurso.utils.LocaleUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewRecordActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    TextView titleTv, dataDateTv;
    EditText amountEt, descriptionEt;
    Spinner categorySpinner;
    Button addButton, cancelButton;
    Record newRecord = new Record();
    String selectedItem = "void";
    private boolean isIncome = false;
    private DatabaseHelper databaseHelper;
    private SharedPreferencesManager sharedPreferencesManager;

    private static final String KEY_CATEGORIAS_ESTANDAR_INGRESO = "categorias_estandar_ingreso";
    private static final String KEY_CATEGORIAS_ESTANDAR_GASTO = "categorias_estandar_gasto";

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        setTheme(sharedPreferencesManager.getTheme());
        setContentView(R.layout.activity_new_record);
        databaseHelper = new DatabaseHelper(this);
        initializeDataFromResources();
        initViews();
        initListeners();
        renderDateTv();

        renderSpinner();
    }

    private void initializeDataFromResources() {
        // Verifica si ya existen listas de categorías en SharedPreferences
        ArrayList<String> ingresosList = sharedPreferencesManager.getStringList(KEY_CATEGORIAS_ESTANDAR_INGRESO);
        ArrayList<String> gastosList = sharedPreferencesManager.getStringList(KEY_CATEGORIAS_ESTANDAR_GASTO);
        // TODO : no se porque no se ordena en el spinner
        Log.d(TAG, "array ingresos: "+ingresosList.toString());
        Log.d(TAG, "array ingresos: "+gastosList.toString());

        // Si las listas no existen, inicialízalas con los valores predeterminados
        if (ingresosList.isEmpty() || ingresosList ==null) {
            // Obtén los arrays de strings de tus recursos
            String[] ingresos = getResources().getStringArray(R.array.categorias_estandar_ingreso);

            // Convierte los arrays en ArrayLists
            ingresosList = new ArrayList<>(Arrays.asList(ingresos));

            // Ordena las listas alfabéticamente
            Collections.sort(ingresosList);

            // Agrega "+" al final
            ingresosList.add("Nuevo ingreso");

            // Almacena los ArrayLists en SharedPreferences
            sharedPreferencesManager.setStringList(KEY_CATEGORIAS_ESTANDAR_INGRESO, ingresosList);
        }

        if (gastosList.isEmpty() || gastosList == null) {
            String[] gastos = getResources().getStringArray(R.array.categorias_estandar_gasto);
            gastosList = new ArrayList<>(Arrays.asList(gastos));
            Collections.sort(gastosList);
            gastosList.add("Nuevo gasto");
            sharedPreferencesManager.setStringList(KEY_CATEGORIAS_ESTANDAR_GASTO, gastosList);
        }
    }

    private void renderSpinner() {
        getModeIntentTittle();
        int modo = getModeIntentTittle();

        ArrayList<String> arrayList;
        if (modo == 1) {
            arrayList = sharedPreferencesManager.getStringList("categorias_estandar_ingreso");
        } else if (modo == 2) {
            arrayList = sharedPreferencesManager.getStringList("categorias_estandar_gasto");
        } else {
            arrayList = new ArrayList<>(); // Fallback por si acaso
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private int getModeIntentTittle() {
        //segun el boton introducido voy a ingreso o gasto
        Intent intent = getIntent();
        int modo = intent.getIntExtra("codigo", 0);

        //si es 1 aparecera el textView INGRESO
        if (modo == 1) {
            titleTv.setText(R.string.mensaje_nuevo_income);
            isIncome = true;
        } else if (modo == 2) {
            //si es 2 aparecera el textView GASTO
            titleTv.setText(R.string.mensaje_nuevo_expense);
            isIncome = false;
        }
        return modo;
    }

    private void renderDateTv() {
        System.out.println("****************************en render****************************************");
        Calendar calendar = createCalendar();
        String formattedDateTime = DateTimeUtils.formatDateNewString(calendar);
        dataDateTv.setText(formattedDateTime);
        Log.d(TAG, "en el textView " + formattedDateTime);
        newRecord.setDate(calendar);  // Pasamos directamente el objeto calendar
        Log.d(TAG, newRecord.toString());
    }

    public void selectedDataPickerDialog() {
        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH);
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, sharedPreferencesManager.getThemeDialog(), (datePicker, year, month, dayOfMonth) -> {
            Calendar selectedDate = createCalendar(year, month, dayOfMonth);
            String formattedDateTime = DateTimeUtils.formatDateNewString(selectedDate);
            Log.d(TAG, formattedDateTime);
            newRecord.setDate(selectedDate);  // Pasamos directamente el objeto calendar
            dataDateTv.setText(formattedDateTime);
            Log.d(TAG, "dia: " + dayOfMonth + " mes: " + month + " anno: " + year);
        }, currentYear, currentMonth, currentDay);
        datePickerDialog.show();
    }

    private void createRecord() {
        double amount = Double.parseDouble(getContentAmountEditText());
        String category = selectedItem;
        String description = getContentDescriptionEditText();
        TransactionType type = TransactionType.EXPENSE; // Puedes cambiar esto según corresponda

        if (isIncome) {
            type = TransactionType.INCOME;
        }

        Record record = new Record(newRecord.getDate(), amount, category, description, type);  // Aquí newRecord.getDate() devuelve un Calendar

        databaseHelper.addRecord(record);
        Log.d(TAG, record.toString());
    }

    private void initListeners() {
        dataDateTv.setOnClickListener(this);
        addButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        categorySpinner.setOnItemSelectedListener(this);
    }

    private void initViews() {
        titleTv = findViewById(R.id.titulo_id);
        amountEt = findViewById(R.id.amountEtNewRecordActivity);
        descriptionEt = findViewById(R.id.descriptionEtNewRecordActivity);
        amountEt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        categorySpinner = findViewById(R.id.categorySpinnerNewRecordActivity);
        dataDateTv = findViewById(R.id.dateTvDataNewRecordActivity);
        addButton = findViewById(R.id.buttonAdd);
        cancelButton = findViewById(R.id.buttonCancel);
    }

    private void actionRestricted(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == dataDateTv.getId()) {
            selectedDataPickerDialog();
        }
        if (id == addButton.getId()) {
            if (amountEt.getText().toString().isEmpty()) {
                amountEt.setError("¡Falta una cantidad!");
            } else if (selectedItem.equals("void")) {
                actionRestricted("¡Falta una categoría!");
            } else if (!validFormat(amountEt.getText().toString())) {
                amountEt.setError("¡2 décimales máximo!");
            } else {
                createRecord();
                finish();
            }
        }
        if (id == cancelButton.getId()) {
            finish();
        }
    }

    private String getContentAmountEditText() {
        String amountStr = amountEt.getText().toString();

        // Verificar si la cantidad es un número con dos decimales o menos
        if (!amountStr.matches("\\d+(\\.\\d{1,2})?")) {
            amountEt.setError("No se puede introducir esa cantidad.");
            return null;
        }

        return amountStr;
    }


    private String getContentDescriptionEditText() {
        String description = descriptionEt.getText().toString();
        if (description.isEmpty())
            description = "Sin descripción";
        return description;
    }




    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar nueva categoría");
        builder.setMessage("Escribe el nombre de la nueva categoría");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        // Limita el número máximo de caracteres a 10
        //input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
        builder.setView(input);

        // Dejamos vacío el OnClickListener del botón positivo por ahora
        builder.setPositiveButton("Aceptar", null);

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();

        // Configuramos nuestro propio OnClickListener después de crear el diálogo
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newCategory = input.getText().toString();
                        if (!newCategory.isEmpty() && newCategory.length() <= 10) {
                            // Aquí puedes manejar la nueva categoría
                            Toast.makeText(getApplicationContext(), "Nueva categoría: " + newCategory, Toast.LENGTH_SHORT).show();

                            // Añadir el nuevo elemento al principio de la lista y actualizar SharedPreferences
                            ArrayList<String> categories;
                            if (isIncome) {
                                categories = sharedPreferencesManager.getStringList(KEY_CATEGORIAS_ESTANDAR_INGRESO);
                                categories.add(0, newCategory);
                                sharedPreferencesManager.setStringList(KEY_CATEGORIAS_ESTANDAR_INGRESO, categories);
                            } else{
                                categories = sharedPreferencesManager.getStringList(KEY_CATEGORIAS_ESTANDAR_GASTO);
                                categories.add(0, newCategory);
                                sharedPreferencesManager.setStringList(KEY_CATEGORIAS_ESTANDAR_GASTO, categories);
                            }

                            // Actualizar el spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(NewRecordActivity.this, android.R.layout.simple_spinner_item, categories);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            categorySpinner.setAdapter(adapter);

                            // Establecer el nuevo elemento como seleccionado
                            categorySpinner.setSelection(0);
                          /*  // Obtén la lista actual de SharedPreferences
                            ArrayList<String> categories = SharedPreferencesManager.getInstance(getApplicationContext())
                                    .getStringList(KEY_CATEGORIAS_ESTANDAR_INGRESO);
                            // Agrega la nueva categoría a la lista
                            categories.add(categories.size() - 1, newCategory); // Agrega la categoría justo antes del "+"

                            // Guarda la lista actualizada en SharedPreferences
                            SharedPreferencesManager.getInstance(getApplicationContext())
                                    .setStringList(KEY_CATEGORIAS_ESTANDAR_INGRESO, categories);

                            // Actualiza el adaptador del spinner con la nueva lista
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                    android.R.layout.simple_spinner_item, categories);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            categorySpinner.setAdapter(adapter);*/

                            dialog.dismiss();
                        } else if(newCategory.isEmpty()){
                            input.setError("¡No puede quedar vacío!");
                        } else {
                            input.setError("¡Máximo 10 caracteres!");
                        }

                    }
                });
            }
        });

        dialog.show();
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedItem = "" + adapterView.getSelectedItem();
        selectedItem = "" + adapterView.getSelectedItem();
        if (selectedItem.equals("Nuevo ingreso") || selectedItem.equals("Nuevo gasto")) {
            showDialog();
        } else {
            Toast.makeText(this, selectedItem, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean validFormat(String numero) {
        int index = numero.indexOf(".");
        if (index != -1) {
            int numDecimales = numero.length() - index - 1;
            return numDecimales <= 2;
        }
        return true;
    }
}