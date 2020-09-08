package com.example.android.ecommerce;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.ecommerce.viewmodel.UserViewModel;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private UserViewModel userViewModel;
    private NavController navController;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        ImageView profileImg = view.findViewById(R.id.profileImg);
        TextView fullName = view.findViewById(R.id.fullName);
        TextView email = view.findViewById(R.id.email);
        Button signOutBtn = view.findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(this);

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                NavOptions options = new NavOptions.Builder()
                        .setPopUpTo(R.id.homeFragment, true)
                        .build();
                navController.navigate(R.id.homeFragment, null, options);
            } else {
                Uri imgUrl = user.imgUrl;
                if (imgUrl != null)
                    Glide.with(ProfileFragment.this).load(imgUrl).into(profileImg);
                fullName.setText(user.fullName);
                email.setText(user.email);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signOutBtn:
                userViewModel.signOut(requireActivity());
                break;
        }
    }
}