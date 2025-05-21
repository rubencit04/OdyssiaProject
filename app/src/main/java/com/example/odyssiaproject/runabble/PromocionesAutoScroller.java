package com.example.odyssiaproject.runabble;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;

// Clase para gestionar el scroll automático BÁSICO (con salto a posición 0 al final)
// NO requiere modificar el Adaptador para Infinite Scrolling (solo necesita size() > 0)
public class PromocionesAutoScroller {

    private Handler handler = new Handler(Looper.getMainLooper());
    private RecyclerView recyclerView;
    private int velocidadScrollPx; // Cantidad de píxeles a desplazar en cada paso
    private long retrasoPasoScrollMs; // Retraso corto entre cada paso de scroll (ej: 50ms)

    private final Runnable runnableScroll = new Runnable() {
        @Override
        public void run() {
            Log.d("AutoScrollerBasico", "RunnableScroll: Paso iniciado. Tiempo: " + System.currentTimeMillis()); // <-- Añade esta línea

            if (recyclerView != null) {
                recyclerView.smoothScrollBy(velocidadScrollPx, 0);

                if (!recyclerView.canScrollHorizontally(1)) {
                    recyclerView.scrollToPosition(0);
                    Log.d("AutoScrollerBasico", "Llegó al final, saltando a posición 0.");
                }

                handler.postDelayed(this, retrasoPasoScrollMs); //
                 Log.d("AutoScrollerBasico", "Reposteando para el siguiente paso.");

            } else {
                detenerScroll();
                Log.w("AutoScrollerBasico", "Scroll básico detenido: RecyclerView es null.");
            }
        }
    };

    /**
     * Constructor para el scroll automático básico (con salto a 0).
     * @param recyclerView El RecyclerView a desplazar.
     * @param velocidadScrollPx Cantidad de píxeles por cada pequeño paso de scroll.
     * @param retrasoPasoScrollMs Retraso entre cada pequeño paso de scroll (milisegundos).
     */
    public PromocionesAutoScroller(RecyclerView recyclerView, int velocidadScrollPx, long retrasoPasoScrollMs) {
        if (recyclerView == null) {
            throw new IllegalArgumentException("RecyclerView no puede ser nulo");
        }
        this.recyclerView = recyclerView;
        this.velocidadScrollPx = velocidadScrollPx;
        this.retrasoPasoScrollMs = retrasoPasoScrollMs;
    }

    /**
     * Inicia el proceso de scroll automático básico.
     */
    public void iniciarScroll() {
        Log.d("AutoScrollerBasico", "Scroll básico iniciado. Posteando primer paso.");
        handler.postDelayed(runnableScroll, retrasoPasoScrollMs);
    }

    /**
     * Detiene el proceso de scroll automático básico.
     */
    public void detenerScroll() {
        handler.removeCallbacks(runnableScroll);
        Log.d("AutoScrollerBasico", "detenerScroll() llamado y ejecutado.");
    }

    // Métodos opcionales de pausa/reanudar ...
}