package com.example.odyssiaproject.adaptador;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class AdaptadorPromociones extends RecyclerView.Adapter<AdaptadorPromociones.ViewHolder> {

    private List<Promociones> listaPromociones = new ArrayList<>();
    private GestorPromociones gestorPromociones = new GestorPromociones();

    public AdaptadorPromociones(ArrayList<Promociones> listaPromociones) {
        this.listaPromociones = listaPromociones;
    }

    public void actualizarDatos(List<Promociones> nuevasPromociones) {
        if (nuevasPromociones != null) {
            this.listaPromociones = nuevasPromociones;
        } else {
            this.listaPromociones = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageButton imagenPromocion;

        public ViewHolder(@NonNull View v) {
            super(v);
            imagenPromocion = v.findViewById(R.id.imagePromotion);
        }
    }

    @NonNull
    @Override
    public AdaptadorPromociones.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promotions, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorPromociones.ViewHolder holder, int position) {
        if (listaPromociones != null && !listaPromociones.isEmpty()) {

            int realPos = position % listaPromociones.size();
            Promociones p = listaPromociones.get(realPos);

            String urlImagen = gestorPromociones.imagenPromocion(p);
            Glide.with(holder.itemView.getContext())
                    .load(urlImagen)
                    .placeholder(R.drawable.imgpromotion)
                    .error(R.drawable.imgpromotion)
                    .into(holder.imagenPromocion);

            holder.imagenPromocion.setOnClickListener(v -> {
                String url = p.getLink();
                if (url != null && !url.isEmpty()) {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                        v.getContext().startActivity(intent);
                    } else {
                        Log.w("AdaptadorPromociones", "No hay app para manejar el enlace: " + url);
                    }
                }
            });
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.imgpromotion)
                    .into(holder.imagenPromocion);
            holder.imagenPromocion.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return (listaPromociones != null && !listaPromociones.isEmpty()) ? Integer.MAX_VALUE : 0;
    }


}
