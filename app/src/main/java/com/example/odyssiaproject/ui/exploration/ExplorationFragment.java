package com.example.odyssiaproject.ui.exploration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssiaproject.R;
import com.example.odyssiaproject.adaptador.AdaptadorExploracion;
import com.example.odyssiaproject.adaptador.AdaptadorPromociones;
import com.example.odyssiaproject.entidad.Actividad;
import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.negocio.GestorPromociones;
import com.example.odyssiaproject.persistencia.api.ApiRenderService;
import com.example.odyssiaproject.runabble.PromocionesAutoScroller;

import java.util.ArrayList;
import java.util.List;

public class ExplorationFragment extends Fragment {

    private RecyclerView rwExploration, rwPromotions;

    private ApiRenderService apiRenderService;

    private AdaptadorExploracion adaptadorExploracion;
    private AdaptadorPromociones adaptadorPromociones;

    private GestorPromociones gestorPromociones;

    private LinearLayoutManager promocionesLayoutManager;

    private PromocionesAutoScroller controladorScrollPromociones;

    private static final int VELOCIDAD_SCROLL_PX_BASICO = 10;
    private static final long RETRASO_PASO_SCROLL_MS_BASICO = 50;

    public ExplorationFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestorPromociones = new GestorPromociones();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_exploration, container, false);

        // Configuración RecyclerView promociones (horizontal)
        rwPromotions = root.findViewById(R.id.rwPromotions);
        rwPromotions.setHasFixedSize(true);
        promocionesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rwPromotions.setLayoutManager(promocionesLayoutManager);

        adaptadorPromociones = new AdaptadorPromociones(new ArrayList<Promociones>());
        rwPromotions.setAdapter(adaptadorPromociones);

        controladorScrollPromociones = new PromocionesAutoScroller(
                rwPromotions,
                VELOCIDAD_SCROLL_PX_BASICO,
                RETRASO_PASO_SCROLL_MS_BASICO
        );

        gestorPromociones.obtenerPromociones(new GestorPromociones.CallbackPromociones() {
            @Override
            public void onPromocionesCargadas(List<Promociones> promociones) {
                if (promociones != null && !promociones.isEmpty()) {
                    adaptadorPromociones.actualizarDatos(promociones);
                    controladorScrollPromociones.iniciarScroll();
                } else {
                    Log.w("ExplorationFragment", "Lista de promociones vacía o nula");
                    adaptadorPromociones.actualizarDatos(new ArrayList<>());
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("ExplorationFragment", "Error cargando promociones", t);
                adaptadorPromociones.actualizarDatos(new ArrayList<>());
            }
        });

        // Configuración RecyclerView países (vertical)
        rwExploration = root.findViewById(R.id.rwExploration);
        rwExploration.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rwExploration.setHasFixedSize(true);

        // Cargar actividades desde drawable directamente
        private List<Actividad> cargarActividadLocal() {

            List<Actividad> actividades = new ArrayList<>();
            actividades.add(new Actividad("Ocio Nocturno"));
            actividades.add(new Actividad("Ocio Diurno"));
            actividades.add(new Actividad("Restaurantes"));
            actividades.add(new Actividad("Cultura"));
            actividades.add(new Actividad("Vuelos"));
            actividades.add(new Actividad("Monumentos"));
            return actividades;
        }

        List<Actividad> listaActividades = cargarActividadLocal();

        adaptadorExploracion = new AdaptadorExploracion(listaActividades);
        rwExploration.setAdapter(adaptadorExploracion);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (controladorScrollPromociones != null) {
            controladorScrollPromociones.detenerScroll();
        }

        rwPromotions = null;
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