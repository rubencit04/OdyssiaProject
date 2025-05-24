package com.example.odyssiaproject.ui.ajustes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.odyssiaproject.LogIn;
import com.example.odyssiaproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ConfigFragment extends Fragment {

    private ConfigViewModel configViewModel;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_config, container, false);

        configViewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        Button btnPerfil = root.findViewById(R.id.btnPerfil);
        Button btnCambioPass = root.findViewById(R.id.btnCambioPass);
        Switch switchNotifications = root.findViewById(R.id.swTema);
        ImageButton btnAcercaDe = root.findViewById(R.id.btnAcercaDe);
        Button btnLogout = root.findViewById(R.id.btnLogOut);
        Button btnEliminarCuenta = root.findViewById(R.id.btnEliminar);

        // Abrir perfil
        btnPerfil.setOnClickListener(v -> {
            PerfilActivity dialog = new PerfilActivity();
            dialog.setOnNombreCambiadoListener(nuevoNombre -> {
                Toast.makeText(getActivity(), "Nombre cambiado a: " + nuevoNombre, Toast.LENGTH_SHORT).show();
                // Aquí podrías guardar el cambio en la base de datos o en SharedPreferences
            });
            dialog.show(getParentFragmentManager(), "EditarPerfilDialog");
        });

        // Ir a cambio de contraseña
        btnCambioPass.setOnClickListener(v -> {
            CambioPasswordActivity dialog = new CambioPasswordActivity();
            dialog.setOnPasswordCambiadaListener(nuevaPassword -> {
                Toast.makeText(getActivity(), "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                // Aquí podrías guardar la nueva contraseña en la base de datos o API
            });
            dialog.show(getParentFragmentManager(), "CambiarPasswordDialog");
        });

        // Observar y actualizar el estado del Switch de notificaciones
        configViewModel.getNotificacionesActivas().observe(getViewLifecycleOwner(), switchNotifications::setChecked);
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            configViewModel.cambiarEstadoNotificaciones(isChecked);
            String mensaje = isChecked ? "Notificaciones activadas" : "Notificaciones desactivadas";
            Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
        });

        // Acerca de
        btnAcercaDe.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Acerca de Odyssia")
                    .setMessage("Odyssia v1.0\nAplicación desarrollada por Odyssia Project.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        // Cerrar sesión
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        mAuth.signOut();
                        // Redirigir al Login
                        Intent intent = new Intent(getActivity(), LogIn.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // Eliminar cuenta
        btnEliminarCuenta.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Eliminar cuenta")
                    .setMessage("Esta acción es irreversible. ¿Seguro que deseas eliminar tu cuenta?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {

                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            user.delete()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                                            // Redirigir al login
                                            Intent intent = new Intent(getActivity(), LogIn.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            getActivity().finish();
                                        } else {
                                            Toast.makeText(getActivity(), "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        return root;
    }
}