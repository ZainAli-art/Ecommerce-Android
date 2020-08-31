package com.example.android.ecommerce.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.ecommerce.model.Chat;
import com.example.android.ecommerce.model.ChatListItem;
import com.example.android.ecommerce.repository.ECommerceRepository;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {
    private ECommerceRepository repo;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        repo = new ECommerceRepository(application);
    }

    public LiveData<List<Chat>> getChats(String senderToken, String receiverToken, String pid) {
        return repo.getChats(senderToken, receiverToken, pid);
    }

    public LiveData<List<ChatListItem>> getChatListItems(String receiverToken) {
        return repo.getChatListItems(receiverToken);
    }

    public void sendMsg(String senderToken, String receiverToken, String pid, String msg) {
        repo.sendMsg(senderToken, receiverToken, pid, msg);
    }
}
