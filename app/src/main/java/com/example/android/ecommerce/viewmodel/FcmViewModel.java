package com.example.android.ecommerce.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.android.ecommerce.model.Fcm;
import com.example.android.ecommerce.repository.ECommerceRepository;

public class FcmViewModel extends AndroidViewModel {
    private ECommerceRepository repo;

    public FcmViewModel(@NonNull Application application) {
        super(application);
        repo = ECommerceRepository.getInstance(application);
    }

    public void insert(String token) {
        repo.insertFcmToken(token);
    }

    public void update(String oldToken, String newToken) {
        repo.updateFcmToken(oldToken, newToken);
    }

    public void update(Fcm fcm) {
        repo.updateFcmToken(fcm);
    }
}
