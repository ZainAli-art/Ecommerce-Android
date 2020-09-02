package com.example.android.ecommerce.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "categories")
public class Category {
    public static final int VERTICAL_TYPE = 1;
    public static final int HORIZONTAL_TYPE = 0;

    @SerializedName("cat_id")
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "cat_id")
    public long id;

    @SerializedName("cat_name")
    @ColumnInfo(name = "cat_name")
    public String name;

    @SerializedName("img_dir")
    @ColumnInfo(name = "img_dir")
    public String imgUrl;

    public Category(long id, String name, String imgUrl) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
    }
}
