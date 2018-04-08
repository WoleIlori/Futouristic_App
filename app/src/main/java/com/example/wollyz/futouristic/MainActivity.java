package com.example.wollyz.futouristic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.wollyz.futouristic.RestApiPOJO.Attractions;
import com.example.wollyz.futouristic.RestApiPOJO.NearbyAttraction;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Intent loginIntent;
    private Button loginTouristBtn;
    private Button loginGuideBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginTouristBtn = (Button)findViewById(R.id.touristLoginBtn);
        loginGuideBtn = (Button)findViewById(R.id.guideLoginBtn);
        loginIntent = new Intent(MainActivity.this, LoginActivity.class);

        loginTouristBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginIntent.putExtra("LOGIN_USER","tourist");
                startActivity(loginIntent);
            }
        });

        loginGuideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginIntent.putExtra("LOGIN_USER","guide");
                startActivity(loginIntent);
            }
        });




    }




}
