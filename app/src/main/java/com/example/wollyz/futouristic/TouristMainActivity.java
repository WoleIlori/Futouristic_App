package com.example.wollyz.futouristic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class TouristMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_main);
        Toast.makeText(this, "this is tourist main page", Toast.LENGTH_SHORT).show();
    }
}
