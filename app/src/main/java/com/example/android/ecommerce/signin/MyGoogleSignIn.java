package com.example.android.ecommerce.signin;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.android.ecommerce.interfaces.SignInListener;
import com.example.android.ecommerce.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MyGoogleSignIn {
    private Context mContext;
    private SignInListener listener;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    public MyGoogleSignIn(Context context, SignInListener listener) {
        mContext = context;
        this.listener = listener;
    }

    public GoogleSignInClient getGoogleSignInClient() {
        if (mGoogleSignInClient == null) {
            mGoogleSignInClient = GoogleSignIn.getClient(mContext, getGoogleSignOptions());
        }
        return mGoogleSignInClient;
    }

    private GoogleSignInOptions getGoogleSignOptions() {
        if (gso == null) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
        }
        return gso;
    }

    public void signIn(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            listener.signIn(User.from(account));
        } catch (ApiException e) {
//            setUser(null);
            e.printStackTrace();
        }
    }

    public void signOut(Activity activity) {
        getGoogleSignInClient().signOut()
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        listener.signOut();
                    }
                });
    }
}
