package com.example.proyectofinaldecurso.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;


public class BiometricUtils {

    public static boolean isDeviceReady(Context context) {
        return getCapability(context) == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public static void showPrompt(
            String title,
            String subtitle,
            String description,
            String cancelButton,
            AppCompatActivity activity,
            BiometricAuthCallback callback) {

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();


        BiometricPrompt prompt = initPrompt(activity, callback);
        prompt.authenticate(promptInfo);
    }

    private static BiometricPrompt initPrompt(
            AppCompatActivity activity,
            BiometricAuthCallback callback) {
        Context context = activity.getApplicationContext();
        BiometricPrompt.AuthenticationCallback authenticationCallBack =
                new BiometricPrompt.AuthenticationCallback() {

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        callback.onSuccess();
                    }

                    @Override
                    public void onAuthenticationError(
                            int errorCode,
                            CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        callback.onError();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        callback.onNotRecognized();
                    }
                };

        return new BiometricPrompt(activity, ContextCompat.getMainExecutor(context),
                authenticationCallBack);
    }

    private static int getCapability(Context context) {
        return BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK |
                BiometricManager.Authenticators.DEVICE_CREDENTIAL);
    }

}
