package com.example.android.ecommerce.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

@Entity(tableName = "orders")
public class Order {
    @PrimaryKey
    @NonNull
    public long oid;

    public String uid;
    public long pid;

    @SerializedName("order_time")
    @ColumnInfo(name = "order_time")
    public Timestamp orderTime;

    public Order(long oid, String uid, long pid, Timestamp orderTime) {
        this.oid = oid;
        this.uid = uid;
        this.pid = pid;
        this.orderTime = orderTime;
    }
}
