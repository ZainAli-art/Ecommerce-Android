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
import com.example.android.ecommerce.adapters.ProductRecyclerViewAdapter;
import com.example.android.ecommerce.model.Product;
import com.example.android.ecommerce.viewmodel.ProductViewModel;

import java.util.List;

public class ProductListFragment extends Fragment implements ProductRecyclerViewAdapter.ProductItemListener {
    private NavController navController;
    private ProductRecyclerViewAdapter adapter;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        RecyclerView productListRecyclerView = view.findViewById(R.id.productListRecyclerView);
        adapter = new ProductRecyclerViewAdapter(Product.VERTICAL_TYPE, this);
        productListRecyclerView.setAdapter(adapter);
        ProductViewModel productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(ProductViewModel.class);
        Bundle args = getArguments();
        String catId = args.getString(HomeFragment.SELECTED_CAT_ID);

        // observers
        productViewModel.getProducts(catId).observe(getViewLifecycleOwner(), products -> {
            adapter.setItems(products);
        });
    }

    @Override
    public void onClickProduct(int pos) {
        Bundle args = new Bundle();
        String pid = String.valueOf(adapter.getItem(pos).getPid());
        args.putString(ProductDetailsFragment.PRODUCT_ID, pid);

        navController.navigate(R.id.action_productListFragment_to_productDetailsFragment, args);
    }
}