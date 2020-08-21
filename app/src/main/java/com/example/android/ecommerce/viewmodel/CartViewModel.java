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
import com.example.android.ecommerce.model.OrderDetails;
import com.example.android.ecommerce.model.OrderedProduct;

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
    public static final String FETCH_CART_PRODUCTS_URL = HOST_URL + "scripts/ordered-products-by-uid-json.php";
    public static final String FETCH_ORDER_DETAILS_URL = HOST_URL + "scripts/order-details-by-oid-json.php";
    public static final String DELETE_ORDER_URL = HOST_URL + "scripts/delete-order-by-oid.php";
    private Context mContext;

    private MutableLiveData<List<OrderedProduct>> cartProducts;
    private MutableLiveData<OrderDetails> detailedOrder;

    public CartViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        cartProducts = new MutableLiveData<>();
        detailedOrder = new MutableLiveData<>();
    }

    public LiveData<List<OrderedProduct>> getCartProducts() {
        return cartProducts;
    }

    public LiveData<OrderDetails> getDetailedOrder() {
        return detailedOrder;
    }

    private OrderedProduct getOrderedProduct(JSONObject jsonObject) throws JSONException {
        long oid = jsonObject.getLong("oid");
        String productName = jsonObject.getString("product");
        String imgUrl = HOST_URL + jsonObject.getString("img_dir");

        return new OrderedProduct(oid, productName, imgUrl);
    }

    private OrderDetails getOrderDetails(JSONObject jsonObject) throws JSONException {
        long oid = jsonObject.getLong("oid");
        String productName = jsonObject.getString("pname");
        String imgUrl = HOST_URL + jsonObject.getString("img_dir");
        String category = jsonObject.getString("category");
        String date = jsonObject.getString("order_time").split("\\s+")[0];
        double price = jsonObject.getDouble("price");

        return new OrderDetails(oid, productName, imgUrl, category, date, price);
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
                        List<OrderedProduct> pList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            pList.add(getOrderedProduct(jsonArray.getJSONObject(i)));
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

    public void fetchOrderDetails(String oid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                FETCH_ORDER_DETAILS_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        detailedOrder.setValue(getOrderDetails(jsonObject));
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
                params.put("oid", oid);
                return params;
            }
        };

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }

    public void deleteOrder(String oid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                DELETE_ORDER_URL,
                response -> {
                },
                error -> {
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("oid", oid);
                return params;
            }
        };

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }
}
