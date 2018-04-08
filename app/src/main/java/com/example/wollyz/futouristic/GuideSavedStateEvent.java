package com.example.wollyz.futouristic;

import com.example.wollyz.futouristic.RestApiPOJO.GuideSavedState;

/**
 * Created by Wollyz on 27/02/2018.
 */
public class GuideSavedStateEvent {
    private GuideSavedState savedState;

    public GuideSavedStateEvent(GuideSavedState savedState){
        this.savedState = savedState;
    }

    public GuideSavedState getSavedState(){
        return savedState;
    }
}
