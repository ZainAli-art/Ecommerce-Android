package com.example.android.ecommerce.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.ecommerce.model.Category;
import com.example.android.ecommerce.repository.ECommerceRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryViewModel extends AndroidViewModel {
    private ECommerceRepository repo;
    private Map<String, Long> catMap;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repo = ECommerceRepository.getInstance(application);
        catMap = new HashMap<>();
    }

    public LiveData<List<Category>> getCategories() {
        List<Category> categories = repo.getCategories().getValue();
        if (categories != null) {
            catMap = new HashMap<>();
            for (Category c : categories)
                catMap.put(c.name, c.id);
        }
        return repo.getCategories();
    }

    public Long getCatIdByName(String name) {
        return catMap.get(name);
    }

    public void refreshCategories() {
        repo.refreshCategories();
    }
}
