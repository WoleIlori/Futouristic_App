package com.example.wollyz.futouristic;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Wollyz on 01/02/2018.
 */
public interface ApiInterface {
    @GET("attraction")
    Call<List<Attractions>> doGetAllAttractions();

    @Headers("Content-Type: application/json")
    @POST("nearlandmark")
    Call<String> addNearbyAttractions(@Body Object nearbyAttraction);

    @GET("nearbytourists")
    Call<List<AmtTouristNearby>> doGetAllTouristNearby(
            @Query("username") String username
    );
}
