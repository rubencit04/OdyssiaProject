package com.example.odyssiaproject.persistencia.api;


import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.entidad.Monumentos;
import com.example.odyssiaproject.entidad.Pais;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface ApiRenderService {

    @GET("/paises")
    Call<List<Pais>> getPaises();

    @GET("/ciudades")
    Call<List<Ciudad>> getCiudades();

    @GET("/ciudades")
    Call<List<Ciudad>> getCiudadesPorPais(@Query("pais") String nombrePais); // Filtrar por país

    @GET("/monumentos")
    Call<List<Monumentos>> getMonumentos();

    @GET("/monumentos")
    Call<List<Monumentos>> getMonumentosPorCiudad(@Query("ciudad") String nombreCiudad); // Filtrar por ciudad

}