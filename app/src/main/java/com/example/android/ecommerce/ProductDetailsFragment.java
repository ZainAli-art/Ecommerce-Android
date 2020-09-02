package com.example.android.ecommerce;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class ProductDetailsFragment extends Fragment implements View.OnClickListener {
    public static final String PRODUCT_ID = "productId";

    private NavController navController;

    private ImageView img;
    private TextView name;
    private TextView date;
    private TextView category;
    private TextView price;
    private TextView seller;
    private TextView contact;

    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel;
    private UserViewModel userViewModel;

    private String uid;
    private long pid;
    private String sellerToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        img = view.findViewById(R.id.img);
        name = view.findViewById(R.id.name);
        date = view.findViewById(R.id.date);
        category = view.findViewById(R.id.category);
        price = view.findViewById(R.id.price);
        seller = view.findViewById(R.id.seller);
        contact = view.findViewById(R.id.contact);
        Button addToCartBtn = view.findViewById(R.id.addToCartBtn);
        addToCartBtn.setOnClickListener(this);
        view.findViewById(R.id.chatSellerButton).setOnClickListener(this);

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
        pid = args.getLong(PRODUCT_ID);
        uid = String.valueOf(userViewModel.getUser().getValue().uid);

        productViewModel.getProductDetails(pid).observe(getViewLifecycleOwner(), productDetails -> {
            if (productDetails != null) {
                updateUi(productDetails);
                sellerToken = productDetails.sellerToken;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addToCartBtn:
                addToCart();
                break;
            case R.id.chatSellerButton:
                openChat();
                break;
        }
    }

    private void updateUi(ProductDetails productDetails) {
        Uri imgUrl = Uri.parse(productDetails.imgUrl);
        Glide.with(requireContext()).load(imgUrl).into(img);
        name.setText(productDetails.product);
        date.setText(productDetails.date.toString());
        category.setText(productDetails.category);
        price.setText("$ " + productDetails.price);
        seller.setText(productDetails.seller);
        contact.setText(productDetails.contact);
    }

    private void addToCart() {
        cartViewModel.addToCart(uid, pid);
        navController.popBackStack();
    }

    private void openChat() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Bundle args = new Bundle();
                args.putString(ChatFragment.SENDER_TOKEN_KEY, instanceIdResult.getToken());
                args.putString(ChatFragment.RECEIVER_TOKEN_KEY, sellerToken);
                args.putLong(ChatFragment.PRODUCT_ID_KEY, pid);

                navController.navigate(R.id.action_productDetailsFragment_to_chatFragment, args);
            }
        });
    }
}