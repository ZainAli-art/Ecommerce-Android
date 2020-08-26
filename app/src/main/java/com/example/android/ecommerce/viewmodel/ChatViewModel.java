package com.example.android.ecommerce.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.android.ecommerce.MySingleton;
import com.example.android.ecommerce.model.Chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class ChatViewModel extends AndroidViewModel {
    private static final String TAG = "ChatViewModel";

    public static final String BASE_URL = HOST_URL + "scripts/chat/";
    public static final String SEND_MSG_URL = BASE_URL + "send-new-msg.php";
    public static final String FETCH_CHAT_URL = BASE_URL + "print-chat-json.php";

    private MutableLiveData<List<Chat>> chatList;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        chatList = new MutableLiveData<>();
    }

    public LiveData<List<Chat>> getChatList() {
        return chatList;
    }

    private Chat getChat(JSONObject jsonObject) throws JSONException {
        String senderToken = jsonObject.getString("sender_token");
        String receiverToken = jsonObject.getString("receiver_token");
        String msg = jsonObject.getString("msg");
        String time = jsonObject.getString("upload_time");

        return new Chat(senderToken, receiverToken, msg, time);
    }

    public void sendMsg(String senderToken, String receiverToken, String msg, String senderName) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                SEND_MSG_URL,
                response -> {
                    Log.d(TAG, "sendMsg response: " + response);
                },
                error -> {
                    Log.d(TAG, "sendMsg url error: " + error);
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sender_token", senderToken);
                params.put("receiver_token", receiverToken);
                params.put("msg", msg);
                params.put("sender_name", senderName);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        MySingleton.getInstance(getApplication().getApplicationContext()).enqueueRequest(request);
    }

    public void fetchChat(String senderToken, String receiverToken) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                FETCH_CHAT_URL,
                response -> {
                    Log.d(TAG, "fetchChat response: " + response);
                    List<Chat> chList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            chList.add(getChat(jsonArray.getJSONObject(i)));
                        }
                        chatList.setValue(chList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.d(TAG, "sendMsg url error: " + error);
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sender_token", senderToken);
                params.put("receiver_token", receiverToken);
                return params;
            }
        };

        MySingleton.getInstance(getApplication().getApplicationContext()).enqueueRequest(request);
    }
}
