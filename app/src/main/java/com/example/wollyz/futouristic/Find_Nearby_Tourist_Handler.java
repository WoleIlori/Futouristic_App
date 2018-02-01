package com.example.wollyz.futouristic;

import android.content.Context;

import java.util.List;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class Find_Nearby_Tourist_Handler {
    private double longitude;
    private double latitude;
    private UserLocationHandler locManager;
    private double[] distance; //distance for attraction
    private int[] attractionIndex;
    private String[] name;
    private int heapSize; //size of k ie. kth nearest attractions
    private Context c;
    //private MaxHeap maxheap;



    Find_Nearby_Tourist_Handler(double latitude, double longitude){ //double latitude, double longitude
        heapSize = 4;
        this.latitude = latitude;
        this.longitude = longitude;
        //maxheap = new MaxHeap(heapSize);
        attractionIndex = new int[heapSize + 1];
    }

    public void getNearestAttractions(List<Attractions> attractions)
    {
        //latitude = locManager.getUserLatitude();
        //longitude = locManager.getUserLongitude();

        distance = new double[attractions.size()];
        int index;

        //calculating distance between user current location and all attractions
        System.out.println("distance:");


        for(int i = 0; i < attractions.size(); i++){
            distance[i] = Harvesine.calculateDist(latitude,longitude,attractions.get(i));
            System.out.println(distance[i]);
        }

    }


    //tourist_loc.getUserLocation(user);
}
