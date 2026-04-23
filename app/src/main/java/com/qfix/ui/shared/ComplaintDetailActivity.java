package com.qfix.ui.shared;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.qfix.R;
import com.qfix.data.model.Complaint;
import com.qfix.data.model.Update;
import com.qfix.data.model.User;
import com.qfix.utils.DateUtils;
import com.qfix.viewmodel.AuthViewModel;
import com.qfix.viewmodel.ComplaintViewModel;
import com.google.android.material.chip.Chip;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComplaintDetailActivity extends AppCompatActivity {
    public static final String EXTRA_COMPLAINT_ID = "complaint_id";

    private ComplaintViewModel complaintViewModel;
    private AuthViewModel authViewModel;

    private TextView ticketIdText;
    private TextView categoryText;
    private TextView priorityText;
    private TextView dateSubmittedText;
    private TextView lastUpdatedText;
    private TextView titleText;
    private TextView descriptionText;
    private LinearLayout timelineContainer;
    private Chip statusChip;
    private CardView authorityInfoCard;
    private TextView authorityNameText;
    private TextView authorityDepartmentText;
    private TextView authorityDesignationText;
    private CardView resolutionCard;
    private TextView resolutionNoteText;
    private TextView resolvedOnText;
    private Button escalateButton;
    private Button reportSimilarButton;
    private Button giveFeedbackButton;
    private ImageButton copyButton;
    private ImageButton shareButton;

    private Complaint currentComplaint;
    private String complaintId;
    private boolean isAuthorityUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_detail);

        complaintViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(ComplaintViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        complaintId = getIntent().getStringExtra(EXTRA_COMPLAINT_ID);
        User currentUser = authViewModel.getCurrentUser();
        isAuthorityUser = currentUser != null && "authority".equalsIgnoreCase(currentUser.getRole());

        initViews();
        setupToolbar();
        setupActions();
        observeData();
        loadData();
    }

    private void initViews() {
        ticketIdText = findViewById(R.id.ticketIdText);
        categoryText = findViewById(R.id.categoryText);
        priorityText = findViewById(R.id.priorityText);
        dateSubmittedText = findViewById(R.id.dateSubmittedText);
        lastUpdatedText = findViewById(R.id.lastUpdatedText);
        titleText = findViewById(R.id.titleText);
        descriptionText = findViewById(R.id.descriptionText);
        timelineContainer = findViewById(R.id.timelineContainer);
        statusChip = findViewById(R.id.statusChip);
        authorityInfoCard = findViewById(R.id.authorityInfoCard);
        authorityNameText = findViewById(R.id.authorityNameText);
        authorityDepartmentText = findViewById(R.id.authorityDepartmentText);
        authorityDesignationText = findViewById(R.id.authorityDesignationText);
        resolutionCard = findViewById(R.id.resolutionCard);
        resolutionNoteText = findViewById(R.id.resolutionNoteText);
        resolvedOnText = findViewById(R.id.resolvedOnText);
        escalateButton = findViewById(R.id.escalateButton);
        reportSimilarButton = findViewById(R.id.reportSimilarButton);
        giveFeedbackButton = findViewById(R.id.giveFeedbackButton);
        copyButton = findViewById(R.id.copyButton);
        shareButton = findViewById(R.id.shareButton);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.complaint_details));
        }
    }

    private void setupActions() {
        copyButton.setOnClickListener(v -> copyTicketId());
        shareButton.setOnClickListener(v -> shareComplaint());

        if (isAuthorityUser) {
            escalateButton.setVisibility(android.view.View.GONE);
            reportSimilarButton.setText(R.string.update_complaint_status);
            reportSimilarButton.setOnClickListener(v -> openUpdateStatusSheet());
            giveFeedbackButton.setVisibility(android.view.View.GONE);
        } else {
            escalateButton.setOnClickListener(v ->
                    Toast.makeText(this, "Escalation flow is not implemented yet", Toast.LENGTH_SHORT).show());
            reportSimilarButton.setOnClickListener(v ->
                    Toast.makeText(this, "Report similar flow is not implemented yet", Toast.LENGTH_SHORT).show());
        }
    }

    private void observeData() {
        complaintViewModel.getComplaintLiveData().observe(this, complaint -> {
            currentComplaint = complaint;
            bindComplaint(complaint);
        });

        complaintViewModel.getUpdatesLiveData().observe(this, this::bindTimeline);

        complaintViewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.trim().isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        if (complaintId == null || complaintId.trim().isEmpty()) {
            Toast.makeText(this, "Complaint not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        complaintViewModel.getComplaint(complaintId);
        complaintViewModel.getUpdatesForComplaint(complaintId);
    }

    private void bindComplaint(Complaint complaint) {
        if (complaint == null) {
            return;
        }

        ticketIdText.setText("Ticket ID: #" + shortTicketId(complaint.getId()));
        categoryText.setText(formatLabel(complaint.getCategory(), "Uncategorized"));
        priorityText.setText(formatLabel(complaint.getPriority(), "Medium"));
        dateSubmittedText.setText(formatDate(complaint.getCreatedAt()));
        lastUpdatedText.setText(formatDate(complaint.getUpdatedAt() != null ? complaint.getUpdatedAt() : complaint.getCreatedAt()));
        titleText.setText(valueOrDefault(complaint.getTitle(), "Untitled complaint"));
        descriptionText.setText(valueOrDefault(complaint.getDescription(), "No description provided"));

        String status = valueOrDefault(complaint.getStatus(), "open");
        statusChip.setText(formatLabel(status, "Open"));
        statusChip.setChipBackgroundColorResource(statusColor(status));

        bindAuthorityInfo(complaint);
        bindResolution(complaint);
    }

    private void bindAuthorityInfo(Complaint complaint) {
        if (complaint.getAssignedDepartment() == null || complaint.getAssignedDepartment().trim().isEmpty()) {
            authorityInfoCard.setVisibility(android.view.View.GONE);
            return;
        }

        authorityInfoCard.setVisibility(android.view.View.VISIBLE);
        User assignedAuthority = null;
        if (complaint.getAssignedAuthorityId() != null && !complaint.getAssignedAuthorityId().trim().isEmpty()) {
            assignedAuthority = authViewModel.getCurrentUser() != null
                    && complaint.getAssignedAuthorityId().equals(authViewModel.getCurrentUser().getUid())
                    ? authViewModel.getCurrentUser()
                    : null;
        }

        authorityNameText.setText(assignedAuthority != null
                ? valueOrDefault(assignedAuthority.getName(), "Assigned Authority")
                : "Assigned Authority");
        authorityDepartmentText.setText(valueOrDefault(complaint.getAssignedDepartment(), "Department"));
        authorityDesignationText.setText(assignedAuthority != null
                ? valueOrDefault(assignedAuthority.getDesignation(), valueOrDefault(assignedAuthority.getWorkArea(), ""))
                : valueOrDefault(complaint.getWard(), ""));
    }

    private void bindResolution(Complaint complaint) {
        boolean isResolved = "resolved".equalsIgnoreCase(complaint.getStatus());
        resolutionCard.setVisibility(isResolved ? android.view.View.VISIBLE : android.view.View.GONE);
        giveFeedbackButton.setVisibility(!isAuthorityUser && isResolved ? android.view.View.VISIBLE : android.view.View.GONE);
        if (!isResolved) {
            return;
        }

        resolutionNoteText.setText(valueOrDefault(complaint.getResolutionNote(), "Resolved by authority"));
        Date resolvedAt = complaint.getResolvedAt() != null ? complaint.getResolvedAt() : complaint.getUpdatedAt();
        resolvedOnText.setText("Resolved on: " + formatDate(resolvedAt));
    }

    private void bindTimeline(List<Update> updates) {
        timelineContainer.removeAllViews();

        if (updates == null || updates.isEmpty()) {
            addTimelineItem("Complaint submitted", "Awaiting first authority update");
            return;
        }

        for (Update update : updates) {
            String title = formatLabel(update.getStatus(), "Update");
            String detail = valueOrDefault(update.getNote(), "No comment added");
            String timestamp = update.getTimestamp() != null ? DateUtils.formatDateTime(update.getTimestamp()) : "Just now";
            addTimelineItem(title, detail + " • " + timestamp);
        }
    }

    private void addTimelineItem(String title, String subtitle) {
        TextView item = new TextView(this);
        item.setText(title + "\n" + subtitle);
        item.setTextColor(getResources().getColor(R.color.text_primary, null));
        item.setPadding(0, 0, 0, 24);
        timelineContainer.addView(item);
    }

    private void openUpdateStatusSheet() {
        if (!isAuthorityUser || currentComplaint == null) {
            return;
        }

        UpdateStatusBottomSheet bottomSheet = UpdateStatusBottomSheet.newInstance(currentComplaint.getStatus());
        bottomSheet.setOnStatusUpdateListener(this::applyStatusUpdate);
        bottomSheet.show(getSupportFragmentManager(), "update_status");
    }

    private void applyStatusUpdate(String status, String comments) {
        if (currentComplaint == null) {
            return;
        }

        User currentUser = authViewModel.getCurrentUser();
        currentComplaint.setStatus(status);
        currentComplaint.setUpdatedAt(new Date());
        currentComplaint.setResolutionNote(comments);
        if ("resolved".equalsIgnoreCase(status)) {
            currentComplaint.setResolvedAt(new Date());
        } else {
            currentComplaint.setResolvedAt(null);
        }

        boolean complaintUpdated = complaintViewModel.updateComplaint(currentComplaint);
        boolean updateAdded = complaintViewModel.addUpdate(new Update(
                currentComplaint.getId(),
                currentUser != null ? currentUser.getUid() : "",
                status,
                comments
        ));

        if (complaintUpdated && updateAdded) {
            bindComplaint(currentComplaint);
            complaintViewModel.getUpdatesForComplaint(currentComplaint.getId());
            Toast.makeText(this, "Complaint status updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update complaint status", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyTicketId() {
        if (currentComplaint == null) {
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("ticket_id", currentComplaint.getId()));
        Toast.makeText(this, "Ticket ID copied", Toast.LENGTH_SHORT).show();
    }

    private void shareComplaint() {
        if (currentComplaint == null) {
            return;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                valueOrDefault(currentComplaint.getTitle(), "Complaint")
                        + "\nTicket ID: " + currentComplaint.getId()
                        + "\nStatus: " + formatLabel(currentComplaint.getStatus(), "Open"));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
    }

    private String shortTicketId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return "QFIX";
        }
        String cleaned = id.replace("-", "").toUpperCase(Locale.ROOT);
        return cleaned.length() > 8 ? cleaned.substring(0, 8) : cleaned;
    }

    private String formatLabel(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        String normalized = value.trim().replace('_', ' ');
        String[] words = normalized.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
            if (word.length() > 1) {
                builder.append(word.substring(1).toLowerCase(Locale.ROOT));
            }
        }
        return builder.toString();
    }

    private String formatDate(Date date) {
        return date != null ? DateUtils.formatDate(date) : "-";
    }

    private String valueOrDefault(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private int statusColor(String status) {
        if ("in_progress".equalsIgnoreCase(status)) {
            return R.color.status_in_progress;
        }
        if ("resolved".equalsIgnoreCase(status)) {
            return R.color.status_resolved;
        }
        if ("rejected".equalsIgnoreCase(status)) {
            return R.color.status_rejected;
        }
        return R.color.status_open;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
