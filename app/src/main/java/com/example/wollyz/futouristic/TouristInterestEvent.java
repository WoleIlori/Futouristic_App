package com.example.wollyz.futouristic;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class TouristInterestEvent {
    private TourNearby selectedTour;

    public TouristInterestEvent(TourNearby selectedTour){
        this.selectedTour = selectedTour;
    }

    public TourNearby getSelectedTour(){
        return selectedTour;
    }
}
