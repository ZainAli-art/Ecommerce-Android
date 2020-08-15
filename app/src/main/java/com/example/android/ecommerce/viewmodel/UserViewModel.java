package com.example.android.ecommerce.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.ecommerce.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class UserViewModel extends AndroidViewModel {
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
}
