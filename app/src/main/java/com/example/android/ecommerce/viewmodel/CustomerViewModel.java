package com.example.android.ecommerce.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.android.ecommerce.MySingleton;
import com.example.android.ecommerce.model.Customer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class CustomerViewModel extends AndroidViewModel {
    public static final String INSERT_CUSTOMER_URL = HOST_URL + "scripts/insert-customer.php";
    public static final String UPLOAD_PRODUCT_URL = HOST_URL + "scripts/upload-product.php";

    private MutableLiveData<Customer> mCustomer;

    private Context mContext;

    public CustomerViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mCustomer = new MutableLiveData<>();
    }

    public LiveData<Customer> getCustomer() {
        return mCustomer;
    }

    public void setCustomer(Customer customer) {
        mCustomer.setValue(customer);
    }

    public void login(String email, String password) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                INSERT_CUSTOMER_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int uid = jsonObject.getInt("uid");
                        if (uid == -1) {
                            setCustomer(null);
                        } else {
                            setCustomer(new Customer(uid, email, password));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getApplication().getApplicationContext(), "URL response error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("pwd", password);
                return params;
            }
        };

        MySingleton.getInstance(getApplication().getApplicationContext()).enqueueRequest(request);
    }
}
