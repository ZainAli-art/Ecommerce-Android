package com.example.android.ecommerce.model;

import android.net.Uri;

public class User {
    private long uid;
    private String email;
    private String fullName;
    private Uri imgUri;

    public User(long uid, String email, String fullName, Uri imgUri) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.imgUri = imgUri;
    }

    public long getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public Uri getImgUri() {
        return imgUri;
    }
}
