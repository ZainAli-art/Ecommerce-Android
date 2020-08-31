package com.example.android.ecommerce.repository;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.android.ecommerce.model.Category;
import com.example.android.ecommerce.model.Chat;
import com.example.android.ecommerce.model.ChatListItem;
import com.example.android.ecommerce.model.ECommerceNetwork;
import com.example.android.ecommerce.model.OrderDetails;
import com.example.android.ecommerce.model.OrderedProduct;
import com.example.android.ecommerce.model.Product;
import com.example.android.ecommerce.model.ProductDetails;
import com.example.android.ecommerce.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class ECommerceRepository {
    static volatile ECommerceRepository INSTANCE;

    private ECommerceNetwork eCommerceNetwork;

    public static ECommerceRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ECommerceRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ECommerceRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    private ECommerceRepository(Application application) {
        eCommerceNetwork = new ECommerceNetwork(application.getApplicationContext());
    }

    public LiveData<User> getUser() {
        return eCommerceNetwork.getUser();
    }

    public LiveData<List<Category>> getCategories() {
        return eCommerceNetwork.getCategories();
    }

    public LiveData<List<Product>> getProducts(String catId) {
        return eCommerceNetwork.getProducts(catId);
    }

    public LiveData<List<Product>> getRecentProducts() {
        return eCommerceNetwork.getRecentProducts();
    }

    public LiveData<ProductDetails> getProductDetails(String pid) {
        return eCommerceNetwork.getProductDetails(pid);
    }

    public LiveData<List<OrderedProduct>> getCartProducts(String uid) {
        return eCommerceNetwork.getCartProducts(uid);
    }

    public LiveData<OrderDetails> getOrderDetails(String oid) {
        return eCommerceNetwork.getOrderDetails(oid);
    }

    public LiveData<List<Chat>> getChats(String senderToken, String receiverToken, String pid) {
        return eCommerceNetwork.getChats(senderToken, receiverToken, pid);
    }

    public LiveData<List<ChatListItem>> getChatListItems(String receiverToken) {
        return eCommerceNetwork.getChatListItems(receiverToken);
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return eCommerceNetwork.getGoogleSignInClient();
    }

    public void signInLastSignedInUser() {
        eCommerceNetwork.signInLastSignedInUser();
    }

    public CallbackManager getFacebookCallbackManager()  {
        return eCommerceNetwork.getFacebookCallbackManager();
    }

    public void loginFromFacebookAccessToken(AccessToken accessToken) {
        eCommerceNetwork.loginFromFacebookAccessToken(accessToken);
    }

    public void signInFromGoogleSignInTask(Task<GoogleSignInAccount> completedTask) {
        eCommerceNetwork.signInFromGoogleSignInTask(completedTask);
    }

    public void signOut(Activity activity) {
        eCommerceNetwork.signOut(activity);
    }

    // ------ product -------

    public void uploadProduct(String uid, String pName, String catId, String img, String price) {
        eCommerceNetwork.uploadProduct(uid, pName, catId, img, price);
    }

    // ------ cart -------

    public void addToCart(String uid, String pid) {
        eCommerceNetwork.addToCart(uid, pid);
    }

    public void deleteOrder(String oid) {
        eCommerceNetwork.deleteOrder(oid);
    }

    // ------ chat -------

    public void sendMsg(String senderToken, String receiverToken, String pid, String msg) {
        eCommerceNetwork.sendMsg(senderToken, receiverToken, pid, msg);
    }
}
