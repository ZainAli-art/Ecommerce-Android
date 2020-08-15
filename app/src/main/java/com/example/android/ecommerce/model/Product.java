package com.example.android.ecommerce.model;

public class Product {
    private String id;
    private String catId;
    private String name;
    private String imgUrl;

    public Product(String id, String catId, String name, String imgUrl) {
        this.id = id;
        this.catId = catId;
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public String getCatId() {
        return catId;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
