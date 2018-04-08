package com.example.wollyz.futouristic.RestApiPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Wollyz on 24/03/2018.
 */
public class TouristSavedState {
    @SerializedName("guide")
    @Expose
    private String guide;
    @SerializedName("attraction")
    @Expose
    private String attraction;
    @SerializedName("start_time")
    @Expose
    private String startTime;

    @SerializedName("price")
    @Expose
    private float price;


    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;


    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    public String getAttraction() {
        return attraction;
    }

    public void setAttraction(String attraction) {
        this.attraction = attraction;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
