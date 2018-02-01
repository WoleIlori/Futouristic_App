package com.example.wollyz.futouristic;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class Harvesine {

    public static double calculateDist(double startLat,double startLong, Attractions attraction){
        //Reference: https://github.com/jasonwinn/haversine/blob/master/Haversine.java
        int earth_radius = 6371;
        double endLat = attraction.getLatitude();
        double endLong = attraction.getLongitude();


        double dLat = Math.toRadians((endLat - startLat)); //difference between latitude
        double dLong = Math.toRadians((endLong - startLong)); //difference between longitude

        startLat = Math.toRadians(startLat);
        endLat  = Math.toRadians(endLat);

        double a = harvesin(dLat) + Math.cos(startLat) * Math.cos(endLat) * harvesin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earth_radius * c;


    }

    private static double harvesin(double val){
        return Math.pow(Math.sin(val / 2), 2);
    }
}
