package com.example.android.ecommerce.model;

public class OrderDetails {
    private long oid;
    private String productName;
    private String imgUrl;
    private String category;
    private String date;
    private double price;

    public OrderDetails(long oid, String productName, String imgUrl, String category, String date, double price) {
        this.oid = oid;
        this.productName = productName;
        this.imgUrl = imgUrl;
        this.category = category;
        this.date = date;
        this.price = price;
    }

    public long getOrderId() {
        return oid;
    }

    public String getProductName() {
        return productName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }
}
