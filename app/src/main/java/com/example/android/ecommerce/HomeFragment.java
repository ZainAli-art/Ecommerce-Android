package com.example.android.ecommerce;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.example.android.ecommerce.model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class HomeFragment extends Fragment implements View.OnClickListener {
    public static final int REQUEST_INTERNET = 1;
    public static final String SELECTED_CAT_ID = "com.example.android.ecommerce.HomeFragment.cat_id";

    public static final String CAT_JSON_URL = HOST_URL + "scripts/all-categories-json.php";

    private RecyclerView categoryRecyclerView;
    private FloatingActionButton addProductFab;
    private CategoryRecyclerViewAdapter adapter;
    private NavController navController;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addProductFab:
                navController.navigate(R.id.action_homeFragment_to_addProductFragment);
                break;
        }
    }

    public interface ListItemListener {
        void onClickListItem(int pos);
    }

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
        adapter = new CategoryRecyclerViewAdapter(new ArrayList<Category>());
        categoryRecyclerView.setAdapter(adapter);
        addProductFab = view.findViewById(R.id.addProductFab);
        navController = Navigation.findNavController(view);
        addProductFab.setOnClickListener(this);

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
                    List<Category> categories = new ArrayList<>();
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
                    adapter.setCatList(categories);
                },
                error -> {
                    Toast.makeText(requireContext(), "Json Response Error", Toast.LENGTH_SHORT).show();
                }
        );

        MySingleton.getInstance(requireContext()).enqueueRequest(request);
    }

    public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.CViewHolder> implements ListItemListener {
        private List<Category> catList;

        public CategoryRecyclerViewAdapter(List<Category> catList) {
            this.catList = catList;
        }

        public void setCatList(List<Category> catList) {
            this.catList = catList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_list_item, parent, false);
            return new CViewHolder(this, itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CViewHolder holder, int position) {
            Category mCat = catList.get(position);
            holder.setImage(mCat.getImgUrl());
            holder.setText(mCat.getName());
        }

        @Override
        public int getItemCount() {
            if (catList != null)
                return catList.size();
            return 0;
        }

        @Override
        public void onClickListItem(int pos) {
            if (catList != null) {
                NavController navController = NavHostFragment.findNavController(HomeFragment.this);

                String catId = String.valueOf(catList.get(pos).getId());
                Bundle args = new Bundle();
                args.putString(SELECTED_CAT_ID, catId);

                navController.navigate(R.id.action_homeFragment_to_productListFragment, args);
            }
        }

        public class CViewHolder extends RecyclerView.ViewHolder {
            private ImageView catImage;
            private TextView catText;

            public CViewHolder(ListItemListener listItemListener, @NonNull View itemView) {
                super(itemView);
                catImage = itemView.findViewById(R.id.catImage);
                catText = itemView.findViewById(R.id.catText);
                itemView.setOnClickListener((view) -> listItemListener.onClickListItem(getAdapterPosition()));
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