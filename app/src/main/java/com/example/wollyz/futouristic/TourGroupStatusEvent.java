package com.example.wollyz.futouristic;

import com.example.wollyz.futouristic.RestApiPOJO.TourGroupStatus;

/**
 * Created by Wollyz on 16/03/2018.
 */
public class TourGroupStatusEvent {
    private TourGroupStatus groupStatus;

    public TourGroupStatusEvent(TourGroupStatus groupStatus){
        this.groupStatus= groupStatus;
    }

    public TourGroupStatus getGroupStatus()
    {
        return groupStatus;
    }
}
