package com.example.android.ecommerce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.android.ecommerce.viewmodel.ChatViewModel;
import com.example.android.ecommerce.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String BROADCAST_PACKAGE = "com.example.android.broadcast";
    public static final String ACTION_REFRESH_CHAT = BROADCAST_PACKAGE + ".ACTION_REFRESH_CHAT";

    private LocalBroadcastManager localBroadcastManager;
    private RefreshChatBroadcastReceiver mRefreshChatBroadCastReceiver;

    private ChatViewModel chatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatViewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())
        ).get(ChatViewModel.class);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        UserViewModel userViewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
        ).get(UserViewModel.class);

        NavigationUI.setupWithNavController(
                bottomNavigationView,
                Navigation.findNavController(this, R.id.navHostFragment)
        );

        userViewModel.getUser().observe(this, user -> {
            if (user == null) {
                bottomNavigationView.setVisibility(View.INVISIBLE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        mRefreshChatBroadCastReceiver = new RefreshChatBroadcastReceiver();

        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        localBroadcastManager.registerReceiver(mRefreshChatBroadCastReceiver, new IntentFilter(ACTION_REFRESH_CHAT));
    }

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(mRefreshChatBroadCastReceiver);
        super.onDestroy();
    }

    private class RefreshChatBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: onReceive called");

            String senderToken = intent.getStringExtra("senderToken");
            String receiverToken = intent.getStringExtra("receiverToken");
            long pid = intent.getLongExtra("pid", 0);

            chatViewModel.refreshChats(senderToken, receiverToken, pid);
        }
    }
}