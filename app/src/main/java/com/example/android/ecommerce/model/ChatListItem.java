package com.example.android.ecommerce.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "chat_list_items")
public class ChatListItem {
    @PrimaryKey
    @NonNull
    private long pid;

    @SerializedName("sender_token")
    @ColumnInfo(name = "sender_token")
    private String senderToken;

    @SerializedName("receiver_token")
    @ColumnInfo(name = "receiver_token")
    private String receiverToken;

    private String msg;
    private String sender;

    @SerializedName("img_dir")
    @ColumnInfo(name = "img_dir")
    private String imgUrl;

    public ChatListItem(long pid, String senderToken, String receiverToken, String msg, String sender, String imgUrl) {
        this.pid = pid;
        this.senderToken = senderToken;
        this.receiverToken = receiverToken;
        this.msg = msg;
        this.sender = sender;
        this.imgUrl = imgUrl;
    }

    public long getPid() {
        return pid;
    }

    public String getSenderToken() {
        return senderToken;
    }

    public String getReceiverToken() {
        return receiverToken;
    }

    public String getMsg() {
        return msg;
    }

    public String getSender() {
        return sender;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
