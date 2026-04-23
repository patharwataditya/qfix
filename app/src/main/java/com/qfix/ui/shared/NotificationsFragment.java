package com.qfix.ui.shared;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.qfix.R;
import com.qfix.viewmodel.ComplaintViewModel;

public class NotificationsFragment extends Fragment {
    private ComplaintViewModel complaintViewModel;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get ViewModel from parent activity
        if (getActivity() != null) {
            complaintViewModel = new ViewModelProvider(getActivity()).get(ComplaintViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }
}