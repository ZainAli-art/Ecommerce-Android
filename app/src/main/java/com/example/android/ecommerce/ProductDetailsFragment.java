package com.example.android.ecommerce;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.ecommerce.model.ProductDetails;
import com.example.android.ecommerce.viewmodel.CartViewModel;
import com.example.android.ecommerce.viewmodel.ProductViewModel;
import com.example.android.ecommerce.viewmodel.UserViewModel;

public class ProductDetailsFragment extends Fragment implements View.OnClickListener {
    public static final String PRODUCT_ID = "com.example.android.ecommerce.ProductDetailsFragment.PRODUCT_ID";

    private ImageView img;
    private TextView name;
    private TextView date;
    private TextView category;
    private TextView price;

    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel;
    private UserViewModel userViewModel;

    private String uid;
    private String pid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        img = view.findViewById(R.id.img);
        name = view.findViewById(R.id.name);
        date = view.findViewById(R.id.date);
        category = view.findViewById(R.id.category);
        price = view.findViewById(R.id.price);
        Button addToCartBtn = view.findViewById(R.id.addToCartBtn);
        addToCartBtn.setOnClickListener(this);

        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(ProductViewModel.class);
        cartViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(CartViewModel.class);
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(UserViewModel.class);

        Bundle args = getArguments();
        pid = args.getString(PRODUCT_ID);
        uid = String.valueOf(userViewModel.getUser().getValue().getUid());

        productViewModel.getDetailedProduct().observe(getViewLifecycleOwner(), productDetails -> {
            if (productDetails != null) {
                updateUi(productDetails);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        productViewModel.fetchProductDetailsByPid(pid);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addToCartBtn:
                addToCart();
                break;
        }
    }

    private void updateUi(ProductDetails productDetails) {
        Uri imgUrl = Uri.parse(productDetails.getImgUrl());
        Glide.with(requireContext()).load(imgUrl).into(img);
        name.setText(productDetails.getProduct());
        date.setText(productDetails.getDate());
        category.setText(productDetails.getCategory());
        price.setText("$ " + productDetails.getPrice());
    }

    private void addToCart() {
        cartViewModel.addToCart(uid, pid);
        NavHostFragment.findNavController(this).popBackStack();
    }
}