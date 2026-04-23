package com.qfix.ui.authority;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.qfix.R;
import com.qfix.data.model.User;
import com.qfix.viewmodel.AuthViewModel;
import com.qfix.viewmodel.ComplaintViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AuthorityDashboardActivity extends AppCompatActivity {
    private AppBarLayout appBarLayout;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private ComplaintViewModel complaintViewModel;
    private AuthViewModel authViewModel;
    private TextView userInfoText;
    private View fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_dashboard);

        // Initialize ViewModel
        complaintViewModel = new ViewModelProvider(this).get(ComplaintViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        bindHeader();
        setupViewPager();
        setupBottomNavigation();
    }

    private void initViews() {
        appBarLayout = findViewById(R.id.appBarLayout);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        userInfoText = findViewById(R.id.userInfoText);
        fragmentContainer = findViewById(R.id.fragment_container);
    }

    private void bindHeader() {
        authViewModel.getUserLiveData().observe(this, this::updateHeader);
        updateHeader(authViewModel.getCurrentUser());
    }

    private void updateHeader(User user) {
        if (userInfoText == null || user == null) {
            return;
        }

        String name = valueOrDefault(user.getName(), "User");
        String department = valueOrDefault(user.getDepartment(), "Department");
        String ward = valueOrDefault(
                user.getWorkArea() != null && !user.getWorkArea().trim().isEmpty() ? user.getWorkArea() : user.getWard(),
                "Ward"
        );
        userInfoText.setText(name + " | " + department + " | " + ward);
    }

    private void setupViewPager() {
        AuthorityPagerAdapter pagerAdapter = new AuthorityPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("All");
                            break;
                        case 1:
                            tab.setText("Open");
                            break;
                        case 2:
                            tab.setText("In Progress");
                            break;
                        case 3:
                            tab.setText("Resolved");
                            break;
                        case 4:
                            tab.setText("Rejected");
                            break;
                    }
                }).attach();
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_dashboard) {
                    return showDashboard();
                } else if (itemId == R.id.nav_complaints) {
                    return showDashboard();
                } else if (itemId == R.id.nav_profile) {
                    return showFragment(new AuthorityProfileFragment());
                } else {
                    return false;
                }
            }
        });
    }

    private boolean showDashboard() {
        if (appBarLayout != null) {
            appBarLayout.setVisibility(View.VISIBLE);
        }
        if (tabLayout != null) {
            tabLayout.setVisibility(View.VISIBLE);
        }
        if (viewPager != null) {
            viewPager.setVisibility(View.VISIBLE);
        }
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.GONE);
        }
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(currentFragment)
                    .commit();
        }
        return true;
    }

    private boolean showFragment(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        if (appBarLayout != null) {
            appBarLayout.setVisibility(View.GONE);
        }
        if (tabLayout != null) {
            tabLayout.setVisibility(View.GONE);
        }
        if (viewPager != null) {
            viewPager.setVisibility(View.GONE);
        }
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.VISIBLE);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        return true;
    }

    private String valueOrDefault(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private class AuthorityPagerAdapter extends FragmentStateAdapter {
        public AuthorityPagerAdapter(AuthorityDashboardActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return AuthorityDashboardFragment.newInstance(getFilterForPosition(position));
        }

        @Override
        public int getItemCount() {
            return 5; // Number of tabs
        }

        private String getFilterForPosition(int position) {
            switch (position) {
                case 1:
                    return "open";
                case 2:
                    return "in_progress";
                case 3:
                    return "resolved";
                case 4:
                    return "rejected";
                default:
                    return null;
            }
        }
    }
}
