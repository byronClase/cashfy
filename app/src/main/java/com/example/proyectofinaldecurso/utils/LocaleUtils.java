package com.example.proyectofinaldecurso.utils;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleUtils {

    public static void setLocale(Context context, String language, String country) {
        Locale locale = new Locale(language, country);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}

