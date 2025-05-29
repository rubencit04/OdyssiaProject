package com.example.odyssiaproject.persistencia;

import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.persistencia.api.RetrofitRenderClient;

import java.util.List;
import retrofit2.Call;

public class DaoPromocion {

    // Obtiene todas las promociones sin filtro
    public Call<List<Promociones>> obtenerPromociones() {
        return RetrofitRenderClient.getApiService().getPromociones(null);
    }

    // Obtiene promociones filtradas por país
    public Call<List<Promociones>> obtenerPromocionesPorPais(String pais) {
        return RetrofitRenderClient.getApiService().getPromociones(pais);
    }
}