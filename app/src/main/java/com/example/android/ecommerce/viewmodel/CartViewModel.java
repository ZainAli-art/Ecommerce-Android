package com.example.android.ecommerce.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.android.ecommerce.model.OrderDetails;
import com.example.android.ecommerce.model.OrderedProduct;
import com.example.android.ecommerce.repository.ECommerceRepository;

import java.util.List;

public class CartViewModel extends AndroidViewModel {
    ECommerceRepository repo;

    public CartViewModel(@NonNull Application application) {
        super(application);
        repo = new ECommerceRepository(application);
    }

    public LiveData<List<OrderedProduct>> getCartProducts() {
        return repo.getCartProducts();
    }

    public LiveData<OrderDetails> getDetailedOrder() {
        return repo.getDetailedOrder();
    }

    public void addToCart(String uid, String pid) {
        repo.addToCart(uid, pid);
    }

    public void fetchCartProducts(String uid) {
        repo.fetchCartProducts(uid);
    }

    public void fetchOrderDetails(String oid) {
        repo.fetchOrderDetails(oid);
    }

    public void deleteOrder(String oid) {
        repo.deleteOrder(oid);
    }
}
