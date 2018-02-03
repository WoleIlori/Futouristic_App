package com.example.wollyz.futouristic;

import java.util.ArrayList;

/**
 * Created by Wollyz on 02/02/2018.
 */
public class GuideLandmarkChoiceEvent {
    private ArrayList<String> chosenLandmarks;
    private int[] chosenLandmarksIndex;

    public GuideLandmarkChoiceEvent(ArrayList<String> chosenLandmarks,int[] chosenLandmarksIndex){
        this.chosenLandmarks = chosenLandmarks;
        this.chosenLandmarksIndex = chosenLandmarksIndex;
    }

    public ArrayList<String> getChosenLandmarks()
    {
        return chosenLandmarks;
    }

    public int[] getChosenLandmarksIndex(){
        return chosenLandmarksIndex;
    }


}
