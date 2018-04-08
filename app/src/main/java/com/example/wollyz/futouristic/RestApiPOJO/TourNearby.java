package com.example.wollyz.futouristic.RestApiPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Wollyz on 01/03/2018.
 */
public class TourNearby implements Serializable {
    @SerializedName("guide_name")
    @Expose
    private String guide_name;

    @SerializedName("landmark")
    @Expose
    private String landmark;

    @SerializedName("price")
    @Expose
    private float price;

    @SerializedName("group_limit")
    @Expose
    private int groupLimit;

    @SerializedName("start_time")
    @Expose
    private String startTime;

    @SerializedName("summary")
    @Expose
    private String summary;




    public String getLandmark() {
        return landmark;
    }

    public void setTotal(String landmark) {
        this.landmark = landmark;
    }

    public String getGuideName() {
        return guide_name;
    }

    public void setGuide(String guide_name) {
        this.guide_name = guide_name;
    }

    public float getPrice() {
        return price;
    }

    public void setGuide(float price) {
        this.price = price;
    }

    public void setGroupLimit(int groupLimit){
        this.groupLimit = groupLimit;
    }

    public int getGroupLimit(){
        return groupLimit;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


}

