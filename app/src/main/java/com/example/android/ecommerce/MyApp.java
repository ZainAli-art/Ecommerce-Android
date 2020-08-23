package com.example.android.ecommerce;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApp extends Application {
    public static final String CHANNEL_ID = "CH_01";

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Transactions";
            int channelImportance = NotificationManager.IMPORTANCE_HIGH;
            String channelDesc = "This channel is about all the e-commerce transactions";

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, channelImportance);
            channel.setDescription(channelDesc);


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
    }
}
