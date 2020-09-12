package com.example.android.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.ecommerce.R;
import com.example.android.ecommerce.interfaces.ECommerceRecyclerViewAdaptable;
import com.example.android.ecommerce.model.ChatListItem;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.ecommerce.utils.Constants.BASE_URL;

public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.CLViewHolder>
        implements ECommerceRecyclerViewAdaptable<ChatListItem> {
    private List<ChatListItem> chatListItems;
    private ChatListItemListener listener;

    public interface ChatListItemListener {
        void onClickChatListItem(int pos);
    }

    public ChatListRecyclerViewAdapter(ChatListItemListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CLViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.chat_list_item, parent, false);
        return new CLViewHolder(itemView, listener, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull CLViewHolder holder, int position) {
        ChatListItem chatListItem = chatListItems.get(position);
        holder.setImg(chatListItem.imgUrl);
        holder.setSenderName(chatListItem.sender);
        holder.setMsg(chatListItem.msg);
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    @Override
    public List<ChatListItem> getItems() {
        if (chatListItems == null) {
            chatListItems = new ArrayList<>();
        }
        return chatListItems;
    }

    @Override
    public void setItems(List<ChatListItem> dataSet) {
        this.chatListItems = dataSet;
        notifyDataSetChanged();
    }

    public static class CLViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context mContext;
        private ChatListItemListener listener;
        private ImageView senderImg;
        private TextView senderName;
        private TextView msg;

        public CLViewHolder(@NonNull View itemView, ChatListItemListener listener, Context context) {
            super(itemView);
            this.mContext = context;
            this.listener = listener;
            itemView.setOnClickListener(this);
            senderImg = itemView.findViewById(R.id.senderImg);
            senderName = itemView.findViewById(R.id.senderName);
            msg = itemView.findViewById(R.id.msg);
        }

        public void setImg(String imgUrl) {
            Glide.with(mContext).load(BASE_URL + imgUrl).into(senderImg);
        }

        public void setSenderName(String name) {
            senderName.setText(name);
        }

        public void setMsg(String msg) {
            this.msg.setText(msg);
        }

        @Override
        public void onClick(View v) {
            listener.onClickChatListItem(getBindingAdapterPosition());
        }
    }
}
