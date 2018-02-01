package com.example.wollyz.futouristic;

import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.NotificationCompat;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class NotificationUtils extends ContextWrapper {
    private NotificationManager manager;
    private static final String CHANNEL_ID = "my_channel_01";
    private static final String CHANNEL_NAME = "ANDROID CHANNEL";

    public NotificationUtils(Context base){
        super(base);
    }

    public NotificationCompat.Builder createNotification(String title, String body)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(body);
        mBuilder.setSmallIcon(getSmallIcon());
        mBuilder.setAutoCancel(true);
        return mBuilder;

    }


    public void notifyTourist(int id, NotificationCompat.Builder notification){
        getManager().notify(id, notification.build());
    }

    private NotificationManager getManager(){
        if(manager == null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    private int getSmallIcon(){
        return android.R.drawable.ic_dialog_map;
    }



}
