package com.example.odyssiaproject.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssiaproject.R;
import com.example.odyssiaproject.entidad.Actividad;
import com.example.odyssiaproject.ui.option.OptionFragment;

import java.util.List;

public class AdaptadorExploracion extends RecyclerView.Adapter<AdaptadorExploracion.ViewHolder> {

    public List<Actividad> listaActividad;

    public AdaptadorExploracion(List<Actividad> listaActividad) {
        this.listaActividad = listaActividad;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout 'item_exploracion' para cada elemento.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exploration, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorExploracion.ViewHolder holder, int position) {

        Actividad actividad = listaActividad.get(position);

        holder.textNombreActividad.setText(actividad.getNombre());

        String nombreImagen = actividad.getNombre().toLowerCase().replace(" ", "_");
        int idImagen = holder.ibEsploration.getContext().getResources().getIdentifier(
                nombreImagen, "drawable", holder.ibEsploration.getContext().getPackageName()
        );

        if (idImagen != 0) {
            holder.ibEsploration.setImageResource(idImagen);
        } else {
            holder.ibEsploration.setImageResource(R.drawable.button_bluegradient);
        }

        holder.ibEsploration.setOnClickListener(v -> {

            AppCompatActivity activity = (AppCompatActivity) v.getContext();

            OptionFragment optionFragment = OptionFragment.newInstance(actividad.getNombre());

            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, optionFragment)
                    .addToBackStack(null)
                    .commit();

        });

    }

    @Override
    public int getItemCount() { return listaActividad.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageButton ibEsploration;

        /**
         * Constructor del ViewHolder.
         *
         * @param v La vista inflada que representa el item.
         */
        public ViewHolder(View v) {
            super(v);
            ibEsploration = v.findViewById(R.id.imageExploration);
        }
    }
}
