package com.example.wollyz.futouristic;

import com.example.wollyz.futouristic.RestApiPOJO.TouristStatus;

/**
 * Created by Wollyz on 02/03/2018.
 */
public class TouristStatusEvent {
    TouristStatus touristStatus;

    public TouristStatusEvent(TouristStatus touristStatus){
        this.touristStatus = touristStatus;
    }

    public TouristStatus getTouristStatus(){
        return touristStatus;
    }
}
