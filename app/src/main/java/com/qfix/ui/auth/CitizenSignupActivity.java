package com.qfix.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.qfix.R;
import com.qfix.data.model.User;
import com.qfix.data.repository.AuthRepository;
import com.qfix.ui.citizen.HomeActivity;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;

public class CitizenSignupActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private de.hdodenhof.circleimageview.CircleImageView profileImage;
    private TextInputLayout nameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout phoneInputLayout;
    private TextInputLayout houseInputLayout;
    private TextInputLayout streetInputLayout;
    private TextInputLayout cityInputLayout;
    private TextInputLayout pincodeInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout confirmPasswordInputLayout;
    private MaterialCheckBox termsCheckbox;
    private Button createAccountButton;

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_signup);

        initViews();
        setupToolbar();
        setupPasswordStrengthChecker();
        setupClickListeners();
        authRepository = new AuthRepository(getApplicationContext());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        profileImage = findViewById(R.id.profileImage);
        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        houseInputLayout = findViewById(R.id.houseInputLayout);
        streetInputLayout = findViewById(R.id.streetInputLayout);
        cityInputLayout = findViewById(R.id.cityInputLayout);
        pincodeInputLayout = findViewById(R.id.pincodeInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
        termsCheckbox = findViewById(R.id.termsCheckbox);
        createAccountButton = findViewById(R.id.createAccountButton);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Citizen Signup");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupPasswordStrengthChecker() {
        passwordInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordStrength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void checkPasswordStrength(String password) {
        if (password.length() < 6) {
            passwordInputLayout.setHelperText("Weak password");
            passwordInputLayout.setHelperTextColor(androidx.core.content.ContextCompat.getColorStateList(this, R.color.error));
        } else if (password.length() < 8) {
            passwordInputLayout.setHelperText("Medium password");
            passwordInputLayout.setHelperTextColor(androidx.core.content.ContextCompat.getColorStateList(this, R.color.warning));
        } else {
            passwordInputLayout.setHelperText("Strong password");
            passwordInputLayout.setHelperTextColor(androidx.core.content.ContextCompat.getColorStateList(this, R.color.success));
        }
    }

    private void setupClickListeners() {
        createAccountButton.setOnClickListener(v -> createAccount());
        profileImage.setOnClickListener(v -> selectProfileImage());
    }

    private void selectProfileImage() {
        // For local implementation, we'll skip image selection
        Toast.makeText(this, "Image selection would be implemented here", Toast.LENGTH_SHORT).show();
    }

    private void createAccount() {
        if (!validateForm()) {
            return;
        }

        String name = nameInputLayout.getEditText().getText().toString().trim();
        String email = emailInputLayout.getEditText().getText().toString().trim();
        String password = passwordInputLayout.getEditText().getText().toString().trim();
        String phone = phoneInputLayout.getEditText().getText().toString().trim();
        String house = houseInputLayout.getEditText().getText().toString().trim();
        String street = streetInputLayout.getEditText().getText().toString().trim();
        String city = cityInputLayout.getEditText().getText().toString().trim();
        String pincode = pincodeInputLayout.getEditText().getText().toString().trim();

        // Create user object
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole("citizen");
        
        // Construct address
        String address = house + ", " + street + ", " + city + " - " + pincode;
        user.setAddress(address);
        user.setWard("WARD_" + pincode.substring(0, Math.min(3, pincode.length()))); // Simple ward assignment
        
        // Register user using local repository
        AuthRepository.AuthResult result = authRepository.signUp(email, password, name);
        
        if (result.isSuccess()) {
            // Save additional user info
            User registeredUser = result.getUser();
            registeredUser.setRole("citizen");
            registeredUser.setPhone(phone);
            registeredUser.setAddress(address);
            registeredUser.setWard("WARD_" + pincode.substring(0, Math.min(3, pincode.length())));
            
            boolean saved = authRepository.saveUser(registeredUser);
            if (saved) {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CitizenSignupActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Failed to save user information", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = nameInputLayout.getEditText().getText().toString().trim();
        if (name.isEmpty()) {
            nameInputLayout.setError("Name is required");
            valid = false;
        } else {
            nameInputLayout.setError(null);
        }

        String email = emailInputLayout.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            emailInputLayout.setError("Email is required");
            valid = false;
        } else {
            emailInputLayout.setError(null);
        }

        String phone = phoneInputLayout.getEditText().getText().toString().trim();
        if (phone.isEmpty()) {
            phoneInputLayout.setError("Phone number is required");
            valid = false;
        } else {
            phoneInputLayout.setError(null);
        }

        String house = houseInputLayout.getEditText().getText().toString().trim();
        if (house.isEmpty()) {
            houseInputLayout.setError("House number is required");
            valid = false;
        } else {
            houseInputLayout.setError(null);
        }

        String street = streetInputLayout.getEditText().getText().toString().trim();
        if (street.isEmpty()) {
            streetInputLayout.setError("Street is required");
            valid = false;
        } else {
            streetInputLayout.setError(null);
        }

        String city = cityInputLayout.getEditText().getText().toString().trim();
        if (city.isEmpty()) {
            cityInputLayout.setError("City is required");
            valid = false;
        } else {
            cityInputLayout.setError(null);
        }

        String pincode = pincodeInputLayout.getEditText().getText().toString().trim();
        if (pincode.isEmpty()) {
            pincodeInputLayout.setError("Pincode is required");
            valid = false;
        } else {
            pincodeInputLayout.setError(null);
        }

        String password = passwordInputLayout.getEditText().getText().toString();
        if (password.isEmpty()) {
            passwordInputLayout.setError("Password is required");
            valid = false;
        } else if (password.length() < 6) {
            passwordInputLayout.setError("Password should be at least 6 characters");
            valid = false;
        } else {
            passwordInputLayout.setError(null);
        }

        String confirmPassword = confirmPasswordInputLayout.getEditText().getText().toString();
        if (!password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError("Passwords do not match");
            valid = false;
        } else {
            confirmPasswordInputLayout.setError(null);
        }

        if (!termsCheckbox.isChecked()) {
            Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }
}
