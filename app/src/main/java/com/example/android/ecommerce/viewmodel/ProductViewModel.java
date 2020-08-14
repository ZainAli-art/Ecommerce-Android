package com.example.android.ecommerce.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.android.ecommerce.MySingleton;
import com.example.android.ecommerce.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class ProductViewModel extends AndroidViewModel {
    public static final String UPLOAD_PRODUCT_URL = HOST_URL + "scripts/upload-product.php";

    private Context mContext;
    private MutableLiveData<List<Product>> products;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
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
