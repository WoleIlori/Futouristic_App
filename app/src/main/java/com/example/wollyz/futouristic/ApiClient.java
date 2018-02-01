package com.example.wollyz.futouristic;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class ApiClient {
    //netstat -an | find "LISTEN" to find ip address in xampp
    private static final String BASE_URL = "https://147.252.139.38/futouristicapi/v1/";

    private static Retrofit retrofit = null;
    private List<Attractions> attractions;
    private String[] names;
    private Context context;
    private OkHttpClient.Builder httpClient;



    ApiClient(Context context)
    {
        this.context = context;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        attractions = new ArrayList<Attractions>();
        httpClient = new OkHttpClient.Builder();
        //.connectTimeout(100, TimeUnit.SECONDS)
        //.readTimeout(100,TimeUnit.SECONDS);

        httpClient.addInterceptor(logging);
        initSSL();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                //.addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

    }


    //Reference:https://gist.github.com/demixdn/3886de5a71dc2812c8f4d27a248a506b
    private void initSSL(){
        SSLContext sslContext = null;
        X509TrustManager trustManager = null;
        try{
            TrustManager[] trustManagers = createCertificate(context.getResources().openRawResource(R.raw.certificate));
            trustManager = (X509TrustManager) trustManagers[0];
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null,new TrustManager[] {trustManager}, null);
        }catch (CertificateException | IOException | KeyStoreException | KeyManagementException |NoSuchAlgorithmException e ){
            e.printStackTrace();
        }
        if(sslContext!=null && trustManager!=null) {
            httpClient.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
            httpClient.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        }

    }

    private TrustManager[] createCertificate(InputStream trustedCertificate) throws CertificateException,IOException,KeyStoreException,NoSuchAlgorithmException,KeyManagementException
    {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca;
        try{
            ca = cf.generateCertificate(trustedCertificate);
        }finally {
            trustedCertificate.close();
        }

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca",ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        return tmf.getTrustManagers();

    }

    public void getAttractions(){

        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<List<Attractions>> call = apiService.doGetAllAttractions();


        call.enqueue(new Callback<List<Attractions>>() {
            @Override
            public void onResponse(Call<List<Attractions>> call, Response<List<Attractions>> response) {
                BusProvider.getInstance().post(new AttractionsReceivedEvent(response.body(),response.message()));
            }

            @Override
            public void onFailure(Call<List<Attractions>> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });
    }

    public void postLandmarksNearTourist(NearbyAttraction nearby){
        //Field parameters
        //String username = nearby.getUsername();
        //List<String> attractions = nearby.getAttractions();
        //List<Double> distances = nearby.getDistances();


        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<String> call = apiService.addNearbyAttractions(nearby);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                BusProvider.getInstance().post(new ResponseEvent(response.message()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });
    }

    public void getAmtTouristsNearby(String username){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<List<AmtTouristNearby>> call = apiService.doGetAllTouristNearby(username);

        call.enqueue(new Callback<List<AmtTouristNearby>>() {
            @Override
            public void onResponse(Call<List<AmtTouristNearby>> call, Response<List<AmtTouristNearby>> response) {
                BusProvider.getInstance().post(new TouristAmtEvent(response.body(),response.message()));
            }

            @Override
            public void onFailure(Call<List<AmtTouristNearby>> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });
    }

    public void postTouristLandmarkChoice(TouristInterest touristInterest){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<String> call = apiService.addTouristLandmarkInterest(touristInterest);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                BusProvider.getInstance().post(new ResponseEvent(response.message()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });
    }




}
