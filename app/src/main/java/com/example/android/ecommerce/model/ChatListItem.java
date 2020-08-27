package com.example.android.ecommerce.model;

public class ChatListItem {
    private long pid;
    private String senderToken;
    private String receiverToken;
    private String msg;
    private String sender;
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
