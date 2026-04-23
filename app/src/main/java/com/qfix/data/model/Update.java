package com.qfix.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.qfix.utils.Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity(tableName = "updates")
@TypeConverters({Converters.class})
public class Update {
    @PrimaryKey
    @NonNull
    private String id;
    private String complaintId;
    private String authorityId;
    private String status; // "open", "in_progress", "resolved", "rejected"
    private String note;
    private Date timestamp;

    // Empty constructor
    public Update() {}
    
    @Ignore
    public Update(String complaintId, String authorityId, String status, String note) {
        this.complaintId = complaintId;
        this.authorityId = authorityId;
        this.status = status;
        this.note = note;
        this.timestamp = new Date();
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

    public String getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(String authorityId) {
        this.authorityId = authorityId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}