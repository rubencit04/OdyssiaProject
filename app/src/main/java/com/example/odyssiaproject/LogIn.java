package com.example.odyssiaproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.odyssiaproject.entidad.Usuario;
import com.example.odyssiaproject.negocio.GestorUsuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        // 1) Referencia a SharedPreferences (sigues usándolo para futuras ejecuciones)
        SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE);
        String correoRecibido = preferences.getString("correo", "");

        // 2) Referencias a vistas del layout
        EditText correoUser = findViewById(R.id.etEmail);
        EditText pass      = findViewById(R.id.etContrasenia);
        Button buttonRegister = findViewById(R.id.btnRegistro);
        Button buttonRecover  = findViewById(R.id.btnRecuperacion);
        Button buttonNext     = findViewById(R.id.btnIniciar);

        // 3) Primero, rellenamos correoUser con lo que tengamos en SharedPreferences:
        correoUser.setText(correoRecibido);

        // 4) Ahora comprobamos si el Intent trajo extra "correo_prefill":
        String correoPrefill = getIntent().getStringExtra("correo_prefill");
        if (correoPrefill != null && !correoPrefill.isEmpty()) {
            // Si viene un correo desde RegistroActivity, ponemos ese y sobreescribimos SharedPreferences
            correoUser.setText(correoPrefill);
            correoUser.setSelection(correoPrefill.length()); // opcional: cursor al final

            // (Opcional) También lo guardamos en SharedPreferences para próximas veces:
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("correo", correoPrefill);
            editor.apply();
        }

        // 5) Inicializamos FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser usuario = mAuth.getCurrentUser();

        // 6) Si ya había usuario logueado, ocultamos el login y vamos a MainActivity:
        if (usuario != null) {
            findViewById(R.id.Login).setVisibility(View.INVISIBLE);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String uid = usuario.getUid();

            db.collection("usuarios").document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            // Aseguramos favoritosCiudades, etc.
                            if (!task.getResult().contains("favoritosCiudades")) {
                                Map<String, Object> update = new HashMap<>();
                                update.put("favoritosCiudades", new ArrayList<String>());
                                db.collection("usuarios").document(uid).update(update);
                            }

                            // Continuamos a MainActivity
                            startActivity(new Intent(LogIn.this, MainActivity.class));
                            finish();
                        } else {
                            mAuth.signOut();
                            mostrarLogin();
                        }
                    });
        } else {
            mostrarLogin();
        }

        // 7) Botón “Registrarse”: va a RegistroActivity
        buttonRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LogIn.this, RegistroActivity.class);
            startActivity(intent);
        });

        // 8) Botón “Recuperar contraseña”: va a RecoverPassActivity
        buttonRecover.setOnClickListener(view -> {
            Intent intent = new Intent(LogIn.this, RecoverPassActivity.class);
            startActivity(intent);
        });

        // 9) Botón “Iniciar sesión”: guardamos el correo en SharedPreferences y lanzamos MainActivity
        buttonNext.setOnClickListener(v -> {
            mAuth.setLanguageCode("es");

            String correo = correoUser.getText().toString().trim();
            String contrasenia = pass.getText().toString().trim();

            // Guardar el correo en SharedPreferences para la próxima apertura
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("correo", correo);
            editor.apply();

            Usuario usuarioLogin = new Usuario(correo, contrasenia);

            GestorUsuario gestorUsuario = new GestorUsuario();
            gestorUsuario.iniciarSesion(usuarioLogin, new GestorUsuario.OnLoginListener() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    Dialogos.showLoading(LogIn.this, "Iniciando Sesión...");

                    // Retarda el startActivity hasta que el diálogo esté visible
                    new Handler().postDelayed(() -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String uid = user.getUid();
                        db.collection("usuarios").document(uid).get().addOnSuccessListener(snapshot -> {
                            if (snapshot.exists() && !snapshot.contains("favoritosCiudades")) {
                                Map<String, Object> update = new HashMap<>();
                                update.put("favoritosCiudades", new ArrayList<String>());
                                db.collection("usuarios").document(uid).update(update);
                            }
                        });

                        startActivity(new Intent(LogIn.this, MainActivity.class));
                        finish();
                    }, 1500); // Espera 1.5 segundos antes de ir a MainActivity
                }

                @Override
                public void onFailure(Exception exception) {
                    Dialogos.showErrorLogin(
                            LogIn.this,
                            obtenerMensajeErrorFirebase(exception)
                    );
                }

                private String obtenerMensajeErrorFirebase(Exception exception) {
                    String mensaje = exception.getMessage();
                    if (mensaje.contains("The supplied auth credential is incorrect, malformed or has expired.")) {
                        return "Usuario o Contraseña inválidos";
                    } else if (mensaje.contains("The email address is badly formatted.")) {
                        return "El correo electrónico tiene un formato inválido";
                    }
                    return mensaje;
                }
            });
        });
    }

    private void mostrarLogin() {
        findViewById(R.id.Login).setVisibility(View.VISIBLE);
    }
}
