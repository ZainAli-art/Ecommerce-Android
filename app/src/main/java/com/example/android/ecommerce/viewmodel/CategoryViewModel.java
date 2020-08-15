package com.example.android.ecommerce.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.toolbox.JsonArrayRequest;
import com.example.android.ecommerce.MySingleton;
import com.example.android.ecommerce.model.Category;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class CategoryViewModel extends AndroidViewModel {
    public static final String CAT_JSON_URL = HOST_URL + "scripts/all-categories-json.php";

    private Context mContext;
    private MutableLiveData<List<Category>> categories;
    private Map<String, Long> catMap;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        categories = new MutableLiveData<>();
        catMap = new HashMap<>();
    }

    public LiveData<List<Category>> getCategories() {
        if (categories == null) {
            setCategories(new ArrayList<>());
        }
        return categories;
    }

    public long getCatIdByName(String categoryName) {
        Long id = catMap.get(categoryName);
        if (id == null) return -1;
        return id;
    }

    public void setCategories(List<Category> categoryList) {
        for (Category c : categoryList)
            catMap.put(c.getName(), c.getId());
        categories.setValue(categoryList);
    }

    public void refreshCategories() {
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
                    setCategories(categories);
                },
                error -> {
                    Toast.makeText(mContext, "Json Response Error", Toast.LENGTH_SHORT).show();
                }
        );

        MySingleton.getInstance(mContext).enqueueRequest(request);
    }
}
