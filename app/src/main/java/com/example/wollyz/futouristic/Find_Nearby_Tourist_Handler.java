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
    private NearbyAttraction nearby;
    private int heapSize; //size of k ie. kth nearest attractions
    private Context c;
    private MaxHeap maxheap;



    Find_Nearby_Tourist_Handler(double latitude, double longitude){ //double latitude, double longitude
        heapSize = 3;
        this.latitude = latitude;
        this.longitude = longitude;
        maxheap = new MaxHeap(heapSize);
        attractionIndex = new int[heapSize + 1];
        nearby = new NearbyAttraction();
    }

    public NearbyAttraction getNearestAttractions(List<Attractions> attractions)
    {
        //latitude = locManager.getUserLatitude();
        //longitude = locManager.getUserLongitude();
        String name;
        double dist;
        distance = new double[attractions.size()];
        int index;

        //calculating distance between user current location and all attractions
        System.out.println("distance:");


        for(int i = 0; i < attractions.size(); i++){
            distance[i] = Harvesine.calculateDist(latitude,longitude,attractions.get(i));
            System.out.println(distance[i]);
        }

        //add distance to max heap
        for(int i = 0; i < distance.length; i++){
            maxheap.insert(distance[i], i);
        }

        //get the index of attractions nearby
        attractionIndex = maxheap.getHeap();

        System.out.println("nearest attractions:");
        for(int i = 1; i < heapSize + 1; i++)
        {
            index = attractionIndex[i];
            name = attractions.get(index).getName();
            nearby.setAttraction(name);
            dist = distance[index];
            nearby.setDistance(dist);

        }
        return nearby;
    }


    //tourist_loc.getUserLocation(user);
}
