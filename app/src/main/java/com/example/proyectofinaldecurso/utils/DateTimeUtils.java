package com.example.proyectofinaldecurso.utils;


import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtils {
    //private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String TIME_FORMAT = "HH:mm";
    private static final String DATE_DEFAULT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static Calendar createCalendar() {
        Calendar calendar = Calendar.getInstance();
        return calendar;
    }

    public static Calendar createCalendar(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        return calendar;
    }

    public static String formatDateTimeDefinitiva(Calendar calendar) {
        //"yyyy-MM-dd'T'HH:mm:ss'Z'"
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_DEFAULT, Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));  // asegura que la fecha esté en UTC
        return dateFormat.format(calendar.getTime());
    }

    // Metodo para formatear la fecha
    public static String formatDateNewString(Calendar date) {
        //"dd/MM/yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return dateFormat.format(date.getTime());
    }

    // Metodo para formatear la hora
    public static String formatTimeNewString(Calendar date) {
        //"HH:mm"
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        return timeFormat.format(date.getTime());
    }

}
