package com.example.proyectofinaldecurso.appWidgets;


import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.proyectofinaldecurso.appWidgets.MyAppWidgetRemoteViewsFactory;

public class MyAppWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyAppWidgetRemoteViewsFactory(getApplicationContext(), intent);
    }
}
