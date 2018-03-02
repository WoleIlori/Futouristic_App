package com.example.wollyz.futouristic;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class GuideLandmarkSelectionActivity extends ListActivity {
    private ApiClient client;
    private ArrayList<String> allLandmarks;
    private String username;
    private ArrayList<String> chosenLandmarks;
    private ArrayList<Integer> chosenLandmarksIndex;


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
        chosenLandmarks = new ArrayList<String>();
        chosenLandmarksIndex = new ArrayList<Integer>();
        ListView lv = getListView();
        lv.setChoiceMode(lv.CHOICE_MODE_MULTIPLE);
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, allLandmarks));

    }


    public void onListItemClick(ListView parent, View v, int position, long id){
        CheckedTextView item = (CheckedTextView) v;
        if(item.isChecked()){
            chosenLandmarks.add(allLandmarks.get(position));
            chosenLandmarksIndex.add(position);
            if(limitReached() == true){
                Intent resultIntent =  new Intent();
                Bundle extras = new Bundle();
                extras.putStringArrayList("CHOSEN_LANDMARKS", chosenLandmarks);
                extras.putIntegerArrayList("INDEX", chosenLandmarksIndex);
                resultIntent.putExtras(extras);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
    }


    public boolean limitReached(){
        if(chosenLandmarks.size() == 3){
            return true;
        }
        else
        {
            return false;
        }
    }


}
