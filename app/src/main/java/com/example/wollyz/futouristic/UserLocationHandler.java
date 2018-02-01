package com.example.wollyz.futouristic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class UserLocationHandler  implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {
        private int MIN_DISTANCE = 0;
        private int MIN_TIME = 500;
        private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;
        private static final int PERMISSION_ACCESS_FINE_LOCATION= 0;
        private LocationManager locationManager;
        private Context context;
        private Location loc;

        UserLocationHandler(Context c)// List<Attractions> a
        {
            context = c;
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            //useGpsService();
        }

    public void getUserLocation(){
        useGpsService();
        
    }

        /*
        public double getUserLatitude(){
            if(loc!=null){
                return loc.getLatitude();
            }
            else{
                return 0;
            }
        }

        public double getUserLongitude()
        {
            if(loc!=null){
                return loc.getLongitude();
            }
            else
            {
                return 0;
            }
        }
        */

        public void useGpsService()
        {

                if( ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)  == PackageManager.PERMISSION_GRANTED)
                {
                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME, MIN_DISTANCE,this);
                    if((loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)) == null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME, MIN_DISTANCE,this);
                    }




                }
                else
                {
                    requestLocationPermission();
                }
            }

            public void requestLocationPermission()
            {

                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_ACCESS_COARSE_LOCATION);

            }

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
            {

                if(requestCode == PERMISSION_ACCESS_FINE_LOCATION) {
                    if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onLocationChanged(Location location)
            {
                //handler.getNearestAttraction(location, attractions);
                String msg ="Current location is " + loc.getLatitude() + ", " + loc.getLongitude();
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(String provider)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
                Toast.makeText(context, "GPS is turned off ", Toast.LENGTH_SHORT).show();

            }

            public void onProviderEnabled(String provider)
            {
                Toast.makeText(context, "GPS is turned on", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras){
            }
}
