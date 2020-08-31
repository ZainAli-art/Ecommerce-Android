package com.example.android.ecommerce;

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

import com.example.android.ecommerce.adapters.OrderedProductRecyclerViewAdapter;
import com.example.android.ecommerce.viewmodel.CartViewModel;
import com.example.android.ecommerce.viewmodel.UserViewModel;

public class CartFragment extends Fragment implements OrderedProductRecyclerViewAdapter.OrderedProductItemListener {
    private NavController navController;
    private CartViewModel cartViewModel;

    private String uid;
    private OrderedProductRecyclerViewAdapter adapter;

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

        RecyclerView cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        adapter = new OrderedProductRecyclerViewAdapter(this);
        cartRecyclerView.setAdapter(adapter);

        cartViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(CartViewModel.class);
        UserViewModel userViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(UserViewModel.class);
        uid = userViewModel.getUser().getValue().getUid();

        cartViewModel.getCartProducts(uid).observe(getViewLifecycleOwner(), cartProducts -> {
            adapter.setOrderedProducts(cartProducts);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClickOrderedProduct(int pos) {
        Bundle args = new Bundle();
        String oid = String.valueOf(cartViewModel.getCartProducts(uid).getValue().get(pos).getOid());
        args.putString(OrderDetailsFragment.ORDER_DETAILS_ID, oid);
        args.putString(OrderDetailsFragment.USER_ID, uid);

        navController.navigate(R.id.action_cartFragment_to_orderDetailsFragment, args);
    }

    @Override
    public void onClickDeleteOrderedProduct(int pos) {
        String oid = String.valueOf(adapter.getOrderedProducts().get(pos).getOid());
        cartViewModel.deleteOrder(oid);
        adapter.getOrderedProducts().remove(pos);
        adapter.notifyItemRemoved(pos);
    }
}