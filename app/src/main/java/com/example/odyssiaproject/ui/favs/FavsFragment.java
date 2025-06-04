package com.example.odyssiaproject.ui.favs;

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
import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.persistencia.api.ApiRenderService;
import com.example.odyssiaproject.persistencia.api.RetrofitRenderClient;
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

public class FavsFragment extends Fragment {

    private RecyclerView recyclerViewFavs;
    private AdaptadorCiudades adaptador;
    private ApiRenderService apiRenderService;
    private Set<String> favoritosNombres = new HashSet<>();
    private List<Ciudad> ciudadesFavoritas = new ArrayList<>();

    public FavsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favs, container, false);

        recyclerViewFavs = root.findViewById(R.id.recyclerViewFavs);
        recyclerViewFavs.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFavs.setHasFixedSize(true);

        apiRenderService = RetrofitRenderClient.getApiService();

        cargarFavoritos();

        return root;
    }

    private void cargarFavoritos() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore.getInstance().collection("usuario")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<String> favs = (List<String>) documentSnapshot.get("favoritosCiudades");
                    if (favs != null) {
                        favoritosNombres.addAll(favs);
                        obtenerCiudadesFavoritas();
                    }
                })
                .addOnFailureListener(e -> Log.e("FavsFragment", "Error cargando favoritos", e));
    }

    private void obtenerCiudadesFavoritas() {
        // Acá traés todas las ciudades desde la API
        apiRenderService.getCiudades(null).enqueue(new Callback<List<Ciudad>>() {
            @Override
            public void onResponse(Call<List<Ciudad>> call, Response<List<Ciudad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Ciudad ciudad : response.body()) {
                        if (favoritosNombres.contains(ciudad.getNombre())) {
                            ciudadesFavoritas.add(ciudad);
                        }
                    }
                    mostrarCiudadesFavoritas();
                }
            }

            @Override
            public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                Log.e("FavsFragment", "Error al obtener ciudades", t);
            }
        });
    }

    private void mostrarCiudadesFavoritas() {
        adaptador = new AdaptadorCiudades(ciudadesFavoritas, favoritosNombres); // igual que en CityFragment
        recyclerViewFavs.setAdapter(adaptador);
    }
}
