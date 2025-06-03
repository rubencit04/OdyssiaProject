package com.example.odyssiaproject.negocio;

import com.example.odyssiaproject.entidad.Actividad;
import com.example.odyssiaproject.entidad.Monumentos;

public class GestorOptions {

    /**
     * Devuelve la URL o nombre de la imagen del monumento.
     * Si no existe imagen, devuelve una URL o nombre por defecto.
     *
     * @param actividad Objeto Monumentos
     * @return URL o nombre de la imagen
     */
    public String imagenMonumento(Actividad actividad) {
        if (actividad == null || actividad.getImagen() == null || actividad.getImagen().isEmpty()) {
            return "https://ejemplo.com/default.jpg"; // imagen por defecto
        }
        return actividad.getImagen();
    }
}