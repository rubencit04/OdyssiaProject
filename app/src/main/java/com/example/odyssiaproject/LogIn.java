package com.example.odyssiaproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE);
        String correoRecibido = preferences.getString("correo", "");

        GestorUsuario gestorUsuario = new GestorUsuario();
        EditText correoUser = findViewById(R.id.etEmail);
        EditText pass = findViewById(R.id.etContrasenia);
        Button buttonRegister = findViewById(R.id.btnRegistro);
        Button buttonRecover = findViewById(R.id.btnRecuperacion);
        Button buttonNext = findViewById(R.id.btnIniciar);
        correoUser.setText(correoRecibido);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser usuario = mAuth.getCurrentUser();

        if (usuario != null) {
            findViewById(R.id.Login).setVisibility(View.INVISIBLE);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String uid = usuario.getUid();

            db.collection("usuarios").document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            // Verificamos si tiene la lista de favoritos
                            if (!task.getResult().contains("favoritosCiudades")) {
                                Map<String, Object> update = new HashMap<>();
                                update.put("favoritosCiudades", new ArrayList<String>());
                                db.collection("usuarios").document(uid).update(update);
                            }

                            // Continuamos al MainActivity
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

        // Configurar listener para el botón de registro
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, RegistroActivity.class);
                startActivity(intent);
            }
        });

        // Configurar listener para el botón de recuperación
        buttonRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, RecoverPassActivity.class);
                startActivity(intent);
            }
        });

        // Configurar listener para el botón de inicio de sesión
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.setLanguageCode("es");

                String correo = correoUser.getText().toString().trim();
                String contrasenia = pass.getText().toString().trim();

                // Guardar el correo en SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("correo", correo);
                editor.apply();

                Usuario usuario = new Usuario(correo, contrasenia);

                gestorUsuario.iniciarSesion(usuario, new GestorUsuario.OnLoginListener() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        Dialogos.showLoading(LogIn.this, "Iniciando Sesion...");
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
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Dialogos.showErrorLogin(LogIn.this, obtenerMensajeErrorFirebase(exception));
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
            }
        });
    }

    private void mostrarLogin() {
        findViewById(R.id.Login).setVisibility(View.VISIBLE);
    }
}
