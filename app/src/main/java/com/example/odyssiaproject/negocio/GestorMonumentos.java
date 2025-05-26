package com.example.odyssiaproject.negocio;

import com.example.odyssiaproject.entidad.Monumentos;

public class GestorMonumentos {

    /**
     * Devuelve la URL o nombre de la imagen del monumento.
     * Si no existe imagen, devuelve una URL o nombre por defecto.
     *
     * @param monumento Objeto Monumentos
     * @return URL o nombre de la imagen
     */
    public String imagenMonumento(Monumentos monumento) {
        if (monumento == null || monumento.getImagen() == null || monumento.getImagen().isEmpty()) {
            return "https://ejemplo.com/default_monumento.jpg"; // imagen por defecto
        }
        return monumento.getImagen();
    }
}