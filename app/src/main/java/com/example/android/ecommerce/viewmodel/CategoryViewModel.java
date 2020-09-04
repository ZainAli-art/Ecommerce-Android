package com.example.android.ecommerce.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.ecommerce.model.Category;
import com.example.android.ecommerce.repository.ECommerceRepository;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private ECommerceRepository repo;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repo = ECommerceRepository.getInstance(application);
    }

    public LiveData<List<Category>> getCategories() {
        return repo.getCategories();
    }

    public void refreshCategories() {
        repo.refreshCategories();
    }
}
