package com.example.odyssiaproject.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssiaproject.R;
import com.example.odyssiaproject.adaptador.AdaptadorPaises;
import com.example.odyssiaproject.adaptador.AdaptadorPromociones;
import com.example.odyssiaproject.entidad.Pais;
import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.persistencia.DaoPromociones;
import com.example.odyssiaproject.persistencia.api.ApiRenderService;
import com.example.odyssiaproject.persistencia.api.RetrofitRenderClient;
import com.example.odyssiaproject.runabble.PromocionesAutoScroller;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * HomeFragment es el fragmento principal de la pantalla de inicio.
 * <p>
 * Muestra dos RecyclerViews:
 * <ul>
 * <li>Uno horizontal para promociones.</li>
 * <li>Otro vertical para la lista de países.</li>
 * </ul>
 * Además, actualiza el encabezado del Navigation Drawer con el correo del usuario autenticado
 * utilizando Firebase Authentication.
 */
public class HomeFragment extends Fragment {

    // RecyclerView para mostrar promociones de forma horizontal.
    private RecyclerView recyclerViewPromociones; // Se mantiene
    // RecyclerView para mostrar la lista de países de forma vertical.
    private RecyclerView recyclerViewPaises; // Se mantiene
    // Lista que almacenará los objetos Pais obtenidos de Firestore.
    private List<Pais> listaPaises = new ArrayList<>();
    // Adaptador para el RecyclerView de promociones.
    private AdaptadorPromociones adaptadorPromociones;
    // Adaptador para el RecyclerView de países.
    private AdaptadorPaises adaptadorPaises;

    // --- INSTANCIA DE CLASE AUTO-SCROLLER (versión de panorámica continua) ---
    private PromocionesAutoScroller controladorScrollPromociones;

    // --- Parámetros para la panorámica continua (AJUSTA ESTOS VALORES) ---
    private static final int VELOCIDAD_SCROLL_PX_BASICO = 10;
    private static final long RETRASO_PASO_SCROLL_MS_BASICO = 50;
    private LinearLayoutManager promocionesLayoutManager;

    private ApiRenderService apiRenderService;

    DaoPromociones dao = new DaoPromociones();

    /**
     * Método del ciclo de vida del fragmento para crear la vista.
     *
     * @param inflater Inflater para inflar la vista.
     * @param container Contenedor padre de la vista.
     * @param savedInstanceState Bundle con el estado previo (si existe).
     * @return Vista inflada del fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        DrawerLayout drawerLayout = getActivity().findViewById(R.id.navBarDrawer);
        NavigationView navigationView = drawerLayout.findViewById(R.id.navBarView);
        if (navigationView != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView userEmailTextView = headerView.findViewById(R.id.twUsuario);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();
                userEmailTextView.setText(userEmail);
            } else {
                userEmailTextView.setText("Usuario no autenticado");
            }
        }

        recyclerViewPromociones = root.findViewById(R.id.rwPromotions);
        recyclerViewPromociones.setHasFixedSize(true);
        LinearLayoutManager promocionesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPromociones.setLayoutManager(promocionesLayoutManager);

        adaptadorPromociones = new AdaptadorPromociones(new ArrayList<>());
        recyclerViewPromociones.setAdapter(adaptadorPromociones);

        controladorScrollPromociones = new PromocionesAutoScroller(
                recyclerViewPromociones,
                VELOCIDAD_SCROLL_PX_BASICO,
                RETRASO_PASO_SCROLL_MS_BASICO
        );

        dao.obtenerPromocionesAleatorias(new DaoPromociones.PromocionCallback() {
            @Override
            public void onPromocionesCargadas(List<Promociones> promociones) {

                if (promociones != null && !promociones.isEmpty()) {
                    adaptadorPromociones.actualizarDatos(promociones);
                    controladorScrollPromociones.iniciarScroll();


                } else {
                    Log.w("HomeFragment", "Lista de promociones cargada está vacía o nula. No se inicia el scroll básico.");
                    adaptadorPromociones.actualizarDatos(new ArrayList<>());
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("HomeFragment", "Error cargando promociones", e);
                adaptadorPromociones.actualizarDatos(new ArrayList<>());
            }
        });

        recyclerViewPaises = root.findViewById(R.id.rwCountries);
        recyclerViewPaises.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewPaises.setHasFixedSize(true);


        apiRenderService = RetrofitRenderClient.getApiService();

        apiRenderService.getPaises().enqueue(new Callback<List<Pais>>() {
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaPaises = response.body();

                    // Crear el adaptador con la lista obtenida
                    adaptadorPaises = new AdaptadorPaises(listaPaises);
                    recyclerViewPaises.setAdapter(adaptadorPaises);

                } else {
                    Log.e("HomeFragment", "Error en la respuesta al obtener países");
                }
            }

            @Override
            public void onFailure(Call<List<Pais>> call, Throwable t) {
                Log.e("HomeFragment", "Fallo al obtener países", t);
            }
        });

        return root;
    }

    /**
     * Método del ciclo de vida del fragmento que se llama cuando la vista
     * del fragmento va a ser destruida.
     * --- IMPORTANTE: Detener el scroll automático aquí para evitar fugas de memoria. ---
     * Este método FALTABA y ha sido AÑADIDO.
     */
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
             // Si implementaste pausa/reanudar en tu clase AutoScroller
             // controladorScrollPromociones.reanudarScroll();
         }
     }

     @Override
     public void onStop() {
         super.onStop();
          if (controladorScrollPromociones != null) {
             // Si implementaste pausa/reanudar en tu clase AutoScroller
             // controladorScrollPromociones.pausarScroll(); // O detenerScroll() si solo quieres parar
         }
     }


}