package com.example.android.ecommerce;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";

    public static final String BASE_URL = HOST_URL + "scripts/fcm/";
    public static final String UPDATE_FCM_TOKEN_URL = BASE_URL + "update-fcm-token.php";
    public static final String ADD_FCM_TOKEN_URL = BASE_URL + "add-fcm-token.php";
    public static final int NOTIFICATION_ID = 0;
    public static final String OLD_TOKEN_KEY = "MyFirebaseMessagingService.OLD_TOKEN";
    public static final String PREFERENCES_NAME = "com.example.android.ecommerce.MyFirebaseMessagingService";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String oldToken = preferences.getString(OLD_TOKEN_KEY, null);
        preferences.edit().putString(OLD_TOKEN_KEY, s).apply();

        if (oldToken == null) {
            addNewTokenOnServer(s);
        } else {
            updateTokenOnServer(oldToken, s);
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

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        Bitmap largeIcon = null;
        Uri imgUrl = remoteMessage.getNotification().getImageUrl();
        try {
            largeIcon = Glide.with(this)
                    .asBitmap()
                    .load(imgUrl == null ? "" : HOST_URL + imgUrl.toString())
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
        manager.notify(NOTIFICATION_ID, notification);
    }

    private void updateTokenOnServer(String oldToken, String newToken) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                UPDATE_FCM_TOKEN_URL,
                response -> {
                },
                error -> {
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("old_token", oldToken);
                params.put("new_token", newToken);
                return params;
            }
        };

        MySingleton.getInstance(this).enqueueRequest(request);
    }

    private void addNewTokenOnServer(String token) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ADD_FCM_TOKEN_URL,
                response -> {
                },
                error -> {
                    error.printStackTrace();
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fcm_token", token);
                return params;
            }
        };

        MySingleton.getInstance(this).enqueueRequest(request);
    }
}
