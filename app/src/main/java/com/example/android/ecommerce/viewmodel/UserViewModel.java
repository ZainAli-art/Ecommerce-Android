package com.example.android.ecommerce.viewmodel;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.ecommerce.model.User;
import com.example.android.ecommerce.repository.ECommerceRepository;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;

public class UserViewModel extends AndroidViewModel {
    private ECommerceRepository repo;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repo = new ECommerceRepository(application);
    }

    public LiveData<User> getUser() {
        return repo.getUser();
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return repo.getGoogleSignInClient();
    }

    public void signInLastSignedInUser() {
        repo.signInLastSignedInUser();
    }

    public CallbackManager getFacebookCallbackManager() {
        return repo.getFacebookCallbackManager();
    }

    public void loginFromFacebookAccessToken(AccessToken accessToken) {
        repo.loginFromFacebookAccessToken(accessToken);
    }

    public void signInFromGoogleSignInTask(Task<GoogleSignInAccount> completedTask) {
        repo.signInFromGoogleSignInTask(completedTask);
    }

    public void signOut(Activity activity) {
        repo.signOut(activity);
    }
}
