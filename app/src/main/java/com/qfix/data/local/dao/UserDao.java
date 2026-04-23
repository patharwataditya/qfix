package com.qfix.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.qfix.data.model.User;
import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    LiveData<User> getUserById(String uid);
    
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    User getUserByIdSync(String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Update
    void update(User user);

    @Query("DELETE FROM users WHERE uid = :uid")
    void delete(String uid);

    @Query("DELETE FROM users")
    void deleteAll();
    
    @Query("SELECT * FROM users")
    List<User> getAllUsers();
}