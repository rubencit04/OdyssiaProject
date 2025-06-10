package com.example.odyssiaproject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ChatService {

    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    Call<ChatResponse> enviarMensaje(
            @Header("Authorization") String authHeader,
            @Body ChatRequest request
    );
}

