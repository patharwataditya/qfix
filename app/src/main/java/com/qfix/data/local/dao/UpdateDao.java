package com.qfix.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.qfix.data.model.Update;

import java.util.List;

@Dao
public interface UpdateDao {
    @Query("SELECT * FROM updates WHERE complaintId = :complaintId ORDER BY timestamp ASC")
    List<Update> getUpdatesForComplaint(String complaintId);

    @Insert
    void insert(Update updateEntity);

    @Query("DELETE FROM updates WHERE id = :id")
    void deleteById(String id);
}