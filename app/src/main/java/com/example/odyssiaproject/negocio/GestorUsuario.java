package com.example.odyssiaproject.negocio;

import com.example.odyssiaproject.entidad.Usuario;
import com.example.odyssiaproject.persistencia.DaoUsuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class GestorUsuario {

    private final DaoUsuario usuarioDao;

    public GestorUsuario() {
        usuarioDao = new DaoUsuario();
    }

    /**
     * Inicia sesión validando que el correo y la contraseña no estén vacíos.
     * @param usuario Objeto Usuario con correo y contraseña.
     * @param listener Callback para manejar éxito o fracaso.
     */
    public void iniciarSesion(Usuario usuario, final OnLoginListener listener) {
        if (usuario.getCorreo().isEmpty() || usuario.getContrasenia().isEmpty()) {
            listener.onFailure(new Exception("Correo y contraseña son obligatorios"));
            return;
        }
        usuarioDao.iniciarSesion(usuario.getCorreo(), usuario.getContrasenia(), new DaoUsuario.OnLoginListener() {
            @Override
            public void onSuccess(FirebaseUser user) {
                listener.onSuccess(user);
            }
            @Override
            public void onFailure(Exception exception) {
                listener.onFailure(exception);
            }
        });
    }

    public interface OnLoginListener {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception exception);
    }

    /**
     * Registra un usuario validando los campos obligatorios.
     * @param usuario Objeto Usuario con la información de registro.
     * @param listener Callback para manejar éxito o fracaso.
     */
    public void registrar(Usuario usuario, final OnRegistroListener listener) {
        if (usuario.getUsuario().isEmpty() ||
                usuario.getCorreo().isEmpty() ||
                usuario.getContrasenia().isEmpty()) {
            listener.onFailure(new Exception("Campos obligatorios vacíos"));
            return;
        }
        usuarioDao.registrarUsuario(usuario, new DaoUsuario.OnRegistroListener() {
            @Override
            public void onSuccess(FirebaseUser user) {
                listener.onSuccess(user);
            }
            @Override
            public void onFailure(Exception exception) {
                listener.onFailure(exception);
            }
        });
    }

    public interface OnRegistroListener {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception exception);
    }

    /**
     * Envía un correo para recuperación de contraseña validando previamente el email.
     * @param email Correo electrónico del usuario.
     * @param listener Callback para manejar éxito o fracaso.
     */
    public void recuperarContrasenia(String email, final OnRecuperacionListener listener) {
        if (email == null || email.trim().isEmpty()){
            listener.onFailure(new Exception("Debes ingresar un correo electrónico."));
            return;
        }
        usuarioDao.recuperarContrasenia(email, new DaoUsuario.OnRecuperacionListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }
            @Override
            public void onFailure(Exception exception) {
                listener.onFailure(exception);
            }
        });
    }

    public interface OnRecuperacionListener {
        void onSuccess();
        void onFailure(Exception exception);
    }

    /**
     * Actualiza la contraseña de un usuario en Firestore.
     * @param uid Identificador único del usuario en Firebase.
     * @param nuevaPassword Nueva contraseña del usuario.
     * @param listener Callback para manejar éxito o fracaso.
     */
    public void actualizarPassword(String uid, String nuevaPassword, OnUpdatePasswordListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(uid)
                .update("contrasenia", nuevaPassword)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public interface OnUpdatePasswordListener {
        void onSuccess();
        void onFailure(Exception e);
    }
}
