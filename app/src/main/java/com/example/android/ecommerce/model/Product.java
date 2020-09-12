package com.example.android.ecommerce.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

@Entity(tableName = "products")
public class Product {
    public static final int VERTICAL_TYPE = 0;
    public static final int HORIZONTAL_TYPE = 1;

    @PrimaryKey
    @NonNull
    public Long pid;

    public String uid;

    @SerializedName("cat_id")
    @ColumnInfo(name = "cat_id")
    public long catId;

    @SerializedName("pname")
    @ColumnInfo(name = "pname")
    public String name;

    @SerializedName("img_dir")
    @ColumnInfo(name = "img_dir")
    public String imgUrl;

    @SerializedName("upload_time")
    @ColumnInfo(name = "upload_time")
    public Timestamp uploadTime;

    public double price;

    public Product(@NonNull Long pid, String uid, long catId, String name,
                   String imgUrl, Timestamp uploadTime, double price) {
        this.pid = pid;
        this.uid = uid;
        this.catId = catId;
        this.name = name;
        this.imgUrl = imgUrl;
        this.uploadTime = uploadTime;
        this.price = price;
    }
}
