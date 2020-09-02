package com.example.android.ecommerce.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.android.ecommerce.model.Chat;
import com.example.android.ecommerce.model.ChatListItem;

import java.util.List;

@Dao
public interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChat(Chat chat);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChats(List<Chat> chats);

    @Query("SELECT * " +
            "FROM chats " +
            "WHERE " +
                "((sender_token = :senderToken AND receiver_token = :receiverToken) OR " +
                "(sender_token = :receiverToken AND receiver_token = :senderToken)) AND " +
                "pid = :pid " +
            "ORDER BY upload_time")
    LiveData<List<Chat>> loadChats(String senderToken, String receiverToken, long pid);

    @Query("SELECT * FROM chat_list_items")
    LiveData<List<ChatListItem>> loadChatListItems();
}
