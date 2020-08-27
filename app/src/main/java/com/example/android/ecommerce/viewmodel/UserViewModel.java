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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class UserViewModel extends AndroidViewModel {
    private static final String TAG = "UserViewModel";

    public static final String BASE_USER_URL = HOST_URL + "scripts/user/";
    public static final String BASE_FCM_URL = HOST_URL + "scripts/fcm/";
    private static final String ADD_USER_URL = BASE_USER_URL + "add-user.php";
    private static final String REGISTER_USER_TOKEN_URL = BASE_FCM_URL + "register-user-fcm-token.php";
    private static final String UNREGISTER_USER_TOKEN_URL = BASE_FCM_URL + "unregister-user-token.php";
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
            loginFromFacebookAccessToken(facebookAccessToken);
        } else if ((googleSignInAccount = GoogleSignIn.getLastSignedInAccount(mContext)) != null) {
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

    public void setUser(User user) {
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

        MySingleton.getInstance(mContext).enqueueRequest(request);
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

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }
}
