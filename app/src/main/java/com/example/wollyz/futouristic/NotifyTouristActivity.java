package com.example.wollyz.futouristic;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class NotifyTouristActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SwipeAdapter swipeAdapter;
    private ArrayList<String> landmarksToNotify;
    private ApiClient client;
    private TouristInterest touristInterest;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_tourist);
        client = new ApiClient(this);
        touristInterest = new TouristInterest();
        landmarksToNotify = new ArrayList<String>();
        Bundle var = getIntent().getExtras();
        landmarksToNotify = var.getStringArrayList("list");
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        swipeAdapter = new SwipeAdapter(this,landmarksToNotify);
        viewPager.setAdapter(swipeAdapter);


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
    public void onLandmarkSelectedEvent(UserInterestEvent userInterest){
        String landmarkChoice = userInterest.getSelectedLandmark();
        touristInterest.setUsername(MainActivity.username);
        touristInterest.setLandmark(landmarkChoice);
        client.postTouristLandmarkChoice(touristInterest);

    }

    @Subscribe
    public void onPostTouristChoiceEvent(ResponseEvent serverEvent){
        String message = serverEvent.getResponseMessage() + ": Tourist interest saved";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }



}
