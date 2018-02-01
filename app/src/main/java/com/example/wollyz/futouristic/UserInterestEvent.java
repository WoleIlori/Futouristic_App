package com.example.wollyz.futouristic;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class UserInterestEvent {
    private String selectedLandmark;

    public UserInterestEvent(String selectedLandmark){
        this.selectedLandmark = selectedLandmark;
    }

    public String getSelectedLandmark(){
        return selectedLandmark;
    }
}
