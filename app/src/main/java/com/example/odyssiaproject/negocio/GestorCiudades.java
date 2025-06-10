package com.example.odyssiaproject.negocio;

import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.persistencia.DaoCiudad;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;



/**
 * Clase para gestionar la lógica relacionada con ciudades (obtención de imágenes, validaciones, etc.).
 */
public class GestorCiudades {
    private final DaoCiudad daoCiudad;
    public GestorCiudades() {
        this.daoCiudad = new DaoCiudad();
    }
    public void obtenerCiudadesPorPais(String nombrePais, final GestorCiudades.CallbackCiudades callback) {
        Call<List<Ciudad>> call = daoCiudad.obtenerCiudadesPorPais(nombrePais);
        call.enqueue(new retrofit2.Callback<List<Ciudad>>() {
            @Override
            public void onResponse(Call<List<Ciudad>> call, retrofit2.Response<List<Ciudad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onCiudadesCargados(response.body());
                } else {
                    callback.onError(new Exception("Respuesta vacía o error en servidor"));
                }
            }

            @Override
            public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
    public void obtenerCiudadesOrdenadosAZ(String nombrePais,final GestorCiudades.CallbackCiudades callback) {
        Call<List<Ciudad>> call = daoCiudad.obtenerCiudadesPorPais(nombrePais);
        call.enqueue(new retrofit2.Callback<List<Ciudad>>() {
            @Override
            public void onResponse(Call<List<Ciudad>> call, retrofit2.Response<List<Ciudad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Ciudad> ordenados = response.body().stream()
                            .sorted(Comparator.comparing(Ciudad::getNombre, String.CASE_INSENSITIVE_ORDER))
                            .collect(Collectors.toList());
                    callback.onCiudadesCargados(ordenados);
                } else {
                    callback.onError(new Exception("Respuesta vacía o error en servidor"));
                }
            }

            @Override
            public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
    /**
     * Obtiene y ordena las ciudades por popularidad (más popular a menos popular)
     * según una lista predefinida.
     *
     * @param callback La interfaz de callback para manejar el resultado.
     */
    public void obtenerCiudadesOrdenadasPorPopularidad(String nombrePais,final CallbackCiudades callback) {
        Call<List<Ciudad>> call = daoCiudad.obtenerCiudadesPorPais(nombrePais);
        call.enqueue(new retrofit2.Callback<List<Ciudad>>() {
            @Override
            public void onResponse(Call<List<Ciudad>> call,  retrofit2.Response<List<Ciudad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> CIUDADES_POPULARES = Arrays.asList(
                            "Paris", "Madrid", "Roma", "Londres", "Barcelona",
                            "Amsterdam", "Lisboa", "Atenas", "Oporto", "Sevilla",
                            "Florencia", "Venecia", "Bruselas", "Lyon", "Marsella",
                            "Oslo", "Amberes", "Zurich", "Salónica", "Berna",
                            "Brujas", "Ginebra", "Rotterdam", "Utrecht", "Manchester",
                            "Liverpool", "Bergen", "Tromso", "Santorini"
                    );
                    List<Ciudad> filtrados = response.body().stream()
                            .filter(c -> CIUDADES_POPULARES.contains(c.getNombre()))
                            .sorted(Comparator.comparingInt(c -> CIUDADES_POPULARES.indexOf(c.getNombre())))
                            .collect(Collectors.toList());
                    callback.onCiudadesCargados(filtrados);
                } else {
                    callback.onError(new Exception("Respuesta vacía o error en servidor al obtener ciudades populares"));
                }
            }

            @Override
            public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /**
     * Obtiene y ordena las ciudades por coste de vida (más barato a más caro)
     * según una lista predefinida.
     *
     * @param callback La interfaz de callback para manejar el resultado.
     */
    public void obtenerCiudadesOrdenadasPorCosteVida(String nombrePais,final CallbackCiudades callback) {
        Call<List<Ciudad>> call = daoCiudad.obtenerCiudadesPorPais(nombrePais);
        call.enqueue(new retrofit2.Callback<List<Ciudad>>() {
            @Override
            public void onResponse(Call<List<Ciudad>> call,retrofit2.Response<List<Ciudad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> CIUDADES_COSTE_VIDA = Arrays.asList(
                            "Braga", "Sevilla", "Salonica", "Oporto", "Liverpool",
                            "Manchester", "Marsella", "Atenas", "Utrecht", "Rotterdam",
                            "Lyon", "Bergen", "Tromso", "Berna", "Brujas",
                            "Amberes", "Bruselas", "Lisboa", "Madrid", "Florencia",
                            "Venecia", "Barcelona", "Roma", "Amsterdam", "Oslo",
                            "Paris", "Ginebra", "Londres", "Zurich", "Santorini"
                    );
                    List<Ciudad> filtrados = response.body().stream()
                            .filter(c -> CIUDADES_COSTE_VIDA.contains(c.getNombre()))
                            .sorted(Comparator.comparingInt(c -> CIUDADES_COSTE_VIDA.indexOf(c.getNombre())))
                            .collect(Collectors.toList());
                    callback.onCiudadesCargados(filtrados);
                } else {
                    callback.onError(new Exception("Respuesta vacía o error en servidor al obtener ciudades por coste de vida"));
                }
            }

            @Override
            public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }


    /**
     * Obtiene la URL de la imagen de una ciudad desde Firestore o un mapa estático.
     *
     * @param ciudad Objeto Ciudad del cual se requiere la imagen.
     * @return URL de la imagen o valor por defecto si no se encuentra.
     */
    public String imagenCiudad(Ciudad ciudad) {
        if (ciudad == null || ciudad.getImagen() == null || ciudad.getImagen().isEmpty()) {
            return "https://ejemplo.com/default.jpg"; // imagen por defecto
        }
        return ciudad.getImagen();
    }
    public interface CallbackCiudades {
        void onCiudadesCargados(List<Ciudad> lista);
        void onError(Throwable t);
    }
}