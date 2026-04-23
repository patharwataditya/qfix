package com.qfix.ui.shared;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.qfix.R;
import com.qfix.viewmodel.ComplaintViewModel;

public class ComplaintDetailActivity extends AppCompatActivity {
    public static final String EXTRA_COMPLAINT_ID = "complaint_id";

    private ComplaintViewModel complaintViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_detail);

        // Initialize ViewModel
        complaintViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(ComplaintViewModel.class);

        String complaintId = getIntent().getStringExtra(EXTRA_COMPLAINT_ID);
        if (complaintId != null && !complaintId.trim().isEmpty()) {
            complaintViewModel.getComplaint(complaintId);
        }

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Complaint Details");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
