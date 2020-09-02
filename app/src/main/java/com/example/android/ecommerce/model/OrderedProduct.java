package com.example.android.ecommerce.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "ordered_products")
public class OrderedProduct {
    @PrimaryKey
    @NonNull
    public long oid;

    @SerializedName("product")
    @ColumnInfo(name = "product")
    public String productName;

    @SerializedName("img_dir")
    @ColumnInfo(name = "img_dir")
    public String imgUrl;

    public OrderedProduct(long oid, String productName, String imgUrl) {
        this.oid = oid;
        this.productName = productName;
        this.imgUrl = imgUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "OrderedProduct => oid: " + oid + " productName: " + productName + " imgUrl: " + imgUrl;
    }
}
