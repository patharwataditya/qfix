package com.qfix.ui.citizen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.qfix.R;
import com.qfix.data.model.Complaint;
import com.qfix.viewmodel.AuthViewModel;
import com.qfix.viewmodel.ComplaintViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Date;

public class ReportComplaintFragment extends Fragment {
    private ViewPager2 reportViewPager;
    private LinearProgressIndicator stepperProgress;
    private MaterialCardView photosStepCard;
    private MaterialCardView detailsStepCard;
    private MaterialCardView locationStepCard;
    
    private ComplaintViewModel complaintViewModel;
    private AuthViewModel authViewModel;
    private ReportPagerAdapter pagerAdapter;

    public ReportComplaintFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        complaintViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
                .create(ComplaintViewModel.class);
        authViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
                .create(AuthViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_complaint, container, false);

        initViews(view);
        setupViewPager();
        observeViewModels();

        return view;
    }

    private void initViews(View view) {
        reportViewPager = view.findViewById(R.id.reportViewPager);
        stepperProgress = view.findViewById(R.id.stepperProgress);
        photosStepCard = view.findViewById(R.id.photosStepCard);
        detailsStepCard = view.findViewById(R.id.detailsStepCard);
        locationStepCard = view.findViewById(R.id.locationStepCard);
    }

    private void setupViewPager() {
        pagerAdapter = new ReportPagerAdapter(this);
        reportViewPager.setAdapter(pagerAdapter);

        // Update progress when page changes
        reportViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateProgress(position);
                updateStepCards(position);
            }
        });
        
        // Set initial step card states
        updateStepCards(0);
    }

    private void updateProgress(int currentPosition) {
        // Update progress indicator (0-100)
        stepperProgress.setProgress((currentPosition + 1) * 50);
    }

    private void updateStepCards(int currentPosition) {
        // Reset all cards
        resetStepCard(photosStepCard);
        resetStepCard(detailsStepCard);
        resetStepCard(locationStepCard);
        
        // Highlight current and completed steps
        switch (currentPosition) {
            case 0:
                highlightStepCard(photosStepCard);
                break;
            case 1:
                highlightStepCard(photosStepCard);
                highlightStepCard(detailsStepCard);
                break;
            case 2:
                highlightStepCard(photosStepCard);
                highlightStepCard(detailsStepCard);
                highlightStepCard(locationStepCard);
                break;
        }
    }

    private void highlightStepCard(MaterialCardView card) {
        card.setCardBackgroundColor(getResources().getColor(R.color.electric_blue, null));
    }

    private void resetStepCard(MaterialCardView card) {
        card.setCardBackgroundColor(getResources().getColor(R.color.text_secondary, null));
    }

    private void observeViewModels() {
        complaintViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            // Handle loading state if needed
        });

        complaintViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void navigateToNext() {
        int currentPosition = reportViewPager.getCurrentItem();
        if (currentPosition < 2) {
            reportViewPager.setCurrentItem(currentPosition + 1, true);
        }
    }

    public void navigateToPrevious() {
        int currentPosition = reportViewPager.getCurrentItem();
        if (currentPosition > 0) {
            reportViewPager.setCurrentItem(currentPosition - 1, true);
        }
    }

    public void submitComplaint() {
        if (pagerAdapter == null) {
            Toast.makeText(getContext(), "Unable to submit complaint right now", Toast.LENGTH_LONG).show();
            return;
        }

        DetailsFragment detailsFragment = pagerAdapter.getDetailsFragment();
        LocationFragment locationFragment = pagerAdapter.getLocationFragment();

        if (detailsFragment == null || locationFragment == null) {
            Toast.makeText(getContext(), "Please complete all steps before submitting", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Create complaint object
        Complaint complaint = new Complaint();
        complaint.setTitle(detailsFragment.getTitle());
        complaint.setCategory(detailsFragment.getSelectedCategory());
        complaint.setDescription(detailsFragment.getDescription());
        complaint.setPriority(detailsFragment.getPriority());
        complaint.setPublic(detailsFragment.isPublic());
        complaint.setLocationText(locationFragment.getLocationText());
        complaint.setWard(locationFragment.getWard());
        complaint.setCitizenId(authViewModel.getCurrentUserId());
        complaint.setStatus("open");
        complaint.setCreatedAt(new Date());
        complaint.setUpdatedAt(new Date());

        // TODO: Upload photos and set photo URLs

        // Save complaint to database
        boolean success = complaintViewModel.createComplaint(complaint);
        if (success) {
            Toast.makeText(getContext(), "Complaint submitted successfully!", Toast.LENGTH_SHORT).show();
            if (isAdded()) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        } else {
            Toast.makeText(getContext(), "Failed to submit complaint", Toast.LENGTH_LONG).show();
        }
    }

    private static class ReportPagerAdapter extends FragmentStateAdapter {
        private final PhotosFragment photosFragment = new PhotosFragment();
        private final DetailsFragment detailsFragment = new DetailsFragment();
        private final LocationFragment locationFragment = new LocationFragment();

        public ReportPagerAdapter(ReportComplaintFragment fragment) {
            super(fragment);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return photosFragment;
                case 1:
                    return detailsFragment;
                case 2:
                    return locationFragment;
                default:
                    return photosFragment;
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        public DetailsFragment getDetailsFragment() {
            return detailsFragment;
        }

        public LocationFragment getLocationFragment() {
            return locationFragment;
        }
    }
}
