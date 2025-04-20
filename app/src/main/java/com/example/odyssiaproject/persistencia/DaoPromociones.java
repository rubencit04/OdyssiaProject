package com.example.odyssiaproject.persistencia;

import com.example.odyssiaproject.entidad.Promociones;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DaoPromociones {

    private FirebaseFirestore db;

    public DaoPromociones() {
        db = FirebaseFirestore.getInstance();
    }

    public interface PromocionCallback {
        void onPromocionesCargadas(List<Promociones> promociones);
        void onError(Exception e);
    }

    public void obtenerPromocionesAleatorias(final PromocionCallback callback) {
        db.collection("promociones")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Promociones> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Promociones promo = doc.toObject(Promociones.class);
                        lista.add(promo);
                    }
                    Collections.shuffle(lista);
                    callback.onPromocionesCargadas(lista);
                })
                .addOnFailureListener(callback::onError);
    }

    public void obtenerPromocionesPorPais(String pais, final PromocionCallback callback) {
        db.collection("promociones")
                .whereEqualTo("pais", pais)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Promociones> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Promociones promo = doc.toObject(Promociones.class);
                        lista.add(promo);
                    }
                    callback.onPromocionesCargadas(lista);
                })
                .addOnFailureListener(callback::onError);
    }
}