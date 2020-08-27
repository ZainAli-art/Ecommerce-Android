package com.example.android.ecommerce.model;

public class Chat {
    private String senderToken;
    private String receiverToken;
    private long pid;
    private String msg;
    private String time;

    public Chat(String senderToken, String receiverToken, long pid, String msg, String time) {
        this.senderToken = senderToken;
        this.receiverToken = receiverToken;
        this.pid = pid;
        this.msg = msg;
        this.time = time;
    }

    public String getSenderToken() {
        return senderToken;
    }

    public String getReceiverToken() {
        return receiverToken;
    }

    public long getPid() {
        return pid;
    }

    public String getMsg() {
        return msg;
    }

    public String getTime() {
        return time;
    }
}
