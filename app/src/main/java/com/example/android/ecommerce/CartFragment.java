package com.example.android.ecommerce;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.ecommerce.adapters.ProductRecyclerViewAdapter;
import com.example.android.ecommerce.model.Product;
import com.example.android.ecommerce.viewmodel.CartViewModel;
import com.example.android.ecommerce.viewmodel.UserViewModel;

public class CartFragment extends Fragment implements ProductRecyclerViewAdapter.ProductItemListener {
    private RecyclerView cartRecyclerView;
    private CartViewModel cartViewModel;
    private UserViewModel userViewModel;

    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        ProductRecyclerViewAdapter adapter = new ProductRecyclerViewAdapter(Product.VERTICAL_TYPE, this);
        cartRecyclerView.setAdapter(adapter);

        cartViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(CartViewModel.class);
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(UserViewModel.class);
        uid = userViewModel.getUser().getValue().getUid();

        cartViewModel.getCartProducts().observe(getViewLifecycleOwner(), cartProducts -> {
            adapter.setProducts(cartProducts);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        cartViewModel.fetchCartProducts(uid);
    }

    @Override
    public void onClickProduct(int pos) {
        // no functionality assigned yet
    }
}