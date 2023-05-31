package com.example.proyectofinaldecurso;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesManager {
    private static final String PREFERENCE_NAME = "MyPreferences";
    private static final String KEY_MONEDA_SIMBOLO = "monedaSimbolo";
    private static final String KEY_THEME = "theme";
    private static final String KEY_THEME_Dialog = "themeDialog";
    private static final String KEY_AUTHENTICATION = "authentication";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharedPreferencesManager instance;

    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context.getApplicationContext());
        }
        return instance;
    }


    public void setAuthentication(boolean authentication) {
        editor.putBoolean(KEY_AUTHENTICATION, authentication);
        editor.apply();
    }

    public boolean getAuthentication() {
        return sharedPreferences.getBoolean(KEY_AUTHENTICATION, false);
    }
    public void setStringList(String key, ArrayList<String> list) {
        Set<String> set = new HashSet<>(list);
        editor.putStringSet(key, set);
        editor.apply();
    }

    public ArrayList<String> getStringList(String key) {
        Set<String> set = sharedPreferences.getStringSet(key, null);
        return set != null ? new ArrayList<>(set) : new ArrayList<String>();
    }


    public void setMonedaSimbolo(String monedaSimbolo) {
        editor.putString(KEY_MONEDA_SIMBOLO, monedaSimbolo);
        editor.apply();
    }

    public String getMonedaSimbolo() {
        return sharedPreferences.getString(KEY_MONEDA_SIMBOLO, "€");
    }

    public void setTheme(int theme) {
        editor.apply();
        editor.putInt(KEY_THEME, theme);
    }

    public int getTheme() {
        return sharedPreferences.getInt(KEY_THEME, R.style.Theme_Light);
    }

    public void setThemeDialog(int theme) {
        editor.putInt(KEY_THEME_Dialog, theme);
        editor.apply();
    }

    public int getThemeDialog() {
        return sharedPreferences.getInt(KEY_THEME_Dialog, R.style.LightDialog);
    }
}

