package com.qfix.ui.citizen;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.qfix.R;
import com.qfix.ui.shared.NotificationsFragment;
import com.qfix.ui.citizen.ProfileFragment;
import com.qfix.data.model.User;
import com.qfix.viewmodel.AuthViewModel;
import com.qfix.viewmodel.ComplaintViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private ComplaintViewModel complaintViewModel;
    private AuthViewModel authViewModel;
    private TextView greetingText;
    private TextView locationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize ViewModel
        complaintViewModel = new ViewModelProvider(this).get(ComplaintViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        observeUser();
        setupBottomNavigation();

        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        greetingText = findViewById(R.id.greetingText);
        locationText = findViewById(R.id.locationText);
    }

    private void observeUser() {
        authViewModel.getUserLiveData().observe(this, this::bindUserHeader);
        bindUserHeader(authViewModel.getCurrentUser());
    }

    private void bindUserHeader(User user) {
        if (user == null) {
            return;
        }

        String name = user.getName();
        if (name != null && !name.trim().isEmpty()) {
            greetingText.setText(name.trim());
        }

        String city = extractCity(user);
        if (city != null && !city.isEmpty()) {
            locationText.setText(city);
        }
    }

    private String extractCity(User user) {
        if (user == null) {
            return "";
        }
        String address = user.getAddress();
        if (address != null && !address.trim().isEmpty()) {
            String[] parts = address.split(",");
            if (parts.length >= 3) {
                String cityPart = parts[2].trim();
                int pincodeSeparator = cityPart.indexOf(" - ");
                if (pincodeSeparator >= 0) {
                    cityPart = cityPart.substring(0, pincodeSeparator).trim();
                }
                if (!cityPart.isEmpty()) {
                    return cityPart;
                }
            }
            return address.trim();
        }
        String ward = user.getWard();
        return ward != null ? ward : "";
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_my_complaints) {
                    selectedFragment = new MyComplaintsFragment();
                } else if (itemId == R.id.nav_notifications) {
                    selectedFragment = new NotificationsFragment();
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                }

                return loadFragment(selectedFragment);
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
