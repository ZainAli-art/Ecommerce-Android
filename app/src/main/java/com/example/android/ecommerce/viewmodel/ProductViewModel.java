package com.example.android.ecommerce.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
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
import com.example.android.ecommerce.model.Product;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class ProductViewModel extends AndroidViewModel {
    public static final String FETCH_PRODUCTS_URL = HOST_URL + "scripts/products-by-cat_id-json.php";
    public static final String UPLOAD_PRODUCT_URL = HOST_URL + "scripts/upload-product.php";

    private Context mContext;
    private MutableLiveData<List<Product>> products;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        products = new MutableLiveData<>();
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public void setProductList(List<Product> productList) {
        products.setValue(productList);
    }

    public void fetchProducts(String catId) {
        Uri.Builder builder = Uri.parse(FETCH_PRODUCTS_URL).buildUpon();
        builder.appendQueryParameter("cat_id", catId);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                builder.toString(),
                null,
                response -> {
                    try {
                        List<Product> products = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            long pid = jsonObject.getLong("pid");
                            long cid = jsonObject.getLong("cat_id");
                            String name = jsonObject.getString("pname");
                            String imgUrl = HOST_URL + jsonObject.getString("img_dir");

                            products.add(new Product(pid, cid, name, imgUrl));
                        }
                        setProductList(products);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                }
        );

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }

    public void uploadProduct(String pName, String catId, String img, String price) {
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
                params.put("price", price);
                return params;
            }
        };

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }
}
