package com.example.android.ecommerce;

public class Category {
    private int id;
    private String name;
    private String imgUrl;

    public Category(int id, String name, String imgUrl) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
