package com.example.odyssiaproject.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.odyssiaproject.negocio.GestorPromociones;
import com.example.odyssiaproject.persistencia.api.ApiRenderService;
import com.example.odyssiaproject.persistencia.api.RetrofitRenderClient;
import com.example.odyssiaproject.runabble.PromocionesAutoScroller;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private ApiRenderService apiRenderService;

    // Aquí instanciamos GestorPromociones, que maneja la lógica de negocio
    private GestorPromociones gestorPromociones = new GestorPromociones();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

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

        // Configuración RecyclerView promociones (horizontal)
        recyclerViewPromociones = root.findViewById(R.id.rwPromotions);
        recyclerViewPromociones.setHasFixedSize(true);
        promocionesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPromociones.setLayoutManager(promocionesLayoutManager);

        adaptadorPromociones = new AdaptadorPromociones(new ArrayList<Promociones>());
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
        apiRenderService = RetrofitRenderClient.getApiService();
        apiRenderService.getPaises().enqueue(new Callback<List<PaisDTO>>() {
            @Override
            public void onResponse(Call<List<PaisDTO>> call, Response<List<PaisDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaPaises = response.body();
                    adaptadorPaises = new AdaptadorPaises(listaPaises);
                    recyclerViewPaises.setAdapter(adaptadorPaises);
                } else {
                    Log.e("HomeFragment", "Error en la respuesta al obtener países");
                }
            }

            @Override
            public void onFailure(Call<List<PaisDTO>> call, Throwable t) {
                Log.e("HomeFragment", "Fallo al obtener países", t);
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
            // controladorScrollPromociones.reanudarScroll(); // Si implementas pausa/reanudar
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (controladorScrollPromociones != null) {
            // controladorScrollPromociones.pausarScroll(); // O detenerScroll() si solo quieres parar
        }
    }
}
