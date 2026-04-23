package com.qfix.ui.shared;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.qfix.R;
import com.qfix.data.model.Complaint;
import com.qfix.data.model.Feedback;
import com.qfix.data.model.Update;
import com.qfix.data.model.User;
import com.qfix.utils.DateUtils;
import com.qfix.viewmodel.AuthViewModel;
import com.qfix.viewmodel.ComplaintViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;
import java.util.ArrayList;
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
    private CardView feedbackCard;
    private TextView feedbackSummaryText;
    private TextView feedbackEmptyText;
    private LinearLayout feedbackListContainer;
    private Button escalateButton;
    private Button reportSimilarButton;
    private Button giveFeedbackButton;
    private ImageButton copyButton;
    private ImageButton shareButton;

    private Complaint currentComplaint;
    private String complaintId;
    private boolean isAuthorityUser;
    private List<Feedback> currentFeedbackList = new ArrayList<>();

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
        feedbackCard = findViewById(R.id.feedbackCard);
        feedbackSummaryText = findViewById(R.id.feedbackSummaryText);
        feedbackEmptyText = findViewById(R.id.feedbackEmptyText);
        feedbackListContainer = findViewById(R.id.feedbackListContainer);
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
            giveFeedbackButton.setOnClickListener(v -> openFeedbackDialog());
        }
    }

    private void observeData() {
        complaintViewModel.getComplaintLiveData().observe(this, complaint -> {
            currentComplaint = complaint;
            bindComplaint(complaint);
        });

        complaintViewModel.getUpdatesLiveData().observe(this, this::bindTimeline);
        complaintViewModel.getFeedbackLiveData().observe(this, this::bindFeedback);

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
        complaintViewModel.getFeedbackForComplaint(complaintId);
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
        bindFeedback(currentFeedbackList);
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

    private void bindFeedback(List<Feedback> feedbackList) {
        currentFeedbackList = feedbackList != null ? new ArrayList<>(feedbackList) : new ArrayList<>();
        if (feedbackListContainer == null || feedbackCard == null || currentComplaint == null) {
            return;
        }

        feedbackListContainer.removeAllViews();

        boolean resolved = "resolved".equalsIgnoreCase(currentComplaint.getStatus());
        Feedback currentUserFeedback = findCurrentUserFeedback(currentFeedbackList);
        boolean shouldShowCard = !currentFeedbackList.isEmpty() || (!isAuthorityUser && resolved);

        feedbackCard.setVisibility(shouldShowCard ? android.view.View.VISIBLE : android.view.View.GONE);
        if (!shouldShowCard) {
            return;
        }

        if (currentFeedbackList.isEmpty()) {
            feedbackSummaryText.setText(getString(R.string.no_feedback_yet));
            feedbackEmptyText.setVisibility(android.view.View.VISIBLE);
            feedbackEmptyText.setText(getString(R.string.share_your_feedback_prompt));
            return;
        }

        float totalRating = 0f;
        for (Feedback feedback : currentFeedbackList) {
            totalRating += feedback.getRating();
        }
        float average = totalRating / currentFeedbackList.size();
        feedbackSummaryText.setText(getString(R.string.average_rating_format, average));
        feedbackEmptyText.setVisibility(android.view.View.VISIBLE);
        feedbackEmptyText.setText(getString(R.string.feedback_count_format, currentFeedbackList.size()));

        if (currentUserFeedback != null && !isAuthorityUser) {
            giveFeedbackButton.setVisibility(android.view.View.GONE);
        }

        for (Feedback feedback : currentFeedbackList) {
            feedbackListContainer.addView(createFeedbackItemView(feedback, feedback == currentUserFeedback));
        }
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

    private View createFeedbackItemView(Feedback feedback, boolean isCurrentUserFeedback) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(0, 0, 0, 24);

        TextView title = new TextView(this);
        title.setText(isCurrentUserFeedback ? getString(R.string.your_feedback) : getString(R.string.citizen_feedback));
        title.setTextColor(getResources().getColor(R.color.text_primary, null));
        title.setTextSize(15);
        item.addView(title);

        TextView rating = new TextView(this);
        rating.setText("Rating: " + feedback.getRating() + "/5");
        rating.setTextColor(getResources().getColor(R.color.text_secondary, null));
        item.addView(rating);

        if (feedback.getComment() != null && !feedback.getComment().trim().isEmpty()) {
            TextView comment = new TextView(this);
            comment.setText(feedback.getComment().trim());
            comment.setTextColor(getResources().getColor(R.color.text_primary, null));
            comment.setPadding(0, 8, 0, 0);
            item.addView(comment);
        }

        if (feedback.getCreatedAt() != null) {
            TextView createdAt = new TextView(this);
            createdAt.setText(DateUtils.formatDateTime(feedback.getCreatedAt()));
            createdAt.setTextColor(getResources().getColor(R.color.text_secondary, null));
            createdAt.setPadding(0, 8, 0, 0);
            item.addView(createdAt);
        }

        return item;
    }

    private void openUpdateStatusSheet() {
        if (!isAuthorityUser || currentComplaint == null) {
            return;
        }

        UpdateStatusBottomSheet bottomSheet = UpdateStatusBottomSheet.newInstance(currentComplaint.getStatus());
        bottomSheet.setOnStatusUpdateListener(this::applyStatusUpdate);
        bottomSheet.show(getSupportFragmentManager(), "update_status");
    }

    private void openFeedbackDialog() {
        if (currentComplaint == null || !"resolved".equalsIgnoreCase(currentComplaint.getStatus())) {
            return;
        }
        if (findCurrentUserFeedback(currentFeedbackList) != null) {
            Toast.makeText(this, getString(R.string.feedback_already_submitted), Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, 0);

        RatingBar ratingBar = new RatingBar(this, null, android.R.attr.ratingBarStyle);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1f);
        container.addView(ratingBar);

        EditText commentInput = new EditText(this);
        commentInput.setHint(getString(R.string.comments));
        commentInput.setMinLines(3);
        commentInput.setMaxLines(5);
        container.addView(commentInput);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.provide_feedback)
                .setView(container)
                .setPositiveButton(R.string.submit_feedback, (dialog, which) -> {
                    int rating = Math.round(ratingBar.getRating());
                    if (rating <= 0) {
                        Toast.makeText(this, getString(R.string.feedback_requires_rating), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    User currentUser = authViewModel.getCurrentUser();
                    if (currentUser == null) {
                        Toast.makeText(this, "Please sign in again", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Feedback feedback = new Feedback(
                            currentComplaint.getId(),
                            currentUser.getUid(),
                            rating,
                            commentInput.getText() != null ? commentInput.getText().toString().trim() : ""
                    );

                    if (complaintViewModel.addFeedback(feedback)) {
                        complaintViewModel.getFeedbackForComplaint(currentComplaint.getId());
                        Toast.makeText(this, getString(R.string.feedback_submitted_successfully), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to submit feedback", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
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
            complaintViewModel.getFeedbackForComplaint(currentComplaint.getId());
            Toast.makeText(this, "Complaint status updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update complaint status", Toast.LENGTH_SHORT).show();
        }
    }

    private Feedback findCurrentUserFeedback(List<Feedback> feedbackList) {
        User currentUser = authViewModel.getCurrentUser();
        if (currentUser == null || feedbackList == null) {
            return null;
        }
        for (Feedback feedback : feedbackList) {
            if (feedback != null && currentUser.getUid().equals(feedback.getCitizenId())) {
                return feedback;
            }
        }
        return null;
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
