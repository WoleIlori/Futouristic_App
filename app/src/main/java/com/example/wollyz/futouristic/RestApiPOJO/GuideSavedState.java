package com.example.wollyz.futouristic.RestApiPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Wollyz on 27/02/2018.
 */
public class GuideSavedState {
    @SerializedName("guides_list")
    @Expose
    private List<String> guidesList = null;
    @SerializedName("tour")
    @Expose
    private String tour;
    @SerializedName("status")
    @Expose
    private String status;

    public List<String> getGuidesList() {
        return guidesList;
    }

    public void setGuidesList(List<String> guidesList) {
        this.guidesList = guidesList;
    }

    public String getTour() {
        return tour;
    }

    public void setTour(String tour) {
        this.tour = tour;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
