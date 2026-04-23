package com.qfix.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.qfix.R;
import com.qfix.data.model.User;
import com.qfix.data.repository.AuthRepository;
import com.qfix.ui.authority.AuthorityDashboardActivity;
import com.qfix.ui.citizen.HomeActivity;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextView forgotPasswordText;
    private Button loginButton;
    private TextView signUpText;

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
        authRepository = new AuthRepository(getApplicationContext());
    }

    private void initViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        loginButton = findViewById(R.id.loginButton);
        signUpText = findViewById(R.id.signUpText);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        signUpText.setOnClickListener(v -> navigateToSignup());
        forgotPasswordText.setOnClickListener(v -> navigateToForgotPassword());
    }

    private void attemptLogin() {
        String email = emailInputLayout.getEditText().getText().toString().trim();
        String password = passwordInputLayout.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            emailInputLayout.setError("Email is required");
            return;
        } else {
            emailInputLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordInputLayout.setError("Password is required");
            return;
        } else {
            passwordInputLayout.setError(null);
        }

        // Perform login using local repository
        AuthRepository.AuthResult result = authRepository.signIn(email, password);
        
        if (result.isSuccess()) {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            User user = result.getUser();
            String role = user != null ? user.getRole() : null;
            if ("authority".equals(role)) {
                startActivity(new Intent(LoginActivity.this, AuthorityDashboardActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }
            finish();
        } else {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToSignup() {
        Intent intent = new Intent(LoginActivity.this, RoleSelectionActivity.class);
        startActivity(intent);
    }

    private void navigateToForgotPassword() {
        Toast.makeText(this, "Password reset functionality would be implemented here", Toast.LENGTH_SHORT).show();
    }
}
