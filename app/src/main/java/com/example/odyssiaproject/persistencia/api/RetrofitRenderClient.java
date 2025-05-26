package com.example.odyssiaproject.persistencia.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRenderClient {

    private static Retrofit retrofit;
    private static ApiRenderService apiRenderService;

    private static final String BASE_URL = "https://oddysseyservidor-oj5k.onrender.com/";

    public static ApiRenderService getApiService() {
        if (apiRenderService == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new RetryInterceptor())
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiRenderService = retrofit.create(ApiRenderService.class);
        }
        return apiRenderService;
    }
}

class RetryInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        IOException lastException = null;

        for (int i = 0; i < 3; i++) { // Reintenta 3 veces
            try {
                return chain.proceed(request);
            } catch (IOException e) {
                lastException = e;
                try {
                    Thread.sleep(2000); // Espera 2 segundos antes de reintentar
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        throw lastException;
    }
}