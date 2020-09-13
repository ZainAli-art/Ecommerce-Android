package com.example.android.ecommerce;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Slide;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.android.ecommerce.adapters.ProductRecyclerViewAdapter;
import com.example.android.ecommerce.model.Product;
import com.example.android.ecommerce.utils.Utility;
import com.example.android.ecommerce.viewmodel.ProductViewModel;

import static com.example.android.ecommerce.ProductDetailsFragment.PRODUCT_TRANSITION_ID;

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

        setEnterTransition(new Slide(Gravity.RIGHT));

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
        postponeEnterTransition();

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
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.productListSwipeRefreshLayout);
        refreshLayout.setOnRefreshListener(this);

        // observers
        ViewGroup parentView = (ViewGroup) view.getParent();
        productViewModel.getProducts(catId).observe(getViewLifecycleOwner(), products -> {
            adapter.setItems(products);

            parentView.getViewTreeObserver()
                    .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            parentView.getViewTreeObserver()
                                    .removeOnPreDrawListener(this);
                            startPostponedEnterTransition();
                            return true;
                        }
                    });

            refreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onClickProduct(View view, int pos) {
        View pImage = view.findViewById(R.id.pImage);
        String transitionName = String.valueOf(adapter.getItem(pos).pid);

        long pid = adapter.getItem(pos).pid;
        Bundle args = getArguments();
        args.putLong(ProductDetailsFragment.PRODUCT_ID, pid);
        args.putString(ProductDetailsFragment.USER_ID, uid);
        args.putString(PRODUCT_TRANSITION_ID, transitionName);

        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(pImage, transitionName)
                .build();

        navController.navigate(R.id.action_productListFragment_to_productDetailsFragment, getArguments(), null, extras);
    }

    @Override
    public void onRefresh() {
        productViewModel.refreshProducts();
    }
}