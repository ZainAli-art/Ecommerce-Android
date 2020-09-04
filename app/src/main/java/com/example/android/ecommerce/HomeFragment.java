package com.example.android.ecommerce;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.ecommerce.adapters.CategoryRecyclerViewAdapter;
import com.example.android.ecommerce.adapters.ProductRecyclerViewAdapter;
import com.example.android.ecommerce.model.Category;
import com.example.android.ecommerce.model.Product;
import com.example.android.ecommerce.viewmodel.CategoryViewModel;
import com.example.android.ecommerce.viewmodel.ProductViewModel;
import com.example.android.ecommerce.viewmodel.UserViewModel;

public class HomeFragment extends Fragment implements CategoryRecyclerViewAdapter.CategoryItemListener,
        ProductRecyclerViewAdapter.ProductItemListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String SELECTED_CAT_ID = "SELECTED_CAT_ID";
    public static final int REQUEST_INTERNET = 1;
    public static final int RECENT_PRODUCTS_LIMIT = 5;

    private NavController navController;
    private CategoryViewModel categoryViewModel;
    private UserViewModel userViewModel;
    private ProductViewModel productViewModel;
    private ProductRecyclerViewAdapter recentProductsAdapter;
    private CategoryRecyclerViewAdapter categoryAdapter;

    private String uid;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        categoryViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(CategoryViewModel.class);
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(UserViewModel.class);
        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(ProductViewModel.class);
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
        categoryAdapter = new CategoryRecyclerViewAdapter(Category.HORIZONTAL_TYPE, this);
        categoryRecyclerView.setAdapter(categoryAdapter);

        RecyclerView recentProductsRecyclerView = view.findViewById(R.id.recentProductsRecyclerView);
        recentProductsAdapter = new ProductRecyclerViewAdapter(Product.HORIZONTAL_TYPE, this);
        recentProductsRecyclerView.setAdapter(recentProductsAdapter);

        SwipeRefreshLayout homeSwipeRefreshLayout = view.findViewById(R.id.homeSwipeRefreshLayout);
        homeSwipeRefreshLayout.setOnRefreshListener(this);

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                navController.navigate(R.id.action_homeFragment_to_signInFragment);
            } else {
                uid = user.uid;
            }
        });
        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (internetPermissionGranted()) {
                categoryAdapter.setItems(categories);
            }
        });
        productViewModel.getRecentProducts(RECENT_PRODUCTS_LIMIT).observe(getViewLifecycleOwner(), recentProducts -> {
            if (internetPermissionGranted()) {
                recentProductsAdapter.setItems(recentProducts);
                homeSwipeRefreshLayout.setRefreshing(false);
            }
        });

        requestPermissions(new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET);
    }

    @Override
    public void onClickCategory(int pos) {
        long catId = categoryAdapter.getItem(pos).id;
        Bundle args = new Bundle();
        args.putLong(SELECTED_CAT_ID, catId);

        navController.navigate(R.id.action_homeFragment_to_productListFragment, args);
    }

    @Override
    public void onClickProduct(int pos) {
        Bundle args = new Bundle();
        long pid = recentProductsAdapter.getItem(pos).pid;
        args.putLong(ProductDetailsFragment.PRODUCT_ID, pid);
        args.putString(ProductDetailsFragment.USER_ID, uid);

        navController.navigate(R.id.action_homeFragment_to_productDetailsFragment, args);
    }

    @Override
    public void onRefresh() {
        categoryViewModel.refreshCategories();
        productViewModel.refreshProducts();
    }

    private boolean internetPermissionGranted() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED;
    }
}