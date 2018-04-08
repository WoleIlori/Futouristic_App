package com.example.wollyz.futouristic;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    private static final int INTERVAL = 120000;
    private static final int FASTEST_INTERVAL = 90000;
    public static final String ACTION_LOCATION_BROADCAST = LocationService.class.getName()+"LocationBroadcast";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    private GoogleApiClient locationClient;
    private LocationRequest locationRequest;
    private static final int LOCATION_PERMISSION = 5;

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        locationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest()
                .setInterval(INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationClient.connect();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onConnected(Bundle dataBundle){
        Log.d("Location Tracker", "Service Connected");
        int finePermissionChk = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermissionChk = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        if(finePermissionChk != PackageManager.PERMISSION_GRANTED && coarsePermissionChk != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        Log.d("Location Tracker", "Enable tracker");
        LocationServices.FusedLocationApi.requestLocationUpdates(locationClient,locationRequest,this);
    }

    /*
    private void createDialog(final int textStringId) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        ActivityCompat.requestPermissions(activityContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
                        dialogInterface.dismiss();
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE: {
                        dialogInterface.dismiss();
                        break;
                    }
                }

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(textStringId).setPositiveButton("Yes",dialogClickListener)
                .setNegativeButton("No",dialogClickListener);
        AlertDialog alert = builder.create();
        alert.show();
    }
    */

    @Override
    public void onConnectionSuspended(int i){
    }

    @Override
    public void onDestroy(){
        LocationServices.FusedLocationApi.removeLocationUpdates(locationClient,this);
        Log.d("TRACKING","Location tracking stopped...");
        super.onDestroy();
    }


    @Override
    public void onLocationChanged(Location location){
        if(location != null){
            sendMessageToUI(location.getLatitude(), location.getLongitude());
        }
    }

    private void sendMessageToUI(double lat, double lng){
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(LATITUDE,lat);
        intent.putExtra(LONGITUDE,lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){

    }

    /*
    public static void setActivityContext(Activity activtyContext){
        activityContext = activtyContext;
    }
    */

}