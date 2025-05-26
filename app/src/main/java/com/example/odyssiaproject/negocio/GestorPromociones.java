import com.example.odyssiaproject.entidad.Promociones;
import com.example.odyssiaproject.singelton.ListaPromocionesSingelton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GestorPromociones {
    private ListaPromocionesSingelton listaPromociones;
    private final FirebaseFirestore db;

    public GestorPromociones() {
        db = FirebaseFirestore.getInstance();
        listaPromociones = ListaPromocionesSingelton.getInstance();
    }

    public String imagenPromocion(Promociones promocion) {
        if (promocion == null || promocion.getImagen() == null || promocion.getImagen().isEmpty()) {
            return "https://ejemplo.com/default-promo.jpg"; // URL por defecto
        }
        return promocion.getImagen();
    }

    /**
     * Filtra las promociones por país.
     *
     * @param nombrePais El nombre del país para filtrar.
     * @return Lista de promociones que pertenecen a ese país.
     */
    public List<Promociones> obtenerPromocionesPorPais(String nombrePais) {
        List<Promociones> filtradas = new ArrayList<>();
        if (listaPromociones.getListaPromociones() != null) {
            for (Promociones promo : listaPromociones.getListaPromociones()) {
                if (promo.getPais() != null && promo.getPais().equalsIgnoreCase(nombrePais)) {
                    filtradas.add(promo);
                }
            }
        }
        return filtradas;
    }
}
