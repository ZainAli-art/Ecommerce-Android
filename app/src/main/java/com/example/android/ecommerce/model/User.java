package com.example.android.ecommerce.model;

import android.net.Uri;

public class User {
    private String uid;
    private String email;
    private String fullName;
    private Uri imgUrl;

    public User(String uid, String email, String fullName, Uri imgUrl) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.imgUrl = imgUrl;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public Uri getImgUrl() {
        return imgUrl;
    }
}
