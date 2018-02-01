package com.example.wollyz.futouristic;

import java.util.List;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class TouristAmtEvent {
    List<AmtTouristNearby> amtTourist;
    String serverMessage;

    public TouristAmtEvent(List<AmtTouristNearby> amtTourist, String serverMessage){
        this.amtTourist = amtTourist;
        this.serverMessage = serverMessage;
    }

    public List<AmtTouristNearby> getAmtTourist(){
        return amtTourist;
    }

    public String getServerMessage()
    {
        return serverMessage;
    }
}
