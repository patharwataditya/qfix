package com.qfix.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.qfix.data.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM category")
    List<Category> getAllCategories();

    @Insert
    void insert(Category category);

    @Insert
    void insertAll(List<Category> categories);

    @Update
    void update(Category category);

    @Query("DELETE FROM category WHERE id = :id")
    void deleteById(String id);
}