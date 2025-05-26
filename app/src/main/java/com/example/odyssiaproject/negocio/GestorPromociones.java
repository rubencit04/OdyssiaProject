package com.example.odyssiaproject.negocio;

import android.util.Log;

import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.persistencia.DaoPromocion;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestorPromociones {

    private static final String TAG = "GestorPromociones";

    private final DaoPromocion daoPromocion;

    public GestorPromociones() {
        this.daoPromocion = new DaoPromocion();
    }

    /**
     * Devuelve la URL de imagen de la promoción o una imagen por defecto si es nula o vacía.
     */
    public String imagenPromocion(Promociones promocion) {
        if (promocion == null || promocion.getImagen() == null || promocion.getImagen().isEmpty()) {
            return "https://ejemplo.com/default-promo.jpg";
        }
        return promocion.getImagen();
    }

    // Callback para entregar promociones o error
    public interface CallbackPromociones {
        void onPromocionesCargadas(List<Promociones> promociones);
        void onError(Throwable t);
    }

    // Método para obtener todas las promociones desde el backend
    public void obtenerPromociones(final CallbackPromociones callback) {
        Call<List<Promociones>> call = daoPromocion.obtenerPromociones();
        call.enqueue(new Callback<List<Promociones>>() {
            @Override
            public void onResponse(Call<List<Promociones>> call, Response<List<Promociones>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onPromocionesCargadas(response.body());
                } else {
                    callback.onError(new Exception("Respuesta vacía o error en servidor"));
                }
            }

            @Override
            public void onFailure(Call<List<Promociones>> call, Throwable t) {
                Log.e(TAG, "Error en Retrofit: " + t.getMessage(), t);
                callback.onError(t);
            }
        });
    }

    // NUEVO método para obtener promociones filtradas por país
    public void obtenerPromocionesPorPais(String pais, final CallbackPromociones callback) {
        Call<List<Promociones>> call = daoPromocion.obtenerPromocionesPorPais(pais);
        call.enqueue(new Callback<List<Promociones>>() {
            @Override
            public void onResponse(Call<List<Promociones>> call, Response<List<Promociones>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onPromocionesCargadas(response.body());
                } else {
                    callback.onError(new Exception("Respuesta vacía o error en servidor"));
                }
            }

            @Override
            public void onFailure(Call<List<Promociones>> call, Throwable t) {
                Log.e(TAG, "Error en Retrofit (por país): " + t.getMessage(), t);
                callback.onError(t);
            }
        });
    }
}
