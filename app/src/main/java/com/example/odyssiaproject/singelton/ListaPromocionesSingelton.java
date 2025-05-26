package com.example.odyssiaproject.singelton;

import android.util.Log;
import com.example.odyssiaproject.entidad.Promociones;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Singleton para gestionar la lista de promociones en la aplicación.
 * Se asegura de que solo haya una única instancia de la lista en toda la aplicación.
 */
public class ListaPromocionesSingelton {

    private static ListaPromocionesSingelton instance;

    private List<Promociones> listaPromociones;

    private int contador = 1;

    /**
     * Constructor privado para evitar la creación de múltiples instancias.
     */
    private ListaPromocionesSingelton() {
        super();
    }

    /**
     * Obtiene la única instancia del Singleton. Si no existe, la crea.
     *
     * @return La instancia única de ListaPromocionesSingelton.
     */
    public static ListaPromocionesSingelton getInstance() {
        if (instance == null) {
            instance = new ListaPromocionesSingelton();
        }
        return instance;
    }


    public List<Promociones> getListaPromociones() {
        return listaPromociones;
    }
}
