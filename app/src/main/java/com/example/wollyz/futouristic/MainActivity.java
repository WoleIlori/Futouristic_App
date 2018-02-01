package com.example.wollyz.futouristic;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
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
    private ArrayList<String> notify_landmarks;
    private String username;
    private NotificationUtils notificationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gpsHandler = new UserLocationHandler(this);
        notify_landmarks = new ArrayList<String>();
        amtTouristList = new ArrayList<AmtTouristNearby>();
        notificationUtils = new NotificationUtils(this);
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
        client.getAmtTouristsNearby(username);

    }

    @Subscribe
    public void onGetEvent(TouristAmtEvent serverEvent){
        AmtTouristNearby amtTourist;
        amtTouristList = serverEvent.getAmtTourist();
        for(int i = 0; i<amtTouristList.size(); i++){
            amtTourist = amtTouristList.get(i);
            if(amtTourist.getTotal() >= REQUIRED_AMT){
                notify_landmarks.add(amtTourist.getName());
            }
        }

        if(notify_landmarks.size() > 0){
            notifyLandmarkToTourist();
        }

    }

    public void notifyLandmarkToTourist() {
        NotificationCompat.Builder builder;
        int notificationID = 0;
        String title = "Tour Notification";
        String text = notify_landmarks.size() + " landmarks available for a tour";
        builder = notificationUtils.createNotification(title, text);
        Intent resultIntent = new Intent(this, NotifyTouristActivity.class);
        resultIntent.putStringArrayListExtra("list", notify_landmarks);

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


}
