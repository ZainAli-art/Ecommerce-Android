package com.example.android.ecommerce.model;

public class Product {
    public static final int VERTICAL_TYPE = 0;
    public static final int HORIZONTAL_TYPE = 1;

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
