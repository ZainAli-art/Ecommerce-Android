package com.example.android.ecommerce.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "chat_list_items", primaryKeys = {"sender_token", "receiver_token", "pid"})
public class ChatListItem {
    @NonNull
    public Long pid;

    @NonNull
    @SerializedName("sender_token")
    @ColumnInfo(name = "sender_token")
    public String senderToken;

    @NonNull
    @SerializedName("receiver_token")
    @ColumnInfo(name = "receiver_token")
    public String receiverToken;

    public String msg;
    public String sender;

    @SerializedName("img_dir")
    @ColumnInfo(name = "img_dir")
    public String imgUrl;

    public ChatListItem(@NonNull Long pid, @NonNull String senderToken, 
                        @NonNull String receiverToken, String msg, String sender, 
                        String imgUrl) {
        this.pid = pid;
        this.senderToken = senderToken;
        this.receiverToken = receiverToken;
        this.msg = msg;
        this.sender = sender;
        this.imgUrl = imgUrl;
    }
}
