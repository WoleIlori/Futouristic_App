package com.example.wollyz.futouristic.RestApiPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class Attractions implements Serializable {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("latitude")
    @Expose
    private Double latitude;

    @SerializedName("longitude")
    @Expose
    private Double longitude;

    public Attractions(){
        name = "";
        latitude = 0.0;
        longitude = 0.0;

    }

    public String getName()
    {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }



}