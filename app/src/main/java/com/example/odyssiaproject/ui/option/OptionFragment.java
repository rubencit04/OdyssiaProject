package com.example.odyssiaproject.ui.option;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssiaproject.R;
import com.example.odyssiaproject.adaptador.AdaptadorOption;
import com.example.odyssiaproject.adaptador.AdaptadorPromociones;
import com.example.odyssiaproject.dto.ActividadDTO;
import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.negocio.GestorActividades;
import com.example.odyssiaproject.negocio.GestorPromociones;
import com.example.odyssiaproject.runabble.PromocionesAutoScroller;

import java.util.ArrayList;
import java.util.List;

public class OptionFragment extends Fragment {

    private RecyclerView recyclerViewPromociones;
    private RecyclerView recyclerViewOptions;

    private AdaptadorPromociones adaptadorPromociones;
    private AdaptadorOption adaptadorActividades;

    private static final String ARG_NOMBRE_CIUDAD = "ciudad";
    private static final String ARG_NOMBRE_ACTIVIDAD = "actividad";
    private static final String ARG_NOMBRE_PAIS = "pais";
    private String nombreCiudad;
    private String nombreActividad;
    private String nombrePais;

    private GestorPromociones gestorPromociones;
    private GestorActividades gestorActividades;

    private PromocionesAutoScroller controladorScrollPromociones;
    private static final int VELOCIDAD_SCROLL_PX_BASICO = 10;
    private static final long RETRASO_PASO_SCROLL_MS_BASICO = 50;

    private ImageButton btnFiltro;
    private String filtroActivo = null;
    private TextView textViewFiltro;

    public OptionFragment() {}

    public static OptionFragment newInstance(String nombreCiudad, String nombreActividad, String nombrePais) {
        OptionFragment fragment = new OptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE_CIUDAD, nombreCiudad);
        args.putString(ARG_NOMBRE_ACTIVIDAD, nombreActividad);
        args.putString(ARG_NOMBRE_PAIS, nombrePais);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestorPromociones = new GestorPromociones();
        gestorActividades = new GestorActividades();

