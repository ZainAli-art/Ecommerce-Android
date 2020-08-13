package com.example.android.ecommerce;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.ecommerce.viewmodel.CustomerViewModel;

public class SignInFragment extends Fragment implements View.OnClickListener {
    private EditText email;
    private EditText password;
    private Button loginBtn;
    private CustomerViewModel viewModel;

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
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        loginBtn = view.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        viewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()))
                .get(CustomerViewModel.class);

        // add observers to live data from view model
        viewModel.getCustomer().observe(getViewLifecycleOwner(), customer -> {
            if (customer == null) {
                Toast.makeText(getContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
            } else {
                moveToHomeFragment(customer.getId());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginBtn:
                viewModel.login(email.getText().toString(), password.getText().toString());
                break;
            default:
                Toast.makeText(getContext(), "No functionality assigned yet.", Toast.LENGTH_SHORT).show();
        }
    }

    public void moveToHomeFragment(int uid) {
        Bundle args = new Bundle();
        args.putInt("uid", uid);
        navController.navigate(R.id.action_signInFragment_to_homeFragment, args);
    }
}