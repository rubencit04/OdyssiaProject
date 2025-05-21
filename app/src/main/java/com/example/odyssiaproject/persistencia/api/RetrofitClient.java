package com.example.odyssiaproject.persistencia.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class    RetrofitClient {
    private static Retrofit retrofit;

    private static ApiService apiService;
    private static final String URL = "https://firestore.googleapis.com/v1/projects/odyssiaproject-ff99f/databases/(default)/documents/";


    public static ApiService getApiService() {
        if (apiService == null) {
            // Configurar Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Crear la instancia de la interfaz
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
