package com.example.android.ecommerce.model;

public class Product {
    private long id;
    private long catId;
    private String name;
    private String imgUrl;

    public Product(long id, long catId, String name, String imgUrl) {
        this.id = id;
        this.catId = catId;
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public long getId() {
        return id;
    }

    public long getCatId() {
        return catId;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
