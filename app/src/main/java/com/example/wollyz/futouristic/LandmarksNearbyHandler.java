package com.example.wollyz.futouristic;

import android.content.Context;

import java.util.List;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class LandmarksNearbyHandler {
    private double longitude;
    private double latitude;
    private double[] distance; //distance for attraction
    private int[] attractionIndex;
    private NearbyAttraction nearby;
    private final int HEAP_SIZE = 3; //size of k ie. kth nearest attractions
    private Context c;
    private MaxHeap maxheap;

    public LandmarksNearbyHandler(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        maxheap = new MaxHeap(HEAP_SIZE);
        attractionIndex = new int[HEAP_SIZE + 1];
        nearby = new NearbyAttraction();
    }

    public NearbyAttraction getNearestAttractions(List<Attractions> attractions)
    {

        String name;
        double dist;
        distance = new double[attractions.size()];
        int index;
        double landmark_lat;
        double landmark_long;

        //calculating distance between user current location and all attractions
        System.out.println("distance:");


        for(int i = 0; i < attractions.size(); i++){
            landmark_lat = attractions.get(i).getLatitude();
            landmark_long = attractions.get(i).getLongitude();
            distance[i] = Harvesine.calculateDist(latitude,longitude,landmark_lat,landmark_long);
            System.out.println(distance[i]);
        }

        //add distance to max heap
        for(int i = 0; i < distance.length; i++){
            maxheap.insert(distance[i], i);
        }

        //get the index of attractions nearby
        attractionIndex = maxheap.getHeap();

        System.out.println("nearest attractions:");
        for(int i = 1; i < HEAP_SIZE + 1; i++)
        {
            index = attractionIndex[i];
            name = attractions.get(index).getName();
            nearby.setAttraction(name);
            dist = distance[index];
            nearby.setDistance(dist);

        }
        return nearby;
    }

}
