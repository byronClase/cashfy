package com.example.proyectofinaldecurso.utils;

public interface BiometricAuthCallback {
    void onSuccess();

    void onError();

    void onNotRecognized();
}
