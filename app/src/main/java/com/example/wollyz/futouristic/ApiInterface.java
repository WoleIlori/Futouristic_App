package com.example.wollyz.futouristic;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Wollyz on 01/02/2018.
 */
public interface ApiInterface {
    @GET("attraction")
    Call<List<Attractions>> doGetAllAttractions();
}
