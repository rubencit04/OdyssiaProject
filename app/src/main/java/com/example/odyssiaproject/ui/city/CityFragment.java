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
import com.example.odyssiaproject.persistencia.api.RetrofitRenderClient;
import com.example.odyssiaproject.runabble.PromocionesAutoScroller;
import com.example.odyssiaproject.persistencia.api.ApiRenderService;
import com.example.odyssiaproject.singelton.ListaPromocionesSingelton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityFragment extends Fragment {

    private RecyclerView recyclerViewPromociones;
    private RecyclerView recyclerViewCiudades;

    private List<Ciudad> listaCiudades = new ArrayList<>();
    // Adaptador de promociones
    private AdaptadorPromociones adaptadorPromociones;
    private AdaptadorCiudades adaptadorCiudades;

    // --- INSTANCIA DE LA CLASE BÁSICA DEL SCROLL ---
    private PromocionesAutoScroller controladorScrollPromociones;

    // --- Parámetros para la panorámica continua (AJUSTA ESTOS VALORES si quieres que vayan distinto al Home) ---
    private static final int VELOCIDAD_SCROLL_PX_BASICO = 10; // Pixeles por paso
    private static final long RETRASO_PASO_SCROLL_MS_BASICO = 50; // Retraso entre pasos

    private Pais pais;
    private static final String ARG_NOMBRE_CIUDAD = "ciudad";
    private String nombreCiudad;

    // Retrofit API
    private ApiRenderService apiRenderService;


    public CityFragment() {

    }

    public static CityFragment newInstance(String nombreCiudad, String nombrePais) {
        CityFragment fragment = new CityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE_CIUDAD, nombreCiudad);
        args.putString("pais", nombrePais); // <-- Añadir esto
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nombreCiudad = getArguments().getString(ARG_NOMBRE_CIUDAD);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_city, container, false);

        recyclerViewPromociones = root.findViewById(R.id.rwPromotions);
        recyclerViewPromociones.setHasFixedSize(true);
        LinearLayoutManager promocionesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPromociones.setLayoutManager(promocionesLayoutManager);

        List<Promociones> listaPromociones = ListaPromocionesSingelton.getInstance().getListaPromociones();
        if(listaPromociones == null){
            listaPromociones = new ArrayList<>();
        }

        // Inicializar adaptador (USA TU ADAPTADOR ORIGINAL SIN LAS MODIFICACIONES DE SCROLL INFINITO)
        // Tu adaptador solo necesita el método actualizarDatos(List<Promociones>) que llama a notifyDataSetChanged()
        adaptadorPromociones = new AdaptadorPromociones(new ArrayList<>());
        recyclerViewPromociones.setAdapter(adaptadorPromociones);
        adaptadorPromociones.actualizarDatos(listaPromociones);
        controladorScrollPromociones = new PromocionesAutoScroller(
                recyclerViewPromociones,
                VELOCIDAD_SCROLL_PX_BASICO,
                RETRASO_PASO_SCROLL_MS_BASICO
        );


        if (!listaPromociones.isEmpty()) {
            controladorScrollPromociones.iniciarScroll();
        } else {
            Log.w("CityFragment", "Lista de promociones del Singleton está vacía. No se inicia el scroll básico.");
        }

        recyclerViewCiudades = root.findViewById(R.id.rwCities);
        recyclerViewCiudades.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewCiudades.setHasFixedSize(true);


        Log.d("CityFragment", "Tamaño de la lista de ciudades: " + listaCiudades.size());

        if (getArguments() != null) {
            String nombrePais = getArguments().getString("pais");
            if (nombrePais != null) {
                pais = new Pais();
                pais.setNombre(nombrePais);
                Log.d("CityFragment", "País inicializado: " + pais.getNombre());
            } else {
                Log.e("CityFragment", "El argumento 'pais' es null");
            }
        } else {
            Log.e("CityFragment", "No se recibieron argumentos");
        }


        apiRenderService = RetrofitRenderClient.getApiService();

        apiRenderService.getCiudadesPorPais(pais.getNombre()).enqueue(new Callback<List<Ciudad>>() {
            @Override
            public void onResponse(Call<List<Ciudad>> call, Response<List<Ciudad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCiudades = response.body();

                    adaptadorCiudades = new AdaptadorCiudades(listaCiudades);
                    recyclerViewCiudades.setAdapter(adaptadorCiudades);

                } else {
                    Log.e("CityFragment", "Error en la respuesta al obtener ciudades");
                }
            }

            @Override
            public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                Log.e("CityFragment", "Fallo al obtener ciudades", t);
            }
        });


        return root;
    }



    /**
     * Método del ciclo de vida del fragmento que se llama cuando la vista
     * del fragmento va a ser destruida.
     * --- IMPORTANTE: Detener el scroll automático aquí para evitar fugas de memoria. ---
     * Este método FALTABA en tu código original de CityFragment y ha sido AÑADIDO.
     */
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
        if (controladorScrollPromociones != null) {
            // Si implementaste pausa/reanudar en tu clase
            // controladorScrollPromocionesBasico.reanudarScroll(); // Debes añadir estos métodos en la clase básica si los necesitas
        }
    }

    @Override
    public void onStop() {
        super.onStop();
         if (controladorScrollPromociones != null) {
            // Si implementaste pausa/reanudar en tu clase
            // controladorScrollPromocionesBasico.pausarScroll(); // Debes añadir estos métodos en la clase básica si los necesitas
         }
    }


}