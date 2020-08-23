package com.example.android.ecommerce;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel transChannel = new NotificationChannel(
                    getString(R.string.trans_channel_id),
                    getString(R.string.trans_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            transChannel.setDescription(getString(R.string.trans_channel_desc));


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(transChannel);
        }
    }
}
