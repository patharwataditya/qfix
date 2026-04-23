package com.qfix.data.model;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.qfix.utils.Converters;

import java.util.Date;
import java.util.List;

@Entity(tableName = "users")
@TypeConverters({Converters.class})
public class User {
    @PrimaryKey
    @NonNull
    private String uid;
    private String name;
    private String email;
    private String phone;
    private String role; // "citizen" or "authority"
    private String profilePhotoUrl;
    private String address;
    private String ward;
    private String department;
    private String employeeId;
    private String password; // Added for local authentication
    private Date createdAt;
    private boolean isVerified; // For authority accounts
    private String designation; // For authority accounts
    private String workArea; // For authority accounts

    // Empty constructor required for Firestore
    public User() {}

    @Ignore
    public User(String uid, String name, String email, String phone, String role) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.createdAt = new Date();
        this.isVerified = role.equals("citizen"); // Citizens are verified by default
    }

    // Getters and setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getWorkArea() {
        return workArea;
    }

    public void setWorkArea(String workArea) {
        this.workArea = workArea;
    }
}
