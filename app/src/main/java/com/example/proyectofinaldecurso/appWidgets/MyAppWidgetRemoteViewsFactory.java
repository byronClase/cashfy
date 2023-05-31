package com.example.proyectofinaldecurso.appWidgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class MyAppWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private String[] mData;

    public MyAppWidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;

        // Obtener los datos del intent
        mData = intent.getStringArrayExtra("datos");
    }

    @Override
    public void onCreate() {
        // No se requiere ninguna inicialización especial
    }

    @Override
    public void onDataSetChanged() {
        // No se requiere ninguna actualización especial para los datos
    }

    @Override
    public void onDestroy() {
        // No se requiere ninguna limpieza especial
    }

    @Override
    public int getCount() {
        return mData.length;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), android.R.layout.simple_list_item_1);
        remoteViews.setTextViewText(android.R.id.text1, mData[position]);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null; // No se requiere una vista de carga especial
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Solo se usa un tipo de vista
    }

    @Override
    public long getItemId(int position) {
        return position; // Se utiliza la posición como ID del elemento
    }

    @Override
    public boolean hasStableIds() {
        return true; // Los IDs de los elementos son estables
    }

}
