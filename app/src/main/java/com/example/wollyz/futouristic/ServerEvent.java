package com.example.wollyz.futouristic;

import java.util.List;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class ServerEvent {
    private List<Attractions> attractions;
    private String serverMessage;

    public ServerEvent(List<Attractions> attractions, String serverMessage)
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
