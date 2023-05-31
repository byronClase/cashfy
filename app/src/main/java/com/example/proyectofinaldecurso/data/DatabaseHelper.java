package com.example.proyectofinaldecurso.data;

import static android.content.ContentValues.TAG;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "budget.db";
    private static final String TABLE_INCOME = "income";
    private static final String TABLE_EXPENSE = "expense";
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TYPE = "type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INCOME_TABLE = "CREATE TABLE " + TABLE_INCOME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DATE + " TEXT,"
                + KEY_AMOUNT + " REAL,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_TYPE + " INTEGER" + ")";
        db.execSQL(CREATE_INCOME_TABLE);

        String CREATE_EXPENSE_TABLE = "CREATE TABLE " + TABLE_EXPENSE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DATE + " TEXT,"
                + KEY_AMOUNT + " REAL,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_TYPE + " INTEGER" + ")";
        db.execSQL(CREATE_EXPENSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar tablas antiguas si existen
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);

        // Crear tablas de nuevo
        onCreate(db);
    }

    public void addRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateAsString = dateFormat.format(record.getDate().getTime());

        values.put(KEY_DATE, dateAsString);
        values.put(KEY_AMOUNT, record.getAmount());
        values.put(KEY_CATEGORY, record.getCategory());
        values.put(KEY_DESCRIPTION, record.getDescription());
        values.put(KEY_TYPE, record.getType().ordinal());

        /*getType().ordinal() para guardar el tipo de transacción como un entero en la base de datos.
        Esto se debe a que los enumeradores no se pueden guardar directamente en la base de datos.
        Para obtener el tipo de transacción de un registro almacenado en la base de datos,
        se utiliza el método TransactionType.values()[cursor.getInt(5)] en el método getAllRecords().*/

        // Insertar registro en la tabla correspondiente según el tipo de transacción
        if (record.getType() == TransactionType.INCOME) {
            db.insert(TABLE_INCOME, null, values);
        } else {
            db.insert(TABLE_EXPENSE, null, values);
        }

        db.close();
    }

    public List<Record> getAllRecords(TransactionType type) throws ParseException {
        List<Record> recordList = new ArrayList<>();

        String selectQuery;
        if (type == TransactionType.INCOME) {
            selectQuery = "SELECT * FROM " + TABLE_INCOME;
        } else {
            selectQuery = "SELECT * FROM " + TABLE_EXPENSE;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Record record = new Record();
                record.setId(cursor.getInt(0));
                /*String dateString = cursor.getString(1); // La fecha en formato de cadena de texto
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = format.parse(dateString); // La fecha convertida a un objeto Date
                record.setDate(String.valueOf(date)); // Asume que Record::setDate acepta un Date. Si no es así, necesitarás cambiar tu clase Record.
                /*record.setDate(cursor.getString(1));*/
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                format.setTimeZone(TimeZone.getTimeZone("UTC")); // Para asegurar que estamos en UTC
                String dateString = cursor.getString(1);
                Date date = format.parse(dateString);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                record.setDate(cal);
                record.setAmount(cursor.getDouble(2));
                record.setCategory(cursor.getString(3));
                record.setDescription(cursor.getString(4));
                record.setType(TransactionType.values()[cursor.getInt(5)]);
                recordList.add(record);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return recordList;
    }


    public List<Record> getAllRecordsSortedByDateInterleaved(Context context) throws ParseException {
        List<Record> records = new ArrayList<>();

        DatabaseHelper db = new DatabaseHelper(context);
        List<Record> incomeRecords = db.getAllRecords(TransactionType.INCOME);
        List<Record> expenseRecords = db.getAllRecords(TransactionType.EXPENSE);

        records.addAll(incomeRecords);
        records.addAll(expenseRecords);

        Collections.sort(records, new Comparator<Record>() {
            @Override
            public int compare(Record r1, Record r2) {
                return r2.getDate().compareTo(r1.getDate());
            }
        });
        return records;
    }


    public void deleteAllRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INCOME, null, null);
        db.delete(TABLE_EXPENSE, null, null);
        db.close();
    }

    public double getBalance() {
        double totalIncome = 0;
        double totalExpense = 0;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + KEY_AMOUNT + ") FROM " + TABLE_INCOME, null);
        if (cursor.moveToFirst()) {
            totalIncome = cursor.getDouble(0);
        }
        cursor.close();

        cursor = db.rawQuery("SELECT SUM(" + KEY_AMOUNT + ") FROM " + TABLE_EXPENSE, null);
        if (cursor.moveToFirst()) {
            totalExpense = cursor.getDouble(0);
        }
        cursor.close();

        db.close();

        return totalIncome - totalExpense;
    }

    public void deleteSingleRecordExpense(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSE, KEY_ID + " = ?", new String[]{id});
        db.close();
    }

    public void deleteSingleRecordIncome(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INCOME, KEY_ID + " = ?", new String[]{id});
        db.close();
    }

    public void updateRecordExpense(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AMOUNT, record.getAmount());
        values.put(KEY_DESCRIPTION, record.getDescription());
        values.put(KEY_CATEGORY, record.getCategory());

        // Actualiza la fila en la base de datos donde el ID coincide con el ID del registro
        db.update(TABLE_EXPENSE, values, KEY_ID + " = ?", new String[]{String.valueOf(record.getId())});
        db.close();
    }

    public void updateRecordIncome(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AMOUNT, record.getAmount());
        values.put(KEY_DESCRIPTION, record.getDescription());
        values.put(KEY_CATEGORY, record.getCategory());
        // Actualiza la fila en la base de datos donde el ID coincide con el ID del registro
        db.update(TABLE_INCOME, values, KEY_ID + " = ?", new String[]{String.valueOf(record.getId())});
        db.close();
    }

    public void exportarCSV(Context context) throws ParseException {
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + "/SQLiteBackup/");
        File folder2 = new File(Environment.getExternalStorageDirectory() + "/" + "/PDFCashfy/");

        DatabaseHelper db = new DatabaseHelper(context);
        boolean isFolderCreated = false;
        if (!folder.exists()) {
            isFolderCreated = folder.mkdir();
        }
        boolean isFolderCreated2 = false;
        if (!folder2.exists()) {
            isFolderCreated2 = folder2.mkdir();
        }

        Log.d(TAG, folder.getAbsolutePath());
        Log.d("CSC_TAG", "exportCSV: " + isFolderCreated);

        Log.d(TAG, folder2.getAbsolutePath());
        Log.d("CSC_TAG", "create folder pdf: " + isFolderCreated2);


        String csvFileName = "Backup.csv";
        String filePathandName = folder.toString() + "/" + csvFileName;

        List<Record> recordList = new ArrayList<>();
        recordList.clear();
        recordList.addAll(db.getAllRecordsSortedByDateInterleaved(context));

        try {
            FileWriter fw = new FileWriter(filePathandName);
            if (recordList.size() > 0) {
                for (int i = 0; i < recordList.size(); i++) {
                    fw.append("," + recordList.get(i).getId());
                    fw.append(",");
                    fw.append("" + recordList.get(i).getAmount());
                    fw.append(",");
                    fw.append("" + recordList.get(i).getCategory());
                    fw.append(",");
                    fw.append("" + recordList.get(i).getDescription());
                    fw.append(",");
                    fw.append("," + recordList.get(i).getDate());
                    fw.append("\n");
                }
            }
            fw.flush();
            fw.close();

        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }

    }


    public void exportToCSV() {
        String path1 = "/storage/emulated/0/Download/csvaux/";
        String path2 = "/storage/emulated/0/Download/pdfaux/";
        File folder = new File(path1);//Environment.getExternalStorageDirectory() + "/" + "/SQLiteBackup/");
        File folder2 = new File(path2);//Environment.getExternalStorageDirectory() + "/" + "/PDFCashfy/");

        boolean isFolderCreated = false;
        if (!folder.exists()) {
            isFolderCreated = folder.mkdir(); // create directory if not exist
        }
        boolean isFolderCreated2 = false;
        if (!folder2.exists()) {
            isFolderCreated2 = folder2.mkdir(); // create directory if not exist
        }

        Log.d(TAG, folder.getAbsolutePath());
        Log.d("CSC_TAG", "exportCSV: " + isFolderCreated);

        Log.d(TAG, folder2.getAbsolutePath());
        Log.d("CSC_TAG", "create folder pdf: " + isFolderCreated2);

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery1 = "SELECT * FROM " + TABLE_INCOME;
        String selectQuery2 = "SELECT * FROM " + TABLE_EXPENSE;
        Cursor cursor1 = db.rawQuery(selectQuery1, null);
        Cursor cursor2 = db.rawQuery(selectQuery2, null);

        try {
            File file = new File(folder, "/historyCashfy.csv");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            writeCursorToCSV(bufferedWriter, cursor1);
            //bufferedWriter.newLine();  // Insert a blank line between the two tables
            writeCursorToCSV(bufferedWriter, cursor2);

            // Close everything
            bufferedWriter.close();
            fileWriter.close();
            cursor1.close();
            cursor2.close();
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeCursorToCSV(BufferedWriter bufferedWriter, Cursor cursor) throws IOException {
        int columnCount = cursor.getColumnCount();

        // Write the column headers
        for (int i = 0; i < columnCount; i++) {
            if (i != 0) {
                bufferedWriter.write(",");
            }
            bufferedWriter.write(cursor.getColumnName(i));
        }
        bufferedWriter.newLine();

        // Write the rows
        while (cursor.moveToNext()) {
            for (int i = 0; i < columnCount; i++) {
                if (i != 0) {
                    bufferedWriter.write(",");
                }
                bufferedWriter.write(cursor.getString(i));
            }
            bufferedWriter.newLine();
        }
    }




    /*public void exportToCSV() {


        File folder = new File(Environment.getExternalStorageDirectory() + "/" + "/SQLiteBackup/");
        File folder2 = new File(Environment.getExternalStorageDirectory() + "/" + "/PDFCashfy/");


        boolean isFolderCreated = false;
        if (!folder.exists()) {
            isFolderCreated = folder.mkdir();
        }
        boolean isFolderCreated2 = false;
        if (!folder2.exists()) {
            isFolderCreated2 = folder2.mkdir();
        }

        Log.d(TAG, folder.getAbsolutePath());
        Log.d("CSC_TAG", "exportCSV: " + isFolderCreated);

        Log.d(TAG, folder2.getAbsolutePath());
        Log.d("CSC_TAG", "create folder pdf: " + isFolderCreated2);
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery2 = "SELECT * FROM " + TABLE_INCOME + " CROSS JOIN " + TABLE_EXPENSE;
        Cursor cursor = db.rawQuery(selectQuery2, null);


        //String selectQuery = "SELECT * FROM " + TABLE_INCOME;

        try {

            // /storage/emulated/0/PDFCashfy/pdfCashfy2.pdf
            File file = new File(folder, "/historial_income.csv");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            int columnCount = cursor.getColumnCount();

            // Write the column headers
            for (int i = 0; i < columnCount; i++) {
                if (i != 0) {
                    bufferedWriter.write(",");
                }
                bufferedWriter.write(cursor.getColumnName(i));
            }
            bufferedWriter.newLine();

            // Write the rows
            while (cursor.moveToNext()) {
                for (int i = 0; i < columnCount; i++) {
                    if (i != 0) {
                        bufferedWriter.write(",");
                    }
                    bufferedWriter.write(cursor.getString(i));
                }
                bufferedWriter.newLine();
            }

            // Close everything
            bufferedWriter.close();
            fileWriter.close();
            cursor.close();
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}