        if (getArguments() != null) {
            nombreCiudad = getArguments().getString(ARG_NOMBRE_CIUDAD);
            nombreActividad = getArguments().getString(ARG_NOMBRE_ACTIVIDAD);
            nombrePais = getArguments().getString(ARG_NOMBRE_PAIS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_option, container, false);

        btnFiltro = root.findViewById(R.id.btnSortBy);
        textViewFiltro = root.findViewById(R.id.tvfiltro);
        recyclerViewOptions = root.findViewById(R.id.rwOptions);
        recyclerViewPromociones = root.findViewById(R.id.rwPromotions);


        recyclerViewOptions.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewOptions.setHasFixedSize(true);

        adaptadorActividades = new AdaptadorOption(requireContext(), new ArrayList<>());
        recyclerViewOptions.setAdapter(adaptadorActividades);

        recyclerViewPromociones.setHasFixedSize(true);
        LinearLayoutManager promocionesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPromociones.setLayoutManager(promocionesLayoutManager);
        controladorScrollPromociones = new PromocionesAutoScroller(
                recyclerViewPromociones, VELOCIDAD_SCROLL_PX_BASICO, RETRASO_PASO_SCROLL_MS_BASICO
        );
        adaptadorPromociones = new AdaptadorPromociones(new ArrayList<>(), controladorScrollPromociones);
        recyclerViewPromociones.setAdapter(adaptadorPromociones);


        if (nombrePais != null) {
            gestorPromociones.obtenerPromocionesPorPais(nombrePais, new GestorPromociones.CallbackPromociones() {
                @Override
                public void onPromocionesCargadas(List<Promociones> promociones) {
                    if (adaptadorPromociones != null) { // Comprobación de seguridad
                        if (promociones != null && !promociones.isEmpty()) {
                            adaptadorPromociones.actualizarDatos(promociones);
                            if (controladorScrollPromociones != null) controladorScrollPromociones.iniciarScroll();
                        } else {
                            Log.w("OptionFragment", "Lista de promociones vacía o nula");
                            adaptadorPromociones.actualizarDatos(new ArrayList<>());
                        }
                    }
                }
                @Override
                public void onError(Throwable t) {
                    Log.e("OptionFragment", "Error cargando promociones", t);
                }
            });
        } else {
            Log.e("OptionFragment", "El nombre del país es nulo. No se pueden cargar promociones.");
        }

        if (nombreActividad != null && nombreCiudad != null) {
            gestorActividades.obtenerActividadesPorTipo(nombreActividad, nombreCiudad, null, new GestorActividades.CallbackActividades() {
                @Override
                public void onActividadesCargadas(List<ActividadDTO> actividades) {
                    if (adaptadorActividades != null) { // Comprobación de seguridad
                        if (actividades != null && !actividades.isEmpty()) {
                            adaptadorActividades.actualizarDatos(actividades);
                        } else {
                            Log.w("OptionFragment", "Lista de actividades vacía o nula");
                            adaptadorActividades.actualizarDatos(new ArrayList<>());
                        }
                    }
                }
                @Override
                public void onError(Throwable t) {
                    Log.e("OptionFragment", "Error cargando actividades", t);
                }
            });
        } else {
            Log.e("OptionFragment", "Nombre de actividad o ciudad son nulos. No se pueden cargar actividades.");
        }



        recyclerViewPromociones.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if(controladorScrollPromociones != null) controladorScrollPromociones.detenerScroll();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(controladorScrollPromociones != null) controladorScrollPromociones.reanudarScrollConRetraso(700);
                }
            }
        });

        btnFiltro.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.activity_sort_menu_activities, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                String nombreFiltro = item.getTitle().toString();

                if (nombreFiltro.equals(filtroActivo)) {
                    filtroActivo = null;
                    textViewFiltro.setText("Filtrado: ninguno");
                    // Volver a cargar la lista original
                    gestorActividades.obtenerActividadesPorTipo(nombreActividad, nombreCiudad, null, new GestorActividades.CallbackActividades() {
                        @Override
                        public void onActividadesCargadas(List<ActividadDTO> lista) {
                            if(adaptadorActividades != null) adaptadorActividades.actualizarDatos(lista);
                        }
                        @Override
                        public void onError(Throwable t) {
                            Log.e("OptionFragment", "Error al quitar filtro", t);
                        }
                    });
                    return true;
                }

                filtroActivo = nombreFiltro;
                textViewFiltro.setText("Filtrado: " + nombreFiltro);

                if (id == R.id.sortA_Z) {
                    gestorActividades.obtenerActividadesOrdenadosAZ(nombreActividad, nombreCiudad, null, new GestorActividades.CallbackActividades(){
                        @Override
                        public void onActividadesCargadas(List<ActividadDTO> lista) {
                            if(adaptadorActividades != null) adaptadorActividades.actualizarDatos(lista);
                        }
                        @Override
                        public void onError(Throwable t) {
                            Log.e("OptionFragment", "Error al ordenar A-Z", t);
                        }
                    });

                } else if (id == R.id.gratis) {
                    gestorActividades.obtenerActividadesGratis(nombreActividad, nombreCiudad, null, new GestorActividades.CallbackActividades() {
                        @Override
                        public void onActividadesCargadas(List<ActividadDTO> lista) {
                            if(adaptadorActividades != null) adaptadorActividades.actualizarDatos(lista);
                        }
                        @Override
                        public void onError(Throwable t) {
                            Log.e("OptionFragment", "Error al ordenar por actividades gratis", t);
                        }
                    });

                } else if (id == R.id.precioBarato) {
                    gestorActividades.obtenerActividadesPorPrecioBarato(nombreActividad, nombreCiudad, null, 0.0, 100000.0, new GestorActividades.CallbackActividades() {
                        @Override
                        public void onActividadesCargadas(List<ActividadDTO> lista) {
                            if(adaptadorActividades != null) adaptadorActividades.actualizarDatos(lista);
                        }
                        @Override
                        public void onError(Throwable t) {
                            Log.e("OptionFragment", "Error al ordenar por popularidad", t);
                        }
                    });
                } else if (id == R.id.precioCaro) {
                    gestorActividades.obtenerActividadesPorPrecioCaro(nombreActividad, nombreCiudad, null, 0.0, 100000.0, new GestorActividades.CallbackActividades() {
                        @Override
                        public void onActividadesCargadas(List<ActividadDTO> lista) {
                            if(adaptadorActividades != null) adaptadorActividades.actualizarDatos(lista);
                        }
                        @Override
                        public void onError(Throwable t) {
                            Log.e("OptionFragment", "Error al ordenar por popularidad", t);
                        }
                    });
                }
                return true;
            });
            popupMenu.show();
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
        btnFiltro = null;
        textViewFiltro = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (controladorScrollPromociones != null) {
            controladorScrollPromociones.reanudarScrollConRetraso(100);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (controladorScrollPromociones != null) {
            controladorScrollPromociones.detenerScroll();
        }
    }
}