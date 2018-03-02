package com.example.wollyz.futouristic;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ApiClient client;
    private List<Attractions> attractions;
    private NearbyAttraction nearby;
    private LandmarksNearbyHandler locHandler;
    private static final int REQUIRED_AMT = 2;
    public static final String username = "jbyrne";
    private NotificationUtils notificationUtils;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        gpsHandler = new UserLocationHandler(this);
        notify_landmarks = new ArrayList<String>();
        notificationUtils = new NotificationUtils(this);
        locHandler = new LandmarksNearbyHandler(53.3428,-6.2980);
        client = new ApiClient(this);
        client.getAttractions();
        */
        Button loginBtn = (Button)findViewById(R.id.btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });



    }



}
