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
import com.example.android.ecommerce.interfaces.ECommerceRecyclerViewAdaptable;
import com.example.android.ecommerce.model.OrderedProduct;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.ecommerce.utils.Constants.BASE_URL;

public class OrderedProductRecyclerViewAdapter extends RecyclerView.Adapter<OrderedProductRecyclerViewAdapter.OPViewHolder>
        implements ECommerceRecyclerViewAdaptable<OrderedProduct> {
    List<OrderedProduct> orderedProducts;
    private OrderedProductItemListener listener;

    public interface OrderedProductItemListener {
        void onClickOrderedProduct(int pos);

        void onClickDeleteOrderedProduct(int pos);
    }

    public OrderedProductRecyclerViewAdapter(OrderedProductItemListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OPViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.ordered_product_list_item, parent, false);
        return new OPViewHolder(itemView, listener, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull OPViewHolder holder, int position) {
        OrderedProduct orderedProduct = orderedProducts.get(position);
        holder.setImage(orderedProduct.imgUrl);
        holder.setTitle(orderedProduct.productName);
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    @Override
    public List<OrderedProduct> getItems() {
        if (orderedProducts == null) {
            orderedProducts = new ArrayList<>();
        }
        return orderedProducts;
    }

    @Override
    public void setItems(List<OrderedProduct> dataSet) {
        this.orderedProducts = dataSet;
        notifyDataSetChanged();
    }

    public static class OPViewHolder extends RecyclerView.ViewHolder {
        private ImageView pImage;
        private TextView pText;
        private Context mContext;

        public OPViewHolder(@NonNull View itemView, OrderedProductItemListener listener, Context context) {
            super(itemView);
            pImage = itemView.findViewById(R.id.oImage);
            pText = itemView.findViewById(R.id.oTitle);
            mContext = context;
            itemView.setOnClickListener((view) -> listener.onClickOrderedProduct(getAdapterPosition()));
            itemView.findViewById(R.id.delOrderBtn).setOnClickListener((view) ->
                    listener.onClickDeleteOrderedProduct(getAdapterPosition()));
        }

        public void setImage(String imgUrl) {
            Glide.with(mContext).load(BASE_URL + imgUrl).into(pImage);
        }

        public void setTitle(CharSequence title) {
            pText.setText(title);
        }
    }
}
