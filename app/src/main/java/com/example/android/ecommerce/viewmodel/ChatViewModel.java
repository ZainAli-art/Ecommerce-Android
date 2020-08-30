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

    public LiveData<List<Chat>> getChats() {
        return repo.getChats();
    }

    public LiveData<List<ChatListItem>> getChatListItems() {
        return repo.getChatListItems();
    }

    public void sendMsg(String senderToken, String receiverToken, String pid, String msg) {
        repo.sendMsg(senderToken, receiverToken, pid, msg);
    }

    public void fetchChat(String senderToken, String receiverToken, String pid) {
        repo.fetchChat(senderToken, receiverToken, pid);
    }

    public void fetchChatListItems(String receiverToken) {
        repo.fetchChatListItems(receiverToken);
    }
}
