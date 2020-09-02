package com.example.android.ecommerce.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fcm")
public class Fcm {
    @NonNull
    @PrimaryKey
    public String token;

    public String uid;

    public Fcm(String token, String uid) {
        this.token = token;
        this.uid = uid;
    }
}
