package com.example.android.ecommerce;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.ecommerce.adapters.ProductRecyclerViewAdapter;
import com.example.android.ecommerce.model.Product;
import com.example.android.ecommerce.utils.Utility;
import com.example.android.ecommerce.viewmodel.ProductViewModel;

public class ProductListFragment extends Fragment implements ProductRecyclerViewAdapter.ProductItemListener,
        SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ProductListFragment";

    public static final String CATEGORY_ID = "CATEGORY_ID";
    public static final String USER_ID = "USER_ID";

    private NavController navController;
    private ProductRecyclerViewAdapter adapter;
    private ProductViewModel productViewModel;

    private long catId;
    private String uid;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        catId = args.getLong(CATEGORY_ID);
        uid = args.getString(USER_ID);

        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(ProductViewModel.class);
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

        Context mContext = requireContext();
        float width = Utility.getScreenWidthDp(mContext);
        Log.d(TAG, "device width: " + width);

        RecyclerView productListRecyclerView = view.findViewById(R.id.productListRecyclerView);
        productListRecyclerView.setLayoutManager(
                new GridLayoutManager(mContext,
                        Utility.getNoOfColumns(mContext, 266)));    // width of 250 and padding of 8 each side
        adapter = new ProductRecyclerViewAdapter(Product.VERTICAL_TYPE, this);
        productListRecyclerView.setAdapter(adapter);

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.productListSwipeRefreshLayout);
        refreshLayout.setOnRefreshListener(this);

        // observers
        productViewModel.getProducts(catId).observe(getViewLifecycleOwner(), products -> {
            adapter.setItems(products);
            refreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onClickProduct(View view, int pos) {
        long pid = adapter.getItem(pos).pid;
        Bundle args = getArguments();
        args.putLong(ProductDetailsFragment.PRODUCT_ID, pid);
        args.putString(ProductDetailsFragment.USER_ID, uid);

        navController.navigate(R.id.action_productListFragment_to_productDetailsFragment, getArguments());
    }

    @Override
    public void onRefresh() {
        productViewModel.refreshProducts();
    }
}