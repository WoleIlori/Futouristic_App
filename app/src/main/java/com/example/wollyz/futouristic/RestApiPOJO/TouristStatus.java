package com.example.wollyz.futouristic.RestApiPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Wollyz on 02/03/2018.
 */
public class TouristStatus {
    @SerializedName("tourist")
    @Expose
    private String tourist;
    @SerializedName("status")
    @Expose
    private String status;

    public String getTourist() {
        return tourist;
    }

    public void setTourist(String tourist) {
        this.tourist = tourist;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
