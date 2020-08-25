package com.example.android.ecommerce.model;

public class Product {
    public static final int VERTICAL_TYPE = 0;
    public static final int HORIZONTAL_TYPE = 1;

    private long pid;
    private String uid;
    private long catId;
    private String name;
    private String imgUrl;

    public Product(long pid, String uid, long catId, String name, String imgUrl) {
        this.pid = pid;
        this.uid = uid;
        this.catId = catId;
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public long getPid() {
        return pid;
    }

    public String getUid() {
        return uid;
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
