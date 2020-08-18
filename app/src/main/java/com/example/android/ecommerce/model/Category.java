package com.example.android.ecommerce.model;

public class Category {
    public static final int VERTICAL_TYPE = 1;
    public static final int HORIZONTAL_TYPE = 0;

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
