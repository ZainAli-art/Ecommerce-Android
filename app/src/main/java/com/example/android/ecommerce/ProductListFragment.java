package com.example.android.ecommerce;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class ProductListFragment extends Fragment {
    private static final String TAG = "ProductListFragment";
    public static final String PRODUCTS_URL = HOST_URL + "scripts/products-by-cat_id-json.php";

    private RecyclerView productListRecyclerView;
    private ProductListRecyclerViewAdapter adapter;

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

        Bundle args = getArguments();
        String catId = args.getString(HomeFragment.SELECTED_CAT_ID);

        // we probably already have internet permission
        // if we are on this page
        fetchProducts(catId);
    }

    private void fetchProducts(String catId) {
        Log.d(TAG, "fetchProducts: called");

        Uri.Builder builder = Uri.parse(PRODUCTS_URL).buildUpon();
        builder.appendQueryParameter("cat_id", catId);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                builder.toString(),
                null,
                response -> {
                    try {
                        List<Product> products = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int pid = jsonObject.getInt("pid");
                            int cid = jsonObject.getInt("cat_id");
                            String name = jsonObject.getString("pname");
                            String imgUrl = HOST_URL + jsonObject.getString("img_dir");

                            products.add(new Product(pid, cid, name, imgUrl));
                        }
                        adapter.setProductList(products);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.d(TAG, "fetchProducts error: " + error);
                }
        );

        MySingleton.getInstance(requireContext()).enqueueRequest(request);
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