package com.example.android.ecommerce;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.ecommerce.adapters.OrderedProductRecyclerViewAdapter;
import com.example.android.ecommerce.viewmodel.CartViewModel;
import com.example.android.ecommerce.viewmodel.UserViewModel;

public class CartFragment extends Fragment implements OrderedProductRecyclerViewAdapter.OrderedProductItemListener,
        SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "CartFragment";

    private NavController navController;

    private CartViewModel cartViewModel;
    private UserViewModel userViewModel;

    private String uid;
    private OrderedProductRecyclerViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cartViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(CartViewModel.class);
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        SwipeRefreshLayout cartSwipeRefreshLayout = view.findViewById(R.id.cartSwipeRefreshLayout);
        cartSwipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        adapter = new OrderedProductRecyclerViewAdapter(this);
        cartRecyclerView.setAdapter(adapter);

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            uid = user.uid;
            Log.d(TAG, "onViewCreated uid: " + uid);
            cartViewModel.getCartProducts(uid).observe(getViewLifecycleOwner(), cartProducts -> {
                adapter.setItems(cartProducts);
                cartSwipeRefreshLayout.setRefreshing(false);
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClickOrderedProduct(int pos) {
        Bundle args = new Bundle();
        String oid = String.valueOf(adapter.getItems().get(pos).oid);
        args.putString(OrderDetailsFragment.ORDER_DETAILS_ID, oid);
        args.putString(OrderDetailsFragment.USER_ID, uid);

        navController.navigate(R.id.action_cartFragment_to_orderDetailsFragment, args);
    }

    @Override
    public void onClickDeleteOrderedProduct(int pos) {
        long oid = adapter.getItems().get(pos).oid;
        adapter.getItems().remove(pos);
        adapter.notifyItemRemoved(pos);
        cartViewModel.deleteOrder(oid);
    }

    @Override
    public void onRefresh() {
        cartViewModel.refreshCart(uid);
    }
}