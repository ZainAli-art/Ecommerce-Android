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
        repo = ECommerceRepository.getInstance(application);
    }

    public LiveData<List<OrderedProduct>> getCartProducts(String uid) {
        return repo.getCartProducts(uid);
    }

    public LiveData<OrderDetails> getOrderDetails(long oid) {
        return repo.getOrderDetails(oid);
    }

    public void refreshCart(String uid) {
        repo.refreshOrders(uid);
    }

    public void addToCart(String uid, long pid) {
        repo.insertOrder(uid, pid);
    }

    public void deleteOrder(long oid) {
        repo.deleteOrder(oid);
    }
}
