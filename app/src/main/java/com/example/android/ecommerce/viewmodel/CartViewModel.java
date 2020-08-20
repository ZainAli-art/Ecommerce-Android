package com.example.android.ecommerce.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.android.ecommerce.MySingleton;
import com.example.android.ecommerce.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class CartViewModel extends AndroidViewModel {
    public static final String ADD_TO_CART_URL = HOST_URL + "scripts/add-order.php";
    public static final String FETCH_CART_PRODUCTS_URL = HOST_URL + "scripts/products-by-uid-json.php";
    private Context mContext;

    private MutableLiveData<List<Product>> cartProducts;

    public CartViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        cartProducts = new MutableLiveData<>();
    }

    public LiveData<List<Product>> getCartProducts() {
        return cartProducts;
    }

    private Product getProduct(JSONObject jsonObject) throws JSONException {
        long pid = jsonObject.getLong("pid");
        long cid = jsonObject.getLong("cat_id");
        String name = jsonObject.getString("pname");
        String imgUrl = HOST_URL + jsonObject.getString("img_dir");

        return new Product(pid, cid, name, imgUrl);
    }

    public void addToCart(String uid, String pid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ADD_TO_CART_URL,
                response -> {
                    Toast.makeText(mContext, "Product added to cart", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(mContext, "Product not added to cart", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("pid", pid);
                return params;
            }
        };

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }

    public void fetchCartProducts(String uid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                FETCH_CART_PRODUCTS_URL,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<Product> pList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            pList.add(getProduct(jsonArray.getJSONObject(i)));
                        }
                        cartProducts.setValue(pList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                return params;
            }
        };

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }
}
