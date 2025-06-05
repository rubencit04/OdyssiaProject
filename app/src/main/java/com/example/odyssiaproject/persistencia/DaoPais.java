package com.example.odyssiaproject.persistencia;

import com.example.odyssiaproject.dto.PaisDTO;
import com.example.odyssiaproject.persistencia.api.RetrofitRenderClient;

import java.util.List;

import retrofit2.Call;

public class DaoPais {
    public Call<List<PaisDTO>> obtenerPaises() {
        return RetrofitRenderClient.getApiService().getPaises();
    }
}
