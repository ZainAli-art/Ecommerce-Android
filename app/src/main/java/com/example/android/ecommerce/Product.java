package com.example.android.ecommerce;

public class Product {
    private int id;
    private int catId;
    private String name;
    private String imgUrl;

    public Product(int id, int catId, String name, String imgUrl) {
        this.id = id;
        this.catId = catId;
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public int getCatId() {
        return catId;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
