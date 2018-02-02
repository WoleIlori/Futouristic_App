package com.example.wollyz.futouristic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class GuideMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_main);
        Toast.makeText(this, "this is guide main page", Toast.LENGTH_SHORT).show();
    }
}
