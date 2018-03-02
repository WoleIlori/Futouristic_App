package com.example.wollyz.futouristic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Wollyz on 01/03/2018.
 */
public class TourGroup {
    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("landmark")
    @Expose
    private String landmark;

    @SerializedName("group_size")
    @Expose
    private int groupSize;

    @SerializedName("price")
    @Expose
    private float price;

    @SerializedName("interval")
    @Expose
    private int interval;

    public TourGroup(){
        username = "";
        landmark = "";
        groupSize = 0;
        price = 0;
        interval = 0;


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

    public void setPrice(float price){
        this.price= price;
    }

    public float getPrice(){
        return price;
    }

    public void setGroupSize(int groupSize){
        this.groupSize = groupSize;
    }

    public int getGroupSize(){
        return groupSize;
    }

    public void setInterval(int interval){
        this.interval = interval;
    }

    public int getInterval(){
        return interval;
    }
}
