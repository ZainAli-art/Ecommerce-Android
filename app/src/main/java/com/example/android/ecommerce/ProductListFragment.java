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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.ecommerce.model.Product;
import com.example.android.ecommerce.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {
    private RecyclerView productListRecyclerView;
    private ProductListRecyclerViewAdapter adapter;
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
        adapter = new ProductListRecyclerViewAdapter(new ArrayList<Product>());
        productListRecyclerView.setAdapter(adapter);
        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(ProductViewModel.class);
        Bundle args = getArguments();
        String catId = args.getString(HomeFragment.SELECTED_CAT_ID);

        // observers
        productViewModel.getProducts().observe(requireActivity(), products -> {
            adapter.setProductList(products);
        });

        productViewModel.fetchProducts(catId);
    }

    public class ProductListRecyclerViewAdapter extends RecyclerView.Adapter<ProductListRecyclerViewAdapter.PViewHolder> {
        private List<Product> productList;

        public ProductListRecyclerViewAdapter(List<Product> productList) {
            this.productList = productList;
        }

        public void setProductList(List<Product> productList) {
            this.productList = productList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_list_item, parent, false);
            return new PViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PViewHolder holder, int position) {
            Product mProduct = productList.get(position);
            holder.setImage(mProduct.getImgUrl());
            holder.setText(mProduct.getName());
        }

        @Override
        public int getItemCount() {
            if (productList != null)
                return productList.size();
            return 0;
        }

        public class PViewHolder extends RecyclerView.ViewHolder {
            private ImageView pImage;
            private TextView pText;

            public PViewHolder(@NonNull View itemView) {
                super(itemView);
                pImage = itemView.findViewById(R.id.pImage);
                pText = itemView.findViewById(R.id.pText);
            }

            public void setImage(String imgUrl) {
                Glide.with(requireActivity()).load(imgUrl).into(pImage);
            }

            public void setText(CharSequence text) {
                pText.setText(text);
            }
        }
    }
}