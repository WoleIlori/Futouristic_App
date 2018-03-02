package com.example.wollyz.futouristic;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by Wollyz on 01/02/2018.
 */
public interface ApiInterface {
    @GET("attraction")
    Call<List<Attractions>> doGetAllAttractions();

    @Headers("Content-Type: application/json")
    @POST("landmarksnearby")
    Call<String> addNearbyAttractions(@Body Object nearbyAttraction);


    @GET("nearbytour")
    Call<List<TourNearby>> getToursNearby(
            @Query("attractions[]") List<String> nearbyLandmarks,
            @Query("total_people") int total_people
    );

    @Headers("Content-Type: application/json")
    @POST("touristselection")
    Call<String> addTouristToTourGroup(@Body Object touristInterest);

    @GET("touristlogin")
    Call<String> getTouristLoginInfo(
            @Query("username") String username,
            @Query("password") String pass
    );

    @GET("guidelogin")
    Call<String> getGuideLoginInfo(
            @Query("username") String username,
            @Query("password") String pass
    );

    @Headers("Content-Type: application/json")
    @POST("guideselection")
    Call<String> addGuideLandmarkSelection(@Body Object guideSelection);

    @Headers("Content-Type: application/json")
    @POST("creategroup")
    Call<String> createGuideTourGroup(@Body Object landmarkTourGroup);

    @PUT("groupon")
    Call<String> setGroupToAvailable(
            @Query("username") String guideUsername,
            @Query("landmark") String landmark
    );

    @PUT("groupoff")
    Call<String> setGroupToUnavailable(
            @Query("username") String guideUsername,
            @Query("landmark") String landmark
    );

    @GET("savedstate")
    Call<GuideSavedState> getGuideSavedState(
            @Query("username") String guideUsername
    );

    @GET("touriststatus")
    Call<TouristStatus> getTouristStatus(
            @Query("username") String guideUsername
    );


}
