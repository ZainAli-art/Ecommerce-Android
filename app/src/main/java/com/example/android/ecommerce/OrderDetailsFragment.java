package com.example.android.ecommerce;

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
import com.example.android.ecommerce.model.OrderDetails;
import com.example.android.ecommerce.viewmodel.CartViewModel;

public class OrderDetailsFragment extends Fragment implements View.OnClickListener {
    public static final String ORDER_DETAILS_ID = "com.example.android.ecommerce.ORDER_DETAILS_ID";
    public static final String USER_ID = "com.example.android.ecommerce.USER_ID";

    private NavController navController;
    private CartViewModel cartViewModel;
    private ImageView img;
    private TextView productName;
    private TextView date;
    private TextView category;
    private TextView price;
    private TextView orderId;

    private long oid;
    private String uid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        oid = args.getLong(ORDER_DETAILS_ID);
        uid = args.getString(USER_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        cartViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(CartViewModel.class);

        img = view.findViewById(R.id.img);
        productName = view.findViewById(R.id.productName);
        date = view.findViewById(R.id.date);
        category = view.findViewById(R.id.category);
        price = view.findViewById(R.id.price);
        orderId = view.findViewById(R.id.orderId);
        Button removeFromCartBtn = view.findViewById(R.id.removeFromCartBtn);
        removeFromCartBtn.setOnClickListener(this);

        cartViewModel.getOrderDetails(oid).observe(getViewLifecycleOwner(), this::updateUi);
    }

    private void updateUi(OrderDetails orderDetails) {
        Glide.with(requireContext()).load(orderDetails.imgUrl).into(img);
        productName.setText(orderDetails.productName);
        date.setText(orderDetails.orderDate.toString());
        category.setText(orderDetails.category);
        price.setText("$ " + orderDetails.price);
        orderId.setText(String.valueOf(orderDetails.oid));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.removeFromCartBtn:
                removeOrder();
                break;
        }
    }

    private void removeOrder() {
        cartViewModel.deleteOrder(oid);
        navController.popBackStack();
    }
}