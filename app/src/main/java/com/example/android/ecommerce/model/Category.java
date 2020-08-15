package com.example.android.ecommerce.model;

public class Category {
    private long id;
    private String name;
    private String imgUrl;

    public Category(long id, String name, String imgUrl) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
