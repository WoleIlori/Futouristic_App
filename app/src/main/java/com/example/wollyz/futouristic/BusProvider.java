package com.example.wollyz.futouristic;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class BusProvider {
    private static final EventBus bus = new EventBus();

    public static EventBus getInstance(){
        return bus;
    }

    BusProvider(){

    }
}
