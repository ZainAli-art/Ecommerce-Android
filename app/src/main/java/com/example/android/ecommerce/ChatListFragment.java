package com.example.android.ecommerce;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.ecommerce.adapters.ChatListRecyclerViewAdapter;
import com.example.android.ecommerce.model.ChatListItem;
import com.example.android.ecommerce.viewmodel.ChatViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class ChatListFragment extends Fragment implements ChatListRecyclerViewAdapter.ChatListItemListener {

    private NavController navController;
    private RecyclerView chatListRecyclerView;
    private ChatViewModel chatViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        chatListRecyclerView = view.findViewById(R.id.chatListRecyclerView);
        ChatListRecyclerViewAdapter adapter = new ChatListRecyclerViewAdapter(this);
        chatListRecyclerView.setAdapter(adapter);

        chatViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(ChatViewModel.class);

        chatViewModel.getChatListItems().observe(getViewLifecycleOwner(), adapter::setChatListItems);
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    public void onClickChatListItem(int pos) {
        ChatListItem chatListItem = chatViewModel.getChatListItems().getValue().get(pos);
        String senderToken = chatListItem.getSenderToken();
        String receiverToken = chatListItem.getReceiverToken();
        String pid = String.valueOf(chatListItem.getPid());

        Bundle args = new Bundle();
        /* sender and receiver will be reversed in the chat */
        args.putString(ChatFragment.SENDER_TOKEN_KEY, receiverToken);
        args.putString(ChatFragment.RECEIVER_TOKEN_KEY, senderToken);
        args.putString(ChatFragment.PRODUCT_ID_KEY, pid);

        navController.navigate(R.id.action_chatListFragment_to_chatFragment, args);
    }

    public void refresh() {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnSuccessListener(instanceIdResult -> {
                    Intent intent = new Intent(MainActivity.ACTION_REFRESH_CHAT_LIST);
                    intent.setPackage(requireActivity().getPackageName());
                    intent.putExtra("token", instanceIdResult.getToken());
                    LocalBroadcastManager.getInstance(requireActivity().getApplicationContext()).sendBroadcast(intent);
                });
    }
}