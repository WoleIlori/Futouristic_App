package com.example.wollyz.futouristic.RestApiPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Wollyz on 16/03/2018.
 */
public class TourGroupStatus {
    @SerializedName("landmark")
    @Expose
    private String landmark;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("no.joined")
    @Expose
    private Integer noJoined;

    public TourGroupStatus(){
        landmark = "";
        startTime = "0:00:00";
        noJoined = 0;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getNoJoined() {
        return noJoined;
    }

    public void setNoJoined(Integer noJoined) {
        this.noJoined = noJoined;
    }
}
