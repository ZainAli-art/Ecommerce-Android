package com.example.android.ecommerce.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.android.ecommerce.model.Product;
import com.example.android.ecommerce.model.ProductDetails;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProduct(Product product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProducts(List<Product> products);

    @Query("SELECT * FROM products WHERE cat_id = :catId")
    LiveData<List<Product>> loadProducts(long catId);

    @Query("SELECT * FROM products ORDER BY upload_time DESC LIMIT :limit")
    LiveData<List<Product>> loadRecentProducts(int limit);

    @Query("SELECT " +
                "pid, " +
                "u.uid as seller_id, " +
                "p.pname AS product, " +
                "cat_name AS category, " +
                "p.img_dir AS img_dir, " +
                "upload_time, price, " +
                "fullname as seller, " +
                "email as contact, " +
                "token as seller_token " +
            "FROM users u " +
            "JOIN fcm ON u.uid = fcm.uid " +
            "JOIN products p ON u.uid = p.uid " +
            "JOIN categories c ON p.cat_id = c.cat_id " +
            "WHERE pid = :pid")
    LiveData<ProductDetails> loadProductDetails(long pid);
}
