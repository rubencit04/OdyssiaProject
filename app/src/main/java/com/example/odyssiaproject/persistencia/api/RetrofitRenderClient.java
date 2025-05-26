package com.example.odyssiaproject.persistencia.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRenderClient {

    private static Retrofit retrofit;
    private static ApiRenderService apiRenderService;

    private static final String BASE_URL = "https://oddysseyservidor.onrender.com/";

    public static ApiRenderService getApiService() {
        if (apiRenderService == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiRenderService = retrofit.create(ApiRenderService.class);
        }
        return apiRenderService;
    }
}