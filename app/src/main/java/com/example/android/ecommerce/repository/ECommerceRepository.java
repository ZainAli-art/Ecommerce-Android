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
    private ECommerceNetwork eCommerceNetwork;

    public ECommerceRepository(Application application) {
        eCommerceNetwork = ECommerceNetwork.getNetwork(application.getApplicationContext());
    }

    public LiveData<User> getUser() {
        return eCommerceNetwork.getUser();
    }

    public LiveData<List<Category>> getCategories() {
        return eCommerceNetwork.getCategories();
    }

    public LiveData<List<Product>> getProducts() {
        return eCommerceNetwork.getProducts();
    }

    public LiveData<List<Product>> getRecentProducts() {
        return eCommerceNetwork.getRecentProducts();
    }

    public LiveData<ProductDetails> getDetailedProduct() {
        return eCommerceNetwork.getDetailedProduct();
    }

    public LiveData<List<OrderedProduct>> getCartProducts() {
        return eCommerceNetwork.getCartProducts();
    }

    public LiveData<OrderDetails> getDetailedOrder() {
        return eCommerceNetwork.getDetailedOrder();
    }

    public LiveData<List<Chat>> getChats() {
        return eCommerceNetwork.getChats();
    }

    public LiveData<List<ChatListItem>> getChatListItems() {
        return eCommerceNetwork.getChatListItems();
    }

    public long getCatIdByName(String categoryName) {
        return eCommerceNetwork.getCatIdByName(categoryName);
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

    // ------ category -------

    public void refreshCategories() {
        eCommerceNetwork.refreshCategories();
    }

    // ------ product -------

    public void fetchProductDetailsByPid(String pid) {
        eCommerceNetwork.fetchProductDetailsByPid(pid);
    }

    public void fetchProductsByCatId(String catId) {
        eCommerceNetwork.fetchProductsByCatId(catId);
    }

    public void fetchRecentProducts() {
        eCommerceNetwork.fetchRecentProducts();
    }

    public void uploadProduct(String uid, String pName, String catId, String img, String price) {
        eCommerceNetwork.uploadProduct(uid, pName, catId, img, price);
    }

    // ------ cart -------

    public void addToCart(String uid, String pid) {
        eCommerceNetwork.addToCart(uid, pid);
    }

    public void fetchCartProducts(String uid) {
        eCommerceNetwork.fetchCartProducts(uid);
    }

    public void fetchOrderDetails(String oid) {
        eCommerceNetwork.fetchOrderDetails(oid);
    }

    public void deleteOrder(String oid) {
        eCommerceNetwork.deleteOrder(oid);
    }

    // ------ chat -------

    public void sendMsg(String senderToken, String receiverToken, String pid, String msg) {
        eCommerceNetwork.sendMsg(senderToken, receiverToken, pid, msg);
    }

    public void fetchChat(String senderToken, String receiverToken, String pid) {
        eCommerceNetwork.fetchChat(senderToken, receiverToken, pid);
    }

    public void fetchChatListItems(String receiverToken) {
        eCommerceNetwork.fetchChatListItems(receiverToken);
    }
}
