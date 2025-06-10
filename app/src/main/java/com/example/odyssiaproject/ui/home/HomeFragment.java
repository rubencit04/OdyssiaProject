package com.example.odyssiaproject.ui.home;

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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssiaproject.R;
import com.example.odyssiaproject.adaptador.AdaptadorPaises;
import com.example.odyssiaproject.adaptador.AdaptadorPromociones;
import com.example.odyssiaproject.dto.PaisDTO;
import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.negocio.GestorPaises;
import com.example.odyssiaproject.negocio.GestorPromociones;
import com.example.odyssiaproject.runabble.PromocionesAutoScroller;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
/**
 * HomeFragment es el fragmento principal de la pantalla de inicio.
 * Muestra promociones y países en RecyclerViews.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPromociones;
    private RecyclerView recyclerViewPaises;
    private List<PaisDTO> listaPaises = new ArrayList<>();
    private AdaptadorPromociones adaptadorPromociones;
    private AdaptadorPaises adaptadorPaises;

    private PromocionesAutoScroller controladorScrollPromociones;

    private static final int VELOCIDAD_SCROLL_PX_BASICO = 10;
    private static final long RETRASO_PASO_SCROLL_MS_BASICO = 50;

    private LinearLayoutManager promocionesLayoutManager;

    private GestorPromociones gestorPromociones = new GestorPromociones();
     private GestorPaises gestorPaises = new GestorPaises();
     private ImageButton btnFiltro;
    private String filtroActivo = null;
    private TextView textViewFiltro;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        btnFiltro = root.findViewById(R.id.btnSortBy);
        TextView textViewFiltro = root.findViewById(R.id.tvfiltro);

        // Configurar header Navigation Drawer con email usuario Firebase
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.navBarDrawer);
        NavigationView navigationView = drawerLayout.findViewById(R.id.navBarView);
        if (navigationView != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView userEmailTextView = headerView.findViewById(R.id.twUsuario);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                userEmailTextView.setText(currentUser.getEmail());
            } else {
                userEmailTextView.setText("Usuario no autenticado");
            }
        }

        recyclerViewPromociones = root.findViewById(R.id.rwPromotions);
        recyclerViewPromociones.setHasFixedSize(true);
        promocionesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPromociones.setLayoutManager(promocionesLayoutManager);


        controladorScrollPromociones = new PromocionesAutoScroller(
                recyclerViewPromociones,
                VELOCIDAD_SCROLL_PX_BASICO,
                RETRASO_PASO_SCROLL_MS_BASICO
        );

        controladorScrollPromociones.iniciarScroll();

        adaptadorPromociones = new AdaptadorPromociones(
                new ArrayList<>(),
                controladorScrollPromociones
        );
        recyclerViewPromociones.setAdapter(adaptadorPromociones);

        recyclerViewPromociones.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    controladorScrollPromociones.detenerScroll();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    controladorScrollPromociones.reanudarScrollConRetraso(700);
                }
            }
        });

        gestorPromociones.obtenerPromociones(new GestorPromociones.CallbackPromociones() {
            @Override
            public void onPromocionesCargadas(List<Promociones> promociones) {
                if (promociones != null && !promociones.isEmpty()) {
                    adaptadorPromociones.actualizarDatos(promociones);

                } else {
                    Log.w("HomeFragment", "Lista de promociones vacía o nula");
                    adaptadorPromociones.actualizarDatos(new ArrayList<>());
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("HomeFragment", "Error cargando promociones", t);
                adaptadorPromociones.actualizarDatos(new ArrayList<>());
            }
        });

        // Configuración RecyclerView países (vertical)
        recyclerViewPaises = root.findViewById(R.id.rwCountries);
        recyclerViewPaises.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewPaises.setHasFixedSize(true);

        // Cargar países desde API directamente (puedes también hacer un gestor si quieres)


        gestorPaises.obtenerPaises(new GestorPaises.CallbackPaises() {
            @Override
            public void onPaisesCargados(List<PaisDTO> listaPaises) {
                adaptadorPaises = new AdaptadorPaises(listaPaises);
                recyclerViewPaises.setAdapter(adaptadorPaises);
            }

            @Override
            public void onError(Throwable t) {
                Log.e("HomeFragment", "Error al cargar países", t);
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
                    textViewFiltro.setText("Filtrado: ninguno");

                    gestorPaises.obtenerPaises(new GestorPaises.CallbackPaises() {
                        @Override
                        public void onPaisesCargados(List<PaisDTO> lista) {
                            adaptadorPaises.actualizarDatos(lista);
                        }

                        @Override
                        public void onError(Throwable t) {
                            Log.e("HomeFragment", "Error al quitar filtro", t);
                        }
                    });

                    return true;
                }

                filtroActivo = nombreFiltro;
                textViewFiltro.setText("Filtrado: " + nombreFiltro);

                if (id == R.id.sortA_Z) {
                    gestorPaises.obtenerPaisesOrdenadosAZ(new GestorPaises.CallbackPaises() {
                        @Override
                        public void onPaisesCargados(List<PaisDTO> lista) {
                            adaptadorPaises.actualizarDatos(lista);
                        }

                        @Override
                        public void onError(Throwable t) {
                            Log.e("HomeFragment", "Error al ordenar A-Z", t);
                        }
                    });

                } else if (id == R.id.sortCoste_Vida) {
                    gestorPaises.obtenerPaisesOrdenadosPorCosteVida(new GestorPaises.CallbackPaises() {
                        @Override
                        public void onPaisesCargados(List<PaisDTO> lista) {
                            adaptadorPaises.actualizarDatos(lista);
                        }

                        @Override
                        public void onError(Throwable t) {
                            Log.e("HomeFragment", "Error al ordenar por coste de vida", t);
                        }
                    });

                } else if (id == R.id.sortPopularidad) {
                    gestorPaises.obtenerPaisesOrdenadosPorPopularidad(new GestorPaises.CallbackPaises() {
                        @Override
                        public void onPaisesCargados(List<PaisDTO> lista) {
                            adaptadorPaises.actualizarDatos(lista);
                        }

                        @Override
                        public void onError(Throwable t) {
                            Log.e("HomeFragment", "Error al ordenar por popularidad", t);
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
        promocionesLayoutManager = null;
        adaptadorPromociones = null;
        controladorScrollPromociones = null;
        recyclerViewPaises = null;
        adaptadorPaises = null;
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
