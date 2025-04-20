package com.example.odyssiaproject.adaptador;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.odyssiaproject.R;
import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.negocio.GestorPromociones;

import java.util.List;

/**
 * AdaptadorPromociones es un adaptador para gestionar la lista de promociones en un RecyclerView.
 * <p>
 * Se encarga de inflar el layout correspondiente a cada ítem, asignar los datos de cada promoción
 * y gestionar la visualización de la imagen de la promoción según la lógica definida en GestorPromociones.
 */
public class AdaptadorPromociones extends RecyclerView.Adapter<AdaptadorPromociones.ViewHolder> {
    // Objeto Promociones utilizado para almacenar la promoción actual en onBindViewHolder.
    private Promociones p;
    // Lista de promociones que se mostrarán en el RecyclerView.
    private List<Promociones> listaPromociones;

    /**
     * Constructor del adaptador.
     *
     * @param listaPromociones Lista de promociones a mostrar.
     */
    public AdaptadorPromociones(List<Promociones> listaPromociones) {
        this.listaPromociones = listaPromociones;
    }

    /**
     * ViewHolder que representa cada ítem de la lista.
     * Contiene la referencia al ImageButton que muestra la imagen de la promoción.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageButton imagenPromocion;

        /**
         * Constructor del ViewHolder.
         *
         * @param v Vista que representa el ítem.
         */
        public ViewHolder(View v) {
            super(v);
            // Se obtiene la referencia al ImageButton definido en el layout item_promotions.
            imagenPromocion = v.findViewById(R.id.imagePromotion);
        }
    }

    /**
     * Infla el diseño XML de cada elemento de la lista.
     *
     * @param parent   El ViewGroup en el que se va a inflar la vista.
     * @param viewType Tipo de vista (en este caso, se utiliza un único tipo de vista).
     * @return Un ViewHolder que contiene la vista del elemento.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout item_promotions para cada ítem del RecyclerView.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promotions, parent, false);
        AdaptadorPromociones.ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    /**
     * Asigna datos a cada elemento de la lista en función de su posición.
     *
     * @param holder   ViewHolder del ítem.
     * @param position Posición del ítem en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull AdaptadorPromociones.ViewHolder holder, int position) {
        // Se obtiene la promoción correspondiente a la posición actual.
        p = listaPromociones.get(position);

        if (p == null) {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.imgpromotion)
                    .into(holder.imagenPromocion);
            return;
        }

        GestorPromociones gestor = new GestorPromociones();
        String urlImagen = gestor.imagenPromocion(p);

        Glide.with(holder.itemView.getContext())
                .load(urlImagen)
                .placeholder(R.drawable.imgpromotion) // Imagen temporal mientras carga
                .error(R.drawable.imgpromotion)       // Imagen si hay error
                .into(holder.imagenPromocion);

        // Click para abrir el enlace de la promoción
        holder.imagenPromocion.setOnClickListener(v -> {
            String url = p.getLink();
            if (url != null && !url.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPromociones.size();
    }
    }

