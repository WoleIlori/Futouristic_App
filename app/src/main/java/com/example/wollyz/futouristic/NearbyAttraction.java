package com.example.wollyz.futouristic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class NearbyAttraction {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("attractions")
    @Expose
    private List<String> attractions;
    @SerializedName("distances")
    @Expose
    private List<Double> distances;

    public NearbyAttraction()
    {
        this.username = "";
        attractions = new ArrayList<String>();
        distances = new ArrayList<Double>();

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getAttractions() {
        return attractions;
    }

    public void setAttraction(String attraction) {
        attractions.add(attraction);
    }

    public List<Double> getDistances() {
        return distances;
    }

    public void setDistance(double distance) {
        distances.add(distance);
    }

    public String getLandmark(int pos)
    {
        return attractions.get(pos);
    }

    public Double getDist(int pos)
    {
        return distances.get(pos);
    }
}
