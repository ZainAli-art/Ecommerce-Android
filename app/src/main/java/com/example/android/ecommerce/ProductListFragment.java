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
import com.example.android.ecommerce.viewmodel.ProductViewModel;

public class ProductListFragment extends Fragment implements ProductRecyclerViewAdapter.ProductItemListener {
    private RecyclerView productListRecyclerView;
    private ProductRecyclerViewAdapter adapter;
    private ProductViewModel productViewModel;

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
        productListRecyclerView = view.findViewById(R.id.productListRecyclerView);
        adapter = new ProductRecyclerViewAdapter(Product.VERTICAL_TYPE, this);
        productListRecyclerView.setAdapter(adapter);
        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(ProductViewModel.class);
        Bundle args = getArguments();
        String catId = args.getString(HomeFragment.SELECTED_CAT_ID);

        // observers
        productViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.setProducts(products);
        });

        productViewModel.fetchProducts(catId);
    }

    @Override
    public void onClickProduct(int pos) {
        // TODO: opens product details
    }
}