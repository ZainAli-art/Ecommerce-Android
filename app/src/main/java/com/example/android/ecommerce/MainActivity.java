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
import android.view.View;

import com.example.android.ecommerce.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String BROADCAST_PACKAGE = "com.example.android.broadcast";
    public static final String ACTION_REFRESH_CHAT = BROADCAST_PACKAGE + ".ACTION_REFRESH_CHAT";

    private LocalBroadcastManager localBroadcastManager;
    private RefreshChatBroadcastReceiver mRefreshChatBroadCastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            String senderToken = intent.getStringExtra("senderToken");
            String receiverToken = intent.getStringExtra("receiverToken");
            String pid = intent.getStringExtra("pid");
//            chatViewModel.fetchChat(senderToken, receiverToken, pid);
//            chatViewModel.fetchChatListItems(receiverToken);
        }
    }
}