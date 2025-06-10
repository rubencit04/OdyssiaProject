package com.example.odyssiaproject.adaptador;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssiaproject.R;
import com.example.odyssiaproject.entidad.Actividad;
import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.ui.option.OptionFragment;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorExploracion extends RecyclerView.Adapter<AdaptadorExploracion.ViewHolder> {

    private List<Actividad> actividades;
    private List<Integer> imagenesDrawable;
    private List<Integer> nombresStringId;
    private String nombrePais;

    public AdaptadorExploracion(List<Actividad> actividades, List<Integer> imagenesDrawable, List<Integer> nombresStringId, String nombrePais) {
        // ¡CORRECCIÓN! Nos aseguramos de que las listas nunca sean nulas para evitar errores.
        this.actividades = (actividades != null) ? actividades : new ArrayList<>();
        this.imagenesDrawable = (imagenesDrawable != null) ? imagenesDrawable : new ArrayList<>();
        this.nombresStringId = (nombresStringId != null) ? nombresStringId : new ArrayList<>();
        this.nombrePais = nombrePais;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Usamos parent.getContext() que es más seguro.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exploration, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // ¡CORRECCIÓN DE SEGURIDAD! Comprobamos que la posición sea válida para TODAS las listas
        // antes de intentar acceder a los datos.
        if (position >= actividades.size() || position >= imagenesDrawable.size() || position >= nombresStringId.size()) {
            Log.e("AdaptadorExploracion", "Posición inválida o listas con tamaños diferentes. Posición: " + position);
            return; // No hacemos nada más para este item para evitar un crash.
        }

        Actividad actividad = actividades.get(position);
        int imagenResId = imagenesDrawable.get(position);
        int textoResId = nombresStringId.get(position);

        holder.imageExploration.setImageResource(imagenResId);
        holder.textNombreActividad.setText(textoResId);

        holder.imageExploration.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {

                // ¡CORRECCIÓN CRÍTICA! Evitamos NullPointerException en la navegación.
                Ciudad ciudad = actividades.get(pos).getCiudad();
                if (ciudad != null && ciudad.getNombre() != null && nombrePais != null) {
                    Context context = v.getContext();
                    String nombreActividad = context.getString(nombresStringId.get(pos));
                    String nombreCiudad = ciudad.getNombre();

                    OptionFragment optionFragment = OptionFragment.newInstance(nombreCiudad, nombreActividad, nombrePais);

                    // Buscamos la Activity contenedora para poder hacer la transacción del fragmento.
                    AppCompatActivity activity = getActivityFromContext(context);
                    if (activity != null) {
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, optionFragment) // Asegúrate de que este ID es el correcto en tu Activity
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Log.e("AdaptadorExploracion", "No se pudo encontrar la AppCompatActivity desde el contexto.");
                    }
                } else {
                    Log.e("AdaptadorExploracion", "La ciudad o el país son nulos para la actividad en la posición: " + pos);
                    Toast.makeText(v.getContext(), "Error: Faltan datos para navegar.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // Devuelve el tamaño de la lista más pequeña para garantizar que no haya IndexOutOfBoundsException.
        return Math.min(actividades.size(), Math.min(imagenesDrawable.size(), nombresStringId.size()));
    }

    /**
     * MÉTODO AÑADIDO para actualizar los datos del adaptador de forma segura.
     * @param nuevasActividades La nueva lista de actividades a mostrar.
     */
    public void actualizarDatos(List<Actividad> nuevasActividades) {
        if (nuevasActividades != null) {
            this.actividades.clear();
            this.actividades.addAll(nuevasActividades);
            notifyDataSetChanged(); // Notifica al RecyclerView que los datos han cambiado.
        }
    }

    /**
     * Método auxiliar para obtener la AppCompatActivity a partir de un Context.
     */
    private AppCompatActivity getActivityFromContext(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof AppCompatActivity) {
                return (AppCompatActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageButton imageExploration;
        private TextView textNombreActividad;

        public ViewHolder(View v) {
            super(v);
            imageExploration = v.findViewById(R.id.imageExploration);
            textNombreActividad = v.findViewById(R.id.textNombreActividad);
        }
    }
}