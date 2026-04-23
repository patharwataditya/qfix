package com.qfix.ui.citizen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.qfix.R;
import com.qfix.viewmodel.ComplaintViewModel;

public class HomeFragment extends Fragment {
    private ComplaintViewModel complaintViewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get ViewModel from parent activity
        complaintViewModel = new ViewModelProvider(requireActivity()).get(ComplaintViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setupQuickActions(view);
        return view;
    }

    private void setupQuickActions(View view) {
        View reportIssueCard = view.findViewById(R.id.reportIssueCard);
        if (reportIssueCard != null) {
            reportIssueCard.setOnClickListener(v -> openFragment(new ReportComplaintFragment()));
        }

        View trackTicketCard = view.findViewById(R.id.trackTicketCard);
        if (trackTicketCard != null) {
            trackTicketCard.setOnClickListener(v -> openFragment(new MyComplaintsFragment()));
        }
    }

    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
