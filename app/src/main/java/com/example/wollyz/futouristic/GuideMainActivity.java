package com.example.wollyz.futouristic;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

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
    private Intent formIntent;
    private String status;


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
        formIntent = new Intent(this, CreateTourGroupActivity.class);
        tourLandmark = "";
        status = "";
        client.getSavedSelection(username);

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
                        if(status != "available"){
                            client.setGroupToAvailable(username,tourLandmark);
                        }

                    }

                }
                else {
                    if (tourLandmark != EMPTY_STRING) {
                        alertUser();

                    }

                }
            }
        });

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


    @Subscribe
    public void onGetAllAttractionsEvent(AttractionsReceivedEvent serverEvent){
        String landmarkName = "";
        allLandmarks = serverEvent.getAttraction();

        for(int i = 0; i < allLandmarks.size(); i++) {
            landmarkName = allLandmarks.get(i).getName();
            landmarks.add(landmarkName);
        }
        /*
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
                        client.setGroupToAvailable(username,tourLandmark);

                    }

                }
                else
                {
                    if(tourLandmark != EMPTY_STRING){
                        alertUser();

                    }

                }


            }

        });

        selBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Bundle extras_list = new Bundle();
                    extras_list.putStringArrayList("LANDMARKS", landmarks);
                    listIntent.putExtras(extras_list);

                startActivityForResult(listIntent,REQUEST_CODE_LIST);
            }
        });
        */


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
    public void onPostResponseEvent(ResponseEvent responseEvent){

        groupCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chosenLandmarks.size() > 0 && chosenLandmarksIndex.size() > 0){
                    Bundle extras = new Bundle();
                    extras.putString("USERNAME", username);
                    extras.putStringArrayList("CHOSEN_LANDMARKS", chosenLandmarks);
                    formIntent.putExtras(extras);
                    startActivityForResult(formIntent,REQUEST_CODE_FORM);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please first select landmarks", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void alertUser(){
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
    @Subscribe
    public void onGetSavedStateEvent(GuideSavedStateEvent event){
        GuideSavedState guideSavedState = event.getSavedState();
        chosenLandmarks = (ArrayList<String>) guideSavedState.getGuidesList();
        tourLandmark = guideSavedState.getTour();
        status = guideSavedState.getStatus();
        client.getAttractions();
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
}
