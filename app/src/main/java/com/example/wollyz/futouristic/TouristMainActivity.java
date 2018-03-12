package com.example.wollyz.futouristic;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TouristMainActivity extends AppCompatActivity {
    private ApiClient client;
    public List<Attractions> attractions;
    private NearbyAttraction nearby;
    private LandmarksNearbyHandler locHandler;
    private static final int REQUIRED_AMT = 3;
    private ArrayList<String> notify_landmarks;
    private String username;
    private String touristUser;
    private final String EMPTY_STRING = "";
    private NotificationUtils notificationUtils;
    private EditText totalPeople;
    private Button viewMapBtn;
    private int total_people;
    private Attractions selectedAttraction;
    private String guide_username;
    private List<TourNearby> availableTours;
    private SwitchCompat findTour;
    private boolean alreadyStartedService;
    private static final int LOCATION_PERMISSION = 3;
    private boolean hasLastLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_main);
        Toast.makeText(this, "this is tourist main page", Toast.LENGTH_SHORT).show();
        username = getIntent().getExtras().getString("USERNAME");
        touristUser = getIntent().getExtras().getString("TOURIST_USER");
        totalPeople = (EditText) findViewById(R.id.total_people);
        findTour = (SwitchCompat) findViewById(R.id.findTour);
        viewMapBtn = (Button)findViewById(R.id.mapBtn);
        nearby = new NearbyAttraction();
        alreadyStartedService = false;
        notify_landmarks = new ArrayList<String>();
        notificationUtils = new NotificationUtils(this);
        hasLastLocation = false;
        client = new ApiClient(this);
        client.getAttractions();
        selectedAttraction = new Attractions();
        guide_username = EMPTY_STRING;
        availableTours = new ArrayList<TourNearby>();
        viewMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(view.getContext(), MapsActivity.class);
                if(NotifyTouristActivity.attraction!=null && NotifyTouristActivity.guide_username!=null){
                    selectedAttraction = NotifyTouristActivity.attraction;
                    guide_username = NotifyTouristActivity.guide_username;
                }
                Bundle extra = new Bundle();
                extra.putSerializable("SELECTED_LANDMARK",selectedAttraction);
                extra.putString("GUIDE",guide_username);
                mapIntent.putExtras(extra);
                startActivity(mapIntent);

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onStop(){
        stopService(new Intent(this,LocationService.class));
        alreadyStartedService = false;
        super.onStop();
    }

    public void checkGooglePlayService(){
        if(isGooglePlayServicesAvailable()){
            if(checkLocationPermission())
            {
                startLocationService();
            }
            else
            {
                Log.d("Permission", "Requesting Location Permission");
                requestLocationPermission();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"no service available",Toast.LENGTH_LONG).show();
        }
    }

    private void callBroadcastManager(){
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        double lat = intent.getDoubleExtra(LocationService.LATITUDE,0);
                        double lng = intent.getDoubleExtra(LocationService.LONGITUDE,0);
                        findNearestAttractions(lat,lng);
                        Toast.makeText(getApplicationContext(),"location: "+lat+","+lng,Toast.LENGTH_LONG).show();

                    }
                },new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );
    }

    @Subscribe
    public void onGetAllAttractionsEvent(AttractionsReceivedEvent serverEvent){
        attractions = serverEvent.getAttraction();
        locHandler = new LandmarksNearbyHandler(attractions);
        callBroadcastManager();
        findTour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    if(totalPeople.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please enter total number of people", Toast.LENGTH_SHORT).show();
                        compoundButton.toggle();
                    }
                    else {
                        total_people = Integer.parseInt(totalPeople.getText().toString());
                        checkGooglePlayService();

                    }

                }
                else {
                    /*
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    Toast.makeText(getApplication(), "Please turn off GPS ", Toast.LENGTH_SHORT).show();
                    */
                }
            }
        });

    }

    public void findNearestAttractions(double userLat,double userLong){
        nearby = locHandler.getNearestAttractions(userLat,userLong);
        if(nearby!= null) {
            nearby.setUsername(username);
            client.postLandmarksNearTourist(nearby);
        }
    }


    @Subscribe
    public void onPostNearbyAttractionsEvent(ResponseEvent serverEvent){

        String message = serverEvent.getResponseMessage();
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        client.checkTouristStatus(username); //check if tourist is available *NEW

    }

    @Subscribe
    public void onGetTouristStatusEvent(TouristStatusEvent serverEvent){
        TouristStatus touristStatus = serverEvent.getTouristStatus();
        if(touristStatus.getStatus().equals("available")){
            client.getToursNearby(nearby.getAttractions(),total_people);
        }
    }

    @Subscribe
    public void onGetToursNearbyEvent(TourNearbyEvent event){
        int error_check = 0; //checks if they were tours sent back from server
        List<TourNearby> returnedTours = event.getToursNearby();
        String tour;
        if(returnedTours != null){

            for(int i = 0; i < returnedTours.size(); i++){
                tour = returnedTours.get(i).getLandmark();
                if(tour.equals(EMPTY_STRING)){
                    error_check += 1;
                }
                else {
                    availableTours.add(returnedTours.get(i));
                }
            }


            if(error_check != returnedTours.size()){
                notifyLandmarkToTourist();
            }
        }


    }

    @Subscribe
    public void onErrorEvent(ErrorEvent errorEvent){
        Toast.makeText(this,""+errorEvent.getErrorMsg(),Toast.LENGTH_SHORT).show();

    }

    public boolean isGooglePlayServicesAvailable(){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(status != ConnectionResult.SUCCESS){
            if(googleApiAvailability.isUserResolvableError(status)){
                googleApiAvailability.getErrorDialog(this,status,2404).show();
            }
            return false;
        }
        Log.d("Permission", "Google Service Available");
        return true;
    }

    private boolean checkLocationPermission(){
        int finePermissionChk = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermissionChk = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        if(finePermissionChk != PackageManager.PERMISSION_GRANTED && coarsePermissionChk != PackageManager.PERMISSION_GRANTED ) {
            return false;
        }
        Log.d("Permission", "Location permission granted");
        return true;

    }

    private void requestLocationPermission(){
        Log.d("Permission", "Calling Permission");
        boolean provideRationale1 = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        boolean provideRationale2 =  ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        /*
        if(provideRationale1 || provideRationale2){

            createDialog(R.string.permission_rationale);
        }
        */
        ActivityCompat.requestPermissions(TouristMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode){
            case LOCATION_PERMISSION:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "Starting tracking service");
                    startLocationService();
                } else {

                }
            }
        }

    }


    private void createDialog(final int textStringId) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        ActivityCompat.requestPermissions(TouristMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
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

    private void startLocationService(){
        if(!alreadyStartedService){
            LocationService.setActivityContext(this);
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
            alreadyStartedService = true;
        }
    }

    public void notifyLandmarkToTourist() {
        NotificationCompat.Builder builder;
        int notificationID = 0;
        String title = "Tour Notification";
        String text = availableTours.size() + " tour(s) available near you";
        builder = notificationUtils.createNotification(title, text);
        Intent resultIntent = new Intent(this, NotifyTouristActivity.class);

        Bundle extras = new Bundle();
        for(int i = 0; i < availableTours.size(); i++) {
            extras.putSerializable("TOUR " + (i + 1), availableTours.get(i));
        }
        extras.putInt("TOTAL_PEOPLE",total_people);
        extras.putInt("NO_TOUR", availableTours.size());
        extras.putString("TOURIST_USERNAME",username);
        extras.putSerializable("ALL_LANDMARKS",(Serializable)attractions);
        resultIntent.putExtras(extras);

        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //stackBuilder.addParentStack(NotifyLandmarksActivity.class);
        //stackBuilder.addNextIntent(resultIntent);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        notificationUtils.notifyTourist(notificationID, builder);

    }

    //getting landmark tourist is interested in



}
