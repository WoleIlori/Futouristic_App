package com.example.wollyz.futouristic;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wollyz.futouristic.PayPalConfig.Config;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class TourPaymentActivity extends AppCompatActivity {
    private EditText emailEdit;
    private EditText nameEdit;
    private Button payBtn;
    private String amt;
    private String paymentDetails;
    private String username;
    private ApiClient apiClient;
    private NotificationUtils notificationUtils;
    public static final int PAYPAL_REQUEST_CODE=5555;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_payment);
        //get amount from intent
        float payamt = getIntent().getFloatExtra("TOUR_AMT", 0);
        amt = Float.toString(payamt);
        username = getIntent().getStringExtra("PAYER");
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);
        apiClient = new ApiClient(this);
        payBtn = (Button)findViewById(R.id.payBtn);
        emailEdit = (EditText)findViewById(R.id.buyerEmail);
        nameEdit = (EditText)findViewById(R.id.buyerName);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processPayment();
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

    @Override
    protected void onDestroy(){
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    private void processPayment(){
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amt)),"EUR",
                "Payment for tour", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent  = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PAYPAL_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if(confirmation != null){
                    try{
                        paymentDetails = confirmation.toJSONObject().toString(4);
                        apiClient.updateTouristPaymentStatus(username);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

            }
            else if(resultCode == Activity.RESULT_CANCELED)
            {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        }
        else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onUpdatePaymentStatus(ResponseEvent event){
        String response = "";
        int paid = 0;
        if(event.getResponseMessage().equals("Paid")) {
            paid = 1;
        }
        try{
            JSONObject jsonObject = new JSONObject(paymentDetails);
            response = jsonObject.getString("id") + jsonObject.getString("state");
        }catch (JSONException e){
            e.printStackTrace();
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("PAY_STATUS", paid);
        resultIntent.putExtra("PAY_MSG", response);
        //resultIntent.putExtra("TOUR_PRICE", amt);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }
}
