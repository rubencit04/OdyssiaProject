package com.example.odyssiaproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AthyssiaFragment extends Fragment {

    private RecyclerView rwChat;
    private EditText etMensaje;
    private ImageButton btEnviarMensaje;
    private AdaptadorChat adaptadorChat;
    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    private final String API_KEY = "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";

    private ChatService chatService;
    private java.util.ArrayList<ChatModal> chatModalArrayList;

    private enum PasoConversacion {
        PREGUNTA_PAIS, PREGUNTA_CIUDAD, PREGUNTA_DIAS, FINALIZADO
    }

    private PasoConversacion pasoActual = PasoConversacion.PREGUNTA_PAIS;
    private String pais = "", ciudad = "";
    private int dias = 0;

    private final Set<String> paisesValidos = new HashSet<>(Arrays.asList(
            "Alemania", "Francia", "España", "Italia", "Portugal", "Países Bajos", "Bélgica",
            "Suiza", "Austria", "Suecia", "Noruega", "Finlandia", "Irlanda", "Grecia", "Polonia",
            "Hungría", "Croacia", "Dinamarca", "República Checa", "Eslovaquia", "Eslovenia",
            "Estonia", "Letonia", "Lituania", "Malta", "Rumanía", "Bulgaria", "Chipre", "Luxemburgo"
    ));

    private final Set<String> ciudadesValidas = new HashSet<>(Arrays.asList(
            "París", "Madrid", "Roma", "Berlín", "Lisboa", "Ámsterdam", "Bruselas", "Viena",
            "Zúrich", "Estocolmo", "Oslo", "Helsinki", "Dublín", "Atenas", "Varsovia", "Budapest",
            "Zagreb", "Copenhague", "Praga", "Bratislava"
    ));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_athyssia, container, false);

        rwChat = view.findViewById(R.id.rwChat);
        etMensaje = view.findViewById(R.id.etMensaje);
        btEnviarMensaje = view.findViewById(R.id.btEnviarMensaje);

        chatModalArrayList = new java.util.ArrayList<>();
        adaptadorChat = new AdaptadorChat(chatModalArrayList, getContext());

        rwChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rwChat.setAdapter(adaptadorChat);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        chatService = retrofit.create(ChatService.class);

        responderConDelay("¡Bienvenido! Soy Athyssia, tu asistente virtual personalizada. ¿Qué país deseas visitar?");

        btEnviarMensaje.setOnClickListener(v -> {
            String mensajeUsuario = etMensaje.getText().toString().trim();
            if (mensajeUsuario.isEmpty()) {
                Toast.makeText(getContext(), "Por favor introduzca un mensaje", Toast.LENGTH_SHORT).show();
                return;
            }
            enviarMensaje(mensajeUsuario);
            etMensaje.setText("");
        });

        return view;
    }

    private void enviarMensaje(String mensaje) {
        chatModalArrayList.add(new ChatModal(mensaje, USER_KEY));
        adaptadorChat.notifyItemInserted(chatModalArrayList.size() - 1);
        rwChat.scrollToPosition(chatModalArrayList.size() - 1);

        switch (pasoActual) {
            case PREGUNTA_PAIS:
                if (paisesValidos.contains(capitalize(mensaje))) {
                    pais = capitalize(mensaje);
                    pasoActual = PasoConversacion.PREGUNTA_CIUDAD;
                    responderConDelay("De acuerdo, ¿qué ciudad quieres visitar en " + pais + "?");
                } else {
                    responderConDelay("Por favor, introduce un país europeo válido.");
                }
                break;

            case PREGUNTA_CIUDAD:
                if (ciudadesValidas.contains(capitalize(mensaje))) {
                    ciudad = capitalize(mensaje);
                    pasoActual = PasoConversacion.PREGUNTA_DIAS;
                    responderConDelay("Perfecto, ¿cuántos días planeas quedarte en " + ciudad + "?");
                } else {
                    responderConDelay("Por favor, introduce una ciudad europea válida.");
                }
                break;

            case PREGUNTA_DIAS:
                try {
                    dias = Integer.parseInt(mensaje);
                    if (dias <= 0 || dias > 60) {
                        responderConDelay("Introduce un número de días válido (1-60).");
                    } else {
                        pasoActual = PasoConversacion.FINALIZADO;
                        responderConDelay("Voy a prepararte una ruta personalizada por " + ciudad + " de " + dias + " días.");
                        ejecutarPrompt();
                    }
                } catch (NumberFormatException e) {
                    responderConDelay("Por favor, introduce un número válido de días.");
                }
                break;

            case FINALIZADO:
                responderConDelay("La ruta ya está siendo generada. Por favor, espera.");
                break;
        }
    }

    private void ejecutarPrompt() {
        String prompt = "Crea un itinerario de viaje de " + dias + " días en " + ciudad + ", " + pais +
                ", incluyendo actividades turísticas, lugares recomendados, restaurantes y consejos locales.";

        ChatRequest chatRequest = new ChatRequest(
                "gpt-3.5-turbo",
                Collections.singletonList(new Message("user", prompt))
        );

        Call<ChatResponse> call = chatService.enviarMensaje("Bearer " + API_KEY, chatRequest);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String respuestaBot = response.body().getChoices().get(0).getMessage().getContent();
                    responderConDelay(respuestaBot);
                } else {
                    mostrarError();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                mostrarError();
            }
        });
    }

    private void responderConDelay(String respuesta) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            chatModalArrayList.add(new ChatModal(respuesta, BOT_KEY));
            adaptadorChat.notifyItemInserted(chatModalArrayList.size() - 1);
            rwChat.scrollToPosition(chatModalArrayList.size() - 1);
        }, 1200);
    }

    private void mostrarError() {
        responderConDelay("Error en la respuesta. Por favor, intente de nuevo.");
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
