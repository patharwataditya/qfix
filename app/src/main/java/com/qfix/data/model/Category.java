package com.qfix.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "category")
public class Category {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String icon; // Icon name for display

    // Empty constructor
    public Category() {}
    
    @Ignore
    public Category(String id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}