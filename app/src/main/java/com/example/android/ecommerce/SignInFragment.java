package com.example.android.ecommerce;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.ecommerce.viewmodel.UserViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;

public class SignInFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SignInFragment";
    private static final int RC_SIGN_IN = 1;
    private SignInButton googleSignInButton;

    private UserViewModel userViewModel;
    private NavController navController;

    public SignInFragment() {
        // Required empty public constructor
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
        // initialize components
        navController = Navigation.findNavController(view);
        googleSignInButton = view.findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(this);
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()))
                .get(UserViewModel.class);

        userViewModel.getUser().observe(requireActivity(), user -> {
            if (user != null) {
                navController.popBackStack();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            userViewModel.signInFromGoogleSignInTask(task);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.googleSignInButton:
                googleSignIn();
                break;
            default:
                Toast.makeText(getContext(), "No functionality assigned yet.", Toast.LENGTH_SHORT).show();
        }
    }

    private void googleSignIn() {
        Log.d(TAG, "googleSignIn: called");
        Intent intent = userViewModel.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }
}