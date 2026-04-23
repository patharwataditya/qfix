package com.qfix.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.qfix.data.model.Feedback;

import java.util.List;

@Dao
public interface FeedbackDao {
    @Query("SELECT * FROM feedback WHERE complaintId = :complaintId")
    List<Feedback> getFeedbackForComplaint(String complaintId);

    @Insert
    void insert(Feedback feedback);

    @Update
    void update(Feedback feedback);

    @Query("DELETE FROM feedback WHERE id = :id")
    void deleteById(String id);
}