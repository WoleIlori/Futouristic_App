package com.example.wollyz.futouristic;

import android.content.Context;

import com.example.wollyz.futouristic.RestApiPOJO.Attractions;
import com.example.wollyz.futouristic.RestApiPOJO.GuideLocation;
import com.example.wollyz.futouristic.RestApiPOJO.GuideSavedState;
import com.example.wollyz.futouristic.RestApiPOJO.GuideSelection;
import com.example.wollyz.futouristic.RestApiPOJO.NearbyAttraction;
import com.example.wollyz.futouristic.RestApiPOJO.RegisterGuide;
import com.example.wollyz.futouristic.RestApiPOJO.RegisterTourist;
import com.example.wollyz.futouristic.RestApiPOJO.TourGroup;
import com.example.wollyz.futouristic.RestApiPOJO.TourGroupStatus;
import com.example.wollyz.futouristic.RestApiPOJO.TourNearby;
import com.example.wollyz.futouristic.RestApiPOJO.TouristInterest;
import com.example.wollyz.futouristic.RestApiPOJO.TouristSavedState;
import com.example.wollyz.futouristic.RestApiPOJO.TouristStatus;
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
    private static final String BASE_URL = "https://192.168.1.3/futouristicapi/v1/";
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

    //Getting all attractions from the database
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

    //inserting into the database, the attractions near tourists
    public void postLandmarksNearTourist(NearbyAttraction nearby){
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



    }


    //retrieving all tours near tourists
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


    //adding tourists that have joined a tour into the database
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

    //getting login credentials of tourists or guides, to check if presented credentials is valid
    public void getUserLoginInfo(String username, String password, String userType){
        ApiInterface apiService = retrofit.create(ApiInterface.class);

        if(userType.equals("tourist")){
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

        if(userType.equals("guide")){
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

    //adding the attractions that guides have chosen to do tours on
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

    //inserting the ads created by guides, tour ads are implemented as tour groups
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

    //updating the status of tour group to available, when available the tour group or ad is notified to tourists
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

    //upate tour group status to unavailable, when unavailable this is notified to tourists
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

    //retrieving guides' choices of attractions and attraction chosen to do a tour on
    public void getGuideSavedSelection(String guideUsername){
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


    //retrieve tours that tourists have joined and payment status for the tour
    //used to remind tourists of the tour and pending payment
    public void getTouristSavedState(String touristUsername){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<TouristSavedState> call = apiService.getTouristSavedState(touristUsername);

        call.enqueue(new Callback<TouristSavedState>() {
            @Override
            public void onResponse(Call<TouristSavedState> call, Response<TouristSavedState> response) {
                BusProvider.getInstance().post(new TouristSavedStateEvent(response.body()));
            }

            @Override
            public void onFailure(Call<TouristSavedState> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });
    }


    public void getTouristStatus(String username){
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

    public void insertGuideCurrentLocation(GuideLocation guideLocation){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<String> call = apiService.addGuideLocation(guideLocation);

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

    public void removeTouristFromTour(String touristUsername){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<String> call = apiService.deleteFromTourGroup(touristUsername);

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

    public void getGroupStatus(String guideUsername, String landmark){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<TourGroupStatus> call = apiService.getTourGroupStatus(guideUsername, landmark);

        call.enqueue(new Callback<TourGroupStatus>() {
            @Override
            public void onResponse(Call<TourGroupStatus> call, Response<TourGroupStatus> response) {
                BusProvider.getInstance().post(new TourGroupStatusEvent(response.body()));
            }

            @Override
            public void onFailure(Call<TourGroupStatus> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });
    }

    public void endTour(String guideUsername, String landmark){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<String> call = apiService.endTourInstance(guideUsername, landmark);

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


    public void updateTouristPaymentStatus(String touristUsername){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<String> call = apiService.updatePaymentStatus(touristUsername);

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

    public void createGuideAccount(RegisterGuide guide){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<String> call = apiService.registerGuide(guide);

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

    public void createTouristAccount(RegisterTourist tourist){
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<String> call = apiService.registerTourist(tourist);

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
