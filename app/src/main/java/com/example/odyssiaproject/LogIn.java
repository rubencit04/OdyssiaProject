package com.example.odyssiaproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.odyssiaproject.entidad.Usuario;
import com.example.odyssiaproject.negocio.GestorUsuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        // Configurar listener para el botón de registro
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, RegistroActivity.class);
                startActivity(intent);
            }
        });

        // Configurar listener para el botón de recuperar contraseña
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
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.setLanguageCode("es");

                String correo = correoUser.getText().toString().trim();
                String contrasenia = pass.getText().toString().trim();

                // Actualizar SharedPreferences con el último correo introducido
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("correo", correo);
                editor.apply();

                // Crear objeto Usuario (asegúrate de que el constructor de Usuario coincida)
                Usuario usuario = new Usuario(correo, contrasenia);

                gestorUsuario.iniciarSesion(usuario, new GestorUsuario.OnLoginListener() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        Dialogos.showLoading(LogIn.this, "Iniciando Sesion...");
                        // Redirigir a la actividad principal
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
                            return "Usuario o Contraseña invalidos";
                        } else if (mensaje.contains("The email address is badly formatted.")) {
                            return "El correo electrónico tiene un formato inválido";
                        }
                        return mensaje;
                    }

                });
            }
        });
    }
}
