package com.example.android.ecommerce.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(tableName = "order_details")
public class OrderDetails {
    @PrimaryKey
    @NonNull
    public Long oid;

    @SerializedName("pname")
    @ColumnInfo(name = "pname")
    public String productName;

    @SerializedName("img_dir")
    @ColumnInfo(name = "img_dir")
    public String imgUrl;

    public String category;

    @SerializedName("order_time")
    @ColumnInfo(name = "order_time")
    public Date orderDate;

    public double price;

    public OrderDetails(@NonNull Long oid, String productName, String imgUrl, String category, Date orderDate, double price) {
        this.oid = oid;
        this.productName = productName;
        this.imgUrl = imgUrl;
        this.category = category;
        this.orderDate = orderDate;
        this.price = price;
    }
}
