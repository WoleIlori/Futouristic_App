package com.example.wollyz.futouristic.RestApiPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wollyz on 02/02/2018.
 */
public class GuideSelection {
    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("landmarks")
    @Expose
    private List<String> landmarks;

    @SerializedName("distances")
    @Expose
    private List<Double> distances;

    public GuideSelection(){
        username = "";
        landmarks = new ArrayList<String>();
        distances = new ArrayList<Double>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getLandmarks() {
        return landmarks;
    }

    public void setLandmarks(List<String> landmarks) {
        this.landmarks = landmarks;
    }

    public void setDistances(List<Double> distances){
        this.distances = distances;
    }
    public List<Double> getDistances(){
        return distances;
    }

}
