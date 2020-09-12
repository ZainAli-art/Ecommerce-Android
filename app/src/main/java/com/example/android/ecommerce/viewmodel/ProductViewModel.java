package com.example.android.ecommerce.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.ecommerce.model.Fcm;
import com.example.android.ecommerce.model.Product;
import com.example.android.ecommerce.model.ProductDetails;
import com.example.android.ecommerce.repository.ECommerceRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    ECommerceRepository repo;
    private boolean firstTime;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        repo = ECommerceRepository.getInstance(application);
        firstTime = true;
    }

    public LiveData<List<Product>> getProducts(long catId) {
        return repo.getProducts(catId);
    }

    public LiveData<List<Product>> getRecentProducts(int limit) {
        return repo.getRecentProducts(limit);
    }

    public LiveData<ProductDetails> getProductDetails(long pid) {
        return repo.getProductDetails(pid);
    }

    public void uploadProduct(String uid, String pName, long catId, String img, String price) {
        repo.insertProduct(uid, pName, catId, img, price);
    }

    public void refreshProducts() {
        repo.refreshProducts();
    }

    /**
     * used for stopping transition postpone if fragment/activity
     * launched for the first time
     *
     * @return firstTime
     * * true if view model is launched for first time
     * * false other wise
     */
    public boolean isFirstTime() {
        return firstTime;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }
}
