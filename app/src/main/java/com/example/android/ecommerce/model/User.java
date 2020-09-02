package com.example.android.ecommerce.model;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    public String uid;

    public String email;

    @SerializedName("fullname")
    @ColumnInfo(name = "fullname")
    public String fullName;

    @SerializedName("img_dir")
    @ColumnInfo(name = "img_dir")
    public Uri imgUrl;

    public User(String uid, String email, String fullName, Uri imgUrl) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.imgUrl = imgUrl;
    }

    public static User from(GoogleSignInAccount account) {
        String uid = account.getId();
        String email = account.getEmail();
        String fullName = account.getDisplayName();
        Uri imgUrl = account.getPhotoUrl();

        return new User(uid, email, fullName, imgUrl);
    }
}
