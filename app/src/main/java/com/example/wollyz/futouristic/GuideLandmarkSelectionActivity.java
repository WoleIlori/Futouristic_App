package com.example.wollyz.futouristic;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class GuideLandmarkSelectionActivity extends AppCompatActivity {
    private ApiClient client;
    private ViewPager viewPager;
    private GuideSwipeAdapter swipeAdapter;
    private ArrayList<String> allLandmarks;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_landmark_selection);
        client = new ApiClient(this);
        allLandmarks = new ArrayList<String>();
        Bundle var = new Bundle();
        var = getIntent().getExtras();
        allLandmarks = var.getStringArrayList("LANDMARKS");
        //username = var.getString("USERNAME");
        viewPager = (ViewPager)findViewById(R.id.guide_view);
        swipeAdapter = new GuideSwipeAdapter(this,allLandmarks);
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
    public void onGuideSelectionEvent(GuideLandmarkChoiceEvent event){
        /*
        GuideSelection guideSel = new GuideSelection();
        guideSel.setUsername(username);
        guideSel.setLandmarks(event.getChosenLandmarks());
        client.postGuideLandmarkSelection(guideSel);
        */
        Intent resultIntent = new Intent();
        Bundle extras = new Bundle();
        extras.putStringArrayList("CHOSEN_LANDMARKS", event.getChosenLandmarks());
        extras.putIntArray("INDEX",event.getChosenLandmarksIndex());
        resultIntent.putExtras(extras);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();


    }


}
