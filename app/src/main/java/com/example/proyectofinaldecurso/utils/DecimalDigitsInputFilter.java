package com.example.proyectofinaldecurso.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter implements InputFilter {

    private Pattern mPattern;

    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        // Con este patrón, permite cualquier número de 0 a 9 seguido de un punto decimal y hasta 'digitsAfterZero' dígitos.
        mPattern = Pattern.compile("[0-9]{0," + digitsBeforeZero + "}+((\\.[0-9]{0," + digitsAfterZero + "})?)");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // Concatena el texto que ya ha sido aceptado (dest) con el nuevo carácter que se está ingresando (source)
        String newInput = dest.toString() + source.toString();

        Matcher matcher = mPattern.matcher(newInput);
        if (!matcher.matches()) {
            // Si el nuevo texto no coincide con el patrón, no acepta el nuevo carácter
            return "";
        }

        return null; // Acepta el nuevo carácter
    }
}
