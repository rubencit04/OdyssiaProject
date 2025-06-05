package com.example.odyssiaproject.adaptador;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.odyssiaproject.R;
import com.example.odyssiaproject.dto.ActividadDTO;
import com.example.odyssiaproject.entidad.Actividad;
import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.negocio.GestorOptions;
import com.example.odyssiaproject.ui.option.OptionFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * AdaptadorOption es un adaptador para un RecyclerView que muestra una lista de monumentos.
 * <p>
 * Cada elemento de la lista presenta la imagen, el nombre, el precio y el horario del monumento.
 * Además, se implementa un gesto de doble toque sobre el botón "like" para cambiar su imagen.
 */
public class AdaptadorOption extends RecyclerView.Adapter<AdaptadorOption.ViewHolder> {

    private Context context;
    private List<ActividadDTO> actividades;
    private OnItemClickListener listener;

    private GestorOptions gestor;

    public interface OnItemClickListener {
        void onAbrirClick(ActividadDTO actividad);
    }

    public AdaptadorOption(Context context,List<ActividadDTO> actividades) {
        this.context = context;
        this.actividades = actividades;
        this.gestor = new GestorOptions();
    }
    public void actualizarDatos(List<ActividadDTO> nuevasActividades) {
        actividades.clear();
        actividades.addAll(nuevasActividades);
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public AdaptadorOption.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_options, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorOption.ViewHolder holder, int position) {

        ActividadDTO actividadActual = actividades.get(position);

        String imagenActividadUrl = gestor.imagenOption(actividadActual);

        // Carga la imagen en el ImageView utilizando Glide.
        Glide.with(holder.itemView.getContext())
                .load(imagenActividadUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(holder.imagen);

        holder.titulo.setText(actividadActual.getNombre());
        holder.precio.setText(actividadActual.getPrecio());
        holder.horario.setText(actividadActual.getHorario());
        holder.direccion.setText(actividadActual.getDireccion());
        holder.botonAbrir.setOnClickListener(v -> {
            String url = actividadActual.getLink();
            String direccion = actividadActual.getDireccion();

            if (url != null && !url.trim().isEmpty()) {
                url = url.trim();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    v.getContext().startActivity(intent);
                } catch (Exception e) {
                    Log.e("AdaptadorOption", "Error al abrir el enlace: " + url, e);
                }

            } else if (direccion != null && !direccion.trim().isEmpty()) {
                String uri = "geo:0,0?q=" + Uri.encode(direccion.trim());
                try {
                    Intent intentMaps = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intentMaps.setPackage("com.google.android.apps.maps");
                    if (intentMaps.resolveActivity(v.getContext().getPackageManager()) != null) {
                        v.getContext().startActivity(intentMaps);
                    } else {
                        Intent fallback = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        v.getContext().startActivity(fallback);
                    }
                } catch (Exception e) {
                    Log.e("AdaptadorOption", "Error al abrir Maps con la dirección: " + direccion, e);
                }

            } else {
                Log.w("AdaptadorOption", "Ni link ni dirección disponibles para: " + actividadActual.getNombre());
            }
        });
    }

    @Override
    public int getItemCount() {
        return actividades.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, direccion, horario, precio;
        ImageView imagen;
        Button botonAbrir;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.twTitleOption);
            direccion = itemView.findViewById(R.id.tvLocationOption);
            horario = itemView.findViewById(R.id.twTimeOption);
            precio = itemView.findViewById(R.id.twPriceOption);
            imagen = itemView.findViewById(R.id.iwOption);
            botonAbrir = itemView.findViewById(R.id.btOpenOption);
        }
    }
}
