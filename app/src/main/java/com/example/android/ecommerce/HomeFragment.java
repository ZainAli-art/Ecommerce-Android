package com.example.android.ecommerce;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    public static final int REQUEST_INTERNET = 1;
    public static final String HOST_URL = "http://192.168.8.101/";
    public static final String CAT_JSON_URL = HOST_URL + "json/categories-json.php";

    private RecyclerView categoryRecyclerView;
    private List<Category> categories;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        requestPermissions(new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_INTERNET) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadCategories();
            } else {
                Toast.makeText(requireContext(), "Internet Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loadCategories() {
        // TODO: parse categories json array using volley to Categories list
        JsonArrayRequest request = new JsonArrayRequest(
                CAT_JSON_URL,
                response -> {
                    categories = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int catId = jsonObject.getInt("cat_id");
                            String catName = jsonObject.getString("cat_name");
                            String imgUrl = HOST_URL + jsonObject.getString("img_dir");

                            categories.add(new Category(catId, catName, imgUrl));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    categoryRecyclerView.setAdapter(new CategoryRecyclerViewAdapter(categories));
                },
                error -> {
                    Toast.makeText(requireContext(), "Json Response Error", Toast.LENGTH_SHORT).show();
                }
        );

        MyInstance.getInstance(requireContext()).enqueueRequest(request);
    }

    public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.CViewHolder> {
        private List<Category> catList;

        public CategoryRecyclerViewAdapter(List<Category> catList) {
            this.catList = catList;
        }

        @NonNull
        @Override
        public CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View listItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_list_item, parent, false);
            return new CViewHolder(listItem);
        }

        @Override
        public void onBindViewHolder(@NonNull CViewHolder holder, int position) {
            Category mCat = catList.get(position);
            holder.setImage(mCat.getImgUrl());
            holder.setText(mCat.getName());
        }

        @Override
        public int getItemCount() {
            return catList.size();
        }

        public class CViewHolder extends RecyclerView.ViewHolder {
            private ImageView catImage;
            private TextView catText;

            public CViewHolder(@NonNull View itemView) {
                super(itemView);
                catImage = itemView.findViewById(R.id.catImage);
                catText = itemView.findViewById(R.id.catText);
            }

            public void setImage(String imgUrl) {
                Glide.with(requireActivity()).load(imgUrl).into(catImage);
            }

            public void setText(CharSequence text) {
                catText.setText(text);
            }
        }
    }
}