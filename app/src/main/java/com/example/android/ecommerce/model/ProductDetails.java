package com.example.android.ecommerce.model;

public class ProductDetails {
    private long pid;
    private String sellerId;
    private String product;
    private String category;
    private String imgUrl;
    private String date;
    private double price;
    private String seller;
    private String contact;

    public ProductDetails(long pid, String sellerId, String product, String category, String imgUrl, String date, double price, String seller, String contact) {
        this.pid = pid;
        this.sellerId = sellerId;
        this.product = product;
        this.category = category;
        this.imgUrl = imgUrl;
        this.date = date;
        this.price = price;
        this.seller = seller;
        this.contact = contact;
    }

    public long getPid() {
        return pid;
    }

    public String getSellerId() {
        return sellerId;
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

    public String getSeller() {
        return seller;
    }

    public String getContact() {
        return contact;
    }
}
