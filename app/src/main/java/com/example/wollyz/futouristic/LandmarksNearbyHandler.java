package com.example.wollyz.futouristic;

import android.content.Context;

import java.util.List;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class LandmarksNearbyHandler {
    private double[] distance; //distance for attraction
    private int[] attractionIndex;
    private List<Attractions> allAttractions;
    private final int HEAP_SIZE = 3; //size of k ie. kth nearest attractions
    private MaxHeap maxheap;

    public LandmarksNearbyHandler(List<Attractions> allAttractions){
        this.allAttractions = allAttractions;
        distance = new double[allAttractions.size()];
        maxheap = new MaxHeap(HEAP_SIZE);
        attractionIndex = new int[HEAP_SIZE + 1];
    }

    //CHANGE
    public NearbyAttraction getNearestAttractions(double userLat,double userLong)
    {
        String name;
        double dist;
        int index;
        double landmark_lat;
        double landmark_long;
        NearbyAttraction nearby = new NearbyAttraction();

        //calculating distance between user current location and all attractions

        for (int i = 0; i < allAttractions.size(); i++) {
            landmark_lat = allAttractions.get(i).getLatitude();
            landmark_long = allAttractions.get(i).getLongitude();
            distance[i] = Harvesine.calculateDist(userLat, userLong, landmark_lat, landmark_long);
        }

        maxheap.EmptyHeap();
        //add distance to max heap
        for (int i = 0; i < distance.length; i++) {
            maxheap.insert(distance[i], i);
        }

        //get the index of attractions nearby
        attractionIndex = maxheap.getHeap();

        //order the index, crucial in updating nearby attraction in the database
        int min;
        int swap;
        int pos = 0;
        for(int i = 1; i < attractionIndex.length-1; i++){
            min = attractionIndex[i];
            for(int j = i+1; j < attractionIndex.length; j++){
                if(attractionIndex[j] < min){
                    min = attractionIndex[j];
                    pos = j;
                }
            }
            swap = attractionIndex[i];
            attractionIndex[i] = min;
            attractionIndex[pos] = swap;

        }

        for (int i = 1; i < HEAP_SIZE + 1; i++) {           //int i = HEAP_SIZE; i >= 1; i--
            index = attractionIndex[i];
            name = allAttractions.get(index).getName();
            nearby.setAttraction(name);
            dist = distance[index];
            nearby.setDistance(dist);
        }


        return nearby;
    }


}
