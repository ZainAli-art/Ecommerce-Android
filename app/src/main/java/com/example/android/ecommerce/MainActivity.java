package com.example.android.ecommerce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.example.android.ecommerce.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
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
    }
}