package com.example.wollyz.futouristic;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wollyz.futouristic.RestApiPOJO.TourGroup;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class CreateTourGroupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Spinner landmark_spinner;
    private ArrayList<String> chosenLandmarks;
    private String username;
    private TourGroup group;
    private EditText priceEditText;
    private EditText sizeEditText;
    private EditText intervalEditText; //each time a tour ad is available this will be used to calculate the start time
    private EditText summaryEditText;
    private TextView displaySummary;
    private int summaryInput;
    private final int MAX_SUMMARY = 3;
    private Button createBtn;
    private Button addSummaryBtn;
    private ApiClient client;
    private ArrayList<String> tourSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour_group);
        landmark_spinner = (Spinner) findViewById(R.id.landmark_drop_down);
        priceEditText = (EditText) findViewById(R.id.price_editText);
        sizeEditText = (EditText) findViewById(R.id.size_editText);
        intervalEditText = (EditText) findViewById(R.id.interval_editText);
        summaryEditText = (EditText) findViewById(R.id.route_editText);
        createBtn = (Button) findViewById(R.id.createGroupBtn);
        addSummaryBtn = (Button)findViewById(R.id.addRouteBtn);
        displaySummary = (TextView)findViewById(R.id.feedbackTv);
        summaryInput = 0;
        tourSummary = new ArrayList<String>();
        Bundle bundle = getIntent().getExtras();
        chosenLandmarks = bundle.getStringArrayList("CHOSEN_LANDMARKS");
        username = bundle.getString("USERNAME");
        group = new TourGroup();
        client = new ApiClient(this);
        addLandmarksOnSpinner();
        landmark_spinner.setOnItemSelectedListener(this);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(formError() == 0)
                {
                    try{
                        group.setUsername(username);
                        int groupNum = Integer.parseInt(sizeEditText.getText().toString());
                        float price = Float.parseFloat(priceEditText.getText().toString());
                        int interval = Integer.parseInt(intervalEditText.getText().toString());
                        group.setGroupSize(groupNum);
                        group.setPrice(price);
                        group.setInterval(interval);
                        group.setSummary(tourSummary);
                        client.createGuideTourGroup(group);
                    } catch(NumberFormatException e){
                        e.printStackTrace();
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(), "Please complete form before submitting", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(summaryInput <= MAX_SUMMARY && !summaryEditText.getText().toString().isEmpty()){
                    tourSummary.add(summaryEditText.getText().toString());
                    summaryInput++;
                    displaySummary.setText("Added summary, " + (MAX_SUMMARY - summaryInput) + " remaining");
                    summaryEditText.setText("");
                }
                else
                {
                    displaySummary.setText("Summary filled");
                }


            }
        });
    }

    public void addLandmarksOnSpinner(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, chosenLandmarks);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        landmark_spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        group.setLandmark(parent.getItemAtPosition(pos).toString());
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
    public void onNothingSelected(AdapterView<?> parent){

    }

    public int formError(){
        int errorCounter = 0;
        if(priceEditText.getText().toString().isEmpty())
        {
            errorCounter++;
        }
        if(sizeEditText.getText().toString().isEmpty())
        {
            errorCounter++;
        }
        if(intervalEditText.getText().toString().isEmpty()){
            errorCounter++;
        }
        if(tourSummary.size() < MAX_SUMMARY){
            errorCounter++;
        }

        return errorCounter;
    }

    @Subscribe
    public void onCreateGroupEvent(ResponseEvent event){
        if(event.getResponseMessage().equals("inserted")) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("TOUR_LANDMARK", group.getLandmark());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }
}
