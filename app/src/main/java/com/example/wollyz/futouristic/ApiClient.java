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
//FOR GOOGLE MAP CREATE ANOTHER RETROFIT CLIENT CLASS
// AFTER RETRIEVING KEY SAVE IN RAW FOLDER
public class ApiClient {

    //netstat -an | find "LISTEN" to find ip address in xampp
    private static final String BASE_URL = "https://147.252.142.56/futouristicapi/v1/";

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
                BusProvider.getInstance().post(new ResponseEvent(response.body()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });

    /*
        else
        {
            Call<String> call = apiService.updateTouristLocation(nearby.getAttractions(),nearby.getDistances(),nearby.getUsername());
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
        */


    }


    public void getToursNearby(List<String> nearbyLandmarks, int total_people){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<List<TourNearby>> call = apiService.getToursNearby(nearbyLandmarks, total_people);

        call.enqueue(new Callback<List<TourNearby>>() {
            @Override
            public void onResponse(Call<List<TourNearby>> call, Response<List<TourNearby>> response) {
                BusProvider.getInstance().post(new TourNearbyEvent(response.body(),response.message()));
            }

            @Override
            public void onFailure(Call<List<TourNearby>> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });
    }


    public void addTouristToTourGroup(TouristInterest touristInterest){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<String> call = apiService.addTouristToTourGroup(touristInterest);

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

    public void getUserLoginInfo(String username, String password, String userType){
        ApiInterface apiService = retrofit.create(ApiInterface.class);

        if(userType == "tourist"){
            Call<String> call = apiService.getTouristLoginInfo(username,password);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    BusProvider.getInstance().post(new ResponseEvent(response.body()));
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

                }
            });
        }

        if(userType == "guide"){
            Call<String> call = apiService.getGuideLoginInfo(username,password);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    BusProvider.getInstance().post(new ResponseEvent(response.body()));
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

                }
            });
        }

    }


    public void postGuideLandmarkSelection(GuideSelection guideSelection){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<String> call = apiService.addGuideLandmarkSelection(guideSelection);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                BusProvider.getInstance().post(new ResponseEvent(response.body()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });
    }

    public void createGuideTourGroup(TourGroup tourGroup){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<String> call = apiService.createGuideTourGroup(tourGroup);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                BusProvider.getInstance().post(new ResponseEvent(response.body()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });

    }
    public void setGroupToAvailable(String guideUsername, String landmark){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<String> call = apiService.setGroupToAvailable(guideUsername,landmark);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                BusProvider.getInstance().post(new ResponseEvent(response.body()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });

    }

    public void setGroupToUnavailable(String guideUsername, String landmark){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<String> call = apiService.setGroupToUnavailable(guideUsername,landmark);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                BusProvider.getInstance().post(new ResponseEvent(response.body()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });

    }

    public void getSavedSelection(String guideUsername){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<GuideSavedState> call = apiService.getGuideSavedState(guideUsername);

        call.enqueue(new Callback<GuideSavedState>() {
            @Override
            public void onResponse(Call<GuideSavedState> call, Response<GuideSavedState> response) {
                BusProvider.getInstance().post(new GuideSavedStateEvent(response.body()));
            }

            @Override
            public void onFailure(Call<GuideSavedState> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });
    }

    public void checkTouristStatus(String username){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<TouristStatus> call = apiService.getTouristStatus(username);

        call.enqueue(new Callback<TouristStatus>() {
            @Override
            public void onResponse(Call<TouristStatus> call, Response<TouristStatus> response) {
                BusProvider.getInstance().post(new TouristStatusEvent(response.body()));
            }

            @Override
            public void onFailure(Call<TouristStatus> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });
    }

    public void getGuideCurrentLocation(String guide_username){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<GuideLocation> call = apiService.getGuideLocation(guide_username);

        call.enqueue(new Callback<GuideLocation>() {
            @Override
            public void onResponse(Call<GuideLocation> call, Response<GuideLocation> response) {
                BusProvider.getInstance().post(new GuideLocationEvent(response.body()));
            }

            @Override
            public void onFailure(Call<GuideLocation> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });
    }

}
