package com.example.android.ecommerce;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.transition.TransitionInflater;

import androidx.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.ecommerce.model.ProductDetails;
import com.example.android.ecommerce.viewmodel.CartViewModel;
import com.example.android.ecommerce.viewmodel.ProductViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import static com.example.android.ecommerce.utils.Constants.BASE_URL;

public class ProductDetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ProductDetailsFragment";

    public static final String PRODUCT_ID = "PRODUCT_ID";
    public static final String USER_ID = "USER_ID";

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

    private String uid;
    private long pid;
    private String transitionName;
    private String sellerToken;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Transition transition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.product_img_trans);
        setSharedElementEnterTransition(transition);

        Bundle args = getArguments();
        pid = args.getLong(PRODUCT_ID);
        uid = args.getString(USER_ID);
        transitionName = args.getString("name");

        Log.d(TAG, "onCreate pid: " + pid);

        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(ProductViewModel.class);
        cartViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(CartViewModel.class);
    }

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

        Log.d(TAG, "onViewCreated transitionName: " + transitionName);
        ViewCompat.setTransitionName(img, transitionName);

        postponeEnterTransition();
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
        Glide.with(requireContext())
                .load(BASE_URL + imgUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(img);
        name.setText(productDetails.product);
        date.setText(productDetails.date.toString());
        category.setText(productDetails.category);
        price.setText("$ " + productDetails.price);
        seller.setText(productDetails.seller);
        contact.setText(productDetails.contact);
    }

    private void addToCart() {
        cartViewModel.addToCart(uid, pid);
        cartViewModel.refreshCart(uid);
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