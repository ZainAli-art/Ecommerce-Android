package com.example.android.ecommerce.database;

import com.example.android.ecommerce.model.Category;
import com.example.android.ecommerce.model.Chat;
import com.example.android.ecommerce.model.ChatListItem;
import com.example.android.ecommerce.model.Fcm;
import com.example.android.ecommerce.model.Order;
import com.example.android.ecommerce.model.OrderDetails;
import com.example.android.ecommerce.model.OrderedProduct;
import com.example.android.ecommerce.model.Product;
import com.example.android.ecommerce.model.ProductDetails;
import com.example.android.ecommerce.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Webservice {
    @FormUrlEncoded
    @POST("scripts/user/add-user.php")
    Call<User> insertUser(@Field("uid") String uid, @Field("email") String email, @Field("full_name") String fullName, @Field("img_dir") String imgUrl);

    @FormUrlEncoded
    @POST("scripts/product/upload-product.php")
    Call<Product> insertProduct(@Field("uid") String uid, @Field("pname") String pName, @Field("cat_id") long catId, @Field("image") String img, @Field("price") String price);

    @FormUrlEncoded
    @POST("scripts/order/add-order.php")
    Call<Order> insertOrder(@Field("uid") String uid, @Field("pid") long pid);

    @FormUrlEncoded
    @POST("scripts/chat/send-new-msg.php")
    Call<Chat> insertChat(@Field("sender_token") String senderToken, @Field("receiver_token") String receiverToken, @Field("pid") long pid, @Field("msg") String msg);

    @FormUrlEncoded
    @POST("scripts/fcm/register-user-fcm-token.php")
    Call<Fcm> insertFcmToken(@Field("token") String token, @Field("uid") String uid);

    @FormUrlEncoded
    @POST("scripts/fcm/add-fcm-token.php")
    Call<Fcm> insertFcmToken(@Field("fcm_token") String token);

    @GET("scripts/category/all-categories-json.php")
    Call<List<Category>> getCategories();

    @GET("scripts/product/all-products-json.php")
    Call<List<Product>> getProducts();

    @GET("scripts/product/products-by-cat_id-json.php")
    Call<List<Product>> getProducts(@Query("cat_id") long catId);

    @GET("scripts/product/fetch-recent-products-by-limit-json.php")
    Call<List<Product>> getRecentProducts();

    @GET("scripts/product/fetch-product-details-by-pid-json.php")
    Call<ProductDetails> getProductDetails(@Query("pid") long pid);

    @FormUrlEncoded
    @POST("scripts/order/orders-by-uid-json.php")
    Call<List<Order>> getOrders(@Field("uid") String uid);

    @FormUrlEncoded
    @POST("scripts/order/ordered-products-by-uid-json.php")
    Call<List<OrderedProduct>> getCartProducts(@Field("uid") String uid);

    @FormUrlEncoded
    @POST("scripts/order/order-details-by-oid-json.php")
    Call<OrderDetails> getOrderDetails(@Field("pid") long pid);

    @FormUrlEncoded
    @POST("scripts/chat/print-chat-json.php")
    Call<List<Chat>> getChats(@Field("sender_token") String senderToken, @Field("receiver_token") String receiverToken, @Field("pid") long pid);

    @FormUrlEncoded
    @POST("scripts/chat/print-chat-list-json.php")
    Call<List<ChatListItem>> getChatListItems( @Field("receiver_token") String receiverToken);

    @FormUrlEncoded
    @POST("scripts/fcm/update-fcm-token.php")
    Call<Void> updateFcmToken(@Field("old_token") String oldToken, @Field("new_token") String newToken);

    @FormUrlEncoded
    @POST("scripts/order/delete-order-by-oid.php")
    Call<Void> deleteOrder(@Field("oid") long oid);

    @FormUrlEncoded
    @POST("scripts/fcm/unregister-user-token.php")
    Call<Void> deleteUserToken(@Field("token") String token);
}
