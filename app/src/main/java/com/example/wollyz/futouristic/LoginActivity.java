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
    private Intent registerIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        client = new ApiClient(this);
        //userType = getIntent().getStringExtra("LOGIN_USER");
        usernameEditText = (EditText) findViewById(R.id.username_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        Button loginBtn = (Button) findViewById(R.id.login);
        Button registerBtn = (Button) findViewById(R.id.signup);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(formError()== 0){
                    client.getUserLoginInfo(usernameEditText.getText().toString(), passwordEditText.getText().toString(), userType);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please enter username and password",Toast.LENGTH_LONG).show();
                }

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userType.equals("tourist")){
                    registerIntent= new Intent(view.getContext(),RegisterTouristActivity.class);
                }
                else
                {
                    registerIntent = new Intent(view.getContext(),RegisterGuideActivity.class);
                }
                startActivity(registerIntent);
            }
        });


    }

    @Override
    public void onResume(){
        super.onResume();
        if(userType == null){
            userType = getIntent().getStringExtra("LOGIN_USER");
        }
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
        Bundle extras = new Bundle();

        if(serverResponse.getResponseMessage().equals("true")){
            if(userType.equals("tourist")){
                intent = new Intent(this,TouristMainActivity.class);
                extras.putString("TOURIST_USERNAME",usernameEditText.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
            }

            if(userType.equals("guide"))
            {
                intent = new Intent(this,GuideMainActivity.class);
                extras.putString("GUIDE_USERNAME",usernameEditText.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Incorrect username or password",Toast.LENGTH_SHORT).show();
        }


    }

    private int formError() {
        int errorCounter = 0;
        if (usernameEditText.getText().toString().isEmpty()) {
            errorCounter++;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            errorCounter++;
        }
        return errorCounter;
    }

    @Subscribe
    public void onErrorEvent(ErrorEvent errorEvent){
        Toast.makeText(this,""+errorEvent.getErrorMsg(),Toast.LENGTH_SHORT).show();

    }
}
