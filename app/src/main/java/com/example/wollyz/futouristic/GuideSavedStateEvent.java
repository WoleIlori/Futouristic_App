package com.example.wollyz.futouristic;

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
