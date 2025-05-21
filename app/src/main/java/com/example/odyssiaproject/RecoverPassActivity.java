package com.example.odyssiaproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.Intent;

import com.example.odyssiaproject.negocio.GestorUsuario;

public class    RecoverPassActivity extends AppCompatActivity {

    private EditText etEmail;
    private ImageButton btnRecuperar;
    private GestorUsuario gestorUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_pass);

        etEmail = findViewById(R.id.etEmail);
        btnRecuperar = findViewById(R.id.btnMandar);
        gestorUsuario = new GestorUsuario();

        btnRecuperar.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();

            gestorUsuario.recuperarContrasenia(email, new GestorUsuario.OnRecuperacionListener() {
                @Override
                public void onSuccess() {
                    Dialogos.showRegoverPass(RecoverPassActivity.this);
                    Intent intent = new Intent(RecoverPassActivity.this, LogIn.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Exception exception) {
                    Dialogos.showErrorRegoverPass(RecoverPassActivity.this, exception.getMessage());
                }
            });
        });
    }
}
