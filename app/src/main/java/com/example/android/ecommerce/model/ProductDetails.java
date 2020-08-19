package com.example.android.ecommerce.model;

public class ProductDetails {
    private long id;
    private String product;
    private String category;
    private String imgUrl;
    private String date;
    private double price;

    public ProductDetails(long id, String product, String category, String imgUrl, String date, double price) {
        this.id = id;
        this.product = product;
        this.category = category;
        this.imgUrl = imgUrl;
        this.date = date;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public String getProduct() {
        return product;
    }

    public String getCategory() {
        return category;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }
}
