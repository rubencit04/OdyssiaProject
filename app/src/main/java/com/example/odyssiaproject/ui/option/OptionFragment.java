
package com.example.odyssiaproject.ui.option;

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
import com.example.odyssiaproject.adaptador.AdaptadorMonumentos;
import com.example.odyssiaproject.adaptador.AdaptadorPromociones;
import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.runabble.PromocionesAutoScroller;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class OptionFragment extends Fragment {

    private RecyclerView recyclerViewPromociones;
    private RecyclerView recyclerViewMonumentos;
    // Adaptador de promociones (USA TU ADAPTADOR ORIGINAL sin modificaciones para scroll infinito)
    private AdaptadorPromociones adaptadorPromociones;
    private AdaptadorMonumentos adaptadorMonumentos;

    // --- INSTANCIA DE LA CLASE BÁSICA DEL SCROLL AUTOMÁTICO ---
    private PromocionesAutoScroller controladorScrollPromociones;

    // --- Parámetros para la panorámica continua (AJUSTA ESTOS VALORES si quieres que vayan distinto) ---
    private static final int VELOCIDAD_SCROLL_PX_BASICO = 10; // Pixeles por paso
    private static final long RETRASO_PASO_SCROLL_MS_BASICO = 50; // Retraso entre pasos

    // Variables para la ciudad (AHORA se obtendrá de getArguments() y se guardará aquí si es necesario)
    private Ciudad ciudad;
    private String nombreCiudad;

    public OptionFragment() {

    }

    public static OptionFragment newInstance(String nombreCiudad) {
        OptionFragment fragment = new OptionFragment();
        Bundle args = new Bundle(); // 2. Crear un Bundle
        args.putString("nombreCiudadKey", nombreCiudad);
        fragment.setArguments(args);
        return fragment;
    }


    // --- Método onCreate (El lugar RECOMENDADO para RECUPERAR los argumentos) ---
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // --- RECUPERAR el dato de los argumentos ---
        if (getArguments() != null) {
            nombreCiudad = getArguments().getString("nombreCiudadKey");
            Log.d("OptionFragment", "Ciudad recuperada (desde args) en onCreate: " + nombreCiudad);
        } else {
            Log.e("OptionFragment", "Error: Fragment creado sin argumentos. nombreCiudad es null.");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_option, container, false);

        DrawerLayout drawerLayout = getActivity().findViewById(R.id.navBarDrawer);
        NavigationView navigationView = drawerLayout.findViewById(R.id.navBarView);
        if (navigationView != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView userEmailTextView = headerView.findViewById(R.id.twUsuario);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                userEmailTextView.setText(currentUser.getEmail());
            } else {
                userEmailTextView.setText("Usuario no autenticado");
            }
        }

        recyclerViewPromociones = root.findViewById(R.id.rwPromotions);
        recyclerViewPromociones.setHasFixedSize(true);
        LinearLayoutManager promocionesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPromociones.setLayoutManager(promocionesLayoutManager);

        /*
        List<Promociones> listaPromociones = ListaPromocionesSingelton.getInstance().getListaPromociones();
                if (listaPromociones == null) {
                    listaPromociones = new ArrayList<>();
                }

                adaptadorPromociones = new AdaptadorPromociones(listaPromociones);
                recyclerViewPromociones.setAdapter(adaptadorPromociones);

              controladorScrollPromociones = new PromocionesAutoScroller(
                       recyclerViewPromociones,
                     VELOCIDAD_SCROLL_PX_BASICO,
                       RETRASO_PASO_SCROLL_MS_BASICO
                );

                if (!listaPromociones.isEmpty()) {
                   controladorScrollPromociones.iniciarScroll();
               } else {
                  Log.w("OptionFragment", "Lista de promociones del Singleton está vacía. No se inicia el scroll automático.");
              }

         */



        recyclerViewMonumentos = root.findViewById(R.id.rwOptions);
        recyclerViewMonumentos.setHasFixedSize(true);
        recyclerViewMonumentos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));




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
        recyclerViewMonumentos = null;
        adaptadorMonumentos = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (controladorScrollPromociones != null) {
            // Si la clase PromocionesAutoScrollerBasico tiene métodos reanudarScroll()
            // controladorScrollPromocionesBasico.reanudarScroll(); // Debes añadir estos métodos en la clase básica si los necesitas
        }
    }

    @Override
    public void onStop() {
        super.onStop();
         if (controladorScrollPromociones != null) {
            // Si la clase PromocionesAutoScrollerBasico tiene métodos pausarScroll()
            // controladorScrollPromocionesBasico.pausarScroll(); // O detenerScroll() si solo quieres parar
         }
    }

}

