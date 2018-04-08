package com.example.wollyz.futouristic;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wollyz.futouristic.RestApiPOJO.Attractions;
import com.example.wollyz.futouristic.RestApiPOJO.NearbyAttraction;
import com.example.wollyz.futouristic.RestApiPOJO.TourNearby;
import com.example.wollyz.futouristic.RestApiPOJO.TouristSavedState;
import com.example.wollyz.futouristic.RestApiPOJO.TouristStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TouristMainActivity extends AppCompatActivity {
    private ApiClient client;
    public List<Attractions> attractions;
    private NearbyAttraction nearby;
    private LandmarksNearbyHandler locHandler;
    private String username;
    private final String EMPTY_STRING = "";
    private NotificationUtils notificationUtils;
    private EditText totalPeople;
    private Button viewMapBtn;
    private Button leaveTourBtn;
    private Button payTourBtn;
    private Button logoutBtn;
    private float tourPrice;
    private int total_people;
    private final int REQUEST_CODE_PAYMENT = 7;
    public static Attractions selectedAttraction;
    public static String guide_username;
    private List<TourNearby> availableTours;
    private SwitchCompat findTour;
    private boolean alreadyStartedService;
    private boolean paid;
    private static final int LOCATION_PERMISSION = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_main);
        Toast.makeText(this, "this is tourist main page", Toast.LENGTH_SHORT).show();
        username = getIntent().getExtras().getString("TOURIST_USERNAME");
        totalPeople = (EditText) findViewById(R.id.total_people);
        findTour = (SwitchCompat) findViewById(R.id.findTour);
        viewMapBtn = (Button)findViewById(R.id.mapBtn);
        leaveTourBtn = (Button)findViewById(R.id.leaveTourBtn);
        payTourBtn = (Button)findViewById(R.id.payTourBtn);
        logoutBtn = (Button)findViewById(R.id.touristLogoutBtn);
        nearby = new NearbyAttraction();
        alreadyStartedService = false;
        paid = false;
        notificationUtils = new NotificationUtils(this);
        client = new ApiClient(this);
        availableTours = new ArrayList<TourNearby>();
        callBroadcastManager();
        setListeners();
        client.getAttractions();


    }

    private void setListeners()
    {
        viewMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass selected attraction and guide if tourist has joined a tour
                Intent mapIntent = new Intent(view.getContext(), MapsActivity.class);
                if(selectedAttraction!=null && guide_username!=null) {

                    Bundle extra = new Bundle();
                    extra.putSerializable("SELECTED_LANDMARK", selectedAttraction);
                    extra.putString("GUIDE", guide_username);
                    mapIntent.putExtras(extra);

                }
                startActivity(mapIntent);

            }
        });


        leaveTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if tourist has already joined a tour, prompt to confirm action
                if(selectedAttraction!=null){
                    createDialog(R.string.alert,"Leave Tour");

                }
                else {
                    Toast.makeText(getApplicationContext(), "You have not joined a tour", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findTour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //checks if tourists has entered total number of accompanies, including theirselves
                if(isChecked) {
                    if(totalPeople.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please enter total number of people", Toast.LENGTH_SHORT).show();
                        compoundButton.toggle();
                    }
                    else {
                        //the app begins to monitor user location to find nearest tours
                        total_people = Integer.parseInt(totalPeople.getText().toString());
                        checkGooglePlayService();

                    }

                }
                else
                {
                    //when tourist toggles slider off, stop monitoring their location
                    stopService(new Intent(compoundButton.getContext(),LocationService.class));
                    alreadyStartedService = false;
                }
            }
        });

        payTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent paymentIntent = new Intent(view.getContext(), TourPaymentActivity.class);
                if(selectedAttraction != null && !paid)
                {

                    paymentIntent.putExtra("TOUR_AMT", tourPrice);
                    paymentIntent.putExtra("PAYER",username);
                    startActivityForResult(paymentIntent,REQUEST_CODE_PAYMENT);

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "You have not joined a tour", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!alreadyStartedService){
                    finishAffinity();
                    Intent intent = new Intent(view.getContext(),MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please toggle slider off first", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }


    //send notification to tourists to when payment is successfull
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case(REQUEST_CODE_PAYMENT):{
                if(resultCode == Activity.RESULT_OK){
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        int hasPaid = extras.getInt("PAY_STATUS");
                        String paymentDetails = extras.getString("PAY_MSG");
                        if(hasPaid == 1){
                            paid = true;
                            sendPaymentConfirmation(paymentDetails);
                        }

                    }

                }
                break;
            }
        }

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

    @Override
    public void onDestroy(){

        stopService(new Intent(this,LocationService.class));
        alreadyStartedService = false;
        super.onDestroy();
    }

    public void checkGooglePlayService(){
        if(isGooglePlayServicesAvailable()){
            if(checkLocationPermission())
            {
                startLocationService();
            }
            else
            {
                Log.d("Permission", "Requesting Location Permission");
                requestLocationPermission();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"no service available",Toast.LENGTH_LONG).show();
        }
    }

    private void callBroadcastManager(){
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        double lat = intent.getDoubleExtra(LocationService.LATITUDE,0);
                        double lng = intent.getDoubleExtra(LocationService.LONGITUDE,0);
                        findNearestAttractions(lat,lng);
                    }
                },new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );
    }

    //receiving all the attraction
    @Subscribe
    public void onGetAllAttractionsEvent(AttractionsReceivedEvent serverEvent){
        attractions = serverEvent.getAttraction();
        locHandler = new LandmarksNearbyHandler(attractions);
        Log.d("Attractions", "Received all attractions");
        client.getTouristSavedState(username);


    }

    @Subscribe
    public void onGetTouristSavedStateEvent(TouristSavedStateEvent event){
        TouristSavedState savedState = event.getTouristSavedState();
        String landmark = savedState.getAttraction();
        if(!landmark.matches(EMPTY_STRING)){
            int index = getLandmarkIndex(landmark);
            selectedAttraction = attractions.get(index);
            guide_username = savedState.getGuide();
            tourPrice = savedState.getPrice();
            if(savedState.getPaymentStatus().equals("Yes")){
                paid = true;
            }
        }

    }

    private int getLandmarkIndex(String landmark){
        for(int i =0; i < attractions.size();i++){
            if(landmark.matches(attractions.get(i).getName()));
            return i;
        }
        return -1;
    }
    public void findNearestAttractions(double userLat,double userLong){

        nearby = locHandler.getNearestAttractions(userLat,userLong);
        nearby.setUsername(username);
        client.postLandmarksNearTourist(nearby);
        Log.d("Nearby attractions","Calculated nearby attractions");

    }


    @Subscribe
    public void onPostNearbyAttractionsEvent(ResponseEvent serverEvent){

        String message = serverEvent.getResponseMessage();

        Log.d("ON","Checking tourist available....");
        //retrieve tourists' status from database, if they are available or not
        client.getTouristStatus(username);


    }

    @Subscribe
    public void onGetTouristStatusEvent(TouristStatusEvent serverEvent){
        TouristStatus touristStatus = serverEvent.getTouristStatus();
        //If they are available look for tours nearby
        if(touristStatus.getStatus().equals("available") && nearby != null){

            client.getToursNearby(nearby.getAttractions(),total_people);
        }
    }

    @Subscribe
    public void onGetToursNearbyEvent(TourNearbyEvent event){
        int error_check = 0;
        List<TourNearby> returnedTours = event.getToursNearby();
        String tour;

        //clear available tours array list
        availableTours.clear();

        //checks if they were non-empty tours returned from server
        if(returnedTours != null){

            for(int i = 0; i < returnedTours.size(); i++){
                tour = returnedTours.get(i).getLandmark();
                if(tour.equals(EMPTY_STRING)){
                    error_check += 1;
                }
                else {
                    availableTours.add(returnedTours.get(i));
                }
            }

            //if some tours are available notify to tourists
            if(error_check != returnedTours.size()){
                notifyLandmarkToTourist();
            }
        }


    }

    @Subscribe
    public void onErrorEvent(ErrorEvent errorEvent){
        Toast.makeText(this,""+errorEvent.getErrorMsg(),Toast.LENGTH_SHORT).show();

    }


    public boolean isGooglePlayServicesAvailable(){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(status != ConnectionResult.SUCCESS){
            if(googleApiAvailability.isUserResolvableError(status)){
                googleApiAvailability.getErrorDialog(this,status,2404).show();
            }
            return false;
        }
        Log.d("Permission", "Google Service Available");
        return true;
    }

    private boolean checkLocationPermission(){
        int finePermissionChk = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermissionChk = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        if(finePermissionChk != PackageManager.PERMISSION_GRANTED && coarsePermissionChk != PackageManager.PERMISSION_GRANTED ) {
            return false;
        }
        Log.d("Permission", "Location permission granted");
        return true;

    }

    private void requestLocationPermission(){
        Log.d("Permission", "Calling Permission");
        ActivityCompat.requestPermissions(TouristMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode){
            case LOCATION_PERMISSION:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "Starting tracking service");
                    startLocationService();
                } else {

                }
            }
        }

    }


    private void createDialog(final int textStringId,final String type) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        switch(type){
                            case "Permission":{
                                ActivityCompat.requestPermissions(TouristMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
                                break;
                            }
                            case "Leave Tour":{
                                client.removeTouristFromTour(username);
                                break;
                            }
                        }
                        dialogInterface.dismiss();
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE: {
                        dialogInterface.dismiss();
                        break;
                    }
                }

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(textStringId).setPositiveButton("Yes",dialogClickListener)
                .setNegativeButton("No",dialogClickListener);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startLocationService(){
        if(!alreadyStartedService){
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
            alreadyStartedService = true;
            Log.d("TRACKING","Location tracking enabled...");
        }
    }

    public void notifyLandmarkToTourist() {
        NotificationCompat.Builder builder;
        Notification notification;
        int notificationID = 0;
        String title = "Tour Notification";
        String text = availableTours.size() + " tour(s) available near you";
        builder = notificationUtils.createNotificationBuilder(title, text);
        Intent resultIntent = new Intent(this, NotifyTouristActivity.class);

        Bundle extras = new Bundle();
        for(int i = 0; i < availableTours.size(); i++) {
            extras.putSerializable("TOUR " + (i + 1), availableTours.get(i));
        }
        extras.putInt("TOTAL_PEOPLE",total_people);
        extras.putInt("NO_TOUR", availableTours.size());
        extras.putString("TOURIST_USERNAME",username);
        extras.putSerializable("ALL_LANDMARKS",(Serializable)attractions);
        resultIntent.putExtras(extras);

        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        notification = notificationUtils.buildNotification(builder);
        notificationUtils.notifyTourist(notificationID, notification);

    }

    private void sendPaymentConfirmation(String paymentInfo){
        NotificationCompat.Builder builder;
        Notification notification;
        int notificationID = 6;
        String title = "Payment Confirmed";
        String text = paymentInfo;
        builder = notificationUtils.createNotificationBuilder(title, text);
        notification = notificationUtils.buildNotification(builder);
        notificationUtils.notifyTourist(notificationID, notification);
    }


}
