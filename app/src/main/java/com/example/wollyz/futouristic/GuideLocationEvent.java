package com.example.wollyz.futouristic;

import com.example.wollyz.futouristic.RestApiPOJO.GuideLocation;

/**
 * Created by Wollyz on 12/03/2018.
 */
public class GuideLocationEvent {
    GuideLocation guide;

    public GuideLocationEvent(GuideLocation guide){
        this.guide = guide;
    }

    public GuideLocation getGuideLocation(){
        return guide;
    }
}
