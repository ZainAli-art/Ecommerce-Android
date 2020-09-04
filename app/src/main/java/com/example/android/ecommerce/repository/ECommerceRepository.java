package com.example.android.ecommerce.repository;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.android.ecommerce.dao.CategoryDAO;
import com.example.android.ecommerce.dao.ChatDao;
import com.example.android.ecommerce.dao.FcmDao;
import com.example.android.ecommerce.dao.OrderDao;
import com.example.android.ecommerce.dao.ProductDao;
import com.example.android.ecommerce.dao.UserDao;
import com.example.android.ecommerce.database.LocalDb;
import com.example.android.ecommerce.database.Webservice;
import com.example.android.ecommerce.interfaces.SignInListener;
import com.example.android.ecommerce.model.Category;
import com.example.android.ecommerce.model.Chat;
import com.example.android.ecommerce.model.ChatListItem;
import com.example.android.ecommerce.model.Fcm;
import com.example.android.ecommerce.model.OrderDetails;
import com.example.android.ecommerce.model.OrderedProduct;
import com.example.android.ecommerce.model.Product;
import com.example.android.ecommerce.model.ProductDetails;
import com.example.android.ecommerce.model.User;
import com.example.android.ecommerce.signin.MyFacebookLogin;
import com.example.android.ecommerce.signin.MyGoogleSignIn;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.android.ecommerce.utils.Constants.BASE_URL;

public class ECommerceRepository implements SignInListener {
    private static final String TAG = "ECommerceRepository";

    static volatile ECommerceRepository INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    private ExecutorService executor;

    private Webservice webservice;

    private CategoryDAO categoryDAO;
    private ChatDao chatDao;
    private OrderDao orderDao;
    private ProductDao productDao;
    private UserDao userDao;
    private FcmDao fcmDao;

    private MyGoogleSignIn googleSignIn;
    private MyFacebookLogin facebookLogin;

    private RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            refreshCategories();
            refreshProducts();
        }
    };

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
        executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        webservice = retrofit.create(Webservice.class);

        LocalDb localDb = LocalDb.getDatabase(application, sRoomDatabaseCallback);
        categoryDAO = localDb.categoryDAO();
        chatDao = localDb.chatDao();
        orderDao = localDb.orderDao();
        productDao = localDb.productDao();
        userDao = localDb.userDao();
        fcmDao = localDb.fcmDao();

        googleSignIn = new MyGoogleSignIn(application.getApplicationContext(), this);
        facebookLogin = new MyFacebookLogin(this);
    }

    public LiveData<User> getUser() {
        return userDao.loadUser();
    }

    public LiveData<List<Category>> getCategories() {
        return categoryDAO.loadCategories();
    }

    public LiveData<List<Product>> getProducts(long catId) {
        return productDao.loadProducts(catId);
    }

    public LiveData<List<Product>> getRecentProducts(int limit) {
        return productDao.loadRecentProducts(limit);
    }

    public LiveData<ProductDetails> getProductDetails(long pid) {
        refreshProductDetails(pid);
        return productDao.loadProductDetails(pid);
    }

    public LiveData<List<OrderedProduct>> getCartProducts(String uid) {
        return orderDao.loadOrderedProducts(uid);
    }

    public LiveData<OrderDetails> getOrderDetails(long oid) {
        return orderDao.loadOrderDetails(oid);
    }

    public LiveData<List<Chat>> getChats(String senderToken, String receiverToken, long pid) {
        return chatDao.loadChats(senderToken, receiverToken, pid);
    }

    public LiveData<List<ChatListItem>> getChatListItems() {
        return chatDao.loadChatListItems();
    }

    public void insertProduct(String uid, String pName, long catId, String img, String price) {
        executor.execute(() -> {
            try {
                webservice.insertProduct(uid, pName, catId, img, price).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void insertOrder(String uid, long pid) {
        executor.execute(() -> {
            try {
                webservice.insertOrder(uid, pid).execute();
            } catch (IOException e) {
                Log.d(TAG, "insertOrder error: " + e.getMessage());
            }
        });
    }

    public void insertChat(String senderToken, String receiverToken, long pid, String msg) {
        executor.execute(() -> {
            try {
                List<Chat> chats = webservice.insertChat(senderToken, receiverToken, pid, msg).execute().body();
                chatDao.insertChats(chats);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void insertFcmToken(String token) {
        executor.execute(() -> {
            try {
                webservice.insertFcmToken(token).execute();
                fcmDao.insert(new Fcm(token, null));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateFcmToken(String oldToken, String newToken) {
        executor.execute(() -> {
            try {
                webservice.updateFcmToken(oldToken, newToken).execute();
                fcmDao.update(oldToken, newToken);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateFcmToken(Fcm fcm) {
        Log.d(TAG, "inserted fcm: " + fcm);
        executor.execute(() -> {
            try {
                fcmDao.insert(fcm);
                webservice.insertFcmToken(fcm.token, fcm.uid).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteOrder(long oid) {
        executor.execute(() -> {
            try {
                webservice.deleteOrder(oid).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void signOut(Activity activity) {
        googleSignIn.signOut(activity);
        facebookLogin.logout();
    }

    public CallbackManager getFacebookCallbackManager() {
        return facebookLogin.getCallbackManager();
    }

    public void loginFromFacebookAccessToken(AccessToken accessToken) {
        facebookLogin.loginFromAccessToken(accessToken);
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignIn.getGoogleSignInClient();
    }

    public void signInFromGoogleSignInTask(Task<GoogleSignInAccount> completedTask) {
        googleSignIn.signIn(completedTask);
    }

    @Override
    public void signIn(User user) {
        executor.execute(() -> userDao.insertUser(user));
    }

    @Override
    public void signOut() {
        executor.execute(() -> userDao.deleteUsers());
    }

    public void refreshCategories() {
        executor.execute(() -> {
            try {
                categoryDAO.insertCategories(webservice.getCategories().execute().body());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    public void refreshProducts() {
        Log.d(TAG, "refreshProducts: called");
        executor.execute(() -> {
            try {
                productDao.insertProducts(webservice.getProducts().execute().body());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void refreshProductDetails(long pid) {
        executor.execute(() -> {
            try {
                ProductDetails details = webservice.getProductDetails(pid).execute().body();
                productDao.insertProductDetails(details);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void refreshOrders(String uid) {
        executor.execute(() -> {
            try {
                orderDao.insertOrders(webservice.getOrders(uid).execute().body());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void refreshChats(String senderToken, String receiverToken, long pid) {
        executor.execute(() -> {
            try {
                chatDao.insertChats(webservice.getChats(senderToken, receiverToken, pid).execute().body());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
