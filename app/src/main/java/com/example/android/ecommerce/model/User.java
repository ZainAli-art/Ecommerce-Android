package com.example.android.ecommerce.model;

public class User {
    private long uid;
    private String email;
    private String fullName;
    private String imgUrl;

    public User(long uid, String email, String fullName, String imgUrl) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.imgUrl = imgUrl;
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

    public String getImgUrl() {
        return imgUrl;
    }
}
