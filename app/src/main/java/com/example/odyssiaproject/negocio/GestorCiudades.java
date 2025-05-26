package com.example.odyssiaproject.negocio;

import com.example.odyssiaproject.entidad.Ciudad;


/**
 * Clase para gestionar la lógica relacionada con ciudades (obtención de imágenes, validaciones, etc.).
 */
public class GestorCiudades {

    /**
     * Obtiene la URL de la imagen de una ciudad desde Firestore o un mapa estático.
     *
     * @param ciudad Objeto Ciudad del cual se requiere la imagen.
     * @return URL de la imagen o valor por defecto si no se encuentra.
     */
    public String imagenCiudad(Ciudad ciudad) {
        if (ciudad == null || ciudad.getImagen() == null || ciudad.getImagen().isEmpty()) {
            return "https://ejemplo.com/default.jpg"; // imagen por defecto
        }
        return ciudad.getImagen();
    }
}