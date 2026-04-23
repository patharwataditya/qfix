package com.qfix.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.qfix.utils.Converters;

import java.util.Date;
import java.util.List;

@Entity(tableName = "complaints")
@TypeConverters({Converters.class})
public class Complaint {
    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String category;
    private String description;
    private String locationText;
    private String ward;
    private List<String> photos;
    private String status; // "open", "in_progress", "resolved", "rejected"
    private String priority; // "low", "medium", "high", "critical"
    private String citizenId;
    private String assignedDepartment;
    private String assignedAuthorityId;
    private Date createdAt;
    private Date updatedAt;
    private Date resolvedAt;
    private String resolutionNote;
    private List<String> resolutionPhotos;
    private boolean isPublic;
    private int upvotes;

    // Empty constructor required for Firestore
    public Complaint() {}

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationText() {
        return locationText;
    }

    public void setLocationText(String locationText) {
        this.locationText = locationText;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public String getAssignedDepartment() {
        return assignedDepartment;
    }

    public void setAssignedDepartment(String assignedDepartment) {
        this.assignedDepartment = assignedDepartment;
    }

    public String getAssignedAuthorityId() {
        return assignedAuthorityId;
    }

    public void setAssignedAuthorityId(String assignedAuthorityId) {
        this.assignedAuthorityId = assignedAuthorityId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Date resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }

    public List<String> getResolutionPhotos() {
        return resolutionPhotos;
    }

    public void setResolutionPhotos(List<String> resolutionPhotos) {
        this.resolutionPhotos = resolutionPhotos;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }
}
