package com.example.odyssiaproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class Dialogos {
    public static void showErrorLogin(Context context, String errorMessage) {
        new AlertDialog.Builder(context)
                .setTitle("Error al iniciar de sesión")
                .setMessage(errorMessage)
                .setPositiveButton("Aceptar", null)
                .setCancelable(false)
                .show();
    }
    public static void showErrorRegister(Context context, String errorMessage) {
        new AlertDialog.Builder(context)
                .setTitle("Error al registrarte")
                .setMessage(errorMessage)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static void showLoading(Context context, String mensaje) {
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.progress_dialog, null);

            TextView progressText = dialogView.findViewById(R.id.texto);
            progressText.setText(mensaje);

            AlertDialog progressDialog = new AlertDialog.Builder(context)
                    .setView(dialogView)
                    .setCancelable(false)
                    .create();

            progressDialog.show();

            new Handler().postDelayed(() -> {
                if (progressDialog.isShowing() && context instanceof Activity && !((Activity) context).isFinishing()) {
                    progressDialog.dismiss();
                }
            }, 5000);
        }
    }
    public static void showErrorRegoverPass(Context context, String errorMessage) {
        new AlertDialog.Builder(context)
                .setTitle("Recuperacion de Contraseña")
                .setMessage(errorMessage)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }
    public static void showRegoverPass(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Recuperacion de Contraseña")
                .setMessage("Se ha enviado un correo para restablecer la contraseña")
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }
}