package com.qfix.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.qfix.R;
import com.qfix.ui.auth.CitizenSignupActivity;
import com.qfix.ui.auth.AuthoritySignupActivity;
import com.qfix.ui.auth.LoginActivity;

public class RoleSelectionActivity extends AppCompatActivity {
    private CardView citizenCard;
    private CardView authorityCard;
    private TextView signInText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        citizenCard = findViewById(R.id.citizenCard);
        authorityCard = findViewById(R.id.authorityCard);
        signInText = findViewById(R.id.signInText);
    }

    private void setupClickListeners() {
        citizenCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoleSelectionActivity.this, CitizenSignupActivity.class));
            }
        });

        authorityCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoleSelectionActivity.this, AuthoritySignupActivity.class));
            }
        });

        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoleSelectionActivity.this, LoginActivity.class));
            }
        });
    }
}