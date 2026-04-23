package com.qfix.ui.citizen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.qfix.R;
import com.qfix.data.model.Complaint;
import com.qfix.ui.shared.ComplaintDetailActivity;
import com.qfix.ui.authority.ComplaintsAdapter;
import com.qfix.viewmodel.AuthViewModel;
import com.qfix.viewmodel.ComplaintViewModel;

import java.util.ArrayList;

public class MyComplaintsFragment extends Fragment implements ComplaintsAdapter.OnComplaintClickListener {
    private ComplaintViewModel complaintViewModel;
    private RecyclerView complaintsRecyclerView;
    private ComplaintsAdapter complaintsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerLayout;
    private LinearLayout emptyStateLayout;
    private AuthViewModel authViewModel;
    private final ArrayList<Complaint> complaintList = new ArrayList<>();

    public MyComplaintsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get ViewModel from parent activity
        complaintViewModel = new ViewModelProvider(requireActivity()).get(ComplaintViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_complaints, container, false);
        
        initViews(view);
        setupRecyclerView();
        observeViewModel();
        
        return view;
    }

    private void initViews(View view) {
        complaintsRecyclerView = view.findViewById(R.id.complaintsRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        
        // Set up refresh listener
        swipeRefreshLayout.setOnRefreshListener(this::loadComplaints);
    }

    private void setupRecyclerView() {
        complaintsAdapter = new ComplaintsAdapter(complaintList, this);
        complaintsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        complaintsRecyclerView.setAdapter(complaintsAdapter);
    }

    private void observeViewModel() {
        complaintViewModel.getComplaints().observe(getViewLifecycleOwner(), complaints -> {
            complaintList.clear();
            if (complaints != null) {
                complaintList.addAll(complaints);
            }
            complaintsAdapter.notifyDataSetChanged();
            toggleEmptyState(complaintList.isEmpty());
        });
        
        complaintViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    showShimmer();
                } else {
                    hideShimmer();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        loadComplaints();
    }

    private void showShimmer() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        complaintsRecyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private void hideShimmer() {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
        complaintsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void toggleEmptyState(boolean isEmpty) {
        if (isEmpty) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            complaintsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            complaintsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onComplaintClick(Complaint complaint) {
        Intent intent = new Intent(requireContext(), ComplaintDetailActivity.class);
        intent.putExtra(ComplaintDetailActivity.EXTRA_COMPLAINT_ID, complaint.getId());
        startActivity(intent);
    }

    private void loadComplaints() {
        String currentUserId = authViewModel.getCurrentUserId();
        if (currentUserId != null) {
            complaintViewModel.getComplaintsByCitizen(currentUserId);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            toggleEmptyState(true);
        }
    }
}
