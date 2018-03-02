package com.example.wollyz.futouristic;

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
}
