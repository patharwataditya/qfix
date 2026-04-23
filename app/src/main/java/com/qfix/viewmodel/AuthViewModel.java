package com.qfix.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.qfix.data.model.User;
import com.qfix.data.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private AuthRepository authRepository;
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<Boolean> isLoadingLiveData;
    private MutableLiveData<String> errorMessageLiveData;
    private MutableLiveData<Boolean> isAuthenticatedLiveData;

    public AuthViewModel(Application application) {
        super(application);
        authRepository = new AuthRepository(application.getApplicationContext());
        userLiveData = new MutableLiveData<>();
        isLoadingLiveData = new MutableLiveData<>();
        errorMessageLiveData = new MutableLiveData<>();
        isAuthenticatedLiveData = new MutableLiveData<>();

        User currentUser = authRepository.getCurrentUser();
        if (currentUser != null) {
            userLiveData.setValue(currentUser);
            isAuthenticatedLiveData.setValue(true);
        } else {
            isAuthenticatedLiveData.setValue(false);
        }
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<Boolean> getIsAuthenticatedLiveData() {
        return isAuthenticatedLiveData;
    }

    public AuthRepository.AuthResult signUp(String email, String password, String name) {
        isLoadingLiveData.setValue(true);
        errorMessageLiveData.setValue(null);
        AuthRepository.AuthResult result = authRepository.signUp(email, password, name);
        isLoadingLiveData.setValue(false);
        
        if (result.isSuccess()) {
            userLiveData.setValue(result.getUser());
            isAuthenticatedLiveData.setValue(true);
        } else {
            errorMessageLiveData.setValue(result.getMessage());
        }
        
        return result;
    }

    public AuthRepository.AuthResult signIn(String email, String password) {
        isLoadingLiveData.setValue(true);
        errorMessageLiveData.setValue(null);
        AuthRepository.AuthResult result = authRepository.signIn(email, password);
        isLoadingLiveData.setValue(false);
        
        if (result.isSuccess()) {
            userLiveData.setValue(result.getUser());
            isAuthenticatedLiveData.setValue(true);
        } else {
            errorMessageLiveData.setValue(result.getMessage());
        }
        
        return result;
    }

    public void signOut() {
        authRepository.signOut();
        isAuthenticatedLiveData.setValue(false);
        userLiveData.setValue(null);
    }

    public boolean saveUser(User user) {
        isLoadingLiveData.setValue(true);
        errorMessageLiveData.setValue(null);
        boolean success = authRepository.saveUser(user);
        isLoadingLiveData.setValue(false);
        
        if (success) {
            userLiveData.setValue(user);
            isAuthenticatedLiveData.setValue(true);
        } else {
            errorMessageLiveData.setValue("Failed to save user");
        }
        
        return success;
    }

    public void getUser(String uid) {
        isLoadingLiveData.setValue(true);
        errorMessageLiveData.setValue(null);
        User user = authRepository.getUser(uid);
        isLoadingLiveData.setValue(false);
        
        if (user != null) {
            userLiveData.setValue(user);
            isAuthenticatedLiveData.setValue(true);
        } else {
            errorMessageLiveData.setValue("User not found");
            isAuthenticatedLiveData.setValue(false);
        }
    }

    public boolean isUserLoggedIn() {
        User currentUser = authRepository.getCurrentUser();
        if (currentUser != null) {
            userLiveData.setValue(currentUser);
            isAuthenticatedLiveData.setValue(true);
            return true;
        }
        return false;
    }

    public String getCurrentUserId() {
        User currentUser = userLiveData.getValue();
        if (currentUser == null) {
            currentUser = authRepository.getCurrentUser();
            if (currentUser != null) {
                userLiveData.setValue(currentUser);
            }
        }
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return null;
    }
    
    public User getCurrentUser() {
        User currentUser = userLiveData.getValue();
        if (currentUser != null) {
            return currentUser;
        }
        return authRepository.getCurrentUser();
    }
}
