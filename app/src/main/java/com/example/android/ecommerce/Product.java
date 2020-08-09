package com.example.android.ecommerce;

public class Product {
    private int id;
    private String name;
    private String imgUrl;

    public Product(int id, String name, String imgUrl) {
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
