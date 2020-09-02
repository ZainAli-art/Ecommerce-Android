package com.example.android.ecommerce.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.android.ecommerce.adapters.TypeConverters;
import com.example.android.ecommerce.dao.CategoryDAO;
import com.example.android.ecommerce.dao.ChatDao;
import com.example.android.ecommerce.dao.OrderDao;
import com.example.android.ecommerce.dao.ProductDao;
import com.example.android.ecommerce.dao.UserDao;
import com.example.android.ecommerce.model.Category;
import com.example.android.ecommerce.model.Chat;
import com.example.android.ecommerce.model.ChatListItem;
import com.example.android.ecommerce.model.Fcm;
import com.example.android.ecommerce.model.Order;
import com.example.android.ecommerce.model.Product;
import com.example.android.ecommerce.model.User;

@Database(entities = {Category.class, Chat.class, Fcm.class, Order.class,
        Product.class, User.class, ChatListItem.class},
        version = 1, exportSchema = false)
@androidx.room.TypeConverters({TypeConverters.class})
public abstract class LocalDb extends RoomDatabase {
    private static volatile LocalDb INSTANCE;

    public abstract CategoryDAO categoryDAO();
    public abstract ChatDao chatDao();
    public abstract OrderDao orderDao();
    public abstract ProductDao productDao();
    public abstract UserDao userDao();

    public static LocalDb getDatabase(final Context context, RoomDatabase.Callback callback) {
        if (INSTANCE == null) {
            synchronized (LocalDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LocalDb.class, "ecommerce_db")
                            .addCallback(callback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
