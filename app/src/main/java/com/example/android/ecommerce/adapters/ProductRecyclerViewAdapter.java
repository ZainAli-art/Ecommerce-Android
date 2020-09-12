package com.example.android.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.ecommerce.R;
import com.example.android.ecommerce.interfaces.ECommerceRecyclerViewAdaptable;
import com.example.android.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.ecommerce.utils.Constants.BASE_URL;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.PViewHolder>
        implements ECommerceRecyclerViewAdaptable<Product> {
    private List<Product> products;
    private int viewType;
    private ProductItemListener listener;

    public interface ProductItemListener {
        void onClickProduct(View view, int pos);
    }

    public ProductRecyclerViewAdapter(int viewType, ProductItemListener listener) {
        this.viewType = viewType;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View itemView = null;
//
//        switch (viewType) {
//            case Product.VERTICAL_TYPE:
//                itemView = inflater.inflate(R.layout.product_list_item_vertical, parent, false);
//                break;
//            case Product.HORIZONTAL_TYPE:
//                itemView = inflater.inflate(R.layout.product_list_item_horizontal, parent, false);
//                break;
//        }
        View itemView = inflater.inflate(R.layout.product_list_item_horizontal, parent, false);

        return new PViewHolder(itemView, listener, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull PViewHolder holder, int position) {
        Product product = products.get(position);
        holder.setImage(product.imgUrl);
        holder.setText(product.name);
        holder.onBind(String.valueOf(product.pid));
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    @Override
    public List<Product> getItems() {
        if (products == null) {
            products = new ArrayList<>();
        }
        return products;
    }

    @Override
    public void setItems(List<Product> dataSet) {
        this.products = dataSet;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    public static class PViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProductItemListener listener;
        private ImageView pImage;
        private TextView pText;
        private Context mContext;

        public PViewHolder(@NonNull View itemView, ProductItemListener listener, Context context) {
            super(itemView);
            this.listener = listener;
            itemView.setOnClickListener(this);
            pImage = itemView.findViewById(R.id.pImage);
            pText = itemView.findViewById(R.id.pText);
            mContext = context;
        }

        public void setImage(String imgUrl) {
            Glide.with(mContext).load(BASE_URL + imgUrl).into(pImage);
        }

        public void setText(CharSequence text) {
            pText.setText(text);
        }

        public void onBind(String id) {
            ViewCompat.setTransitionName(pImage, id);
        }

        @Override
        public void onClick(View v) {
            listener.onClickProduct(v, getBindingAdapterPosition());
        }
    }
}
