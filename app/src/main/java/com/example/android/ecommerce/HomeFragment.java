package com.example.android.ecommerce;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.ecommerce.adapters.CategoryRecyclerViewAdapter;
import com.example.android.ecommerce.model.Category;
import com.example.android.ecommerce.utils.NavigationUtils;
import com.example.android.ecommerce.viewmodel.CategoryViewModel;
import com.example.android.ecommerce.viewmodel.UserViewModel;

import java.util.List;

public class HomeFragment extends Fragment implements CategoryRecyclerViewAdapter.CategoryItemListener {
    public static final int REQUEST_INTERNET = 1;
    public static final String SELECTED_CAT_ID = "com.example.android.ecommerce.HomeFragment.cat_id";

    private List<Category> categories;
    private CategoryRecyclerViewAdapter adapter;
    private NavController navController;
    private CategoryViewModel categoryViewModel;
    private UserViewModel userViewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        adapter = new CategoryRecyclerViewAdapter(Category.HORIZONTAL_TYPE, this);
        categoryRecyclerView.setAdapter(adapter);
        navController = NavHostFragment.findNavController(this);
        categoryViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(CategoryViewModel.class);
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(UserViewModel.class);

        // check if any user was previously logged in
        userViewModel.signInLastSignedInUser();

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null && NavigationUtils.isValidInContext(navController, R.id.homeFragment)) {
                navController.navigate(R.id.action_homeFragment_to_signInFragment);
            }
        });

        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            this.categories = categories;
            adapter.setCategories(categories);
        });

        requestPermissions(new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_INTERNET) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                categoryViewModel.refreshCategories();
            } else {
                Toast.makeText(requireContext(), "Internet Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClickCategory(int pos) {
        NavController navController = NavHostFragment.findNavController(HomeFragment.this);

        String catId = String.valueOf(categories.get(pos).getId());
        Bundle args = new Bundle();
        args.putString(SELECTED_CAT_ID, catId);

        navController.navigate(R.id.action_homeFragment_to_productListFragment, args);
    }
}