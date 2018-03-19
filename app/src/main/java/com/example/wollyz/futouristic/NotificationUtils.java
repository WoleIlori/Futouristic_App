package com.example.wollyz.futouristic;

import android.app.Notification;
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

    /*
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        CharSequence name = "Tour Channel";
        String description = "Receive Notifcations of tours available";
        int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,importance);
        channel.setDescription(description);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.createNotificationChannel(channel);
    }
    */
    public NotificationUtils(Context base){
        super(base);
    }

    public NotificationCompat.Builder createNotificationBuilder(String title, String body)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(body);
        mBuilder.setSmallIcon(getSmallIcon());
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        return mBuilder;

    }

    public Notification buildNotification(NotificationCompat.Builder mBuilder){
        return mBuilder.build();
    }

    public void notifyTourist(int id, Notification notification){
        getManager().notify(id, notification);
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
