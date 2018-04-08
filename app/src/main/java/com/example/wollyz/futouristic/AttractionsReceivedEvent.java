package com.example.wollyz.futouristic;

import com.example.wollyz.futouristic.RestApiPOJO.Attractions;

import java.util.List;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class AttractionsReceivedEvent {
    private List<Attractions> attractions;
    private String serverMessage;

    public AttractionsReceivedEvent(List<Attractions> attractions, String serverMessage)
    {
        this.attractions = attractions;
        this.serverMessage = serverMessage;
    }

    public List<Attractions> getAttraction()
    {
        return attractions;
    }

    public String getServerMessage(){
        return serverMessage;
    }
}
