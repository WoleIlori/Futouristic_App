package com.example.wollyz.futouristic;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class Harvesine {

    public static double calculateDist(double startLat,double startLong, double landmarkLat, double landmarkLong){
        //Reference: https://github.com/jasonwinn/haversine/blob/master/Haversine.java
        final int EARTH_RADIUS = 6371;
        double endLat = landmarkLat;
        double endLong = landmarkLong;


        double dLat = Math.toRadians((endLat - startLat)); //difference between latitude
        double dLong = Math.toRadians((endLong - startLong)); //difference between longitude

        startLat = Math.toRadians(startLat);
        endLat  = Math.toRadians(endLat);

        double a = harvesin(dLat) + Math.cos(startLat) * Math.cos(endLat) * harvesin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS * c;


    }

    private static double harvesin(double val){
        return Math.pow(Math.sin(val / 2), 2);
    }
}
