package com.example.android.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.ecommerce.R;
import com.example.android.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.PViewHolder> {
    private List<Product> products;
    private int viewType;
    private ProductItemListener listener;

    public interface ProductItemListener {
        void onClickProduct(int pos);
    }

    public ProductRecyclerViewAdapter(int viewType, ProductItemListener listener) {
        this.viewType = viewType;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = null;

        switch (viewType) {
            case Product.VERTICAL_TYPE:
                itemView = inflater.inflate(R.layout.product_list_item_vertical, parent, false);
                break;
            case Product.HORIZONTAL_TYPE:
                itemView = inflater.inflate(R.layout.product_list_item_horizontal, parent, false);
                break;
        }

        return new PViewHolder(itemView, listener, parent.getContext());
    }

    public List<Product> getProducts() {
        if (products == null) {
            products = new ArrayList<>();
        }
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull PViewHolder holder, int position) {
        Product product = products.get(position);
        holder.setImage(product.getImgUrl());
        holder.setText(product.getName());
    }

    @Override
    public int getItemCount() {
        return getProducts().size();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    public static class PViewHolder extends RecyclerView.ViewHolder {
        private ImageView pImage;
        private TextView pText;
        private Context mContext;

        public PViewHolder(@NonNull View itemView, ProductItemListener listener, Context context) {
            super(itemView);
            pImage = itemView.findViewById(R.id.pImage);
            pText = itemView.findViewById(R.id.pText);
            itemView.setOnClickListener((view) -> listener.onClickProduct(getAdapterPosition()));
            mContext = context;
        }

        public void setImage(String imgUrl) {
            Glide.with(mContext).load(imgUrl).into(pImage);
        }

        public void setText(CharSequence text) {
            pText.setText(text);
        }
    }
}
