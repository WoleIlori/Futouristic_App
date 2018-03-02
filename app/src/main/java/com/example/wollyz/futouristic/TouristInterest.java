package com.example.wollyz.futouristic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class TouristInterest {
    @SerializedName("tourist_username")
    @Expose
    private String tourist_username;

    @SerializedName("guide_username")
    @Expose
    private String guide_username;

    @SerializedName("landmark")
    @Expose
    private String landmark;

    @SerializedName("total_people")
    @Expose
    private int total_people;

    public TouristInterest(){
        tourist_username = "";
        guide_username = "";
        landmark = "";
        total_people = 0;
    }

    public String getTouristUsername() {
        return tourist_username;
    }

    public void setTouristUsername(String tourist_username) {
        this.tourist_username = tourist_username;
    }

    public String getGuideUsername() {
        return guide_username;
    }

    public void setGuideUsername(String guide_username) {
        this.guide_username = guide_username;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public int getTotalPeople() {
        return total_people;
    }

    public void setTotalPeople(int total_people) {
        this.total_people = total_people;
    }


}

