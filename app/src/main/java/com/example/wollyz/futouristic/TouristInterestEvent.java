package com.example.wollyz.futouristic;

import com.example.wollyz.futouristic.RestApiPOJO.TourNearby;

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
