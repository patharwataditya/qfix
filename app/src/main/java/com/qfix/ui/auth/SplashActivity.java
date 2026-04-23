package com.qfix.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.qfix.R;
import com.qfix.ui.citizen.HomeActivity;
import com.qfix.ui.authority.AuthorityDashboardActivity;
import com.qfix.viewmodel.AuthViewModel;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2500; // 2.5 seconds
    private AuthViewModel authViewModel;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize ViewModel
        authViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(AuthViewModel.class);

        // Animate the loading dots
        animateLoadingDots();

        // Set up the delayed redirect
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                checkAuthStateAndRedirect();
            }
        };
        handler.postDelayed(runnable, SPLASH_DELAY);
    }

    private void animateLoadingDots() {
        View dot1 = findViewById(R.id.dot1);
        View dot2 = findViewById(R.id.dot2);
        View dot3 = findViewById(R.id.dot3);

        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        pulse.setStartOffset(0);
        dot1.startAnimation(pulse);

        Animation pulse2 = AnimationUtils.loadAnimation(this, R.anim.pulse);
        pulse2.setStartOffset(300);
        dot2.startAnimation(pulse2);

        Animation pulse3 = AnimationUtils.loadAnimation(this, R.anim.pulse);
        pulse3.setStartOffset(600);
        dot3.startAnimation(pulse3);
    }

    private void checkAuthStateAndRedirect() {
        if (authViewModel.isUserLoggedIn()) {
            if (authViewModel.getCurrentUser() != null &&
                    "authority".equals(authViewModel.getCurrentUser().getRole())) {
                startActivity(new Intent(SplashActivity.this, AuthorityDashboardActivity.class));
            } else if (authViewModel.getCurrentUser() != null) {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            } else {
                redirectToOnboarding();
                return;
            }
            finish();
        } else {
            // User is not logged in, redirect to onboarding
            redirectToOnboarding();
        }
    }

    private void redirectToOnboarding() {
        startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
