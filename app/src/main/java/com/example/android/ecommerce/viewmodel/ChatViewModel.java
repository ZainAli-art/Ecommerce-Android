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
        repo = ECommerceRepository.getInstance(application);
    }

    public LiveData<List<Chat>> getChats(String senderToken, String receiverToken, long pid) {
        return repo.getChats(senderToken, receiverToken, pid);
    }

    public LiveData<List<ChatListItem>> getChatListItems() {
        return repo.getChatListItems();
    }

    public void refreshChats(String senderToken, String receiverToken, long pid) {
        repo.refreshChats(senderToken, receiverToken, pid);
    }

    public void sendMsg(String senderToken, String receiverToken, long pid, String msg) {
        repo.insertChat(senderToken, receiverToken, pid, msg);
    }
}
