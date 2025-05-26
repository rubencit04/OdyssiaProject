package com.example.odyssiaproject.ui.ajustes;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.odyssiaproject.LogIn;
import com.example.odyssiaproject.MainActivity;
import com.example.odyssiaproject.R;


public class ConfigFragment extends Fragment {

    private Button btnPerfil, btnCambioPass, btnAcercaDe, btnContinuar, btnLogOut, btnEliminar, btnConfirmar, btnCancelar;
    private Switch swTema;
    private EditText etNombrePerfil, etCambioPass;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_config, container, false);

        // Referencias a los botones y switch
        btnPerfil = view.findViewById(R.id.btnPerfil);
        btnCambioPass = view.findViewById(R.id.btnCambioPass);
        btnAcercaDe = view.findViewById(R.id.btnAcercaDe);
        btnContinuar = view.findViewById(R.id.btnContinuar);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        btnEliminar = view.findViewById(R.id.btnEliminar);
        swTema = view.findViewById(R.id.swTema);

        setupListeners();

        return view;
    }

    private void setupListeners() {

        btnPerfil.setOnClickListener(v -> showProfileDialog());

        btnCambioPass.setOnClickListener(v -> showCambioPassDialog());

        btnAcercaDe.setOnClickListener(v -> showAcercaDeDialog());

        swTema.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Aquí puedes alternar tema claro/oscuro o notificaciones
            String mensaje = isChecked ? "Notificaciones activadas" : "Notificaciones desactivadas";
            Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
        });

        btnContinuar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Continuando a la siguiente pantalla...", Toast.LENGTH_SHORT).show();
            // Navegar al HomeFragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnLogOut.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro que deseas cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // 1. Limpiar datos del usuario
                        SharedPreferences prefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();  // o .remove("clave") si quieres solo algunas
                        editor.apply();

                        // 2. Volver al LoginActivity
                        Intent intent = new Intent(requireActivity(), LogIn.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Evita volver con "back"
                        startActivity(intent);

                        // 3. Mensaje de confirmación
                        Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });


        btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar cuenta")
                    .setMessage("Esta acción eliminará tu cuenta permanentemente. ¿Deseas continuar?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        Toast.makeText(getContext(), "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                        // Aquí va la lógica real para eliminar cuenta
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void showProfileDialog() {
        // Inflamos el layout personalizado
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.v2_dialog_profile, null);

        // Creamos el diálogo usando AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext()); // Puedes definir un estilo si lo deseas
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Referencias a los componentes del layout
        etNombrePerfil = dialogView.findViewById(R.id.etNombrePerfil);
        btnConfirmar = dialogView.findViewById(R.id.btnConfirmar);
        btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        // Acción al confirmar
        btnConfirmar.setOnClickListener(v -> {
            String nombre = etNombrePerfil.getText().toString().trim();
            if (!nombre.isEmpty()) {
                // Guardar en SharedPreferences
                SharedPreferences prefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("nombre_usuario", nombre);
                editor.apply();

                Toast.makeText(requireContext(), "Nombre guardado: " + nombre, Toast.LENGTH_SHORT).show();

                // Notificar a MainActivity para actualizar el TextView
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).actualizarNombreUsuario();

                }
                dialog.dismiss();


            } else {
                Toast.makeText(requireContext(), "Por favor ingresa un nombre.", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción al cancelar
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();
    }

    private void showCambioPassDialog() {
        // Inflamos el layout personalizado
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.v2_dialog_cambiar_pass, null);

        // Creamos el diálogo usando AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext()); // Puedes definir un estilo si lo deseas
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Referencias a los componentes del layout
        etCambioPass = dialogView.findViewById(R.id.etCambioPass);
        btnConfirmar = dialogView.findViewById(R.id.btnConfirmar);
        btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        // Acción al confirmar
        btnConfirmar.setOnClickListener(v -> {
            String contrasenia = etCambioPass.getText().toString().trim();
            if (!contrasenia.isEmpty()) {
                // Guardar en SharedPreferences
                SharedPreferences prefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("contrasenia", contrasenia);
                editor.apply();

                Toast.makeText(requireContext(), "Contraseña guardada: " + contrasenia, Toast.LENGTH_SHORT).show();

                // Notificar a MainActivity para actualizar el TextView
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).actualizarNombreUsuario();
                }
                dialog.dismiss();
            } else {
                Toast.makeText(requireContext(), "Por favor ingresa una contraseña.", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción al cancelar
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();
    }

    private void showAcercaDeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Acerca de Odyssia");
        builder.setMessage("Odyssia v2.0\nAplicación desarrollada por Odyssia Project.");
        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}