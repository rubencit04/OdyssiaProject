package com.example.odyssiaproject.ui.exploration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssiaproject.R;
import com.example.odyssiaproject.adaptador.AdaptadorExploracion;
import com.example.odyssiaproject.adaptador.AdaptadorPromociones;
import com.example.odyssiaproject.entidad.Actividad;
import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.entidad.Pais;
import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.negocio.GestorPromociones;
import com.example.odyssiaproject.runabble.PromocionesAutoScroller;

import java.util.ArrayList;
import java.util.List;

public class ExplorationFragment extends Fragment {

    private RecyclerView rwExploration, recyclerViewPromociones;

    private static final String ARG_CIUDAD = "ciudad";
    private static final String ARG_PAIS = "pais";
    Pais pais;
    private List<Actividad> listaActividades = new ArrayList<>();

    private AdaptadorExploracion adaptadorExploracion;
    private AdaptadorPromociones adaptadorPromociones;

    private GestorPromociones gestorPromociones;

    private PromocionesAutoScroller controladorScrollPromociones;

    private static final int VELOCIDAD_SCROLL_PX_BASICO = 10;
    private static final long RETRASO_PASO_SCROLL_MS_BASICO = 50;

    public ExplorationFragment() {}

    public static ExplorationFragment newInstance(String nombrePais, String nombreCiudad) {
        ExplorationFragment fragment = new ExplorationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PAIS, nombrePais);
        args.putString(ARG_CIUDAD, nombreCiudad);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String nombrePais = getArguments().getString(ARG_PAIS);
            String nombreCiudad = getArguments().getString(ARG_CIUDAD);
            Log.d("ExplorationFragment", "Argumentos recibidos -> País: " + nombrePais + ", Ciudad: " + nombreCiudad);
        }
        gestorPromociones = new GestorPromociones();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_exploration, container, false);

        rwExploration = root.findViewById(R.id.rwExploration);
        rwExploration.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rwExploration.setHasFixedSize(true);

        recyclerViewPromociones = root.findViewById(R.id.rwPromotions);
        recyclerViewPromociones.setHasFixedSize(true);
        LinearLayoutManager promocionesLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPromociones.setLayoutManager(promocionesLayoutManager);

        adaptadorPromociones = new AdaptadorPromociones(new ArrayList<>());
        recyclerViewPromociones.setAdapter(adaptadorPromociones);

        controladorScrollPromociones = new PromocionesAutoScroller(
                recyclerViewPromociones,
                VELOCIDAD_SCROLL_PX_BASICO,
                RETRASO_PASO_SCROLL_MS_BASICO
        );

        if (getArguments() != null) {
            String nombrePais = getArguments().getString("pais");
            String nombreCiudad = getArguments().getString("ciudad");

            if (nombrePais != null && !nombrePais.isEmpty()) {
                pais = new Pais();
                pais.setNombre(nombrePais);

                Ciudad ciudad = new Ciudad();
                ciudad.setNombre(nombreCiudad);

                Log.d("ExplorationFragment", "País inicializado: " + pais.getNombre());

                // Cambiar la carga de promociones para usar el gestor:
                gestorPromociones.obtenerPromocionesPorPais(pais.getNombre(), new GestorPromociones.CallbackPromociones() {
                    @Override
                    public void onPromocionesCargadas(List<Promociones> promociones) {
                        if (promociones != null && !promociones.isEmpty()) {
                            adaptadorPromociones.actualizarDatos(promociones);
                            controladorScrollPromociones.iniciarScroll();
                        } else {
                            Log.w("ExplorationFragment", "Lista de promociones está vacía o nula. No se inicia scroll.");
                            adaptadorPromociones.actualizarDatos(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e("ExplorationFragment", "Error cargando promociones por país", t);
                        adaptadorPromociones.actualizarDatos(new ArrayList<>());
                    }
                });

                listaActividades = cargarActividadLocal(ciudad); // ciudad incluida

                List<Integer> imagenesDrawable = List.of(
                        R.drawable.v2_ocionocturnocard,
                        R.drawable.v2_actividadescard,
                        R.drawable.v2_restaurantescard,
                        R.drawable.v2_culturacard,
                        R.drawable.v2_alojamientoscard,
                        R.drawable.v2_monumentoscard
                );

                List<Integer> nombresStringId = List.of(
                        R.string.titulo_ocio_nocturno,
                        R.string.titulo_ocio,
                        R.string.titulo_restaurantes,
                        R.string.titulo_cultura,
                        R.string.titulo_vuelos,
                        R.string.titulo_monumentos
                );

                adaptadorExploracion = new AdaptadorExploracion(listaActividades, imagenesDrawable, nombresStringId);
                rwExploration.setAdapter(adaptadorExploracion);

            } else {
                Log.e("ExplorationFragment", "El argumento 'pais' es null o vacío");
            }
        } else {
            Log.e("ExplorationFragment", "No se recibieron argumentos");
        }

        return root;

    }

    private List<Actividad> cargarActividadLocal(Ciudad ciudad) {
        List<Actividad> actividades = new ArrayList<>();
        String[] nombres = {"Ocio Nocturno", "Ocio Diurno", "Restaurantes", "Cultura", "Alojamientos", "Monumentos"};

        for (String nombre : nombres) {
            Actividad actividad = new Actividad(nombre);
            actividad.setCiudad(ciudad);
            actividades.add(actividad);
        }

        return actividades;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (controladorScrollPromociones != null) {
            controladorScrollPromociones.detenerScroll();
        }

        recyclerViewPromociones = null;
        adaptadorPromociones = null;
        controladorScrollPromociones = null;

        rwExploration = null;
        adaptadorExploracion = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (controladorScrollPromociones != null) {
            // controladorScrollPromociones.reanudarScroll(); // si implementaste pausa/reanudar
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (controladorScrollPromociones != null) {
            // controladorScrollPromociones.pausarScroll(); // o detenerScroll()
        }
    }

}