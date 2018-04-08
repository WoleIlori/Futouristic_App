package com.example.wollyz.futouristic;

import android.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.wollyz.futouristic.MapsPOJO.Main;
import com.example.wollyz.futouristic.MapsPOJO.Route;
import com.example.wollyz.futouristic.MapsPOJO.RouteEvent;
import com.example.wollyz.futouristic.MapsPOJO.Step;
import com.example.wollyz.futouristic.RestApiPOJO.Attractions;
import com.example.wollyz.futouristic.RestApiPOJO.GuideLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST_CODE = 99;
    private boolean alreadyStartedService;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private Marker mTourLocationMarker;
    private Marker mGuideLocationMarker;
    private Attractions landmark;
    private GoogleMapsApiClient googleClient;
    private ApiClient apiClient;
    private com.google.android.gms.maps.model.Polyline tourPolyline;
    private String origin;
    private String destination;
    private String guide_username;
    private final String EMPTY_STRING = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        googleClient = new GoogleMapsApiClient();
        apiClient = new ApiClient(this);
        origin = EMPTY_STRING;
        destination = EMPTY_STRING;
        landmark = (Attractions) getIntent().getSerializableExtra("SELECTED_LANDMARK");
        guide_username = getIntent().getStringExtra("GUIDE");
        mLastLocation = new Location("");
        alreadyStartedService = false;
        callBroadcastManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.get_directions:{
                if(destination.matches(EMPTY_STRING)){
                    Toast.makeText(getApplicationContext(),"No tour selected",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    googleClient.getMapDirections(origin,destination);
                }
                return true;
            }
            case R.id.guide_location:{
                if(destination.matches(EMPTY_STRING)){
                    Toast.makeText(getApplicationContext(),"No tour selected",Toast.LENGTH_SHORT).show();
                }
                else{
                    apiClient.getGuideCurrentLocation(guide_username);
                }
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume(){
        super.onResume();
        BusProvider.getInstance().register(this);
    }


    @Override
    public void onStop(){
        stopService(new Intent(this,LocationService.class));
        alreadyStartedService = false;
        BusProvider.getInstance().unregister(this);
        super.onStop();
    }


    private void createLandmarkMarker(){

        if(mTourLocationMarker != null){
            mTourLocationMarker.remove();
        }
        destination = landmark.getLatitude()+","+landmark.getLongitude();
        if(!landmark.getName().matches(EMPTY_STRING)){
            LatLng latLng =new LatLng(landmark.getLatitude(),landmark.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title("Tour is here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mTourLocationMarker = mMap.addMarker(markerOptions);
        }

    }

    private void createGuideMarker(double lat, double lng){
        if (tourPolyline !=null){
            tourPolyline.remove();
        }

        if(mGuideLocationMarker!=null){
            mGuideLocationMarker.remove();
        }
        LatLng latLng =new LatLng(lat,lng);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Guide is here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mGuideLocationMarker = mMap.addMarker(markerOptions);

    }


    private void callBroadcastManager(){
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        double lat = intent.getDoubleExtra(LocationService.LATITUDE,0);
                        double lng = intent.getDoubleExtra(LocationService.LONGITUDE,0);
                        updateCurrentLocationMarker(lat,lng);
                        if(mTourLocationMarker == null && landmark!= null){
                            createLandmarkMarker();
                        }
                    }
                },new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );
    }

    private void updateCurrentLocationMarker(double lat, double lng){

        mLastLocation.setLatitude(lat);
        mLastLocation.setLongitude(lng);

        //remove current location marker
        if(mCurrLocationMarker!=null){
            mCurrLocationMarker.remove();
        }

        origin = lat+","+lng;
        LatLng latLng = new LatLng(lat,lng);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

    }


    @Subscribe
    public void onGetGuideLocationEvent(GuideLocationEvent server){
        GuideLocation guideLocation = server.getGuideLocation();
        createGuideMarker(guideLocation.getLatitude(),guideLocation.getLongitude());

    }


    @Subscribe
    public void onGetMapDirectionEvent(RouteEvent googleResponse){
        ArrayList<LatLng> routelist = new ArrayList<LatLng>();
        Main directionResults = googleResponse.getDirections();
        if(directionResults.getRoutes().size() > 0){
            List<LatLng> decodelist;
            Route routeA = directionResults.getRoutes().get(0);
            if(routeA.getLegs().size()>0){
                List<Step> steps = routeA.getLegs().get(0).getSteps();
                Step step;
                String polyline;
                for(int i=0; i < steps.size(); i++){
                    step = steps.get(i);
                    routelist.add(new LatLng(step.getStartLocation().getLat(),step.getStartLocation().getLng()));
                    polyline = step.getPolyline().getPoints();
                    decodelist = PolyUtil.decode(polyline);
                    routelist.addAll(decodelist);
                    routelist.add(new LatLng(step.getEndLocation().getLat(),step.getEndLocation().getLng()));
                }
            }
        }

        if(routelist.size() > 0){
            PolylineOptions rectline = new PolylineOptions().width(10).color(Color.RED);
            for(int i =0; i < routelist.size(); i++){
                rectline.add(routelist.get(i));
            }
            tourPolyline = mMap.addPolyline(rectline);
            createLandmarkMarker();
        }

        if(mGuideLocationMarker!=null){
            mGuideLocationMarker.remove();
        }
    }

    private void startLocationService(){
        if(alreadyStartedService == false){
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
            alreadyStartedService = true;
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkGooglePlayService();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkGooglePlayService();
                } else {

                }
            }
        }
    }

    public void checkGooglePlayService(){
        if(isGooglePlayServicesAvailable()){
            if(checkLocationPermission())
            {
                startLocationService();
                mMap.setMyLocationEnabled(true);

            }
            else
            {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Google play service is not available",Toast.LENGTH_LONG).show();
        }
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
        return true;
    }

    private boolean checkLocationPermission(){
        int finePermissionChk = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermissionChk = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        if(finePermissionChk != PackageManager.PERMISSION_GRANTED && coarsePermissionChk != PackageManager.PERMISSION_GRANTED ) {
            return false;
        }
        return true;

    }


}