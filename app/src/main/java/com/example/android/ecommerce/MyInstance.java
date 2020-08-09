package com.example.android.ecommerce;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyInstance {
    private static MyInstance mInstance;
    private static Context ctx;

    private RequestQueue requestQueue;

    private MyInstance(Context context) {
        // private constructor (Singleton Pattern)
        ctx = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized MyInstance getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyInstance(context);
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
