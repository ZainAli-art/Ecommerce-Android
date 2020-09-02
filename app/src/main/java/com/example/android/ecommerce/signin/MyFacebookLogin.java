package com.example.android.ecommerce.signin;

import android.net.Uri;
import android.os.Bundle;

import com.example.android.ecommerce.interfaces.SignInListener;
import com.example.android.ecommerce.model.User;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFacebookLogin {
    private SignInListener listener;
    private AccessTokenTracker facebookAccessTokenTracker;
    private CallbackManager callbackManager;

    public MyFacebookLogin(SignInListener listener) {
        this.listener = listener;
        facebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken != null)
                    loginFromAccessToken(currentAccessToken);
            }
        };
    }

    public void loginFromAccessToken(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String uid = object.getString("id");
                    String name = object.getString("name");
                    String email = object.getString("email");
                    Uri imgUrl = Uri.parse("https://graph.facebook.com/" + uid + "/picture?type=normal");

                    listener.signIn(new User(uid, email, name, imgUrl));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle params = new Bundle();
        params.putString("fields", "name,email,id");
        request.setParameters(params);
        request.executeAsync();
    }

    public void logout() {
        LoginManager.getInstance().logOut();
        listener.signOut();
    }

    public CallbackManager getCallbackManager() {
        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }
        return callbackManager;
    }
}
