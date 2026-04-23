package com.qfix.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.qfix.R;
import com.qfix.data.model.User;
import com.qfix.data.repository.AuthRepository;
import com.qfix.ui.authority.AuthorityDashboardActivity;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

public class AuthoritySignupActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputLayout nameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout phoneInputLayout;
    private TextInputLayout departmentInputLayout;
    private TextInputLayout employeeIdInputLayout;
    private TextInputLayout designationInputLayout;
    private TextInputLayout workAreaInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout confirmPasswordInputLayout;
    private Button submitVerificationButton;

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_signup);

        initViews();
        setupToolbar();
        setupDepartmentDropdown();
        setupClickListeners();
        authRepository = new AuthRepository(getApplicationContext());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        departmentInputLayout = findViewById(R.id.departmentInputLayout);
        employeeIdInputLayout = findViewById(R.id.employeeIdInputLayout);
        designationInputLayout = findViewById(R.id.designationInputLayout);
        workAreaInputLayout = findViewById(R.id.workAreaInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
        submitVerificationButton = findViewById(R.id.submitVerificationButton);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Authority Signup");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupDepartmentDropdown() {
        String[] departments = {
            "Public Works Department",
            "Health Department", 
            "Education Department",
            "Police Department",
            "Fire Department",
            "Water Supply Department",
            "Sanitation Department",
            "Transport Department",
            "Revenue Department",
            "Urban Development Department"
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            departments
        );
        
        MaterialAutoCompleteTextView departmentDropdown = (MaterialAutoCompleteTextView) departmentInputLayout.getEditText();
        departmentDropdown.setAdapter(adapter);
    }

    private void setupClickListeners() {
        submitVerificationButton.setOnClickListener(v -> submitForVerification());
    }

    private void submitForVerification() {
        if (!validateForm()) {
            return;
        }

        String name = nameInputLayout.getEditText().getText().toString().trim();
        String email = emailInputLayout.getEditText().getText().toString().trim();
        String phone = phoneInputLayout.getEditText().getText().toString().trim();
        String department = departmentInputLayout.getEditText().getText().toString().trim();
        String employeeId = employeeIdInputLayout.getEditText().getText().toString().trim();
        String designation = designationInputLayout.getEditText().getText().toString().trim();
        String workArea = workAreaInputLayout.getEditText().getText().toString().trim();
        String password = passwordInputLayout.getEditText().getText().toString().trim();

        // Create user object
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole("authority");
        user.setDepartment(department);
        user.setEmployeeId(employeeId);
        user.setDesignation(designation);
        user.setWorkArea(workArea);
        user.setWard(workArea);
        user.setVerified(true); // For local implementation, we'll auto-verify
        
        // Register user using local repository
        AuthRepository.AuthResult result = authRepository.signUp(email, password, name);
        
        if (result.isSuccess()) {
            // Save additional user info
            User registeredUser = result.getUser();
            registeredUser.setRole("authority");
            registeredUser.setPhone(phone);
            registeredUser.setDepartment(department);
            registeredUser.setEmployeeId(employeeId);
            registeredUser.setDesignation(designation);
            registeredUser.setWorkArea(workArea);
            registeredUser.setWard(workArea);
            registeredUser.setVerified(true);
            
            boolean saved = authRepository.saveUser(registeredUser);
            if (saved) {
                Toast.makeText(this, "Account created successfully! Verification submitted.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(AuthoritySignupActivity.this, AuthorityDashboardActivity.class));
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

        String department = departmentInputLayout.getEditText().getText().toString().trim();
        if (department.isEmpty()) {
            departmentInputLayout.setError("Department is required");
            valid = false;
        } else {
            departmentInputLayout.setError(null);
        }

        String employeeId = employeeIdInputLayout.getEditText().getText().toString().trim();
        if (employeeId.isEmpty()) {
            employeeIdInputLayout.setError("Employee ID is required");
            valid = false;
        } else {
            employeeIdInputLayout.setError(null);
        }

        String designation = designationInputLayout.getEditText().getText().toString().trim();
        if (designation.isEmpty()) {
            designationInputLayout.setError("Designation is required");
            valid = false;
        } else {
            designationInputLayout.setError(null);
        }

        String workArea = workAreaInputLayout.getEditText().getText().toString().trim();
        if (workArea.isEmpty()) {
            workAreaInputLayout.setError("Work area is required");
            valid = false;
        } else {
            workAreaInputLayout.setError(null);
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

        return valid;
    }
}
