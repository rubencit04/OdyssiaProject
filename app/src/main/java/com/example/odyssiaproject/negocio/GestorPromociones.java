package com.example.odyssiaproject.negocio;

import android.util.Log;

import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.singelton.ListaPromocionesSingelton;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * GestorPromociones se encarga de la lógica relacionada con las promociones.
 * <p>
 * Este gestor utiliza el Singleton {@link ListaPromocionesSingelton} para obtener una promoción
 * a partir de su identificador y, en base a ese dato, determinar qué valor entero se debe retornar,
 * lo que puede interpretarse como un código para asignar una imagen.
 */
public class GestorPromociones {
    // Instancia del Singleton que almacena la lista de promociones.
    private ListaPromocionesSingelton listaPromociones;
    private final FirebaseFirestore db;

    public GestorPromociones() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Obtiene la URL de la imagen de una promoción desde Firestore.
     *
     * @param promocion Objeto Promociones con al menos la URL de imagen.
     * @return URL de la imagen o valor por defecto si no se encuentra.
     */
    public String imagenPromocion(Promociones promocion) {
        if (promocion == null || promocion.getImagen() == null || promocion.getImagen().isEmpty()) {
            return "https://ejemplo.com/default-promo.jpg"; // URL por defecto
        }
        return promocion.getImagen();
    }


}
