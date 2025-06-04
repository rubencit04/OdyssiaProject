package com.example.odyssiaproject.persistencia;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.odyssiaproject.entidad.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DaoUsuario {
    private static final String TAG = "DaoUsuario";
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    // Constructor que inicializa Firebase Authentication y Firestore
    public DaoUsuario() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Inicia sesión con correo y contraseña.
     * @param correo Correo electrónico del usuario.
     * @param contrasenia Contraseña del usuario.
     * @param listener Callback para manejar éxito o fracaso.
     */
    public void iniciarSesion(String correo, String contrasenia, final OnLoginListener listener) {
        mAuth.signInWithEmailAndPassword(correo, contrasenia)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        listener.onSuccess(user);
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    public interface OnLoginListener {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception exception);
    }

    /**
     * Registra un usuario verificando que el correo no esté en uso.
     * @param usuario Objeto Usuario con la información de registro.
     * @param listener Callback para manejar éxito o fracaso.
     */
    public void registrarUsuario(final Usuario usuario, final OnRegistroListener listener) {
        db.collection("usuario").whereEqualTo("correo", usuario.getCorreo()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        listener.onFailure(new Exception("El correo ya está en uso."));
                    } else {
                        crearUsuarioEnAuth(usuario, listener);
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    /**
     * Crea un usuario en Firebase Authentication.
     * @param usuario Objeto Usuario con la información de registro.
     * @param listener Callback para manejar éxito o fracaso.
     */
    private void crearUsuarioEnAuth(final Usuario usuario, final OnRegistroListener listener) {
        mAuth.createUserWithEmailAndPassword(usuario.getCorreo(), usuario.getContrasenia())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            guardarUsuarioEnFirestore(firebaseUser, usuario, listener);
                        } else {
                            listener.onFailure(new Exception("No se pudo obtener el usuario después del registro."));
                        }
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    /**
     * Guarda los datos del usuario en Firestore.
     * @param firebaseUser Usuario autenticado en Firebase.
     * @param usuario Objeto Usuario con la información de registro.
     * @param listener Callback para manejar éxito o fracaso.
     */
    private void guardarUsuarioEnFirestore(FirebaseUser firebaseUser, Usuario usuario, OnRegistroListener listener) {
        String uid = firebaseUser.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("usuario", usuario.getUsuario());
        userData.put("contrasenia", usuario.getContrasenia());
        userData.put("nacionalidad", usuario.getNacionalidad());
        userData.put("correo", usuario.getCorreo());
        userData.put("tema", usuario.getTema());

        // Asegurate de iniciar favoritosCiudades como lista vacía
        userData.put("favoritosCiudades", new ArrayList<String>());

        db.collection("usuario").document(uid).set(userData)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Usuario registrado con éxito en Firestore");
                    listener.onSuccess(firebaseUser);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public interface OnRegistroListener {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception exception);
    }

    /**
     * Envía un correo de recuperación de contraseña.
     * @param email Correo electrónico del usuario.
     * @param listener Callback para manejar éxito o fracaso.
     */
    public void recuperarContrasenia(String email, final OnRecuperacionListener listener) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess();
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    public interface OnRecuperacionListener {
        void onSuccess();
        void onFailure(Exception exception);
    }
}
