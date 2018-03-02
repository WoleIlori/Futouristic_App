package com.example.wollyz.futouristic;

/**
 * Created by Wollyz on 02/03/2018.
 */
public class ListModel {
    String landmark;
    int value;

    public ListModel(String landmark, int value){
        this.landmark = landmark;
        this.value = value;

    }

    public String getLandmark(){
        return landmark;
    }

    public int getValue(){
        return this.value;
    }

    public void setValue(int value){
        this.value = value;
    }
}
