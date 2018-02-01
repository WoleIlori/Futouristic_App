package com.example.wollyz.futouristic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private UserLocationHandler gpsHandler;
    private ApiClient client;
    private List<Attractions> attractions;
    private NearbyAttraction nearby;
    private LandmarksNearbyHandler locHandler;
    private List<AmtTouristNearby> amtTouristList;
    private static final int REQUIRED_AMT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gpsHandler = new UserLocationHandler(this);
        amtTouristList = new ArrayList<AmtTouristNearby>();
        locHandler = new LandmarksNearbyHandler(53.3428,-6.2980);
        client = new ApiClient(this);
        client.getAttractions();


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
    public void onServerEvent(ServerEvent serverEvent){
        attractions = serverEvent.getAttraction();
        //tv.setText(attractions.get(0).getName());
        Toast.makeText(this,""+serverEvent.getServerMessage(),Toast.LENGTH_SHORT).show();
        nearby = locHandler.getNearestAttractions(attractions);
        if(nearby!= null) {
            client.postLandmarksNearTourist(nearby);

        }



    }
    @Subscribe
    public void onErrorEvent(ErrorEvent errorEvent){
        Toast.makeText(this,""+errorEvent.getErrorMsg(),Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onPostEvent(ResponseEvent serverEvent){
        String message = serverEvent.getResponseMessage();
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        //client.getAmtTouristsNearby(username);

    }

    @Subscribe
    public void onGetEvent(TouristAmtEvent serverEvent){
        AmtTouristNearby amtTourist;
        amtTouristList = serverEvent.getAmtTourist();

    }


}
