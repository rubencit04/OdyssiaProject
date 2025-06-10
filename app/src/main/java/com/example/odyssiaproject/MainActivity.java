package com.example.odyssiaproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.odyssiaproject.ui.ajustes.ConfigFragment;
import com.example.odyssiaproject.ui.favs.FavsFragment;
import com.example.odyssiaproject.ui.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;

/**
 * MainActivity es la actividad principal de la aplicación.
 *
 * Esta actividad configura y gestiona el Navigation Drawer, la Toolbar y el cambio de fragmentos.
 * Implementa {@link NavigationView.OnNavigationItemSelectedListener} para manejar las selecciones del menú.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Declaración de variables para los elementos de la interfaz de usuario.
    private Toolbar toolbar;
    private ImageButton btnMenu;
    DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    /**
     * Método del ciclo de vida onCreate.
     * Se encarga de inicializar la interfaz, configurar la Toolbar, el Drawer y cargar el fragmento inicial.
     *
     * @param savedInstanceState Objeto Bundle que contiene el estado previo de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización del DrawerLayout y NavigationView.
        drawerLayout = findViewById(R.id.navBarDrawer);
        navigationView = findViewById(R.id.navBarView);
        navigationView.setNavigationItemSelectedListener(this);

        // Configuración de la Toolbar.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configuración del ActionBarDrawerToggle para sincronizar el estado del Drawer con la Toolbar.
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        actualizarNombreUsuario();

        // Cargar el fragmento inicial (HomeFragment) si no hay estado previo guardado.
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Configuración del botón de menú para abrir o cerrar el Navigation Drawer.
        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log para verificar que el clic se está registrando.
                Log.d("MainActivity", "Botón de menú presionado");

                // Abrir o cerrar el Drawer dependiendo de su estado actual.
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}
            @Override public void onDrawerOpened(@NonNull View drawerView) {
                actualizarNombreUsuario();
            }
            @Override public void onDrawerClosed(@NonNull View drawerView) {}
            @Override public void onDrawerStateChanged(int newState) {}
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof ConfigFragment || currentFragment instanceof FavsFragment) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().hide();
                }
            } else {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().show();
                }
            }
        });

    }

    /**
     * Maneja la selección de elementos en el Navigation Drawer.
     * Según el ítem seleccionado, carga el fragmento correspondiente.
     *
     * @param item Elemento de menú seleccionado.
     * @return true si el evento se ha manejado correctamente.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        Log.d("NAVIGATION", "Item seleccionado: " + item.getItemId());

        try {
            // Determina qué fragmento cargar en función del ítem seleccionado.
            if (item.getItemId() == R.id.athyssia) {
                Log.d("NAVIGATION", "Cargando AthyssiaFragment...");
                loadFragment(new AthyssiaFragment());
            } else if (item.getItemId() == R.id.favs) {
                Log.d("NAVIGATION", "Cargando FavsFragment...");
                loadFragment(new FavsFragment());
            } else if (item.getItemId() == R.id.miOdyssia) {
                Log.d("NAVIGATION", "Cargando MiOdyssiaFragment...");
                loadFragment(new FavsFragment());
            } else if (item.getItemId() == R.id.ajustes) {
                Log.d("NAVIGATION", "Cargando ConfigFragment...");
                loadFragment(new ConfigFragment());
            } else if (item.getItemId() == R.id.marketplace) {
                Log.d("NAVIGATION", "Cargando MarketplaceFragment...");
                loadFragment(new ConfigFragment());
            }
        } catch (Exception e) {
            Log.e("NAVIGATION", "Error al cargar fragmento: ", e);
        }
        // Cierra el Navigation Drawer tras la selección.
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Método auxiliar para cargar y reemplazar el fragmento actual en el contenedor.
     *
     * @param fragment Fragmento a cargar.
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        if (fragment instanceof ConfigFragment ||
                fragment instanceof FavsFragment ) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
        }
    }

    /**
     * Maneja la selección de elementos en la Toolbar.
     * En este caso, delega el manejo del toggle del Navigation Drawer.
     *
     * @param item Elemento de menú seleccionado.
     * @return true si el evento se ha manejado; de lo contrario, se llama a la implementación de la superclase.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Método público para cambiar el fragmento actual.
     * Funciona de manera similar a {@link #loadFragment(Fragment)}.
     *
     * @param fragment Fragmento a mostrar.
     */
    public void cambiarFragments(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    public void actualizarNombreUsuario() {
        View headerView = navigationView.getHeaderView(0);
        TextView twUsuario = headerView.findViewById(R.id.twUsuario);

        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String nombre = prefs.getString("nombre_usuario", "Usuario"); // Valor por defecto: "Usuario"

        twUsuario.setText(nombre);
    }
}
