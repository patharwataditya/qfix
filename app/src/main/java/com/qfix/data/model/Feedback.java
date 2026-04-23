package com.qfix.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "feedback")
public class Feedback {
    @PrimaryKey
    @NonNull
    private String id;
    private String complaintId;
    private String citizenId;
    private int rating; // 1-5
    private String comment;
    private Date createdAt;

    // Empty constructor
    public Feedback() {}
    
    @Ignore
    public Feedback(String complaintId, String citizenId, int rating, String comment) {
        this.complaintId = complaintId;
        this.citizenId = citizenId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = new Date();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}