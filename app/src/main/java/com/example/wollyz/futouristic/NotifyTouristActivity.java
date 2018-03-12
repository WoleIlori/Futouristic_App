package com.example.wollyz.futouristic;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class NotifyTouristActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TouristSwipeAdapter swipeAdapter;
    private ArrayList<TourNearby> availableTours;
    private TourNearby selectedTour;
    private TouristInterest touristInterest;
    private int size;
    private ApiClient client;
    private int total_people;
    private String tourist_username;
    private ArrayList<Attractions> allLandmarks;
    public static Attractions attraction;
    public static String guide_username;





    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_tourist);
        client = new ApiClient(this);
        availableTours = new ArrayList<TourNearby>();
        touristInterest = new TouristInterest();
        size = getIntent().getIntExtra("NO_TOUR",0);
        allLandmarks = (ArrayList<Attractions>) getIntent().getSerializableExtra("ALL_LANDMARKS");
        tourist_username = getIntent().getStringExtra("TOURIST_USERNAME");
        total_people = getIntent().getIntExtra("TOTAL_PEOPLE", 0);
        for(int i = 0; i < size; i++){
            TourNearby g = (TourNearby)getIntent().getSerializableExtra("TOUR "+(i+1));
            availableTours.add(g);
        }

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        swipeAdapter = new TouristSwipeAdapter(this,availableTours);
        viewPager.setAdapter(swipeAdapter);


    }

    @Override
    public void onResume(){
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    /*
    @Override
    public void onPause(){
        super.onPause();

    }
    */

    @Override
    public void onStop(){
        BusProvider.getInstance().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onLandmarkSelectedEvent(TouristInterestEvent event){
        TourNearby selected = event.getSelectedTour();
        guide_username = selected.getGuideName();
        touristInterest.setGuideUsername(selected.getGuideName());
        touristInterest.setLandmark(selected.getLandmark());
        touristInterest.setTouristUsername(tourist_username);
        touristInterest.setTotalPeople(total_people);
        client.addTouristToTourGroup(touristInterest);

    }

    @Subscribe
    public void onPostTouristInterestEvent(ResponseEvent serverEvent){
        String message = serverEvent.getResponseMessage() + ": Tourist added to tour group";
        //gets its lat and long
        int tourIndex = getIndex(touristInterest.getLandmark());
        if(tourIndex >= 0){
            attraction = allLandmarks.get(tourIndex);
        }
        finish();
    }

    private int getIndex(String landmark){
        for(int i =0; i < allLandmarks.size();i++){
            if(landmark.matches(allLandmarks.get(i).getName()));
            return i;
        }
        return -1;
    }



}