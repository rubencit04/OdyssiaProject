package com.example.odyssiaproject.adaptador;

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
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssiaproject.R;
import com.example.odyssiaproject.entidad.Monumentos;
import com.example.odyssiaproject.negocio.GestorMonumentos;

import java.util.List;

/**
 * AdaptadorMonumentos es un adaptador para un RecyclerView que muestra una lista de monumentos.
 * <p>
 * Cada elemento de la lista presenta la imagen, el nombre, el precio y el horario del monumento.
 * Además, se implementa un gesto de doble toque sobre el botón "like" para cambiar su imagen.
 */
public class AdaptadorMonumentos extends RecyclerView.Adapter<AdaptadorMonumentos.ViewHolder> {

    // Lista de monumentos a mostrar.
    private List<Monumentos> listaMonumentos;
    // Objeto Monumentos que almacena el monumento actual en onBindViewHolder.
    private Monumentos m;

    /**
     * Constructor del adaptador.
     *
     * @param listaMonumentos Lista de monumentos a mostrar en el RecyclerView.
     */
    public AdaptadorMonumentos(List<Monumentos> listaMonumentos) {
        this.listaMonumentos = listaMonumentos;
    }

    /**
     * Infla el layout XML para cada elemento del RecyclerView.
     *
     * @param parent   El ViewGroup al que se adjuntará la vista.
     * @param viewType Tipo de vista (en este caso, se utiliza un único tipo).
     * @return Un ViewHolder que contiene la vista inflada.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout definido en item_options.xml
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_options, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Asocia los datos de un monumento a la vista correspondiente.
     *
     * @param holder   ViewHolder que contiene los elementos de la vista.
     * @param position Posición del monumento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Se obtiene el monumento de la posición actual.
        m = listaMonumentos.get(position);

        // Si el objeto es nulo, se asignan valores por defecto y se detiene la ejecución.
        if (m == null) {
            holder.tvNamePlace.setText("Nombre no disponible");
            holder.tvPricePlace.setText("Precio no disponible");
            return;
        }

        // Se instancia GestorMonumentos para obtener la imagen correspondiente al monumento.
        GestorMonumentos nM = new GestorMonumentos();
        String resultadoImagen = nM.imagenMonumento(m);

        // Asigna los textos correspondientes al nombre, precio y horario del monumento.
        holder.tvNamePlace.setText(m.getNombre());
        holder.tvPricePlace.setText("Precio: " + m.getPrecio());

        // Configura el gesto de doble toque en el botón "like" para cambiar su imagen.
        holder.like.setOnTouchListener(new View.OnTouchListener() {
            private final GestureDetector gestureDetector = new GestureDetector(holder.itemView.getContext(),
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            holder.like.setImageResource(R.drawable.buttonlikered);
                            return true;
                        }
                    });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    /**
     * Retorna el número total de elementos en la lista de monumentos.
     *
     * @return Cantidad de monumentos.
     */
    @Override
    public int getItemCount() {
        return listaMonumentos.size();
    }

    /**
     * ViewHolder que contiene las vistas de cada ítem del RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // ImageView que muestra la imagen del monumento.
        ImageView imagenMonumento;
        // TextViews para mostrar el nombre, precio y horario del monumento.
        TextView tvNamePlace, tvPricePlace;
        // ImageButton que actúa como botón "like".
        ImageButton like;
        // Botón para abrir más opciones o detalles (su funcionalidad se puede definir según la lógica de la app).
        Button abrir;

        /**
         * Constructor del ViewHolder.
         *
         * @param v Vista que representa el ítem.
         */
        public ViewHolder(View v) {
            super(v);
            imagenMonumento = v.findViewById(R.id.iwPlace);
            tvNamePlace = v.findViewById(R.id.tvNamePlace);
            tvPricePlace = v.findViewById(R.id.tvPricePlace);
            like = v.findViewById(R.id.buttonLikePlace);
            abrir = v.findViewById(R.id.buttonOpen);
        }
    }
}
