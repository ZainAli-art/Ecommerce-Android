package com.example.android.ecommerce.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.android.ecommerce.model.Fcm;

@Dao
public interface FcmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Fcm fcm);

    @Query("UPDATE fcm SET token = :newToken WHERE token = :oldToken")
    void update(String oldToken, String newToken);

    @Update
    void update(Fcm fcm);
}
