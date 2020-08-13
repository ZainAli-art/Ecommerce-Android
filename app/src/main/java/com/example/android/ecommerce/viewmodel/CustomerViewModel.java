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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.android.ecommerce.MySingleton;
import com.example.android.ecommerce.model.Category;
import com.example.android.ecommerce.model.Customer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class CustomerViewModel extends AndroidViewModel {
    public static final String INSERT_CUSTOMER_URL = HOST_URL + "scripts/insert-customer.php";
    public static final String CAT_JSON_URL = HOST_URL + "scripts/all-categories-json.php";
    public static final String UPLOAD_PRODUCT_URL = HOST_URL + "scripts/upload-product.php";

    private MutableLiveData<Customer> mCustomer;
    private MutableLiveData<List<Category>> catList;
    private Map<String, Integer> catMap;
    private Context mContext;

    public CustomerViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mCustomer = new MutableLiveData<>();
        catList = new MutableLiveData<>();
        catMap = new HashMap<>();
    }

    public LiveData<Customer> getCustomer() {
        return mCustomer;
    }

    public LiveData<List<Category>> getCategoryList() {
        if (catList.getValue() == null)
            setCatList(new ArrayList<>());
        return catList;
    }

    public int getCatIdByName(String categoryName) {
        Integer id = catMap.get(categoryName);
        if (id == null) return -1;
        return id;
    }

    public void setCustomer(Customer customer) {
        mCustomer.setValue(customer);
    }

    public void setCatList(List<Category> categories) {
        for (Category c : categories)
            catMap.put(c.getName(), c.getId());
        catList.setValue(categories);
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

    public void refreshCategories() {
        JsonArrayRequest request = new JsonArrayRequest(
                CAT_JSON_URL,
                response -> {
                    List<Category> categories = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int catId = jsonObject.getInt("cat_id");
                            String catName = jsonObject.getString("cat_name");
                            String imgUrl = HOST_URL + jsonObject.getString("img_dir");

                            categories.add(new Category(catId, catName, imgUrl));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setCatList(categories);
                },
                error -> {
                    Toast.makeText(mContext, "Json Response Error", Toast.LENGTH_SHORT).show();
                }
        );

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }

    public void uploadProduct(String pName, String catId, String img) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                UPLOAD_PRODUCT_URL,
                response -> {
                    Toast.makeText(mContext, "Product Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(mContext, "Response Error.", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("image", img);
                params.put("pname", pName);
                params.put("cat_id", catId);
                return params;
            }
        };

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }
}
