package com.example.android.ecommerce;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.ecommerce.adapters.ChatRecyclerViewAdapter;
import com.example.android.ecommerce.model.User;
import com.example.android.ecommerce.viewmodel.ChatViewModel;
import com.example.android.ecommerce.viewmodel.UserViewModel;

public class ChatFragment extends Fragment implements View.OnClickListener {
    public static final String SENDER_TOKEN_KEY = "from_id";
    public static final String RECEIVER_TOKEN_KEY = "to_id";

    private String senderToken;
    private String receiverToken;

    private EditText msgEditText;
    private UserViewModel userViewModel;
    private ChatViewModel chatViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        senderToken = args.getString(SENDER_TOKEN_KEY);
        receiverToken = args.getString(RECEIVER_TOKEN_KEY);

        RecyclerView chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        ChatRecyclerViewAdapter adapter = new ChatRecyclerViewAdapter(senderToken);
        chatRecyclerView.setAdapter(adapter);
        msgEditText = view.findViewById(R.id.msgEditText);
        view.findViewById(R.id.sendMsgBtn).setOnClickListener(this);

        userViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(UserViewModel.class);
        chatViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(ChatViewModel.class);

        chatViewModel.getChatList().observe(getViewLifecycleOwner(), chats -> {
            adapter.setChatList(chats);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendMsgBtn:
                sendMsg();
                break;
        }
    }

    public void sendMsg() {
        User user = userViewModel.getUser().getValue();
        if (user == null) {
            Toast.makeText(getContext(), "You are not logged in yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        String msg = msgEditText.getText().toString();
        String senderName = user.getFullName();

        chatViewModel.sendMsg(senderToken, receiverToken, msg, senderName);
        msgEditText.setText("");
        refresh();
    }

    public void refresh() {
//        chatViewModel.fetchChat(senderToken, receiverToken);
        Intent intent = new Intent(MainActivity.ACTION_REFRESH_CHAT);
        intent.setPackage(requireActivity().getPackageName());
        intent.putExtra("senderToken", senderToken);
        intent.putExtra("receiverToken", receiverToken);
        LocalBroadcastManager.getInstance(requireActivity().getApplicationContext()).sendBroadcast(intent);
    }
}