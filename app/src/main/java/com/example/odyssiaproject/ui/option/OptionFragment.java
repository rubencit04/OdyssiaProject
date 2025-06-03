
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
import com.example.odyssiaproject.dto.ActividadDTO;
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

    private List<ActividadDTO> listaActividades = new ArrayList<>();
    private AdaptadorPromociones adaptadorPromociones;
    private AdaptadorOption adaptadorActividades;

    private static final String ARG_NOMBRE_CIUDAD = "ciudad";
    private static final String ARG_NOMBRE_ACTIVIDAD = "actividad";
    private String nombreCiudad;
    private String nombreActividad;

    private GestorPromociones gestorPromociones;

    private PromocionesAutoScroller controladorScrollPromociones;

    private static final int VELOCIDAD_SCROLL_PX_BASICO = 10;
    private static final long RETRASO_PASO_SCROLL_MS_BASICO = 50;

    public OptionFragment() {}

    public static OptionFragment newInstance(String nombreCiudad, String nombreActividad) {
        OptionFragment fragment = new OptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE_CIUDAD, nombreCiudad);
        args.putString(ARG_NOMBRE_ACTIVIDAD, nombreActividad);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instanciar el gestor
        gestorPromociones = new GestorPromociones();
        if (getArguments() != null) {
            nombreCiudad = getArguments().getString(ARG_NOMBRE_CIUDAD);
            nombreActividad = getArguments().getString(ARG_NOMBRE_ACTIVIDAD);
        }


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_option, container, false);

        recyclerViewOptions = root.findViewById(R.id.rwOptions);
        recyclerViewOptions.setLayoutManager(new LinearLayoutManager(getContext()));
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

        if (getArguments() != null) {
            String nombreCiudad = getArguments().getString(ARG_NOMBRE_CIUDAD);
            String nombreActividad = getArguments().getString(ARG_NOMBRE_ACTIVIDAD);

            cargarActividades(nombreActividad, nombreCiudad, null, recyclerViewOptions);
        } else {
            Log.e("OptionFragment", "No se encontraron argumentos");
        }

        return root;
    }


    private void cargarActividades(String actividad, String ciudad, String pais, RecyclerView recyclerView) {
        Log.d("OptionFragment", "cargarActividades con actividad=" + actividad + ", ciudad=" + ciudad);
        actividad = actividad.toLowerCase().replace("-", " ");
        ApiRenderService apiService = RetrofitRenderClient.getApiService();

        Call<List<ActividadDTO>> call;

        switch (actividad) {
            case "alojamientos":
                call = apiService.getAlojamientos(ciudad, pais);
                break;
            case "cultura":
                call = apiService.getCultura(ciudad, pais);
                break;
            case "monumentos":
                call = apiService.getMonumentos(ciudad, pais);
                break;
            case "ocio nocturno":
                call = apiService.getOcioNocturno(ciudad, pais);
                break;
            case "ocio":
                call = apiService.getOcio(ciudad, pais);
                break;
            case "restaurantes":
                call = apiService.getRestaurantes(ciudad, pais);
                break;
            default:
                Log.e("OptionFragment", "Actividad no reconocida: " + actividad);
                return;
        }

        call.enqueue(new Callback<List<ActividadDTO>>() {
            @Override
            public void onResponse(Call<List<ActividadDTO>> call, Response<List<ActividadDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ActividadDTO> todasActividades = response.body();
                    Log.d("OptionFragment", "Recibidas " + todasActividades.size() + " actividades");

                    listaActividades = todasActividades;

                    adaptadorActividades = new AdaptadorOption(requireContext(), listaActividades);
                    recyclerView.setAdapter(adaptadorActividades);

                } else {
                    Log.e("OptionFragment", "Respuesta no exitosa o cuerpo null: " + response.code());
                }
            }


            @Override
            public void onFailure(Call<List<ActividadDTO>> call, Throwable t) {
                Log.e("OptionFragment", "Fallo al obtener actividades", t);
            }
        });
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

