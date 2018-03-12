package com.example.wollyz.futouristic;

import com.example.wollyz.futouristic.MapsPOJO.Main;
import com.example.wollyz.futouristic.MapsPOJO.RouteEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Wollyz on 12/03/2018.
 */
public class GoogleMapsApiClient {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/";
    private static Retrofit retrofit = null;
    private OkHttpClient.Builder httpClient;

    public GoogleMapsApiClient(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                //.addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
    }

    public void getMapDirections(String origin,String destination){
        //origin = fromPosition.getLatitude+","+fromPosition.getLongitude;
        //destination = toPosition.getLatitude+","+toPosition.getLongitude;
        GoogleMapsApiInterface apiService = retrofit.create(GoogleMapsApiInterface.class);
        Call<Main> call = apiService.getMapDirections(origin,destination);

        call.enqueue(new Callback<Main>() {
            @Override
            public void onResponse(Call<Main> call, Response<Main> response) {
                BusProvider.getInstance().post(new RouteEvent(response.body()));
            }

            @Override
            public void onFailure(Call<Main> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });
    }

}
