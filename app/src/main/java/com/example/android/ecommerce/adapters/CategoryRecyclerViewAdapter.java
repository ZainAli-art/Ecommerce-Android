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
import com.example.android.ecommerce.model.Category;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.ecommerce.utils.Constants.BASE_URL;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.CatViewHolder>
        implements ECommerceRecyclerViewAdaptable<Category> {
    private List<Category> categories;
    private int viewType;
    private CategoryItemListener listener;

    public interface CategoryItemListener {
        void onClickCategory(int pos);
    }

    public CategoryRecyclerViewAdapter(int viewType, CategoryItemListener listener) {
        this.viewType = viewType;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = null;

        switch (viewType) {
            case Category.HORIZONTAL_TYPE:
                itemView = inflater.inflate(R.layout.category_list_item_horizontal, parent, false);
        }

        return new CatViewHolder(itemView, listener, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.setImage(category.imgUrl);
        holder.setText(category.name);
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    @Override
    public List<Category> getItems() {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        return categories;
    }

    @Override
    public void setItems(List<Category> dataSet) {
        this.categories = dataSet;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    public static class CatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CategoryItemListener listener;
        private ImageView catImage;
        private TextView catText;
        private Context mContext;

        public CatViewHolder(@NonNull View itemView, CategoryItemListener listener, Context context) {
            super(itemView);
            this.listener = listener;
            itemView.setOnClickListener(this);
            mContext = context;
            catImage = itemView.findViewById(R.id.catImage);
            catText = itemView.findViewById(R.id.catText);
        }

        public void setImage(String imgUrl) {
            Glide.with(mContext).load(BASE_URL + imgUrl).into(catImage);
        }

        public void setText(CharSequence text) {
            catText.setText(text);
        }

        @Override
        public void onClick(View v) {
            listener.onClickCategory(getAdapterPosition());
        }
    }
}