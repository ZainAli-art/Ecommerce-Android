package com.example.android.ecommerce.model;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.android.ecommerce.MySingleton;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

/**
 * Responsible for all network processing (database and other)
 */
public class ECommerceNetwork {
    private static final String TAG = "ECommerceNetwork";

    private static volatile ECommerceNetwork INSTANCE;

    public static final String USER_BASE_URL = HOST_URL + "scripts/user/";
    public static final String CATEGORY_BASE_URL = HOST_URL + "scripts/category/";
    public static final String FCM_BASE_URL = HOST_URL + "scripts/fcm/";
    public static final String PRODUCT_BASE_URL = HOST_URL + "scripts/product/";
    public static final String CART_BASE_URL = HOST_URL + "scripts/order/";
    public static final String CHAT_BASE_URL = HOST_URL + "scripts/chat/";

    private static final String ADD_USER_URL = USER_BASE_URL + "add-user.php";

    private static final String REGISTER_USER_TOKEN_URL = FCM_BASE_URL + "register-user-fcm-token.php";
    private static final String UNREGISTER_USER_TOKEN_URL = FCM_BASE_URL + "unregister-user-token.php";

    public static final String CAT_JSON_URL = CATEGORY_BASE_URL + "all-categories-json.php";

    public static final String FETCH_PRODUCT_BY_UID_URL = PRODUCT_BASE_URL + "fetch-product-details-by-pid-json.php";
    public static final String FETCH_PRODUCTS_BY_CAT_ID_URL = PRODUCT_BASE_URL + "products-by-cat_id-json.php";
    public static final String FETCH_RECENT_PRODUCTS_URL = PRODUCT_BASE_URL + "fetch-recent-products-by-limit-json.php";
    public static final String UPLOAD_PRODUCT_URL = PRODUCT_BASE_URL + "upload-product.php";

    public static final String ADD_TO_CART_URL = CART_BASE_URL + "add-order.php";
    public static final String FETCH_CART_PRODUCTS_URL = CART_BASE_URL + "ordered-products-by-uid-json.php";
    public static final String FETCH_ORDER_DETAILS_URL = CART_BASE_URL + "order-details-by-oid-json.php";
    public static final String DELETE_ORDER_URL = CART_BASE_URL + "delete-order-by-oid.php";

    public static final String SEND_MSG_URL = CHAT_BASE_URL + "send-new-msg.php";
    public static final String FETCH_CHATS_URL = CHAT_BASE_URL + "print-chat-json.php";
    public static final String FETCH_CHAT_LIST_ITEMS_URL = CHAT_BASE_URL + "print-chat-list-json.php";

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager facebookCallbackManager;
    private AccessTokenTracker facebookAccessTokenTracker;
    private Context mContext;

    private MutableLiveData<User> mUser;
    private MutableLiveData<List<Category>> categories;
    private MutableLiveData<List<Product>> products;
    private MutableLiveData<List<Product>> recentProducts;
    private MutableLiveData<ProductDetails> detailedProduct;
    private MutableLiveData<List<OrderedProduct>> cartProducts;
    private MutableLiveData<OrderDetails> detailedOrder;
    private MutableLiveData<List<Chat>> chats;
    private MutableLiveData<List<ChatListItem>> chatListItems;

    private Map<String, Long> catMap;

