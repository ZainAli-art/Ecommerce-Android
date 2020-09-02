package com.example.android.ecommerce;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.ecommerce.adapters.ChatListRecyclerViewAdapter;
import com.example.android.ecommerce.model.ChatListItem;
import com.example.android.ecommerce.viewmodel.ChatViewModel;

public class ChatListFragment extends Fragment implements ChatListRecyclerViewAdapter.ChatListItemListener {
    private NavController navController;
    private RecyclerView chatListRecyclerView;
    private ChatViewModel chatViewModel;
    private ChatListRecyclerViewAdapter adapter;

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
        adapter = new ChatListRecyclerViewAdapter(this);
        chatListRecyclerView.setAdapter(adapter);

        chatViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(ChatViewModel.class);

//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
//            String token = instanceIdResult.getToken();
//            chatViewModel.getChatListItems(token).observe(getViewLifecycleOwner(), adapter::setItems);
//        });
        chatViewModel.getChatListItems().observe(getViewLifecycleOwner(), adapter::setItems);
    }

    @Override
    public void onClickChatListItem(int pos) {
        ChatListItem chatListItem = adapter.getItem(pos);
        String senderToken = chatListItem.getSenderToken();
        String receiverToken = chatListItem.getReceiverToken();
        long pid = chatListItem.getPid();

        Bundle args = new Bundle();
        /* sender and receiver will be reversed in the chat */
        args.putString(ChatFragment.SENDER_TOKEN_KEY, receiverToken);
        args.putString(ChatFragment.RECEIVER_TOKEN_KEY, senderToken);
        args.putLong(ChatFragment.PRODUCT_ID_KEY, pid);

        navController.navigate(R.id.action_chatListFragment_to_chatFragment, args);
    }
}