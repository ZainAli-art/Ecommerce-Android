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
import com.example.android.ecommerce.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.CatViewHolder> {
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

    public List<Category> getCategories() {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
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
        holder.setImage(category.getImgUrl());
        holder.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return getCategories().size();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    public static class CatViewHolder extends RecyclerView.ViewHolder {
        private ImageView catImage;
        private TextView catText;
        private Context mContext;

        public CatViewHolder(@NonNull View itemView, CategoryItemListener listener, Context context) {
            super(itemView);
            mContext = context;
            catImage = itemView.findViewById(R.id.catImage);
            catText = itemView.findViewById(R.id.catText);
            itemView.setOnClickListener((view) -> listener.onClickCategory(getAdapterPosition()));
        }

        public void setImage(String imgUrl) {
            Glide.with(mContext).load(imgUrl).into(catImage);
        }

        public void setText(CharSequence text) {
            catText.setText(text);
        }
    }
}