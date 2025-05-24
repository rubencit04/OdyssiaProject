package com.example.odyssiaproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssiaproject.adaptador.AdaptadorCiudades;
import com.example.odyssiaproject.adaptador.AdaptadorPromociones;
import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.entidad.Pais;
import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.persistencia.api.RetrofitClient;
import com.example.odyssiaproject.runabble.PromocionesAutoScroller;
import com.example.odyssiaproject.singelton.ListaPromocionesSingelton;
import com.example.odyssiaproject.ui.ajustes.ConfigFragment;
import com.example.odyssiaproject.ui.city.CityFragment;
import com.example.odyssiaproject.ui.favs.FavsFragment;
import com.example.odyssiaproject.ui.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OptionActivity extends Fragment{
    private Toolbar toolbar;
    private ImageButton btnMenu;
    DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private RecyclerView recyclerViewPromociones;
    private RecyclerView recyclerViewOpciones;
    private AdaptadorPromociones adaptadorPromociones;
    private PromocionesAutoScroller controladorScrollPromociones;
    private static final int VELOCIDAD_SCROLL_PX_BASICO = 10; // Pixeles por paso
    private static final long RETRASO_PASO_SCROLL_MS_BASICO = 50; // Retraso entre pasos

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_option, container, false);

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

        recyclerViewOpciones = root.findViewById(R.id.rwOptions);
        recyclerViewOpciones.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewOpciones.setHasFixedSize(true);

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
        recyclerViewOpciones = null;
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