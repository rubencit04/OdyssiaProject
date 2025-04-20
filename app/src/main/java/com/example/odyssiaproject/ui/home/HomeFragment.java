package com.example.odyssiaproject.ui.home;

import android.os.Bundle;
import android.os.Handler;
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
import com.example.odyssiaproject.persistencia.api.ApiService;
import com.example.odyssiaproject.persistencia.api.RetrofitClient;
import com.example.odyssiaproject.singelton.ListaPromocionesSingelton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * HomeFragment es el fragmento principal de la pantalla de inicio.
 * <p>
 * Muestra dos RecyclerViews:
 * <ul>
 *     <li>Uno horizontal para promociones.</li>
 *     <li>Otro vertical para la lista de países.</li>
 * </ul>
 * Además, actualiza el encabezado del Navigation Drawer con el correo del usuario autenticado
 * utilizando Firebase Authentication.
 */
public class HomeFragment extends Fragment {

    // RecyclerView para mostrar promociones de forma horizontal.
    private RecyclerView recyclerViewPromociones;
    // RecyclerView para mostrar la lista de países de forma vertical.
    private RecyclerView recyclerViewPaises;
    // Lista que almacenará los objetos Pais obtenidos de Firestore.
    private List<Pais> listaPaises = new ArrayList<>();
    // Adaptador para el RecyclerView de promociones.
    private AdaptadorPromociones adaptadorPromociones;
    // Adaptador para el RecyclerView de países.
    private AdaptadorPaises adaptadorPaises;
    // Handler para gestionar la ejecución repetida de tareas (scroll automático).
    private Handler handler = new Handler();
    // Velocidad del scroll automático en píxeles.
    private int scrollSpeed = 10;

    // Instancia de la API de Retrofit para comunicarse con Firestore (no se usa en este fragmento, pero se inicializa).
    private ApiService apiService;

    DaoPromociones dao = new DaoPromociones();

    /**
     * Runnable encargado de realizar el scroll automático del RecyclerView de promociones.
     * Se desplaza suavemente hacia la derecha y, al llegar al final, vuelve al inicio.
     */
    private final Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            // Realiza un scroll suave horizontalmente.
            recyclerViewPromociones.smoothScrollBy(scrollSpeed, 0);

            // Si no se puede seguir desplazando hacia la derecha, reinicia la posición al inicio.
            if (!recyclerViewPromociones.canScrollHorizontally(1)) {
                recyclerViewPromociones.scrollToPosition(0);
            }
            // Vuelve a ejecutar el runnable después de 50 milisegundos.
            handler.postDelayed(this, 50);
        }
    };

    /**
     * Método del ciclo de vida del fragmento para crear la vista.
     *
     * @param inflater           Inflater para inflar la vista.
     * @param container          Contenedor padre de la vista.
     * @param savedInstanceState Bundle con el estado previo (si existe).
     * @return Vista inflada del fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla el layout del fragmento.
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Configuración del Navigation Drawer: se obtiene el DrawerLayout y el NavigationView.
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.navBarDrawer);
        NavigationView navigationView = drawerLayout.findViewById(R.id.navBarView);
        if (navigationView != null) {
            // Accede a la vista del encabezado dentro del NavigationView.
            View headerView = navigationView.getHeaderView(0);
            TextView userEmailTextView = headerView.findViewById(R.id.twUsuario);

            // Obtiene el correo del usuario autenticado mediante Firebase.
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                String userEmail = currentUser.getEmail();
                userEmailTextView.setText(userEmail);  // Establece el correo en el TextView del encabezado.
            } else {
                // Maneja el caso en el que no haya un usuario autenticado.
                userEmailTextView.setText("Usuario no autenticado");
            }
        }

        recyclerViewPromociones = root.findViewById(R.id.rwPromotions);
        recyclerViewPromociones.setHasFixedSize(true);
        recyclerViewPromociones.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

// Inicializa el adaptador de promociones con una lista vacía inicialmente
        adaptadorPromociones = new AdaptadorPromociones(new ArrayList<>());
        recyclerViewPromociones.setAdapter(adaptadorPromociones);

// Cargar las promociones desde Firestore
        dao.obtenerPromocionesAleatorias(new DaoPromociones.PromocionCallback() {
            @Override
            public void onPromocionesCargadas(List<Promociones> promociones) {
                // Aquí tienes la lista de promociones aleatorias
                // Puedes asignarlas a tu adaptador para mostrarlas en un RecyclerView
                adaptadorPromociones = new AdaptadorPromociones(promociones);
                recyclerViewPromociones.setAdapter(adaptadorPromociones);
            }

            @Override
            public void onError(Exception e) {
                // Aquí puedes manejar el error, por ejemplo, mostrando un mensaje de error
                Log.e("HomeFragment", "Error cargando promociones", e);
            }
        });

        // Inicia el scroll automático del RecyclerView de promociones tras 1 segundo.
        handler.postDelayed(scrollRunnable, 1000);

        // Configura el RecyclerView de Países.
        recyclerViewPaises = root.findViewById(R.id.rwCountries);
        recyclerViewPaises.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewPaises.setHasFixedSize(true);

        // Inicializa la instancia de la API (Retrofit) y carga la lista de países.
        apiService = RetrofitClient.getApiService();
        loadCountries();

        return root;
    }

    /**
     * Carga la lista de países desde Firestore y actualiza el RecyclerView correspondiente.
     */
    private void loadCountries() {
        // Obtiene la instancia de Firestore.
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Consulta la colección "paises" en Firestore.
        db.collection("paises")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Limpia la lista actual para evitar duplicados.
                        listaPaises.clear();
                        // Itera sobre cada documento obtenido.
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String nombre = document.getString("nombre"); // Obtiene el nombre del país.
                            String imagen = document.getString("imagen"); // Obtiene la URL o referencia de la imagen.

                            // Verifica que ni el nombre ni la imagen sean nulos.
                            if (nombre != null && imagen != null) {
                                listaPaises.add(new Pais(nombre, imagen));
                            } else {
                                Log.w("HomeFragment", "Nombre o imagen nulos en el documento: " + document.getId());
                            }
                        }

                        // Si el adaptador aún no ha sido inicializado, se crea y asigna al RecyclerView.
                        if (adaptadorPaises == null) {
                            adaptadorPaises = new AdaptadorPaises(listaPaises);
                            recyclerViewPaises.setAdapter(adaptadorPaises);
                        } else {
                            // Si el adaptador ya existe, se notifica el cambio en los datos.
                            adaptadorPaises.notifyDataSetChanged();
                        }
                    } else {
                        Log.e("HomeFragment", "Error getting documents.", task.getException());
                        // Aquí podrías manejar el error, por ejemplo, mostrando un mensaje al usuario.
                    }
                });
    }
}
