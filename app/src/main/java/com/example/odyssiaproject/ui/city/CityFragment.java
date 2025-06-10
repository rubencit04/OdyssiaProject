package com.example.odyssiaproject.ui.city;

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
import com.example.odyssiaproject.adaptador.AdaptadorCiudades;
import com.example.odyssiaproject.adaptador.AdaptadorPromociones;
import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.entidad.Pais;
import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.negocio.GestorCiudades;
import com.example.odyssiaproject.negocio.GestorPromociones;
import com.example.odyssiaproject.persistencia.api.ApiRenderService;
import com.example.odyssiaproject.runabble.PromocionesAutoScroller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CityFragment extends Fragment {

    private RecyclerView recyclerViewPromociones;
    private RecyclerView recyclerViewCiudades;

    private Set<String> favoritosCiudades = new HashSet<>();
    private AdaptadorPromociones adaptadorPromociones;
    private AdaptadorCiudades adaptadorCiudades;

    private Pais pais;
    private static final String ARG_NOMBRE_CIUDAD = "ciudad"; // Aunque no se usa aquí, lo mantenemos por consistencia
    private String nombreCiudad;

    private GestorPromociones gestorPromociones;
    private GestorCiudades gestorCiudades;

    private PromocionesAutoScroller controladorScrollPromociones;

    private static final int VELOCIDAD_SCROLL_PX_BASICO = 10;
    private static final long RETRASO_PASO_SCROLL_MS_BASICO = 50;

    private ImageButton btnFiltro;
    private String filtroActivo = null;
    private TextView textViewFiltro;

    public CityFragment() {}

    public static CityFragment newInstance(String nombreCiudad, String nombrePais) {
        CityFragment fragment = new CityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE_CIUDAD, nombreCiudad);
        args.putString("pais", nombrePais);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestorPromociones = new GestorPromociones();
        gestorCiudades = new GestorCiudades();

        if (getArguments() != null) {
            String nombrePais = getArguments().getString("pais");
            if (nombrePais != null) {
                pais = new Pais();
                pais.setNombre(nombrePais);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_city, container, false);

        btnFiltro = root.findViewById(R.id.btnSortBy);
        textViewFiltro = root.findViewById(R.id.tvfiltro);
        recyclerViewCiudades = root.findViewById(R.id.rwCities);
        recyclerViewPromociones = root.findViewById(R.id.rwPromotions);


        recyclerViewPromociones.setHasFixedSize(true);
        LinearLayoutManager promocionesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPromociones.setLayoutManager(promocionesLayoutManager);
        controladorScrollPromociones = new PromocionesAutoScroller(recyclerViewPromociones, VELOCIDAD_SCROLL_PX_BASICO, RETRASO_PASO_SCROLL_MS_BASICO);

        adaptadorPromociones = new AdaptadorPromociones(new ArrayList<>(), controladorScrollPromociones);
        recyclerViewPromociones.setAdapter(adaptadorPromociones);

        recyclerViewCiudades.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCiudades.setHasFixedSize(true);

        adaptadorCiudades = new AdaptadorCiudades(new ArrayList<>(), favoritosCiudades);
        recyclerViewCiudades.setAdapter(adaptadorCiudades);

        if (pais != null && pais.getNombre() != null && !pais.getNombre().isEmpty()) {

            // Cargar promociones
            gestorPromociones.obtenerPromocionesPorPais(pais.getNombre(), new GestorPromociones.CallbackPromociones() {
                @Override
                public void onPromocionesCargadas(List<Promociones> promociones) {
                    if (adaptadorPromociones != null && promociones != null) {
                        adaptadorPromociones.actualizarDatos(promociones);
                        if (!promociones.isEmpty() && controladorScrollPromociones != null) {
                            controladorScrollPromociones.iniciarScroll();
                        }
                    }
                }
                @Override
                public void onError(Throwable t) {
                    Log.e("CityFragment", "Error cargando promociones por país", t);
                }
            });

            // Cargar ciudades
            gestorCiudades.obtenerCiudadesPorPais(pais.getNombre(), new GestorCiudades.CallbackCiudades() {
                @Override
                public void onCiudadesCargados(List<Ciudad> listaCiudades) {
                    // ¡CORRECCIÓN! El adaptador ya no se crea aquí, solo se actualizan sus datos.
                    if (adaptadorCiudades != null && listaCiudades != null) {
                        adaptadorCiudades.actualizarDatos(listaCiudades);
                        cargarFavoritosDesdeFirestore(); // Cargar favoritos después de que las ciudades estén listas
                    }
                }
                @Override
                public void onError(Throwable t) {
                    Log.e("CityFragment", "Error al cargar ciudades", t);
                }
            });


            btnFiltro.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(requireContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.activity_sort_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    String nombreFiltro = item.getTitle().toString();

                    if (nombreFiltro.equals(filtroActivo)) {
                        filtroActivo = null;
                        textViewFiltro.setText("ninguno");
                        gestorCiudades.obtenerCiudadesPorPais(pais.getNombre(), new GestorCiudades.CallbackCiudades() {
                            @Override
                            public void onCiudadesCargados(List<Ciudad> lista) {
                                // ¡CORRECCIÓN! Añadimos check de seguridad
                                if(adaptadorCiudades != null) adaptadorCiudades.actualizarDatos(lista);
                            }
                            @Override
                            public void onError(Throwable t) {
                                Log.e("CityFragment", "Error al quitar filtro", t);
                            }
                        });
                        return true;
                    }

                    filtroActivo = nombreFiltro;
                    textViewFiltro.setText(nombreFiltro);

                    if (id == R.id.sortA_Z) {
                        gestorCiudades.obtenerCiudadesOrdenadosAZ(pais.getNombre(), new GestorCiudades.CallbackCiudades() {
                            @Override
                            public void onCiudadesCargados(List<Ciudad> lista) {
                                if(adaptadorCiudades != null) adaptadorCiudades.actualizarDatos(lista);
                            }
                            @Override
                            public void onError(Throwable t) {
                                Log.e("CityFragment", "Error al ordenar A-Z", t);
                            }
                        });
                    } else if (id == R.id.sortCoste_Vida) {
                        gestorCiudades.obtenerCiudadesOrdenadasPorCosteVida(pais.getNombre(), new GestorCiudades.CallbackCiudades() {
                            @Override
                            public void onCiudadesCargados(List<Ciudad> lista) {
                                if(adaptadorCiudades != null) adaptadorCiudades.actualizarDatos(lista);
                            }
                            @Override
                            public void onError(Throwable t) {
                                Log.e("CityFragment", "Error al ordenar por coste de vida", t);
                            }
                        });
                    } else if (id == R.id.sortPopularidad) {
                        gestorCiudades.obtenerCiudadesOrdenadasPorPopularidad(pais.getNombre(), new GestorCiudades.CallbackCiudades() {
                            @Override
                            public void onCiudadesCargados(List<Ciudad> lista) {
                                if(adaptadorCiudades != null) adaptadorCiudades.actualizarDatos(lista);
                            }
                            @Override
                            public void onError(Throwable t) {
                                Log.e("CityFragment", "Error al ordenar por popularidad", t);
                            }
                        });
                    }
                    return true;
                });
                popupMenu.show();
            });

        } else {
            Log.e("CityFragment", "No se recibieron argumentos o el nombre del país es nulo.");
        }

        recyclerViewPromociones.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if(controladorScrollPromociones != null) controladorScrollPromociones.detenerScroll();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(controladorScrollPromociones != null) controladorScrollPromociones.reanudarScrollConRetraso(300);
                }
            }
        });

        return root;
    }

    private void cargarFavoritosDesdeFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("usuario").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        // ¡CORRECCIÓN! Check de seguridad
                        if (adaptadorCiudades == null) return;

                        favoritosCiudades.clear();
                        if (documentSnapshot.exists()) {
                            List<String> favList = (List<String>) documentSnapshot.get("favoritosCiudades");
                            if (favList != null) {
                                favoritosCiudades.addAll(favList);
                            }
                        }
                        adaptadorCiudades.setFavoritos(favoritosCiudades);
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error cargando favoritos", e));
        }
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
        recyclerViewCiudades = null;
        adaptadorCiudades = null;
        btnFiltro = null;
        textViewFiltro = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (controladorScrollPromociones != null) {
            controladorScrollPromociones.reanudarScrollConRetraso(700);
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