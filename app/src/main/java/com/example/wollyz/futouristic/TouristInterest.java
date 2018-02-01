package com.example.wollyz.futouristic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class TouristInterest {
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("landmark")
    @Expose
    private String landmark;

    public TouristInterest(){
        username = "";
        landmark = "";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
}
