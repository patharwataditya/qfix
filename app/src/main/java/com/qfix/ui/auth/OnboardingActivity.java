package com.qfix.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.qfix.R;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private LinearLayout pageIndicator;
    private Button skipButton;
    private OnboardingPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initViews();
        setupViewPager();
        setupPageIndicator();
        setupSkipButton();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        pageIndicator = findViewById(R.id.pageIndicator);
        skipButton = findViewById(R.id.skipButton);
    }

    private void setupViewPager() {
        adapter = new OnboardingPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Add onboarding pages
        adapter.addFragment(OnboardingFragment.newInstance(
                getString(R.string.report_instantly),
                getString(R.string.report_instantly_desc),
                R.raw.splash_animation,
                getColor(R.color.electric_blue)
        ));

        adapter.addFragment(OnboardingFragment.newInstance(
                getString(R.string.track_progress),
                getString(R.string.track_progress_desc),
                R.raw.bell_animation,
                getColor(R.color.emerald_green)
        ));

        adapter.addFragment(OnboardingFragment.newInstance(
                getString(R.string.make_difference),
                getString(R.string.make_difference_desc),
                R.raw.empty_clipboard,
                getColor(R.color.vivid_orange)
        ));
    }

    private void setupPageIndicator() {
        // Clear existing indicators
        pageIndicator.removeAllViews();

        // Create indicators
        for (int i = 0; i < adapter.getItemCount(); i++) {
            View indicator = new View(this);
            indicator.setBackgroundResource(R.drawable.indicator_inactive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) getResources().getDimension(R.dimen.indicator_size),
                    (int) getResources().getDimension(R.dimen.indicator_size)
            );
            params.setMargins(
                    (int) getResources().getDimension(R.dimen.indicator_margin),
                    0,
                    (int) getResources().getDimension(R.dimen.indicator_margin),
                    0
            );
            indicator.setLayoutParams(params);
            pageIndicator.addView(indicator);
        }

        // Set initial selection
        updateIndicators(0);

        // Set up page change listener
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position);
            }
        });
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < pageIndicator.getChildCount(); i++) {
            View indicator = pageIndicator.getChildAt(i);
            if (i == position) {
                indicator.setBackgroundResource(R.drawable.indicator_active);
            } else {
                indicator.setBackgroundResource(R.drawable.indicator_inactive);
            }
        }
    }

    private void setupSkipButton() {
        skipButton.setOnClickListener(v -> {
            // Navigate to role selection
            startActivity(new Intent(OnboardingActivity.this, RoleSelectionActivity.class));
            finish();
        });
    }

    private static class OnboardingPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments = new ArrayList<>();

        public OnboardingPagerAdapter(OnboardingActivity activity) {
            super(activity);
        }

        public void addFragment(Fragment fragment) {
            fragments.add(fragment);
        }

        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}
