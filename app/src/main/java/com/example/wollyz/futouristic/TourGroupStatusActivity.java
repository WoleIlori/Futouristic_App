package com.example.wollyz.futouristic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wollyz.futouristic.RestApiPOJO.TourGroupStatus;

import org.greenrobot.eventbus.Subscribe;

public class TourGroupStatusActivity extends AppCompatActivity {
    private TextView startTimeTv;
    private TextView totalJoinedTv;
    private TextView attractionTv;
    private Button startTourBtn;
    private Button endTourBtn;
    private TourGroupStatus groupStatus;
    private ApiClient apiClient;
    private final String EMPTY_STRING = "";
    private String guide;
    private boolean tourStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_group_status);
        guide = getIntent().getStringExtra("GUIDE_1");
        groupStatus = (TourGroupStatus)getIntent().getSerializableExtra("GROUP_STATUS");
        startTimeTv = (TextView)findViewById(R.id.startTimeTv);
        totalJoinedTv = (TextView)findViewById(R.id.totalJoinedTv);
        attractionTv = (TextView)findViewById(R.id.attractonTv);
        startTourBtn = (Button)findViewById(R.id.startTourBtn);
        endTourBtn = (Button)findViewById(R.id.endTourBtn);
        apiClient = new ApiClient(this);
        attractionTv.setText(groupStatus.getLandmark());
        startTimeTv.setText(groupStatus.getStartTime());
        totalJoinedTv.setText(groupStatus.getNoJoined().toString());
        tourStarted = false;


        startTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(groupStatus!=null){
                    if(groupStatus.getLandmark()!= EMPTY_STRING){
                        apiClient.setGroupToUnavailable(guide,groupStatus.getLandmark());
                        Log.d("TOUR","Closing Tour entries");
                    }

                }

            }
        });

        endTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tourStarted){
                    apiClient.endTour(guide,groupStatus.getLandmark());
                    Log.d("TOUR","Tour entries now opened");
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
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onStartTourEvent(ResponseEvent serverEvent){
        if(serverEvent.getResponseMessage().matches("Updated")){
            Toast.makeText(getApplicationContext(), "Tour has started", Toast.LENGTH_SHORT).show();
            tourStarted = true;
        }
    }

    @Subscribe
    public void onEndTourEvent(ResponseEvent event){
        if(event.getResponseMessage().matches("ended")){
            Toast.makeText(getApplicationContext(), "Tour has ended", Toast.LENGTH_SHORT).show();
        }
    }
}
