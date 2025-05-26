package com.example.odyssiaproject.adaptador;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.odyssiaproject.R;
import com.example.odyssiaproject.entidad.Pais;
import com.example.odyssiaproject.negocio.GestorPaises;
import com.example.odyssiaproject.ui.city.CityFragment;

import java.util.List;

/**
 * AdaptadorPaises es un adaptador para un RecyclerView que muestra una lista de países.
 * <p>
 * Cada elemento se representa mediante un ImageButton que muestra la imagen del país.
 * Al pulsar sobre la imagen, se navega a un fragmento (CityFragment) que muestra detalles
 * o información relacionada con el país.
 */
public class AdaptadorPaises extends RecyclerView.Adapter<AdaptadorPaises.ViewHolder> {

    // Lista de países a mostrar.
    private List<Pais> listaPais;
    // Gestor para operaciones relacionadas con países, utilizado para obtener la URL de la imagen.
    private GestorPaises gestorPaises;

    /**
     * Constructor del adaptador.
     *
     * @param listaPaises Lista de objetos Pais que se mostrarán en el RecyclerView.
     */
    public AdaptadorPaises(List<Pais> listaPaises) {
        this.listaPais = listaPaises;
        this.gestorPaises = new GestorPaises();
    }

    /**
     * ViewHolder que contiene la vista del elemento.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // ImageButton que muestra la imagen del país.
        private ImageButton imagenPais;

        /**
         * Constructor del ViewHolder.
         *
         * @param v Vista inflada del elemento.
         */
        public ViewHolder(View v) {
            super(v);
            // Vincula el ImageButton definido en el layout item_countries.xml.
            imagenPais = v.findViewById(R.id.imageCountries);
        }
    }

    /**
     * Infla el layout de cada elemento y crea el ViewHolder.
     *
     * @param parent   El ViewGroup al que se añadirá la vista.
     * @param viewType Tipo de vista (único en este caso).
     * @return Un nuevo ViewHolder que contiene la vista inflada.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout definido en item_countries.xml para cada elemento.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_countries, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Vincula los datos de un objeto Pais a la vista del ViewHolder.
     *
     * @param holder   ViewHolder que contiene los elementos de la vista.
     * @param position Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Obtiene el país correspondiente a la posición actual.
        Pais paisActual = listaPais.get(position);

        // Intenta obtener la URL de la imagen del país utilizando el GestorPaises.
        String imagenPaisesUrl = gestorPaises.imagenPaises(paisActual);

        Glide.with(holder.itemView.getContext())
                .load(imagenPaisesUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(holder.imagenPais);

        // Establece un listener para manejar clics en la imagen del país.
        holder.imagenPais.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Pais paisClick = listaPais.get(pos);

                // Usamos el método newInstance, pasando null como nombre de ciudad (aún no seleccionada)
                // y el nombre del país para que CityFragment lo reciba correctamente.
                CityFragment cityFragment = CityFragment.newInstance(null, paisClick.getNombre());

                Context context = v.getContext();
                if (context instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) context;

                    if (!activity.isFinishing() && !activity.getSupportFragmentManager().isStateSaved()) {
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, cityFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            }
        });
    }

    /**
     * Retorna el número total de elementos en la lista.
     *
     * @return El tamaño de la lista de países.
     */
    @Override
    public int getItemCount() {
        return listaPais.size();
    }
}
