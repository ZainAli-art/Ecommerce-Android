package com.example.android.ecommerce.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(tableName = "product_details")
public class ProductDetails {
    @PrimaryKey
    @NonNull
    public Long pid;

    @SerializedName("seller_id")
    @ColumnInfo(name = "seller_id")
    public String sellerId;

    public String product;
    public String category;

    @SerializedName("img_dir")
    @ColumnInfo(name = "img_dir")
    public String imgUrl;

    @SerializedName("upload_time")
    @ColumnInfo(name = "upload_time")
    public Date date;

    public double price;
    public String seller;
    public String contact;

    @SerializedName("seller_token")
    @ColumnInfo(name = "seller_token")
    public String sellerToken;

    public ProductDetails(@NonNull Long pid, String sellerId, String product,
                          String category, String imgUrl, Date date, double price,
                          String seller, String contact, String sellerToken) {
        this.pid = pid;
        this.sellerId = sellerId;
        this.product = product;
        this.category = category;
        this.imgUrl = imgUrl;
        this.date = date;
        this.price = price;
        this.seller = seller;
        this.contact = contact;
        this.sellerToken = sellerToken;
    }
}
