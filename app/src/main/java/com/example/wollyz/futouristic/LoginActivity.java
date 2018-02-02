package com.example.wollyz.futouristic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

public class LoginActivity extends AppCompatActivity {

    private String userType;
    private ApiClient client;
    private EditText usernameEditText;
    private EditText passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        client = new ApiClient(this);
        userType = "";
        usernameEditText = (EditText) findViewById(R.id.username_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        RadioButton guideRadioBtn = (RadioButton) findViewById(R.id.guide);
        RadioButton touristRadioBtn = (RadioButton) findViewById(R.id.tourist);
        Button loginBtn = (Button) findViewById(R.id.login);

        View.OnClickListener touristListen = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userType = "tourist";
            }
        };

        View.OnClickListener guideListen = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userType = "guide";
            }
        };

        guideRadioBtn.setOnClickListener(guideListen);
        touristRadioBtn.setOnClickListener(touristListen);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.getUserLoginInfo(usernameEditText.getText().toString(), passwordEditText.getText().toString(), userType);
            }
        });


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

    @Subscribe
    public void onLoginEvent(ResponseEvent serverResponse){
        Intent intent;

        if(userType == "tourist"){
            intent = new Intent(this,TouristMainActivity.class);
            intent.putExtra("username",usernameEditText.getText().toString());
        }
        else
        {
            intent = new Intent(this,GuideMainActivity.class);
            intent.putExtra("username", usernameEditText.getText().toString());
        }

        if(serverResponse.getResponseMessage() == "true"){
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(), "Incorrect username or password",Toast.LENGTH_SHORT).show();
        }


    }
}
