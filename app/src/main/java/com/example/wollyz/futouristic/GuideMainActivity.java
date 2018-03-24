package com.example.wollyz.futouristic;

import android.Manifest;
import android.app.Activity;
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
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//THINGS TO DO
//1.WHEN A GUIDE LOGS IN RETURN LANDMARKS HE HAS PREVIOULSY SELECTED
//2.GUIDE CAN EDIT WHICH CREATED TOUR GROUP --NOT IMPORTANT
//3.IF GUIDE TOGGLE OFF AND HE HAS A GROUP TOUR CREATED SET GROUP STATUS TO UNAVAILABLE *

public class GuideMainActivity extends AppCompatActivity {
    private ApiClient client;
    private ArrayList<String> landmarks;
    private List<Attractions> allLandmarks;
    private String username;
    private Button selBtn;
    private final String EMPTY_STRING = "";
    private Button groupCreateBtn;
    private Button viewGroupBtn;
    private final int REQUEST_CODE_LIST = 1;
    private final int REQUEST_CODE_FORM = 2;
    private ArrayList<String> chosenLandmarks;
    private List<Integer> chosenLandmarksIndex;
    private List<Double> chosenLandmarkDist;
    private String guideUser;
    private SwitchCompat tourToggle;
    private String tourLandmark; //stores landmark guide decides to do
    private Intent listIntent;
    //private Intent formIntent;
    private String status;
    private boolean alreadyStartedService;
    private static final int LOCATION_PERMISSION = 5;
    private GuideLocation guideLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_main);
        Toast.makeText(this, "this is guide main page", Toast.LENGTH_SHORT).show();
        client = new ApiClient(this);
        username = getIntent().getExtras().getString("USERNAME");
        guideUser = getIntent().getExtras().getString("GUIDE_USER");
        selBtn = (Button)findViewById(R.id.guide_selection);
        groupCreateBtn = (Button) findViewById(R.id.createGroup);
        viewGroupBtn = (Button)findViewById(R.id.viewGroupBtn);
        tourToggle = (SwitchCompat) findViewById(R.id.tourToggle);
        landmarks = new ArrayList<String>();
        chosenLandmarks = new ArrayList<String>();
        chosenLandmarksIndex = new ArrayList<Integer>();
        chosenLandmarkDist = new ArrayList<Double>();
        listIntent = new Intent(this, GuideLandmarkSelectionActivity.class);
        //formIntent = new Intent(this, CreateTourGroupActivity.class);
        tourLandmark = EMPTY_STRING;
        status = EMPTY_STRING;
        alreadyStartedService = false;
        guideLocation = new GuideLocation();
        guideLocation.setUsername(username);
        callBroadcastManager();
        createWidgetListeners();
        client.getAttractions();



    }

    private void createWidgetListeners(){
        //Toggle Listeners
        tourToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //if Guide toggles but has not selected landmarks
                    if(tourLandmark == EMPTY_STRING){
                        Toast.makeText(getApplicationContext(), "Please select landmarks and create a tour group", Toast.LENGTH_SHORT).show();
                        compoundButton.toggle();
                    }
                    else
                    {

                        //set tour group in database to available
                        if(!status.matches("available")){
                            client.setGroupToAvailable(username,tourLandmark);
                        }

                    }

                }
                else {
                    if (tourLandmark != EMPTY_STRING) {
                        ToggleoffAlert();

                    }

                }
            }
        });

        //Select landmarks can do a tour on Listener
        selBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allLandmarks.size()>0){
                    Bundle extras_list = new Bundle();
                    extras_list.putStringArrayList("LANDMARKS", landmarks);
                    listIntent.putExtras(extras_list);
                    startActivityForResult(listIntent,REQUEST_CODE_LIST);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Retrieving all attractions", Toast.LENGTH_SHORT).show();
                }

            }
        });


        viewGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tourLandmark.matches(EMPTY_STRING)){
                    Toast.makeText(getApplicationContext(), "No tour group have been created", Toast.LENGTH_SHORT).show();
                }
                else {
                    //call api to retireve status
                    client.getGroupStatus(username,tourLandmark);
                }
            }
        });

        groupCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chosenLandmarks.size() > 0 && chosenLandmarksIndex.size() > 0 && tourLandmark.matches(EMPTY_STRING)) {
                    Intent formIntent = new Intent(view.getContext(), CreateTourGroupActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("USERNAME", username);
                    extras.putStringArrayList("CHOSEN_LANDMARKS", chosenLandmarks);
                    formIntent.putExtras(extras);
                    startActivityForResult(formIntent, REQUEST_CODE_FORM);

                }

                if(!tourLandmark.matches(EMPTY_STRING)){
                    //Toast.makeText(getApplicationContext(), "Please first select landmarks", Toast.LENGTH_SHORT).show();
                    ModifyTourGroupAlert();
                }



            }
        });


    }
    @Override
    public void onResume(){
        super.onResume();
        BusProvider.getInstance().register(this);
        checkGooglePlayService();
    }

    @Override
    public void onPause(){
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onDestroy(){
        stopService(new Intent(this,LocationService.class));
        alreadyStartedService = false;
        Log.d("DESTROY","Shutting down");
        super.onDestroy();
    }


    @Subscribe
    public void onGetAllAttractionsEvent(AttractionsReceivedEvent serverEvent){
        String landmarkName = "";
        allLandmarks = serverEvent.getAttraction();

        for(int i = 0; i < allLandmarks.size(); i++) {
            landmarkName = allLandmarks.get(i).getName();
            landmarks.add(landmarkName);
        }
        client.getGuideSavedSelection(username);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        //super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case(REQUEST_CODE_LIST):{
                if(resultCode == Activity.RESULT_OK){
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        chosenLandmarks = extras.getStringArrayList("CHOSEN_LANDMARKS");
                        chosenLandmarksIndex = extras.getIntegerArrayList("INDEX");
                        calculateDistFromChosenLandmarks(53.3458,-6.2789);
                    }

                }
                break;
            }

            case (REQUEST_CODE_FORM):{
                if(resultCode == Activity.RESULT_OK){
                    tourLandmark = data.getStringExtra("TOUR_LANDMARK");

                }
                break;
            }
        }

    }


    public void calculateDistFromChosenLandmarks(double guideLat, double guideLong){
        double dist;
        int index;
        double landmarkLat;
        double landmarkLong;
        for(int i = 0; i < chosenLandmarks.size(); i++){
            index = chosenLandmarksIndex.get(i);
            landmarkLat = allLandmarks.get(index).getLatitude();
            landmarkLong = allLandmarks.get(index).getLongitude();
            dist = Harvesine.calculateDist(guideLat, guideLong, landmarkLat, landmarkLong);
            chosenLandmarkDist.add(dist);
        }

        if(chosenLandmarkDist.size() == chosenLandmarks.size()){
            GuideSelection guideSel = new GuideSelection();
            guideSel.setUsername(username);
            guideSel.setLandmarks(chosenLandmarks);
            guideSel.setDistances(chosenLandmarkDist);
            client.postGuideLandmarkSelection(guideSel);
        }
    }


    @Subscribe
    public void onCreateGroupEvent(ResponseEvent responseEvent){

        if(responseEvent.getResponseMessage().matches("inserted"));{
            Toast.makeText(getApplicationContext(), "Tour Group Created", Toast.LENGTH_SHORT).show();
        }


    }

    @Subscribe
    public void onAddGuideLocationEvent(ResponseEvent responseEvent){
        if(responseEvent.getResponseMessage().matches("success"));{
            Toast.makeText(getApplicationContext(), "Guide location added to database", Toast.LENGTH_SHORT).show();
        }
    }

    public void ToggleoffAlert(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:{
                        client.setGroupToUnavailable(username,tourLandmark);
                        dialogInterface.dismiss();
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE:{
                        dialogInterface.dismiss();
                        break;
                    }
                }

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure ? Start time for tour will change").setPositiveButton("Yes",dialogClickListener)
                .setNegativeButton("No",dialogClickListener);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void ModifyTourGroupAlert(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:{
                        Intent formIntent = new Intent(GuideMainActivity.this, CreateTourGroupActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("USERNAME", username);
                        extras.putStringArrayList("CHOSEN_LANDMARKS", chosenLandmarks);
                        formIntent.putExtras(extras);
                        startActivityForResult(formIntent,REQUEST_CODE_FORM);
                        dialogInterface.dismiss();
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE:{
                        dialogInterface.dismiss();
                        break;
                    }
                }

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure? Previous Tour Group will be removed").setPositiveButton("Yes",dialogClickListener)
                .setNegativeButton("No",dialogClickListener);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Subscribe
    public void onGetSavedStateEvent(GuideSavedStateEvent event){
        GuideSavedState guideSavedState = event.getSavedState();
        chosenLandmarks = (ArrayList<String>) guideSavedState.getGuidesList();
        tourLandmark = guideSavedState.getTour();
        status = guideSavedState.getStatus();
        if(tourLandmark.matches(EMPTY_STRING)){
            for(int i =0; i < chosenLandmarks.size(); i++){
                chosenLandmarksIndex.add(getChosenLandmarkIndex(chosenLandmarks.get(i)));
            }

        }

    }

    private int getChosenLandmarkIndex(String landmark){
        for(int i =0; i < allLandmarks.size();i++){
            if(landmark.matches(allLandmarks.get(i).getName()));
            return i;
        }
        return -1;
    }

    @Subscribe
    public  void onGetTourGroupStatusEvent(TourGroupStatusEvent event){
        TourGroupStatus groupStatus = event.getGroupStatus();
        Intent groupStatusIntent = new Intent(this, TourGroupStatusActivity.class);
        groupStatusIntent.putExtra("GROUP_STATUS", (Serializable)groupStatus);
        groupStatusIntent.putExtra("GUIDE_1",username);
        startActivity(groupStatusIntent);

    }

    @Subscribe
    public void onErrorEvent(ErrorEvent errorEvent){
        Toast.makeText(this,""+errorEvent.getErrorMsg(),Toast.LENGTH_SHORT).show();

    }

    private void callBroadcastManager(){
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        double lat = intent.getDoubleExtra(LocationService.LATITUDE,0);
                        double lng = intent.getDoubleExtra(LocationService.LONGITUDE,0);
                        Toast.makeText(getApplicationContext(),"Guide location:"+lat+","+lng,Toast.LENGTH_LONG).show();
                        guideLocation.setLatitude(lat);
                        guideLocation.setLongitude(lng);
                        client.insertGuideCurrentLocation(guideLocation);

                    }
                },new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );
    }

    public void checkGooglePlayService(){
        if(isGooglePlayServicesAvailable()){
            if(checkLocationPermission())
            {
                startLocationService();
            }
            else
            {
                Log.d("GUIDE_TRACKING", "Requesting Location Permission");
                requestLocationPermission();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"no service available",Toast.LENGTH_LONG).show();
        }
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
        Log.d("GUIDE_TRACKING", "Google Service Available");
        return true;
    }

    private boolean checkLocationPermission(){
        int finePermissionChk = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermissionChk = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        if(finePermissionChk != PackageManager.PERMISSION_GRANTED && coarsePermissionChk != PackageManager.PERMISSION_GRANTED ) {
            return false;
        }
        Log.d("GUIDE_TRACKING", "Location permission granted");
        return true;

    }

    private void requestLocationPermission(){
        Log.d("GUIDE_TRACKING", "Calling Permission");
        boolean provideRationale1 = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        boolean provideRationale2 =  ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        /*
        if(provideRationale1 || provideRationale2){

            createDialog(R.string.permission_rationale, "permission");
        }
        */
        ActivityCompat.requestPermissions(GuideMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode){
            case LOCATION_PERMISSION:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("GUIDE_TRACKING", "Starting tracking service");
                    startLocationService();
                } else {

                }
            }
        }

    }

    private void startLocationService(){
        if(!alreadyStartedService){
            //LocationService.setActivityContext(this);
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
            alreadyStartedService = true;
            Log.d("TRACKING","Location tracking enabled...");
        }
    }

}
