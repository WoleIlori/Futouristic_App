package com.example.wollyz.futouristic;

import com.example.wollyz.futouristic.MapsPOJO.Main;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Wollyz on 12/03/2018.
 */
public interface GoogleMapsApiInterface {

    @GET("api/directions/json?mode=walking&key=AIzaSyDkB6-B35xOqoJk7hYb51GsFxnfY6GpwFQ")
    Call<Main> getMapDirections(
            @Query("origin")String origin,
            @Query("destination") String destination
    );

}
