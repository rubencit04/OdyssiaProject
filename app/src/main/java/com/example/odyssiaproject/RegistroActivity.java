package com.example.odyssiaproject;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.odyssiaproject.entidad.Usuario;
import com.example.odyssiaproject.negocio.GestorUsuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegistroActivity extends AppCompatActivity {

    private EditText etUsuario, etEmail, etContrasenia, etNacionalidad;
    private Button btnContinuar;
    private GestorUsuario gestorUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Asegúrate de que este nombre coincide con tu archivo XML

        // Referencia a los campos del layout
        etUsuario = findViewById(R.id.etUsuario);
        etEmail = findViewById(R.id.etEmail);
        etContrasenia = findViewById(R.id.etContrasenia);
        etNacionalidad = findViewById(R.id.etNacionalidad);
        btnContinuar = findViewById(R.id.btnContinuar);

        gestorUsuario = new GestorUsuario();

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsuario.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etContrasenia.getText().toString().trim();
                String nacionalidad = etNacionalidad.getText().toString().trim();

                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setUsuario(username);
                nuevoUsuario.setCorreo(email);
                nuevoUsuario.setContrasenia(password);
                nuevoUsuario.setNacionalidad(nacionalidad);

                gestorUsuario.registrar(nuevoUsuario, new GestorUsuario.OnRegistroListener() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        Toast.makeText(
                                RegistroActivity.this,
                                "Registro exitoso: " + user.getEmail(),
                                Toast.LENGTH_LONG
                        ).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(RegistroActivity.this, LogIn.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(
                                RegistroActivity.this,
                                "Error al registrar: " + exception.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
            }
        });
    }
}
