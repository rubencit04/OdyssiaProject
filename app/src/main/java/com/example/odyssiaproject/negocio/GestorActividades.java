package com.example.odyssiaproject.negocio;

import android.util.Log;

import com.example.odyssiaproject.dto.ActividadDTO;
import com.example.odyssiaproject.persistencia.DaoActividad;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestorActividades {

    private final DaoActividad daoActividad;

    public GestorActividades() {
        this.daoActividad = new DaoActividad();
    }

    public interface CallbackActividades {
        void onActividadesCargadas(List<ActividadDTO> lista);
        void onError(Throwable t);
    }


    /**
     * Obtiene una lista general de actividades, filtrando opcionalmente por ciudad y/o país.
     *
     * @param nombreCiudad El nombre de la ciudad para filtrar (puede ser null para no filtrar por ciudad).
     * @param nombrePais El nombre del país para filtrar (puede ser null para no filtrar por país).
     * @param callback La interfaz de callback para manejar el resultado.
     */
    public void obtenerActividades(String nombreCiudad, String nombrePais, final CallbackActividades callback) {
        Call<List<ActividadDTO>> call = daoActividad.obtenerActividades(nombreCiudad, nombrePais);
        call.enqueue(new Callback<List<ActividadDTO>>() {
            @Override
            public void onResponse(Call<List<ActividadDTO>> call, Response<List<ActividadDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onActividadesCargadas(response.body());
                } else {
                    callback.onError(new Exception("Respuesta vacía o error en servidor para actividades generales"));
                }
            }

            @Override
            public void onFailure(Call<List<ActividadDTO>> call, Throwable t) {
                Log.e("GestorActividades", "Error en Retrofit (obtenerActividades): " + t.getMessage(), t);
                callback.onError(t);
            }
        });
    }


    /**
     * Obtiene una lista de actividades de un tipo específico, filtrando opcionalmente por ciudad y/o país.
     * Este método solo carga las actividades de la API, sin aplicar filtros de precio.
     *
     * @param tipoActividad El tipo de actividad a obtener (ej. "cultura", "ocio", "restaurantes").
     * @param nombreCiudad El nombre de la ciudad para filtrar (puede ser null).
     * @param nombrePais El nombre del país para filtrar (puede ser null).
     * @param callback La interfaz de callback para manejar el resultado.
     */
    public void obtenerActividadesPorTipo(String tipoActividad, String nombreCiudad, String nombrePais, final CallbackActividades callback) {
        tipoActividad = tipoActividad.toLowerCase().replace("-", " ");
        Call<List<ActividadDTO>> call;
        switch (tipoActividad.toLowerCase()) {
            case "actividades":
                call = daoActividad.obtenerActividades(nombreCiudad, nombrePais);
                break;
            case "monumentos":
                call = daoActividad.obtenerMonumentos(nombreCiudad, nombrePais);
                break;
            case "cultura":
                call = daoActividad.obtenerCultura(nombreCiudad, nombrePais);
                break;
            case "ocio":
                call = daoActividad.obtenerOcio(nombreCiudad, nombrePais);
                break;
            case "ocio nocturno":
                call = daoActividad.obtenerOcioNocturno(nombreCiudad, nombrePais);
                break;
            case "restaurantes":
                call = daoActividad.obtenerRestaurantes(nombreCiudad, nombrePais);
                break;
            case "alojamientos":
                call = daoActividad.obtenerAlojamientos(nombreCiudad, nombrePais);
                break;
            default:
                callback.onError(new IllegalArgumentException("Tipo de actividad no reconocido: " + tipoActividad));
                return;
        }

        call.enqueue(new Callback<List<ActividadDTO>>() {
            @Override
            public void onResponse(Call<List<ActividadDTO>> call, Response<List<ActividadDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onActividadesCargadas(response.body());
                } else {
                    callback.onError(new Exception("Respuesta vacía o error en servidor al obtener " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<ActividadDTO>> call, Throwable t) {
                Log.e("GestorActividades", "Error en Retrofit (obtenerActividadesPorTipo - " + t + "): " + t.getMessage(), t);
                callback.onError(t);
            }
        });
    }


    public void obtenerActividadesPorPrecioBarato(String tipoActividad, String nombreCiudad, String nombrePais, double precioMin, double precioMax, final CallbackActividades callback) {
        obtenerActividadesPorTipo(tipoActividad, nombreCiudad, nombrePais, new CallbackActividades() {
            @Override
            public void onActividadesCargadas(List<ActividadDTO> listaOriginal) {
                if (listaOriginal == null) {
                    callback.onError(new Exception("Lista de actividades nula"));
                    return;
                }

                // Recalcular precioNumerico en cada actividad para asegurar datos actualizados
                for (ActividadDTO act : listaOriginal) {
                    act.calcularPrecioNumerico();
                }

                List<ActividadDTO> filtradasPorPrecio = listaOriginal.stream()
                        .filter(act -> {
                            double p = act.getPrecioNumerico();
                            return p != -1.0 && p >= precioMin && p <= precioMax;
                        })
                        .sorted(Comparator.comparingDouble(ActividadDTO::getPrecioNumerico))
                        .collect(Collectors.toList());

                callback.onActividadesCargadas(filtradasPorPrecio);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }



    public void obtenerActividadesGratis(String tipoActividad, String nombreCiudad, String nombrePais, final CallbackActividades callback) {
        obtenerActividadesPorTipo(tipoActividad, nombreCiudad, nombrePais, new CallbackActividades() {
            @Override
            public void onActividadesCargadas(List<ActividadDTO> listaOriginal) {
                if (listaOriginal == null || listaOriginal.isEmpty()) {
                    callback.onError(new Exception("No hay actividades disponibles"));
                    return;
                }
                for (ActividadDTO act : listaOriginal) {
                    act.calcularPrecioNumerico();
                }

                List<ActividadDTO> actividadesGratis = listaOriginal.stream()
                        .filter(act -> act.getPrecioNumerico() == 0.0)
                        .collect(Collectors.toList());

                callback.onActividadesCargadas(actividadesGratis);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }




    public void obtenerActividadesPorPrecioCaro(String tipoActividad, String nombreCiudad, String nombrePais, double precioMin, double precioMax, final CallbackActividades callback) {
        obtenerActividadesPorTipo(tipoActividad, nombreCiudad, nombrePais, new CallbackActividades() {
            @Override
            public void onActividadesCargadas(List<ActividadDTO> listaOriginal) {
                if (listaOriginal == null) {
                    callback.onError(new Exception("Lista de actividades nula"));
                    return;
                }

                // Recalcular precioNumerico en cada actividad
                for (ActividadDTO act : listaOriginal) {
                    act.calcularPrecioNumerico();
                }

                List<ActividadDTO> filtradasPorPrecio = listaOriginal.stream()
                        .filter(act -> {
                            double p = act.getPrecioNumerico();
                            return p != -1.0 && p >= precioMin && p <= precioMax;
                        })
                        .sorted(Comparator.comparingDouble(ActividadDTO::getPrecioNumerico).reversed())
                        .collect(Collectors.toList());

                callback.onActividadesCargadas(filtradasPorPrecio);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }


    public void obtenerActividadesOrdenadosAZ(String tipoActividad, String nombreCiudad, String nombrePais, final CallbackActividades callback) {
        obtenerActividadesPorTipo(tipoActividad, nombreCiudad, nombrePais, new CallbackActividades() {
            @Override
            public void onActividadesCargadas(List<ActividadDTO> listaOriginal) {
                if (listaOriginal == null) {
                    callback.onError(new Exception("Lista de actividades nula"));
                    return;
                }

                List<ActividadDTO> ordenados = listaOriginal.stream()
                        .sorted(Comparator.comparing(a -> a.getNombre().toLowerCase()))
                        .collect(Collectors.toList());

                callback.onActividadesCargadas(ordenados);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }

    /**
     * Devuelve la URL de imagen de la actividad o una imagen por defecto si es nula o vacía.
     * @param actividad Objeto ActividadDTO del cual se requiere la imagen.
     * @return URL de la imagen o valor por defecto si no se encuentra.
     */
    public String imagenActividad(ActividadDTO actividad) {
        if (actividad == null || actividad.getImagen() == null || actividad.getImagen().isEmpty()) {
            return "https://ejemplo.com/default-activity.jpg"; // imagen por defecto
        }
        return actividad.getImagen();
    }
}