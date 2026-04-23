package com.qfix.ui.authority;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qfix.R;
import com.qfix.data.model.Complaint;
import com.qfix.data.model.User;
import com.qfix.ui.shared.ComplaintDetailActivity;
import com.qfix.viewmodel.AuthViewModel;
import com.qfix.viewmodel.ComplaintViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AuthorityComplaintsFragment extends Fragment {
    private RecyclerView complaintsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerLayout;
    private ChipGroup statusChipGroup;
    private View filterButton;
    
    private ComplaintViewModel complaintViewModel;
    private AuthViewModel authViewModel;
    private ComplaintsAdapter complaintsAdapter;
    private List<Complaint> complaintList;
    private List<Complaint> allComplaintList;

    public AuthorityComplaintsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        complaintList = new ArrayList<>();
        allComplaintList = new ArrayList<>();
        complaintViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
                .create(ComplaintViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authority_complaints, container, false);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        observeViewModels();
        loadComplaints();

        return view;
    }

    private void initViews(View view) {
        complaintsRecyclerView = view.findViewById(R.id.complaintsRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        statusChipGroup = view.findViewById(R.id.statusChipGroup);
        filterButton = view.findViewById(R.id.filterButton);
    }

    private void setupRecyclerView() {
        complaintsAdapter = new ComplaintsAdapter(complaintList, complaint -> {
            Intent intent = new Intent(requireContext(), ComplaintDetailActivity.class);
            intent.putExtra(ComplaintDetailActivity.EXTRA_COMPLAINT_ID, complaint.getId());
            startActivity(intent);
        });
        complaintsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        complaintsRecyclerView.setAdapter(complaintsAdapter);
    }

    private void setupClickListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadComplaints);
        
        filterButton.setOnClickListener(v -> {
            // TODO: Show filter options
            Toast.makeText(getContext(), "Filter options would appear here", Toast.LENGTH_SHORT).show();
        });
        
        statusChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            applyStatusFilter(checkedId);
        });
    }

    private void observeViewModels() {
        complaintViewModel.getComplaintsLiveData().observe(getViewLifecycleOwner(), complaints -> {
            if (complaints != null) {
                allComplaintList.clear();
                allComplaintList.addAll(complaints);
            } else {
                allComplaintList.clear();
            }
            applyStatusFilter(statusChipGroup.getCheckedChipId());
            hideLoading();
        });

        complaintViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                showLoading();
            }
        });

        complaintViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                hideLoading();
            }
        });
    }

    private void loadComplaints() {
        User currentUser = authViewModel.getCurrentUser();
        complaintViewModel.getComplaintsForAuthority(currentUser);
    }

    private void applyStatusFilter(int chipId) {
        String status = getStatusForChip(chipId);
        complaintList.clear();
        for (Complaint complaint : allComplaintList) {
            if (status == null || status.equals(normalizeStatus(complaint.getStatus()))) {
                complaintList.add(complaint);
            }
        }
        complaintsAdapter.notifyDataSetChanged();
    }

    private String getStatusForChip(int chipId) {
        if (chipId == R.id.openChip) {
            return "open";
        }
        if (chipId == R.id.inProgressChip) {
            return "in_progress";
        }
        if (chipId == R.id.resolvedChip) {
            return "resolved";
        }
        return null;
    }

    private String normalizeStatus(String status) {
        return status == null ? "open" : status.trim().toLowerCase(Locale.ROOT);
    }

    private void showLoading() {
        shimmerLayout.setVisibility(View.VISIBLE);
        complaintsRecyclerView.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) {
            shimmerLayout.hideShimmer();
        } else {
            shimmerLayout.showShimmer(true);
        }
    }

    private void hideLoading() {
        shimmerLayout.hideShimmer();
        shimmerLayout.setVisibility(View.GONE);
        complaintsRecyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }
}
