package com.example.wollyz.futouristic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wollyz.futouristic.RestApiPOJO.RegisterGuide;

import org.greenrobot.eventbus.Subscribe;

public class RegisterGuideActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText; //
    private EditText companyEditText;
    private Button registerButton;
    private ApiClient apiClient;
    private RegisterGuide guide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_guide);
        apiClient = new ApiClient(this);
        usernameEditText = (EditText)findViewById(R.id.username_guide);
        passwordEditText = (EditText)findViewById(R.id.password_guide);
        emailEditText = (EditText)findViewById(R.id.email_guide);
        companyEditText = (EditText)findViewById(R.id.tourCompany);
        registerButton = (Button)findViewById(R.id.registerGuideBtn);
        guide = new RegisterGuide();
        setButtonListener();
    }

    private void setButtonListener(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(formError() == 0){
                    guide.setUsername(usernameEditText.getText().toString());
                    guide.setPassword(passwordEditText.getText().toString());
                    guide.setEmail(emailEditText.getText().toString());
                    guide.setCompany(companyEditText.getText().toString());
                    apiClient.createGuideAccount(guide);
                }
                else{
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
        if(companyEditText.getText().toString().isEmpty()){
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
        Intent signinIntent = new Intent(RegisterGuideActivity.this, LoginActivity.class);
        signinIntent.putExtra("LOGIN_USER","guide");
        signinIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(signinIntent);
        finish();


    }

}
