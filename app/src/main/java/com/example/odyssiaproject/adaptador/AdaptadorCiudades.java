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
import com.example.odyssiaproject.dto.PaisDTO;
import com.example.odyssiaproject.entidad.Ciudad;
import com.example.odyssiaproject.negocio.GestorCiudades;
import com.example.odyssiaproject.ui.exploration.ExplorationFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdaptadorCiudades extends RecyclerView.Adapter<AdaptadorCiudades.ViewHolder> {

    private List<Ciudad> listaCiudades;
    private GestorCiudades gestorCiudades;
    private Set<String> favoritos = new HashSet<>();

    public AdaptadorCiudades(List<Ciudad> listaCiudades, Set<String> favoritosCiudades) {
        this.listaCiudades = listaCiudades;
        this.gestorCiudades = new GestorCiudades();
        this.favoritos = favoritosCiudades;
    }
    public void actualizarDatos(List<Ciudad> nuevosCiudades) {
        listaCiudades.clear();
        listaCiudades.addAll(nuevosCiudades);
        notifyDataSetChanged();
    }

    public void setFavoritos(Set<String> favoritos) {
        this.favoritos = favoritos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cities, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ciudad ciudadActual = listaCiudades.get(position);
        String imagenCiudadUrl = gestorCiudades.imagenCiudad(ciudadActual);

        Glide.with(holder.itemView.getContext())
                .load(imagenCiudadUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(holder.imagenCiudad);

        holder.nombreCiudad.setText(ciudadActual.getNombre());
        holder.descripcionCiudad.setText(ciudadActual.getDescripcion());

        boolean esFavorita = favoritos.contains(ciudadActual.getNombre());
        holder.like.setImageResource(esFavorita ? R.drawable.buttonlikered : R.drawable.buttonlike);

        holder.like.setOnTouchListener(new View.OnTouchListener() {
            private final GestureDetector gestureDetector = new GestureDetector(holder.itemView.getContext(),
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            String ciudadNombre = ciudadActual.getNombre();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                if (favoritos.contains(ciudadNombre)) {
                                    favoritos.remove(ciudadNombre);
                                    holder.like.setImageResource(R.drawable.buttonlike);
                                }else {
                                    if (!favoritos.contains(ciudadNombre)) {
                                        favoritos.add(ciudadNombre);
                                        holder.like.setImageResource(R.drawable.buttonlikered);

                                        db.collection("usuario").document(uid)
                                                .update("favoritosCiudades", new java.util.ArrayList<>(favoritos))
                                                .addOnSuccessListener(aVoid -> Log.d("AdaptadorCiudades", "Favoritos actualizados"))
                                                .addOnFailureListener(ea -> {
                                                    Log.e("AdaptadorCiudades", "Error actualizando favoritos");
                                                    ea.printStackTrace();
                                                });
                                    } else {
                                        Log.d("AdaptadorCiudades", "Ciudad ya en favoritos, no se vuelve a agregar");
                                    }
                                    return true;
                                }

                                db.collection("usuario").document(uid)
                                        .update("favoritosCiudades", new java.util.ArrayList<>(favoritos))
                                        .addOnSuccessListener(aVoid -> Log.d("AdaptadorCiudades", "Favoritos actualizados"))
                                        .addOnFailureListener(ea -> {
                                    Log.e("AdaptadorCiudades", "Error actualizando favoritos");
                                    ea.printStackTrace();
                                });
                            }
                            return true;
                        }
                    });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        holder.abrir.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Ciudad ciudadClick = listaCiudades.get(pos);
                String nombreCiudad = ciudadClick.getNombre();
                String nombrePais = ciudadClick.getPais();

                Context context = v.getContext();
                while (!(context instanceof AppCompatActivity) && context instanceof ContextWrapper) {
                    context = ((ContextWrapper) context).getBaseContext();
                }
                AppCompatActivity activity = (AppCompatActivity) context;

                ExplorationFragment explorationFragment = ExplorationFragment.newInstance(nombrePais, nombreCiudad);

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, explorationFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCiudades.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagenCiudad;
        private TextView nombreCiudad;
        private ImageButton like;
        private TextView descripcionCiudad;
        private Button abrir;

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
