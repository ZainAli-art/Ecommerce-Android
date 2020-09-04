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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProductDetails(ProductDetails details);

    @Query("SELECT * FROM products WHERE cat_id = :catId")
    LiveData<List<Product>> loadProducts(long catId);

    @Query("SELECT * FROM products ORDER BY upload_time DESC LIMIT :limit")
    LiveData<List<Product>> loadRecentProducts(int limit);

    @Query("SELECT * FROM product_details WHERE pid = :pid")
    LiveData<ProductDetails> loadProductDetails(long pid);

    @Query("SELECT * FROM product_details WHERE pid = :pid")
    ProductDetails getProductDetails(long pid);
}
