package com.qfix.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.qfix.data.local.DatabaseClient;
import com.qfix.data.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthRepository {
    private static final String PREFS_NAME = "qfix_auth";
    private static final String KEY_CURRENT_USER_UID = "current_user_uid";

    private DatabaseClient databaseClient;
    private SharedPreferences sharedPreferences;
    
    public AuthRepository(Context context) {
        databaseClient = DatabaseClient.getInstance(context);
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public AuthResult signUp(String email, String password, String name) {
        // Check if user already exists
        List<User> existingUsers = databaseClient.getAppDatabase().userDao().getAllUsers();
        for (User user : existingUsers) {
            if (user.getEmail().equals(email)) {
                return new AuthResult(false, "User already exists");
            }
        }
        
        // Create new user
        User user = new User();
        user.setUid(UUID.randomUUID().toString());
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password); // In a real app, this would be hashed
        
        // Save user to database
        databaseClient.getAppDatabase().userDao().insert(user);
        setCurrentUserUid(user.getUid());
        
        return new AuthResult(true, "User created successfully", user);
    }
    
    public AuthResult signIn(String email, String password) {
        List<User> users = databaseClient.getAppDatabase().userDao().getAllUsers();
        for (User user : users) {
            if (user.getEmail().equals(email) && 
                user.getPassword() != null && 
                user.getPassword().equals(password)) { // In a real app, this would check the hash
                setCurrentUserUid(user.getUid());
                return new AuthResult(true, "Sign in successful", user);
            }
        }
        return new AuthResult(false, "Invalid email or password");
    }
    
    public void signOut() {
        sharedPreferences.edit().remove(KEY_CURRENT_USER_UID).apply();
    }
    
    public User getCurrentUser() {
        String uid = sharedPreferences.getString(KEY_CURRENT_USER_UID, null);
        if (uid == null || uid.trim().isEmpty()) {
            return null;
        }
        return getUser(uid);
    }
    
    public boolean sendPasswordResetEmail(String email) {
        // Local implementation would typically involve storing a reset token
        // For now, just return true to simulate success
        return true;
    }
    
    public boolean saveUser(User user) {
        try {
            databaseClient.getAppDatabase().userDao().update(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public User getUser(String uid) {
        return databaseClient.getAppDatabase().userDao().getUserByIdSync(uid);
    }

    private void setCurrentUserUid(String uid) {
        sharedPreferences.edit().putString(KEY_CURRENT_USER_UID, uid).apply();
    }
    
    // Local class to represent authentication results
    public static class AuthResult {
        private boolean success;
        private String message;
        private User user;
        
        public AuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public User getUser() {
            return user;
        }
    }
}
