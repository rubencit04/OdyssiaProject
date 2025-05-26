package com.example.odyssiaproject.negocio;

import com.example.odyssiaproject.entidad.Pais;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GestorPaises {

    /**
     * Obtiene la URL de la imagen de un país desde Firestore.
     *
     * @param pais Objeto Pais con al menos el nombre.
     * @return URL de la imagen o valor por defecto si no se encuentra.
     */
    public String imagenPaises(Pais pais) {
        if (pais == null || pais.getImagen() == null || pais.getImagen().isEmpty()) {
            return "https://ejemplo.com/default.jpg"; // URL por defecto
        }
        return pais.getImagen();
    }

}