    public static ECommerceNetwork getNetwork(final Context context) {
        if (INSTANCE == null) {
            synchronized (ECommerceNetwork.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ECommerceNetwork(context);
                }
            }
        }
        return INSTANCE;
    }

    private ECommerceNetwork(Context context) {
        mContext = context;
        mUser = new MutableLiveData<>();
        facebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken != null)
                    loginFromFacebookAccessToken(currentAccessToken);
            }
        };
        categories = new MutableLiveData<>();
        catMap = new HashMap<>();
        products = new MutableLiveData<>();
        recentProducts = new MutableLiveData<>();
        detailedProduct = new MutableLiveData<>();
        cartProducts = new MutableLiveData<>();
        detailedOrder = new MutableLiveData<>();
        chats = new MutableLiveData<>();
        chatListItems = new MutableLiveData<>();
    }
    /* Live Data Getters */
    public LiveData<User> getUser() {
        return mUser;
    }

    public LiveData<List<Category>> getCategories() {
        if (categories == null) {
            setCategories(new ArrayList<>());
        }
        return categories;
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

    public LiveData<List<OrderedProduct>> getCartProducts() {
        return cartProducts;
    }

    public LiveData<OrderDetails> getDetailedOrder() {
        return detailedOrder;
    }

    public LiveData<List<Chat>> getChats() {
        return chats;
    }

    public LiveData<List<ChatListItem>> getChatListItems() {
        return chatListItems;
    }

    /* setters */
    private void setUser(User user) {
        addUserToServer(user);
        mUser.setValue(user);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();

                if (user == null) {
                    unregisterUserToken(token);
                } else {
                    String uid = user.getUid();
                    registerUserToken(uid, token);
                }
            }
        });
    }

    private void setCategories(List<Category> categoryList) {
        for (Category c : categoryList)
            catMap.put(c.getName(), c.getId());
        categories.setValue(categoryList);
    }

    /* remote database querying */

        // --------- User ---------

    public GoogleSignInOptions getGoogleSignOptions() {
        if (gso == null) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
        }
        return gso;
    }

    public GoogleSignInClient getGoogleSignInClient() {
        if (mGoogleSignInClient == null) {
            mGoogleSignInClient = GoogleSignIn.getClient(mContext, getGoogleSignOptions());
        }
        return mGoogleSignInClient;
    }

    private CallbackManager getFacebookCallbackManager() {
        if (facebookCallbackManager == null) {
            facebookCallbackManager = CallbackManager.Factory.create();
        }
        return facebookCallbackManager;
    }

    public void signInLastSignedInUser() {
        GoogleSignInAccount googleSignInAccount;
        AccessToken facebookAccessToken;

        if ((facebookAccessToken = AccessToken.getCurrentAccessToken()) != null) {
            loginFromFacebookAccessToken(facebookAccessToken);
        } else if ((googleSignInAccount = GoogleSignIn.getLastSignedInAccount(mContext)) != null) {
            setUser(adaptUserFromGoogleSignInAccount(googleSignInAccount));
        } else {
            setUser(null);
        }
    }

    public void loginFromFacebookAccessToken(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String uid = object.getString("id");
                    String name = object.getString("name");
                    String email = object.getString("email");
                    Uri imgUrl = Uri.parse("https://graph.facebook.com/" + uid + "/picture?type=normal");

                    setUser(new User(uid, email, name, imgUrl));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                }
            }
        });

        Bundle params = new Bundle();
        params.putString("fields", "name,email,id");
        request.setParameters(params);
        request.executeAsync();
    }

    public void signInFromGoogleSignInTask(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            setUser(adaptUserFromGoogleSignInAccount(account));
        } catch (ApiException e) {
            setUser(null);
        }
    }

    public void signOut(Activity activity) {
        signOutFromGoogleAccount(activity);
        facebookLogout();
    }

    private void facebookLogout() {
        LoginManager.getInstance().logOut();
    }

    private void signOutFromGoogleAccount(Activity activity) {
        getGoogleSignInClient().signOut()
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        setUser(null);
                    }
                });
    }

    private void addUserToServer(User user) {
        if (user == null) return;

        StringRequest request = new StringRequest(
                Request.Method.POST,
                ADD_USER_URL,
                response -> {
                },
                error -> {
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", user.getUid());
                params.put("email", user.getEmail());
                params.put("fullname", user.getFullName());
                if (user.getImgUrl() != null)
                    params.put("img_dir", user.getImgUrl().toString());
                return params;
            }
        };

        enqueueRequest(request);
    }

    public void registerUserToken(String uid, String token) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                REGISTER_USER_TOKEN_URL,
                response -> {
                    Log.d(TAG, "registerUserToken response: " + response);
                },
                error -> {}
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("token", token);
                return params;
            }
        };

        enqueueRequest(request);
    }

    public void unregisterUserToken(String token) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                UNREGISTER_USER_TOKEN_URL,
                response -> {
                    Log.d(TAG, "unregisterUserToken response: " + response);
                },
                error -> {}
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };

        enqueueRequest(request);
    }

    // ------ category -------

    public long getCatIdByName(String categoryName) {
        Long id = catMap.get(categoryName);
        if (id == null) return -1;
        return id;
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
                    setCategories(categories);
                },
                error -> {
                    Toast.makeText(mContext, "Json Response Error", Toast.LENGTH_SHORT).show();
                }
        );

        enqueueRequest(request);
    }

    // ------ product -------

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
        String sellerToken = jsonObject.getString("seller_token");

        return new ProductDetails(pid, sellerId, product, category, imgUrl, date, price, seller, contact, sellerToken);
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

        enqueueRequest(request);
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

        enqueueRequest(request);
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

        enqueueRequest(request);
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

        enqueueRequest(request);
    }

    // ------ cart -------

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

        enqueueRequest(request);
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

        enqueueRequest(request);
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

        enqueueRequest(request);
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

        enqueueRequest(request);
    }

    // ------ chat -------

    private Chat getChat(JSONObject jsonObject) throws JSONException {
        String senderToken = jsonObject.getString("sender_token");
        String receiverToken = jsonObject.getString("receiver_token");
        long pid = jsonObject.getLong("pid");
        String msg = jsonObject.getString("msg");
        String time = jsonObject.getString("upload_time");

        return new Chat(senderToken, receiverToken, pid, msg, time);
    }

    private ChatListItem getChatListItem(JSONObject jsonObject) throws JSONException {
        long pid = jsonObject.getLong("pid");
        String senderToken = jsonObject.getString("sender_token");
        String receiverToken = jsonObject.getString("receiver_token");
        String msg = jsonObject.getString("msg");
        String sender = jsonObject.getString("sender");
        String imgUrl = jsonObject.getString("img_dir");

        return new ChatListItem(pid, senderToken, receiverToken, msg, sender, imgUrl);
    }

    public void sendMsg(String senderToken, String receiverToken, String pid, String msg) {
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
                params.put("pid", pid);
                params.put("msg", msg);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        enqueueRequest(request);
    }

    public void fetchChat(String senderToken, String receiverToken, String pid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                FETCH_CHATS_URL,
                response -> {
                    Log.d(TAG, "fetchChat response: " + response);
                    List<Chat> chList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            chList.add(getChat(jsonArray.getJSONObject(i)));
                        }
                        chats.setValue(chList);
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
                params.put("pid", pid);
                return params;
            }
        };

        enqueueRequest(request);
    }

    public void fetchChatListItems(String receiverToken) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                FETCH_CHAT_LIST_ITEMS_URL,
                response -> {
                    Log.d(TAG, "fetchChat response: " + response);
                    List<ChatListItem> chList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            chList.add(getChatListItem(jsonArray.getJSONObject(i)));
                        }
                        chatListItems.setValue(chList);
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
                params.put("receiver_token", receiverToken);
                return params;
            }
        };

        enqueueRequest(request);
    }

    /* adapters */

    private User adaptUserFromGoogleSignInAccount(GoogleSignInAccount account) {
        String uid = account.getId();
        String email = account.getEmail();
        String fullName = account.getDisplayName();
        Uri imgUrl = account.getPhotoUrl();

        return new User(uid, email, fullName, imgUrl);
    }

    private <T>void enqueueRequest(Request<T> request) {
        MySingleton.getInstance(mContext).enqueueRequest(request);
    }
}
