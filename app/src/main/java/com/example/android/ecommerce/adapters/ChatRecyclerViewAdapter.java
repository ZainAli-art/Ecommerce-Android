package com.example.android.ecommerce.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.ecommerce.R;
import com.example.android.ecommerce.model.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatViewHolder> {
    private static final String TAG = "ChatRecyclerViewAdapter";

    private List<Chat> chatList;
    private String senderToken;

    public ChatRecyclerViewAdapter(String senderToken) {
        this.senderToken = senderToken;
    }

    public List<Chat> getChatList() {
        if (chatList == null) {
            chatList = new ArrayList<>();
        }
        return chatList;
    }

    public void setChatList(List<Chat> chatList) {
        this.chatList = chatList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.chat_list_item, parent, false);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        holder.setMsg(chat.getMsg());

        if (chat.getSenderToken().equals(senderToken)) {
            Log.d(TAG, "onBindViewHolder: id matches");

            holder.setBackgroundColor(Color.parseColor("#0099ff"));
            holder.setGravity(Gravity.RIGHT);
        } else {
            Log.d(TAG, "onBindViewHolder: id does not match");

            holder.setBackgroundColor(Color.parseColor("#cccccc"));
            holder.setGravity(Gravity.START);
        }
    }

    @Override
    public int getItemCount() {
        return getChatList().size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout chatItemContainer;
        private CardView msgCard;
        private TextView msgText;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatItemContainer = itemView.findViewById(R.id.chatItemContainer);
            msgCard = itemView.findViewById(R.id.msgCard);
            msgText = itemView.findViewById(R.id.msgText);
        }

        public void setBackgroundColor(int color) {
            msgCard.setCardBackgroundColor(color);
        }

        public void setGravity(int gravity) {
            chatItemContainer.setGravity(gravity);
        }

        public void setMsg(String msg) {
            msgText.setText(msg);
        }
    }
}
