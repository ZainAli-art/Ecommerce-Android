package com.example.android.ecommerce.interfaces;

import com.example.android.ecommerce.model.User;

public interface SignInListener {
    void signIn(User user);
    void signOut();
}
