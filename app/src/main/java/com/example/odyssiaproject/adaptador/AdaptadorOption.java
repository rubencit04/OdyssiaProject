package com.example.odyssiaproject.adaptador;

import android.content.Context;
import android.content.ContextWrapper;
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
import com.example.odyssiaproject.entidad.Actividad;
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
    private List<Actividad> actividadesFiltradas;
    private String tipoSeleccionado;
    private OnItemClickListener listener;

    private GestorOptions gestor;

    public interface OnItemClickListener {
        void onAbrirClick(Actividad actividad);
    }

    public AdaptadorOption(List<Actividad> actividades) {
        this.actividadesFiltradas = new ArrayList<>();

        // Filtrar por tipo
        for (Actividad actividad : actividades) {
            if (actividad.getNombre().equalsIgnoreCase(tipoSeleccionado)) {
                actividadesFiltradas.add(actividad);
            }
        }
    }

    @NonNull
    @Override
    public AdaptadorOption.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_options, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorOption.ViewHolder holder, int position) {

        Actividad actividadActual = actividadesFiltradas.get(position);

        String imagenActividadUrl = gestor.imagenMonumento(actividadActual);

        // Carga la imagen en el ImageView utilizando Glide.
        Glide.with(holder.itemView.getContext())
                .load(imagenActividadUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(holder.imagen);

        holder.titulo.setText(actividadActual.getNombre());
        holder.precio.setText(actividadActual.getPrecio());
        holder.horario.setText(actividadActual.getHorario());
    }

    @Override
    public int getItemCount() {
        return actividadesFiltradas.size();
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
