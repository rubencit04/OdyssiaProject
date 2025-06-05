package com.example.odyssiaproject.negocio;

import com.example.odyssiaproject.dto.PaisDTO;
import com.example.odyssiaproject.persistencia.DaoPais;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;

public class GestorPaises {
    private final DaoPais daoPais;

    public GestorPaises() {
            this.daoPais = new DaoPais();
    }

    public void obtenerPaises(final CallbackPaises callback) {
        Call<List<PaisDTO>> call = daoPais.obtenerPaises();
        call.enqueue(new retrofit2.Callback<List<PaisDTO>>() {
            @Override
            public void onResponse(Call<List<PaisDTO>> call, retrofit2.Response<List<PaisDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onPaisesCargados(response.body());
                } else {
                    callback.onError(new Exception("Respuesta vacía o error en servidor"));
                }
            }

            @Override
            public void onFailure(Call<List<PaisDTO>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void obtenerPaisesOrdenadosAZ(final CallbackPaises callback) {
        Call<List<PaisDTO>> call = daoPais.obtenerPaises();
        call.enqueue(new retrofit2.Callback<List<PaisDTO>>() {
            @Override
            public void onResponse(Call<List<PaisDTO>> call, retrofit2.Response<List<PaisDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PaisDTO> ordenados = response.body().stream()
                            .sorted(Comparator.comparing(PaisDTO::getNombre, String.CASE_INSENSITIVE_ORDER))
                            .collect(Collectors.toList());
                    callback.onPaisesCargados(ordenados);
                } else {
                    callback.onError(new Exception("Respuesta vacía o error en servidor"));
                }
            }

            @Override
            public void onFailure(Call<List<PaisDTO>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void obtenerPaisesOrdenadosPorPopularidad(final CallbackPaises callback) {
        Call<List<PaisDTO>> call = daoPais.obtenerPaises();
        call.enqueue(new retrofit2.Callback<List<PaisDTO>>() {
            @Override
            public void onResponse(Call<List<PaisDTO>> call, retrofit2.Response<List<PaisDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> PAISES_POPULARES = Arrays.asList(
                            "Francia", "España", "Italia", "Grecia", "Inglaterra",
                            "Portugal", "Holanda", "Bélgica", "Suiza", "Noruega"
                    );
                    List<PaisDTO> filtrados = response.body().stream()
                            .filter(p -> PAISES_POPULARES.contains(p.getNombre()))
                            .sorted(Comparator.comparingInt(p -> PAISES_POPULARES.indexOf(p.getNombre())))
                            .collect(Collectors.toList());
                    callback.onPaisesCargados(filtrados);
                } else {
                    callback.onError(new Exception("Respuesta vacía o error en servidor"));
                }
            }

            @Override
            public void onFailure(Call<List<PaisDTO>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void obtenerPaisesOrdenadosPorCosteVida(final CallbackPaises callback) {
        Call<List<PaisDTO>> call = daoPais.obtenerPaises();
        call.enqueue(new retrofit2.Callback<List<PaisDTO>>() {
            @Override
            public void onResponse(Call<List<PaisDTO>> call, retrofit2.Response<List<PaisDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> PAISES_COSTE_VIDA = Arrays.asList(
                            "Portugal", "España", "Grecia", "Italia", "Francia",
                            "Inglaterra", "Holanda", "Noruega", "Bélgica", "Suiza"
                    );
                    List<PaisDTO> filtrados = response.body().stream()
                            .filter(p -> PAISES_COSTE_VIDA.contains(p.getNombre()))
                            .sorted(Comparator.comparingInt(p -> PAISES_COSTE_VIDA.indexOf(p.getNombre())))
                            .collect(Collectors.toList());
                    callback.onPaisesCargados(filtrados);
                } else {
                    callback.onError(new Exception("Respuesta vacía o error en servidor"));
                }
            }

            @Override
            public void onFailure(Call<List<PaisDTO>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public String imagenPaises(PaisDTO pais) {
        if (pais == null || pais.getImagen() == null || pais.getImagen().isEmpty()) {
            return "https://ejemplo.com/default-promo.jpg";
        }
        return pais.getImagen();
    }


    // Callback para recibir las listas de países
    public interface CallbackPaises {
        void onPaisesCargados(List<PaisDTO> lista);
        void onError(Throwable t);
    }

}
