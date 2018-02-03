package com.example.wollyz.futouristic;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class GuideMainActivity extends AppCompatActivity {
    private UserLocationHandler gpsHandler;
    private ApiClient client;
    private ArrayList<String> landmarks;
    private List<Attractions> allLandmarks;
    private String username;
    private Button selBtn;
    private final int INTEGER_VALUE = 1;
    private List<String> chosenLandmarks;
    private int[] chosenLandmarksIndex;
    private List<Double> landmarkDist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_main);
        Toast.makeText(this, "this is guide main page", Toast.LENGTH_SHORT).show();
        //gpsHandler = new UserLocationHandler(this);
        client = new ApiClient(this);
        client.getAttractions();
        username = getIntent().getExtras().getString("username");
        selBtn = (Button)findViewById(R.id.guide_selection);
        landmarks = new ArrayList<String>();
        chosenLandmarks = new ArrayList<String>();
        chosenLandmarksIndex = new int[3];
        landmarkDist = new ArrayList<Double>();

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

        //change
        final Intent guideIntent = new Intent(this, GuideLandmarkSelectionActivity.class);
        Bundle extras = new Bundle();
        extras.putStringArrayList("LANDMARKS",landmarks);
        guideIntent.putExtras(extras);
        //extras.putString("USERNAME", username);
        selBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(guideIntent,INTEGER_VALUE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);
        switch (requestCode){
            case (INTEGER_VALUE):{
                if(resultCode == Activity.RESULT_OK){
                    Bundle extras = new Bundle();
                    extras = intent.getExtras();
                    chosenLandmarks = extras.getStringArrayList("CHOSEN_LANDMARKS");
                    chosenLandmarksIndex = extras.getIntArray("INDEX");
                    calculateDistFromChosenLandmarks(53.3458,-6.2789);



                }
            }
        }

    }


    public void calculateDistFromChosenLandmarks(double guideLat, double guideLong){
        double dist;
        int index;
        double landmarkLat;
        double landmarkLong;
        for(int i = 0; i < chosenLandmarks.size(); i++){
            index = chosenLandmarksIndex[i];
            landmarkLat = allLandmarks.get(index).getLatitude();
            landmarkLong = allLandmarks.get(index).getLongitude();
            dist = Harvesine.calculateDist(guideLat, guideLong, landmarkLat, landmarkLong);
            landmarkDist.add(dist);
        }

        if(landmarkDist.size() == chosenLandmarks.size()){
            GuideSelection guideSel = new GuideSelection();
            guideSel.setUsername(username);
            guideSel.setLandmarks(chosenLandmarks);
            guideSel.setDistances(landmarkDist);
            client.postGuideLandmarkSelection(guideSel);
        }
    }

    @Subscribe
    public void onPostResponseEvent(ResponseEvent responseEvent){
        Toast.makeText(this, responseEvent.getResponseMessage(), Toast.LENGTH_SHORT).show();
    }
}
