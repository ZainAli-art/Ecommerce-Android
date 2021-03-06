package com.example.android.ecommerce;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavDeepLinkBuilder;

import com.bumptech.glide.Glide;
import com.example.android.ecommerce.viewmodel.FcmViewModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.concurrent.ExecutionException;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";

    public static final int RECENT_PRODUCT_NOTIFCICATION_ID = 1;
    public static final int CHAT_NOTIFICATION_ID = 2;
    public static final String OLD_TOKEN_KEY = "MyFirebaseMessagingService.OLD_TOKEN";
    public static final String PREFERENCES_NAME = "com.example.android.ecommerce.MyFirebaseMessagingService";
    public static final String TAG_RECENT_PRODUCT = "newProduct";
    public static final String TAG_CHAT = "chat";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String oldToken = preferences.getString(OLD_TOKEN_KEY, null);
        preferences.edit().putString(OLD_TOKEN_KEY, s).apply();

        FcmViewModel fcmViewModel = new FcmViewModel(getApplication());
        if (oldToken == null) {
            fcmViewModel.insert(s);
        } else {
            fcmViewModel.update(oldToken, s);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: new");

        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() == null) {
            Log.d(TAG, "onMessageReceived: notification is null");
            return;
        }

        String tag = remoteMessage.getNotification().getTag();
        switch (tag) {
            case TAG_RECENT_PRODUCT:
                notifyRecentProduct(remoteMessage);
                break;
            case TAG_CHAT:
                notifyChat(remoteMessage);
                break;
        }
    }

    public void notifyRecentProduct(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        Bitmap largeIcon = null;
        Uri imgUrl = remoteMessage.getNotification().getImageUrl();
        try {
            largeIcon = Glide.with(this)
                    .asBitmap()
                    .load("192.168.8.101" + imgUrl.toString())
                    .submit(100, 100)
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, getString(R.string.trans_channel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)    // required
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(RECENT_PRODUCT_NOTIFCICATION_ID, notification);
    }

    public void notifyChat(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String senderToken = remoteMessage.getData().get("sender_token");
        String receiverToken = remoteMessage.getData().get("receiver_token");
        long pid = Long.parseLong(remoteMessage.getData().get("pid"));

        // --- trigger broadcast to refresh chat ---
        Intent intent = new Intent(MainActivity.ACTION_REFRESH_CHAT);
        intent.setPackage(getPackageName());
        intent.putExtra("senderToken", senderToken);
        intent.putExtra("receiverToken", receiverToken);
        intent.putExtra("pid", pid);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // --- send notification ---
        Bundle args = new Bundle();
        /* sender and receiver will be reversed in the chat */
        args.putString(ChatFragment.SENDER_TOKEN_KEY, receiverToken);
        args.putString(ChatFragment.RECEIVER_TOKEN_KEY, senderToken);
        args.putLong(ChatFragment.PRODUCT_ID_KEY, pid);

        // create pending intent for the notification
        PendingIntent pendingIntent = new NavDeepLinkBuilder(this)
                .setComponentName(MainActivity.class)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.chatFragment)
                .setArguments(args)
                .createPendingIntent();

        // build and trigger notification
        Notification notification = new NotificationCompat.Builder(this, getString(R.string.trans_channel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)    // required
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(CHAT_NOTIFICATION_ID, notification);
    }
}
