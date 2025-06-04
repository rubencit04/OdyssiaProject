package com.example.odyssiaproject.ui.city;

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
import com.example.odyssiaproject.adaptador.AdaptadorCiudades;
import com.example.odyssiaproject.adaptador.AdaptadorPromociones;
import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.entidad.Pais;
import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.negocio.GestorPromociones;
import com.example.odyssiaproject.persistencia.api.ApiRenderService;
import com.example.odyssiaproject.persistencia.api.RetrofitRenderClient;
import com.example.odyssiaproject.runabble.PromocionesAutoScroller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityFragment extends Fragment {

    private RecyclerView recyclerViewPromociones;
    private RecyclerView recyclerViewCiudades;

    private List<Ciudad> listaCiudades = new ArrayList<>();

    private Set<String> favoritosCiudades = new HashSet<>();
    private AdaptadorPromociones adaptadorPromociones;
    private AdaptadorCiudades adaptadorCiudades;

    private Pais pais;
    private static final String ARG_NOMBRE_CIUDAD = "ciudad";
    private String nombreCiudad;

    private ApiRenderService apiRenderService;

    private GestorPromociones gestorPromociones;

    private PromocionesAutoScroller controladorScrollPromociones;

    private static final int VELOCIDAD_SCROLL_PX_BASICO = 10;
    private static final long RETRASO_PASO_SCROLL_MS_BASICO = 50;


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

        if (getArguments() != null) {
            nombreCiudad = getArguments().getString(ARG_NOMBRE_CIUDAD);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_city, container, false);

        recyclerViewCiudades = root.findViewById(R.id.rwCities);
        recyclerViewCiudades.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerViewCiudades.setHasFixedSize(true);

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

        if (getArguments() != null) {
            String nombrePais = getArguments().getString("pais");
            if (nombrePais != null && !nombrePais.isEmpty()) {
                pais = new Pais();
                pais.setNombre(nombrePais);
                Log.d("CityFragment", "País inicializado: " + pais.getNombre());

                gestorPromociones.obtenerPromocionesPorPais(pais.getNombre(), new GestorPromociones.CallbackPromociones() {
                    @Override
                    public void onPromocionesCargadas(List<Promociones> promociones) {
                        if (promociones != null && !promociones.isEmpty()) {
                            adaptadorPromociones.actualizarDatos(promociones);
                            controladorScrollPromociones.iniciarScroll();
                        } else {
                            Log.w("CityFragment", "Lista de promociones está vacía o nula. No se inicia scroll.");
                            adaptadorPromociones.actualizarDatos(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e("CityFragment", "Error cargando promociones por país", t);
                        adaptadorPromociones.actualizarDatos(new ArrayList<>());
                    }
                });

                // Obtener ciudades del país
                apiRenderService = RetrofitRenderClient.getApiService();
                apiRenderService.getCiudades(pais.getNombre()).enqueue(new Callback<List<Ciudad>>() {
                    @Override
                    public void onResponse(Call<List<Ciudad>> call, Response<List<Ciudad>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            listaCiudades = response.body();
                            cargarFavoritosDesdeFirestore(); // ← ahora lo haces aquí
                        } else {
                            Log.e("CityFragment", "Error en la respuesta al obtener ciudades");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                        Log.e("CityFragment", "Fallo al obtener ciudades", t);
                    }
                });

            } else {
                Log.e("CityFragment", "El argumento 'pais' es null o vacío");
            }
        } else {
            Log.e("CityFragment", "No se recibieron argumentos");
        }

        return root;
    }

    private void cargarFavoritosDesdeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            db.collection("usuario").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            List<String> favList = (List<String>) documentSnapshot.get("favoritosCiudades");
                            if (favList != null) {
                                favoritosCiudades.addAll(favList);
                            } else {
                                // Campo no existe → lo inicializamos vacío
                                db.collection("usuario").document(user.getUid())
                                        .update("favoritosCiudades", new ArrayList<>())
                                        .addOnSuccessListener(aVoid -> Log.d("CityFragment", "Campo favoritosCiudades inicializado"))
                                        .addOnFailureListener(e -> Log.e("CityFragment", "Error inicializando favoritosCiudades", e));
                            }
                        }

                        adaptadorCiudades = new AdaptadorCiudades(listaCiudades, favoritosCiudades);
                        recyclerViewCiudades.setAdapter(adaptadorCiudades);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error cargando favoritos", e);
                        adaptadorCiudades = new AdaptadorCiudades(listaCiudades, favoritosCiudades);
                        recyclerViewCiudades.setAdapter(adaptadorCiudades);
                    });
        } else {
            Log.w("Firestore", "Usuario no logueado");
            adaptadorCiudades = new AdaptadorCiudades(listaCiudades, favoritosCiudades);
            recyclerViewCiudades.setAdapter(adaptadorCiudades);
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
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
