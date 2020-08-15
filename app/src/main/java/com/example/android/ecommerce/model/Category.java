package com.example.android.ecommerce.model;

public class Category {
    private String id;
    private String name;
    private String imgUrl;

    public Category(String id, String name, String imgUrl) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
