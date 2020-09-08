package com.example.android.ecommerce;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.ecommerce.model.Fcm;
import com.example.android.ecommerce.viewmodel.FcmViewModel;
import com.example.android.ecommerce.viewmodel.UserViewModel;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;

public class SignInFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SignInFragment";

    private static final int RC_SIGN_IN = 1;

    private NavController navController;

    private UserViewModel userViewModel;
    private FcmViewModel fcmViewModel;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()))
                .get(UserViewModel.class);
        fcmViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()))
                .get(FcmViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        // initialize components
        SignInButton googleSignInButton = view.findViewById(R.id.googleSignInBtn);
        LoginButton facebookLoginBtn = view.findViewById(R.id.facebookLoginBtn);

        googleSignInButton.setOnClickListener(this);
        facebookLoginBtn.setPermissions(Arrays.asList("email", "public_profile"));
        facebookLoginBtn.registerCallback(userViewModel.getFacebookCallbackManager(), new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                userViewModel.loginFromFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        facebookLoginBtn.setOnClickListener(this);

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                navController.popBackStack();

                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d(TAG, "onSuccess: called");
                        fcmViewModel.update(new Fcm(instanceIdResult.getToken(), user.uid));
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        userViewModel.getFacebookCallbackManager().onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            userViewModel.signInFromGoogleSignInTask(task);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.googleSignInBtn:
                googleSignIn();
                break;
            case R.id.facebookLoginBtn:
                facebookLogin();
                break;
            default:
                Toast.makeText(getContext(), "No functionality assigned yet.", Toast.LENGTH_SHORT).show();
        }
    }

    private void googleSignIn() {
        Intent intent = userViewModel.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void facebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }
}