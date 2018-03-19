package com.example.wollyz.futouristic;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class NotifyTouristActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TouristSwipeAdapter swipeAdapter;
    private ArrayList<TourNearby> availableTours;
    private TourNearby selected;
    private TouristInterest touristInterest;
    private int size;
    private ApiClient client;
    private int total_people;
    private String tourist_username;
    private ArrayList<Attractions> allLandmarks;
    public static Attractions attraction;
    public static String guide_username;
    private NotificationUtils notificationUtils;





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
        notificationUtils = new NotificationUtils(this);
        attraction = new Attractions();
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
        selected = event.getSelectedTour();
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
        StringTokenizer time = new StringTokenizer(selected.getStartTime(),":");
        String time_hr = time.nextToken();
        String time_min = time.nextToken();
        String time_sec = time.nextToken();

        int hr = Integer.parseInt(time_hr)* 60 * 60 * 1000;
        int min = Integer.parseInt(time_min) * 60 * 1000;
        int sec = Integer.parseInt(time_sec) * 1000;

        //start time in milliseconds
        int startTime = hr + min + sec;

        //remind 10 mins before tour starts
        int notifyTime = startTime - (10 * 60 * 1000);

        scheduleReminder(50000);




        //finish();
    }

    private int getIndex(String landmark){
        for(int i =0; i < allLandmarks.size();i++){
            if(landmark.matches(allLandmarks.get(i).getName()));
            return i;
        }
        return -1;
    }

    private void scheduleReminder(int delay){
        NotificationCompat.Builder builder;
        Notification notification;

        String title = "Tour Notification";
        String content = "Reminder tour will start soon. Please make your way there";

        builder = notificationUtils.createNotificationBuilder(title,content);
        notification = notificationUtils.buildNotification(builder);
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);

        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,futureInMillis,pendingIntent);
        finish();

    }



}