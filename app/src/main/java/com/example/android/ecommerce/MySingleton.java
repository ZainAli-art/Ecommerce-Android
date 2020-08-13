package com.example.android.ecommerce;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    private static MySingleton mInstance;
    private static Context ctx;
    public static final String HOST_URL = "http://192.168.8.100/";

    private RequestQueue requestQueue;

    private MySingleton(Context context) {
        // private constructor (Singleton Pattern)
        ctx = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public <T>void enqueueRequest(Request<T> request) {
        requestQueue.add(request);
    }
}
