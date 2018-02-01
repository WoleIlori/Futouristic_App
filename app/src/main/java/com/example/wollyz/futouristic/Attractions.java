package com.example.wollyz.futouristic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class Attractions {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("latitude")
    @Expose
    private Double latitude;

    @SerializedName("longitude")
    @Expose
    private Double longitude;

    public String getName()
    {
        return name;
    }

    public Double getLatitude()
    {
        return latitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }



}
