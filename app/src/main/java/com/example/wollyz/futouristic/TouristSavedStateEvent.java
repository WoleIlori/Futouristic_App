package com.example.wollyz.futouristic;

/**
 * Created by Wollyz on 24/03/2018.
 */
public class TouristSavedStateEvent {
    TouristSavedState touristSavedState;

    public TouristSavedStateEvent(TouristSavedState touristSavedState){
        this.touristSavedState = touristSavedState;
    }

    public TouristSavedState getTouristSavedState(){
        return touristSavedState;
    }
}
