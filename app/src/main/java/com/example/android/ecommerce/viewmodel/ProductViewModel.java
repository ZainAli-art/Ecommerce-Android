package com.example.android.ecommerce.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.android.ecommerce.MySingleton;
import com.example.android.ecommerce.model.Product;
import com.example.android.ecommerce.model.ProductDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class ProductViewModel extends AndroidViewModel {
    private static final String TAG = "ProductViewModel";

    public static final String BASE_URL = HOST_URL + "scripts/product/";
    public static final String FETCH_PRODUCT_BY_UID_URL = BASE_URL + "fetch-product-details-by-pid-json.php";
    public static final String FETCH_PRODUCTS_BY_CAT_ID_URL = BASE_URL + "products-by-cat_id-json.php";
    public static final String FETCH_RECENT_PRODUCTS_URL = BASE_URL + "fetch-recent-products-by-limit-json.php";
    public static final String UPLOAD_PRODUCT_URL = BASE_URL + "upload-product.php";

    private Context mContext;
    private MutableLiveData<List<Product>> products;
    private MutableLiveData<List<Product>> recentProducts;
    private MutableLiveData<ProductDetails> detailedProduct;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        products = new MutableLiveData<>();
        recentProducts = new MutableLiveData<>();
        detailedProduct = new MutableLiveData<>();
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<List<Product>> getRecentProducts() {
        return recentProducts;
    }

    public LiveData<ProductDetails> getDetailedProduct() {
        return detailedProduct;
    }

    private Product getProduct(JSONObject jsonObject) throws JSONException {
        long pid = jsonObject.getLong("pid");
        String uid = jsonObject.getString("uid");
        long cid = jsonObject.getLong("cat_id");
        String name = jsonObject.getString("pname");
        String imgUrl = HOST_URL + jsonObject.getString("img_dir");

        return new Product(pid, uid, cid, name, imgUrl);
    }

    private ProductDetails getProductDetails(JSONObject jsonObject) throws JSONException {
        long pid = jsonObject.getLong("pid");
        String sellerId = jsonObject.getString("seller_id");
        String product = jsonObject.getString("product");
        String category = jsonObject.getString("category");
        String imgUrl = HOST_URL + jsonObject.getString("img_dir");
        String date = jsonObject.getString("upload_time").split("\\s+")[0];
        double price = jsonObject.getLong("price");
        String seller = jsonObject.getString("seller");
        String contact = jsonObject.getString("contact");

        return new ProductDetails(pid, sellerId, product, category, imgUrl, date, price, seller, contact);
    }

    public void fetchProductDetailsByPid(String pid) {
        String url = Uri.parse(FETCH_PRODUCT_BY_UID_URL)
                .buildUpon()
                .appendQueryParameter("pid", pid)
                .build()
                .toString();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d(TAG, "fetchProductDetailsByPid response: " + response);
                    try {
                        detailedProduct.setValue(getProductDetails(response));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {}
        );

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }

    public void fetchProductsByCatId(String catId) {
        Uri.Builder builder = Uri.parse(FETCH_PRODUCTS_BY_CAT_ID_URL).buildUpon();
        builder.appendQueryParameter("cat_id", catId);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                builder.toString(),
                null,
                response -> {
                    try {
                        List<Product> pList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            pList.add(getProduct(response.getJSONObject(i)));
                        }
                        products.setValue(pList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                }
        );

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }

    public void fetchRecentProducts() {
        String recentProductsUrl = Uri.parse(FETCH_RECENT_PRODUCTS_URL)
                .buildUpon()
                .appendQueryParameter("limit", "5")
                .build()
                .toString();

        JsonArrayRequest request = new JsonArrayRequest(
                recentProductsUrl,
                response -> {
                    try {
                        List<Product> pList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            pList.add(getProduct(response.getJSONObject(i)));
                        }
                        recentProducts.setValue(pList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {}
        );

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }

    public void uploadProduct(String uid, String pName, String catId, String img, String price) {
        Log.d(TAG, "uploadProduct uid: " + uid);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                UPLOAD_PRODUCT_URL,
                response -> {
                    Log.d(TAG, "uploadProduct response: " + response);
                    Toast.makeText(mContext, "Product Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(mContext, "Response Error.", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("image", img);
                params.put("pname", pName);
                params.put("cat_id", catId);
                params.put("price", price);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }
}
