package com.example.wollyz.futouristic;

import com.example.wollyz.futouristic.RestApiPOJO.TourNearby;

import java.util.List;

/**
 * Created by Wollyz on 01/03/2018.
 */
public class TourNearbyEvent {
    List<TourNearby> toursNearby;
    String serverMessage;

    public TourNearbyEvent(List<TourNearby> toursNearby, String serverMessage){
        this.toursNearby = toursNearby;
        this.serverMessage = serverMessage;
    }

    public List<TourNearby> getToursNearby(){
        return toursNearby;
    }

    public String getServerMessage()
    {
        return serverMessage;
    }
}

