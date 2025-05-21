package com.example.odyssiaproject.adaptador;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.odyssiaproject.R;
import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.negocio.GestorCiudades;
import com.example.odyssiaproject.ui.city.CityFragment;
import com.example.odyssiaproject.ui.option.OptionFragment;

import java.util.List;

/**
 * AdaptadorCiudades es un adaptador para un RecyclerView que muestra una lista de ciudades.
 * <p>
 * Cada elemento de la lista muestra la imagen, el nombre, la descripción de la ciudad y dos acciones:
 * <ul>
 *     <li>Un botón para abrir un fragmento con detalles o opciones de la ciudad.</li>
 *     <li>Un gesto de doble toque sobre un botón "like" para marcar la ciudad como favorita (se cambia la imagen del botón).</li>
 * </ul>
 */
public class AdaptadorCiudades extends RecyclerView.Adapter<AdaptadorCiudades.ViewHolder> {

    // Lista de objetos Ciudad a mostrar.
    private List<Ciudad> listaCiudades;
    // Instancia del GestorCiudades para obtener la URL de la imagen de la ciudad.
    private GestorCiudades gestorCiudades;

    /**
     * Constructor del adaptador.
     *
     * @param listaCiudades Lista de ciudades a mostrar en el RecyclerView.
     */
    public AdaptadorCiudades(List<Ciudad> listaCiudades) {
        this.listaCiudades = listaCiudades;
        this.gestorCiudades = new GestorCiudades();
    }

    /**
     * Infla la vista para cada elemento del RecyclerView.
     *
     * @param parent   El ViewGroup en el que se crea la vista.
     * @param viewType Tipo de vista, en este caso se usa un único tipo.
     * @return Un ViewHolder que contiene la vista del elemento.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout 'item_cities' para cada elemento.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cities, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Asocia los datos de una ciudad a la vista correspondiente.
     *
     * @param holder   El ViewHolder que contiene los views del elemento.
     * @param position La posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Obtiene la ciudad actual según la posición.
        Ciudad ciudadActual = listaCiudades.get(position);
        // Obtiene la URL de la imagen de la ciudad utilizando el GestorCiudades.
        String imagenCiudadesUrl = gestorCiudades.imagenCiudad(ciudadActual);
        if (imagenCiudadesUrl == null || imagenCiudadesUrl.isEmpty()) {
            Log.w("Glide", "URL de la imagen es nula o vacía para: " + ciudadActual.getNombre());
            imagenCiudadesUrl = "url_default_image";
        }

        // Carga la imagen en el ImageView utilizando Glide.
        Glide.with(holder.itemView.getContext())
                .load(imagenCiudadesUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(holder.imagenCiudad);

        // Asigna el nombre y la descripción de la ciudad a los TextViews.
        holder.nombreCiudad.setText(ciudadActual.getNombre());
        holder.descripcionCiudad.setText(ciudadActual.getDescripcion());

        // Configura el botón "abrir" para crear y mostrar el fragmento con opciones de la ciudad.
        holder.abrir.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Ciudad ciudadClick = listaCiudades.get(pos);

                Context context = v.getContext();
                while (!(context instanceof AppCompatActivity) && context instanceof ContextWrapper) {
                    context = ((ContextWrapper) context).getBaseContext();
                }
                AppCompatActivity activity = (AppCompatActivity) context;


                OptionFragment optionFragment = OptionFragment.newInstance(ciudadClick.getNombre());


                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, optionFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Configura el gesto de doble toque en el botón "like".
        holder.like.setOnTouchListener(new View.OnTouchListener() {
            // Se utiliza un GestureDetector para detectar el doble toque.
            private final GestureDetector gestureDetector = new GestureDetector(holder.itemView.getContext(),
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            // Cambia la imagen del botón "like" al recurso 'buttonlikered' al detectar doble toque.
                            holder.like.setImageResource(R.drawable.buttonlikered);
                            return true;
                        }
                    });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Delegar el evento de toque al GestureDetector.
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    /**
     * Retorna el número total de elementos en la lista.
     *
     * @return El tamaño de la lista de ciudades.
     */
    @Override
    public int getItemCount() {
        return listaCiudades.size();
    }

    /**
     * ViewHolder que contiene los elementos de cada item del RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // ImageView para mostrar la imagen de la ciudad.
        private ImageView imagenCiudad;
        // TextView para mostrar el nombre de la ciudad.
        private TextView nombreCiudad;
        // ImageButton que actúa como botón "like".
        private ImageButton like;
        // TextView para mostrar la descripción de la ciudad.
        private TextView descripcionCiudad;
        // Botón para abrir el fragmento con opciones de la ciudad.
        private Button abrir;

        /**
         * Constructor del ViewHolder.
         *
         * @param v La vista inflada que representa el item.
         */
        public ViewHolder(View v) {
            super(v);
            imagenCiudad = v.findViewById(R.id.imageView);
            nombreCiudad = v.findViewById(R.id.tvNameCity);
            like = v.findViewById(R.id.buttonLikeCity);
            descripcionCiudad = v.findViewById(R.id.descriptionCity);
            abrir = v.findViewById(R.id.buttonOpen);
        }
    }
}
