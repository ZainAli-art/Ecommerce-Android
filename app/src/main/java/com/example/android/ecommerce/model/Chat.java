package com.example.android.ecommerce.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

@Entity(tableName = "chats", primaryKeys = {"sender_token", "receiver_token", "upload_time"})
public class Chat {
    @SerializedName("sender_token")
    @ColumnInfo(name = "sender_token")
    @NonNull
    public String senderToken;

    @SerializedName("receiver_token")
    @ColumnInfo(name = "receiver_token")
    @NonNull
    public String receiverToken;

    @ColumnInfo(name = "pid")
    public long pid;

    public String msg;

    @SerializedName("upload_time")
    @ColumnInfo(name = "upload_time")
    @NonNull
    public Timestamp time;

    public Chat(@NonNull String senderToken, @NonNull String receiverToken, long pid, String msg, @NonNull Timestamp time) {
        this.senderToken = senderToken;
        this.receiverToken = receiverToken;
        this.pid = pid;
        this.msg = msg;
        this.time = time;
    }
}
