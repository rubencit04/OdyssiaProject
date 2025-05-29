package com.example.odyssiaproject.persistencia.api;


import com.example.odyssiaproject.dto.ActividadDTO;
import com.example.odyssiaproject.dto.PaisDTO;
import com.example.odyssiaproject.dto.VueloDTO;
import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.entidad.Promociones;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface ApiRenderService {

    @GET("/paises")
    Call<List<PaisDTO>> getPaises();

    @GET("/ciudades")
    Call<List<Ciudad>> getCiudades();

    @GET("/ciudades")
    Call<List<Ciudad>> getCiudadesPorPais(@Query("pais") String nombrePais); // Filtrar por país


    @GET("/promociones")
    Call<List<Promociones>> getPromociones();

    @GET("/promociones")
    Call<List<Promociones>> getPromocionesPorPais(@Query("pais") String nombrePais); // Filtrar por país

    @GET("/vuelos")
    Call<List<VueloDTO>> getVuelos(
            @Query("paisOrigen") String paisOrigen,
            @Query("paisDestino") String paisDestino
    );
    @GET("/restaurante")
    Call<List<ActividadDTO>> getActividades(
            @Query("ciudad") String ciudad,
            @Query("pais") String pais
    );



}