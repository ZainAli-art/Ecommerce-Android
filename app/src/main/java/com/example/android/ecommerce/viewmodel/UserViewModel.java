package com.example.android.ecommerce.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.android.ecommerce.MySingleton;
import com.example.android.ecommerce.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

public class UserViewModel extends AndroidViewModel {
    private static final String TAG = "UserViewModel";
    private static final String ADD_USER_URL = MySingleton.HOST_URL + "scripts/add-user.php";
    private Context mContext;
    private MutableLiveData<User> mUser;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    public UserViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mUser = new MutableLiveData<>();
    }

    public LiveData<User> getUser() {
        return mUser;
    }

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

    public User getLastSignedInUser() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);
        if (account == null) return null;
        return adaptUserFromGoogleSignInAccount(account);
    }

    private User adaptUserFromGoogleSignInAccount(GoogleSignInAccount account) {
        String uid = account.getId();
        String email = account.getEmail();
        String fullName = account.getDisplayName();
        Uri imgUrl = account.getPhotoUrl();

        return new User(uid, email, fullName, imgUrl);
    }

    public void setUser(User user) {
        addUserToServer(user);
        mUser.setValue(user);
    }

    public void signInFromGoogleSignInTask(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            setUser(adaptUserFromGoogleSignInAccount(account));
        } catch (ApiException e) {
            setUser(null);
        }
    }
    
    public void addUserToServer(User user) {
        if (user == null) return;

        Log.d(TAG, "addUserToServer: processing...");
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ADD_USER_URL,
                response -> {
                    Log.d(TAG, "addUserToServer response: " + response);
                },
                error -> {}
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d(TAG, "getParams: called");
                Map<String, String> params = new HashMap<>();
                params.put("uid", user.getUid());
                params.put("email", user.getEmail());
                params.put("fullname", user.getFullName());
                String imgUrl = user.getImgUrl() == null ? "null" : user.getImgUrl().toString();
                params.put("img_dir", imgUrl);
                return params;
            }
        };

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }
}
