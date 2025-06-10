package com.example.odyssiaproject.persistencia;

import com.example.odyssiaproject.dto.ActividadDTO;
import com.example.odyssiaproject.persistencia.api.RetrofitRenderClient;

import java.util.List;

import retrofit2.Call;

public class DaoActividad {
    public Call<List<ActividadDTO>> obtenerActividades(String nombreCiudad,String nombrePais) {
        return RetrofitRenderClient.getApiService().getActividades(nombreCiudad,nombrePais);
    }
    public Call<List<ActividadDTO>> obtenerMonumentos(String nombreCiudad, String nombrePais) {
        return RetrofitRenderClient.getApiService().getMonumentos(nombreCiudad,nombrePais);
    }
    public Call<List<ActividadDTO>> obtenerCultura(String nombreCiudad, String nombrePais) {
        return RetrofitRenderClient.getApiService().getCultura(nombreCiudad,nombrePais);
    }
    public Call<List<ActividadDTO>> obtenerOcio(String nombreCiudad, String nombrePais) {
        return RetrofitRenderClient.getApiService().getOcio(nombreCiudad,nombrePais);
    }
    public Call<List<ActividadDTO>> obtenerOcioNocturno(String nombreCiudad, String nombrePais) {
        return RetrofitRenderClient.getApiService().getOcioNocturno(nombreCiudad,nombrePais);
    }
    public Call<List<ActividadDTO>> obtenerRestaurantes(String nombreCiudad, String nombrePais) {
        return RetrofitRenderClient.getApiService().getRestaurantes(nombreCiudad,nombrePais);
    }
    public Call<List<ActividadDTO>> obtenerAlojamientos(String nombreCiudad, String nombrePais) {
        return RetrofitRenderClient.getApiService().getAlojamientos(nombreCiudad,nombrePais);
    }

}
