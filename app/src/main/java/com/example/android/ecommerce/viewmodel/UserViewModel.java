package com.example.android.ecommerce.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserViewModel extends AndroidViewModel {
    private static final String TAG = "UserViewModel";
    private static final String ADD_USER_URL = MySingleton.HOST_URL + "scripts/add-user.php";
    private Context mContext;
    private MutableLiveData<User> mUser;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager facebookCallbackManager;
    private AccessTokenTracker facebookAccessTokenTracker;

    public UserViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mUser = new MutableLiveData<>();
        facebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken != null)
                    loginFromFacebookAccessToken(currentAccessToken);
            }
        };
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

    public void signInLastSignedInUser() {
        GoogleSignInAccount googleSignInAccount;
        AccessToken facebookAccessToken;

        if ((facebookAccessToken = AccessToken.getCurrentAccessToken()) != null) {
            Log.d(TAG, "signInLastSignedInUser: previous facebook account is not null");
            loginFromFacebookAccessToken(facebookAccessToken);
        } else if ((googleSignInAccount = GoogleSignIn.getLastSignedInAccount(mContext)) != null) {
            Log.d(TAG, "signInLastSignedInUser: previous google account is not null");
            setUser(adaptUserFromGoogleSignInAccount(googleSignInAccount));
        } else {
            setUser(null);
        }
    }

    public CallbackManager getFacebookCallbackManager() {
        if (facebookCallbackManager == null) {
            facebookCallbackManager = CallbackManager.Factory.create();
        }
        return facebookCallbackManager;
    }

    private User adaptUserFromGoogleSignInAccount(GoogleSignInAccount account) {
        String uid = account.getId();
        String email = account.getEmail();
        String fullName = account.getDisplayName();
        Uri imgUrl = account.getPhotoUrl();

        return new User(uid, email, fullName, imgUrl);
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

                    setUser(new User(uid, name, email, imgUrl));
                    Log.d(TAG, "onCompleted: new user set via facebook");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onCompleted error: " + e.getMessage());
                } catch (NullPointerException e) {
                    Log.d(TAG, "onCompleted error: " + e.getMessage());
                }
            }
        });

        Bundle params = new Bundle();
        params.putString("fields", "name,email,id");
        request.setParameters(params);
        request.executeAsync();
    }

    public void setUser(User user) {
        Log.d(TAG, "setUser user is: " + user);
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
    
    public void addUserToServer(User user) {
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

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }
}
