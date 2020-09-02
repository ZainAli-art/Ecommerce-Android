package com.example.android.ecommerce.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.android.ecommerce.model.Order;
import com.example.android.ecommerce.model.OrderDetails;
import com.example.android.ecommerce.model.OrderedProduct;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrder(Order order);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrders(List<Order> orders);

    @Query("SELECT oid, pname AS product, img_dir " +
            "FROM orders o JOIN products p ON o.pid = p.pid " +
            "WHERE o.uid = :uid")
    LiveData<List<OrderedProduct>> loadOrderedProducts(String uid);

    @Query("SELECT " +
                "o.oid AS oid, " +
                "pname, " +
                "p.img_dir AS img_dir, " +
                "c.cat_name AS category, " +
                "order_time, " +
                "price  " +
            "FROM orders o " +
            "JOIN products p ON p.pid = o.pid " +
            "JOIN categories c ON c.cat_id = p.cat_id " +
            "WHERE oid = :oid;")
    LiveData<OrderDetails> loadOrderDetails(long oid);

    @Delete
    void deleteOrder(Order order);
}
