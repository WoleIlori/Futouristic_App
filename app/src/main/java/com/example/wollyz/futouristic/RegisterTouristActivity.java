package com.example.wollyz.futouristic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wollyz.futouristic.RestApiPOJO.RegisterTourist;

import org.greenrobot.eventbus.Subscribe;

public class RegisterTouristActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText; //
    private EditText nationalityEditText;
    private Button registerButton;
    private ApiClient apiClient;
    private RegisterTourist tourist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tourist);
        apiClient = new ApiClient(this);
        usernameEditText = (EditText)findViewById(R.id.username_tourist);
        passwordEditText = (EditText)findViewById(R.id.password_tourist);
        emailEditText = (EditText)findViewById(R.id.email_tourist);
        nationalityEditText = (EditText)findViewById(R.id.nationality);
        registerButton = (Button)findViewById(R.id.registerTouristBtn);
        tourist = new RegisterTourist();
        setButtonListener();
    }



    private void setButtonListener(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(formError() == 0){
                    tourist.setUsername(usernameEditText.getText().toString());
                    tourist.setPassword(passwordEditText.getText().toString());
                    tourist.setEmail(emailEditText.getText().toString());
                    tourist.setNationality(nationalityEditText.getText().toString());
                    apiClient.createTouristAccount(tourist);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please complete all fields",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private int formError(){
        int errorCounter = 0;
        if(usernameEditText.getText().toString().isEmpty())
        {
            errorCounter++;
        }
        if(passwordEditText.getText().toString().isEmpty())
        {
            errorCounter++;
        }
        if(emailEditText.getText().toString().isEmpty()){
            errorCounter++;
        }
        if(nationalityEditText.getText().toString().isEmpty()){
            errorCounter++;
        }
        return errorCounter;
    }

    @Override
    public void onResume(){
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy(){
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onRegisterTouristEvent(ResponseEvent server){
        Intent signinIntent = new Intent(RegisterTouristActivity.this, LoginActivity.class);
        signinIntent.putExtra("LOGIN_USER","tourist");
        signinIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(signinIntent);
        finish();

    }
}
