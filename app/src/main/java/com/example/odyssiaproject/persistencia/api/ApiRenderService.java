package com.example.odyssiaproject.persistencia.api;


import com.example.odyssiaproject.dto.ActividadDTO;
import com.example.odyssiaproject.dto.PaisDTO;
import com.example.odyssiaproject.dto.VueloDTO;


import com.example.odyssiaproject.entidad.Actividad;

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
    Call<List<Ciudad>> getCiudades(@Query("pais") String pais);

    @GET("/promociones")
    Call<List<Promociones>> getPromociones(@Query("pais") String pais);

    @GET("/vuelos")
    Call<List<VueloDTO>> getVuelos(@Query("ciudad") String ciudad, @Query("pais") String pais);

    @GET("/actividades")
    Call<List<ActividadDTO>> getActividades(@Query("ciudad") String ciudad, @Query("pais") String pais);

    @GET("/alojamientos")
    Call<List<ActividadDTO>> getAlojamientos(@Query("ciudad") String ciudad, @Query("pais") String pais);

    @GET("/cultura")
    Call<List<ActividadDTO>> getCultura(@Query("ciudad") String ciudad, @Query("pais") String pais);

    @GET("/monumentos")
    Call<List<ActividadDTO>> getMonumentos(@Query("ciudad") String ciudad, @Query("pais") String pais);

    @GET("/ocio")
    Call<List<ActividadDTO>> getOcio(@Query("ciudad") String ciudad, @Query("pais") String pais);

    @GET("/ocionocturno")
    Call<List<ActividadDTO>> getOcioNocturno(@Query("ciudad") String ciudad, @Query("pais") String pais);

    @GET("/restaurantes")
    Call<List<ActividadDTO>> getRestaurantes(@Query("ciudad") String ciudad, @Query("pais") String pais);
}
