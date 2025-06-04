package com.example.odyssiaproject.adaptador;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssiaproject.R;
import com.example.odyssiaproject.entidad.Actividad;
import com.example.odyssiaproject.ui.option.OptionFragment;

import java.util.List;

public class AdaptadorExploracion extends RecyclerView.Adapter<AdaptadorExploracion.ViewHolder> {

    private List<Actividad> actividades;
    private List<Integer> imagenesDrawable;
    private List<Integer> nombresStringId;
    private String nombrePais;

    public AdaptadorExploracion(List<Actividad> actividades, List<Integer> imagenesDrawable, List<Integer> nombresStringId, String nombrePais) {
        this.actividades = actividades;
        this.imagenesDrawable = imagenesDrawable;
        this.nombresStringId = nombresStringId;
        this.nombrePais = nombrePais;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout 'item_exploracion' para cada elemento.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exploration, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Actividad actividad = actividades.get(position);
        int imagenResId = imagenesDrawable.get(position);
        int textoResId = nombresStringId.get(position);

        holder.imageExploration.setImageResource(imagenResId);
        holder.textNombreActividad.setText(textoResId);

        holder.imageExploration.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Context context = v.getContext();
                String nombreActividad = context.getString(nombresStringId.get(pos));
                String ciudad = actividades.get(pos).getCiudad().getNombre();

                // Asumiendo que OptionFragment acepta estos parámetros:
                OptionFragment optionFragment = OptionFragment.newInstance(ciudad, nombreActividad, nombrePais);

                // Para obtener AppCompatActivity:
                while (!(context instanceof AppCompatActivity) && context instanceof ContextWrapper) {
                    context = ((ContextWrapper) context).getBaseContext();
                }
                AppCompatActivity activity = (AppCompatActivity) context;

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, optionFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() { return actividades.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageButton imageExploration;
        private TextView textNombreActividad;

        /**
         * Constructor del ViewHolder.
         *
         * @param v La vista inflada que representa el item.
         */
        public ViewHolder(View v) {
            super(v);
            imageExploration = v.findViewById(R.id.imageExploration);
            textNombreActividad = v.findViewById(R.id.textNombreActividad);
        }
    }
}
