
package com.example.odyssiaproject.ui.option;

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
import com.example.odyssiaproject.adaptador.AdaptadorOption;
import com.example.odyssiaproject.adaptador.AdaptadorPromociones;
import com.example.odyssiaproject.entidad.Actividad;
import com.example.odyssiaproject.entidad.Pais;
import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.negocio.GestorPromociones;
import com.example.odyssiaproject.persistencia.api.ApiRenderService;
import com.example.odyssiaproject.persistencia.api.RetrofitRenderClient;
import com.example.odyssiaproject.runabble.PromocionesAutoScroller;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OptionFragment extends Fragment {

    private RecyclerView recyclerViewPromociones;
    private RecyclerView recyclerViewOptions;

    private List<Actividad> listaActividades = new ArrayList<>();
    private AdaptadorPromociones adaptadorPromociones;
    private AdaptadorOption adaptadorActividades;

    private static final String ARG_NOMBRE_CIUDAD = "ciudad";

    private GestorPromociones gestorPromociones;

    private PromocionesAutoScroller controladorScrollPromociones;

    private static final int VELOCIDAD_SCROLL_PX_BASICO = 10;
    private static final long RETRASO_PASO_SCROLL_MS_BASICO = 50;

    public OptionFragment() {}

    public static OptionFragment newInstance(String nombreCiudad, String nombreActividad) {
        OptionFragment fragment = new OptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE_CIUDAD, nombreCiudad);
        args.putString("actividad", nombreActividad);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instanciar el gestor
        gestorPromociones = new GestorPromociones();

        if (getArguments() != null) {
            String nombreCiudad = getArguments().getString(ARG_NOMBRE_CIUDAD);
            String nombreActividad = getArguments().getString("actividad");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_option, container, false);

        recyclerViewOptions = root.findViewById(R.id.rwOptions);
        recyclerViewOptions.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerViewOptions.setHasFixedSize(true);

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

        gestorPromociones.obtenerPromociones(new GestorPromociones.CallbackPromociones() {
            @Override
            public void onPromocionesCargadas(List<Promociones> promociones) {
                if (promociones != null && !promociones.isEmpty()) {
                    adaptadorPromociones.actualizarDatos(promociones);
                    controladorScrollPromociones.iniciarScroll();
                } else {
                    Log.w("OptionFragment", "Lista de promociones vacía o nula");
                    adaptadorPromociones.actualizarDatos(new ArrayList<>());
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("OptionFragment", "Error cargando promociones", t);
                adaptadorPromociones.actualizarDatos(new ArrayList<>());
            }
        });

        return root;
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

        recyclerViewOptions = null;
        adaptadorActividades = null;
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

