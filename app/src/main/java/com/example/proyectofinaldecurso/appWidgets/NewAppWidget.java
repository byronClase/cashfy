package com.example.proyectofinaldecurso.appWidgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.proyectofinaldecurso.R;
import com.example.proyectofinaldecurso.SharedPreferencesManager;
import com.example.proyectofinaldecurso.adapters.RecordAdapter;
import com.example.proyectofinaldecurso.data.Record;
import com.example.proyectofinaldecurso.utils.DateTimeUtils;

import java.text.ParseException;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

            // Obtener el símbolo de la moneda del intent
            SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
            String symbol = sharedPreferencesManager.getMonedaSimbolo();

            // Crear una instancia de RecordAdapter pasando el símbolo de la moneda
            RecordAdapter recordAdapter;
            try {
                recordAdapter = new RecordAdapter(context);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            List<Record> records = recordAdapter.getRecords();

            //para cambiar el idioma del dialogo
            //LocaleUtils.setLocale(context, "es", "ES");

            // Crear un arreglo de Strings para los datos del ListView
            String[] datos = new String[records.size()];
            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                String formattedDate = DateTimeUtils.formatDateNewString(record.getDate());// Formatear la fecha utilizando DateTimeUtils.formatDate() record.getDate();
                String data = formattedDate + " - " + record.getAmount() + " " + symbol + " - " + record.getCategory(); // Ajusta la forma en que deseas mostrar los datos
                datos[i] = data;
            }


            // Crear un Intent para llamar a MyAppWidgetService
            Intent intent = new Intent(context, MyAppWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            // Pasar los datos al servicio
            intent.putExtra("datos", datos);

            // Establecer el adaptador en el ListView
            views.setRemoteAdapter(R.id.list_view, intent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}