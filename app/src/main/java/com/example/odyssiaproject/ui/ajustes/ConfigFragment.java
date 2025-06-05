package com.example.odyssiaproject.ui.ajustes;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.graphics.Color;

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
                        SharedPreferences prefs = requireActivity()
                                .getSharedPreferences("user_data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.apply();

                        // 2. Volver al LoginActivity
                        Intent intent = new Intent(requireActivity(), LogIn.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        // 3. Mensaje de confirmación
                        Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        btnEliminar.setOnClickListener(v -> {
            // 1) Creamos el AlertDialog con create(), sin mostrarlo todavía
            AlertDialog dialogConfirm = new AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar cuenta")
                    .setMessage("Esta acción eliminará tu cuenta permanentemente. ¿Deseas continuar?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        // 2) Lógica para borrar usuario en Firebase
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.delete().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // 3) Al eliminar correctamente, mostramos un diálogo de confirmación final
                                    AlertDialog dialogSuccess = new AlertDialog.Builder(requireContext())
                                            .setTitle("Cuenta eliminada")
                                            .setMessage("Tu cuenta ha sido eliminada correctamente.")
                                            .setPositiveButton("Aceptar", (dlg, w) -> {
                                                // 4) Cuando pulsen "Aceptar", limpiamos SharedPreferences y vamos a Login
                                                SharedPreferences prefs = requireActivity()
                                                        .getSharedPreferences("user_data", Context.MODE_PRIVATE);
                                                prefs.edit().clear().apply();

                                                Intent intent = new Intent(requireActivity(), LogIn.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            })
                                            .setCancelable(false)
                                            .create();

                                    // Mostrar el diálogo de éxito
                                    dialogSuccess.show();

                                    // Forzar color de texto de botones en el diálogo de éxito
                                    Button btnOk = dialogSuccess.getButton(AlertDialog.BUTTON_POSITIVE);
                                    if (btnOk != null) {
                                        btnOk.setTextColor(Color.BLACK);
                                    }
                                } else {
                                    // 5) Si hubo error al eliminar en Firebase, lo mostramos en otro diálogo
                                    String errorMsg = (task.getException() != null)
                                            ? task.getException().getMessage()
                                            : "Error desconocido";
                                    AlertDialog dialogError = new AlertDialog.Builder(requireContext())
                                            .setTitle("Error al eliminar")
                                            .setMessage("No se pudo eliminar la cuenta: " + errorMsg)
                                            .setPositiveButton("Aceptar", null)
                                            .create();

                                    dialogError.show();

                                    // Forzar color de texto de botones en el diálogo de error
                                    Button btnErrorOk = dialogError.getButton(AlertDialog.BUTTON_POSITIVE);
                                    if (btnErrorOk != null) {
                                        btnErrorOk.setTextColor(Color.BLACK);
                                    }
                                }
                            });
                        } else {
                            // Caso extremo: no hay usuario logueado
                            Toast.makeText(getContext(), "No se encontró usuario para eliminar.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .create();

            // 6) Mostrar el diálogo de confirmación inicial
            dialogConfirm.show();

            // 7) Forzar color de texto de los botones "Eliminar" y "Cancelar"
            Button btnPos = dialogConfirm.getButton(AlertDialog.BUTTON_POSITIVE);
            Button btnNeg = dialogConfirm.getButton(AlertDialog.BUTTON_NEGATIVE);
            if (btnPos != null) {
                btnPos.setTextColor(Color.BLACK);
            }
            if (btnNeg != null) {
                btnNeg.setTextColor(Color.BLACK);
            }
        });
    }

    private void showProfileDialog() {
        // Inflamos el layout personalizado
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.v2_dialog_profile, null);

        // Creamos el diálogo usando AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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
                SharedPreferences prefs = requireActivity()
                        .getSharedPreferences("user_data", Context.MODE_PRIVATE);
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
        // 1) Inflamos tu layout actual (v2_dialog_cambiar_pass.xml)
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.v2_dialog_cambiar_pass, null);

        // 2) Referencias a los campos de tu layout
        EditText etCambioPass = dialogView.findViewById(R.id.etCambioPass);
        Button btnConfirmar = dialogView.findViewById(R.id.btnConfirmar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        // 3) Creamos el AlertDialog sin mostrarlo todavía
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        // 4) Lógica al pulsar "Confirmar"
        btnConfirmar.setOnClickListener(v -> {
            String nuevaContrasena = etCambioPass.getText().toString().trim();
            if (nuevaContrasena.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Por favor ingresa una nueva contraseña.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // 5) Obtenemos el usuario actualmente logueado
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(requireContext(),
                        "No se encontró usuario autenticado.",
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            // 6) Llamamos a updatePassword() en Firebase
            user.updatePassword(nuevaContrasena)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // 7) Si se cambió bien, mostramos un diálogo de éxito
                            AlertDialog dialogSuccess = new AlertDialog.Builder(requireContext())
                                    .setTitle("Contraseña cambiada")
                                    .setMessage("Tu contraseña se ha actualizado correctamente.")
                                    .setPositiveButton("Aceptar", (d, w) -> {
                                        dialog.dismiss();
                                    })
                                    .setCancelable(false)
                                    .create();

                            dialogSuccess.show();

                            // Forzar color de texto del botón "Aceptar"
                            Button btnOk = dialogSuccess.getButton(AlertDialog.BUTTON_POSITIVE);
                            if (btnOk != null) {
                                btnOk.setTextColor(Color.BLACK);
                            }
                        } else {
                            // 8) Si hay error al actualizar, mostramos un diálogo con el mensaje
                            String errorMsg = (task.getException() != null)
                                    ? task.getException().getMessage()
                                    : "Error desconocido al cambiar contraseña.";
                            AlertDialog dialogError = new AlertDialog.Builder(requireContext())
                                    .setTitle("Error")
                                    .setMessage(errorMsg)
                                    .setPositiveButton("Aceptar", null)
                                    .create();

                            dialogError.show();

                            // Forzar color de texto del botón "Aceptar"
                            Button btnOkErr = dialogError.getButton(AlertDialog.BUTTON_POSITIVE);
                            if (btnOkErr != null) {
                                btnOkErr.setTextColor(Color.BLACK);
                            }
                        }
                    });
        });

        // 9) Lógica al pulsar "Cancelar"
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        // 10) Mostramos el diálogo inicial y forzamos color en sus botones (si tuviera botones nativos)
        dialog.show();
        Button btnPos = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNeg = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (btnPos != null) btnPos.setTextColor(Color.BLACK);
        if (btnNeg != null) btnNeg.setTextColor(Color.BLACK);
    }


    private void showAcercaDeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Acerca de Odyssia");
        builder.setMessage("Odyssia v2.0\nAplicación desarrollada por Odyssia Project.");
        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
