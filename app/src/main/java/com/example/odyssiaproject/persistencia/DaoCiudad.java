package com.example.odyssiaproject.persistencia;

import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.persistencia.api.RetrofitRenderClient;

import java.util.List;

import retrofit2.Call;

public class DaoCiudad {
    public Call<List<Ciudad>> obtenerCiudadesPorPais(String nombrePais) {
        return RetrofitRenderClient.getApiService().getCiudades(nombrePais,null);
    }
}
