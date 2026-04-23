package com.qfix.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.qfix.data.model.Complaint;

import java.util.List;

@Dao
public interface ComplaintDao {
    @Query("SELECT * FROM complaints ORDER BY createdAt DESC")
    LiveData<List<Complaint>> getAllComplaints();
    
    @Query("SELECT * FROM complaints ORDER BY createdAt DESC")
    List<Complaint> getAllComplaintsSync();

    @Query("SELECT * FROM complaints WHERE citizenId = :citizenId ORDER BY createdAt DESC")
    LiveData<List<Complaint>> getComplaintsByCitizen(String citizenId);
    
    @Query("SELECT * FROM complaints WHERE citizenId = :citizenId ORDER BY createdAt DESC")
    List<Complaint> getComplaintsByCitizenSync(String citizenId);

    @Query("SELECT * FROM complaints WHERE id = :complaintId LIMIT 1")
    LiveData<Complaint> getComplaintById(String complaintId);
    
    @Query("SELECT * FROM complaints WHERE id = :complaintId LIMIT 1")
    Complaint getComplaintByIdSync(String complaintId);

    @Query("SELECT * FROM complaints WHERE status = :status ORDER BY createdAt DESC")
    LiveData<List<Complaint>> getComplaintsByStatus(String status);
    
    @Query("SELECT * FROM complaints WHERE status = :status ORDER BY createdAt DESC")
    List<Complaint> getComplaintsByStatusSync(String status);

    @Query("SELECT * FROM complaints WHERE priority = :priority ORDER BY createdAt DESC")
    LiveData<List<Complaint>> getComplaintsByPriority(String priority);
    
    @Query("SELECT * FROM complaints WHERE priority = :priority ORDER BY createdAt DESC")
    List<Complaint> getComplaintsByPrioritySync(String priority);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Complaint complaint);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Complaint> complaints);

    @Update
    void update(Complaint complaint);

    @Query("DELETE FROM complaints WHERE id = :complaintId")
    void delete(String complaintId);

    @Query("DELETE FROM complaints")
    void deleteAll();
}