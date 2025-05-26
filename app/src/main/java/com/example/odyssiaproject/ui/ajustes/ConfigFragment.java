package com.example.odyssiaproject.ui.ajustes;

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
import com.example.odyssiaproject.ui.home.HomeFragment;


public class ConfigFragment extends Fragment {

    private Button btnPerfil, btnCambioPass, btnAcercaDe, btnContinuar, btnLogOut, btnEliminar;
    private Switch swTema;

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

        btnPerfil.setOnClickListener(v -> showPerfilDialog());

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

    private void showPerfilDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Editar Perfil");

        final EditText input = new EditText(requireContext());
        input.setHint("Escribe tu nombre...");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setPadding(60, 40, 60, 40);

        builder.setView(input);

        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            String nombre = input.getText().toString().trim();
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

            } else {
                Toast.makeText(requireContext(), "Por favor ingresa un nombre.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    private void showCambioPassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cambiar Contraseña");
        builder.setMessage("Funcionalidad para cambiar la contraseña.");
        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showAcercaDeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Acerca de Odyssia");
        builder.setMessage("Odyssia v2.0\nAplicación desarrollada por Odyssia Project.");
        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}