package com.qfix.ui.authority;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.qfix.R;
import com.qfix.data.model.Complaint;
import com.qfix.data.model.User;
import com.qfix.utils.DateUtils;
import com.qfix.viewmodel.AuthViewModel;
import com.qfix.viewmodel.ComplaintViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AuthorityDashboardFragment extends Fragment {
    private static final String ARG_STATUS_FILTER = "status_filter";

    private ComplaintViewModel complaintViewModel;
    private AuthViewModel authViewModel;
    
    private TextView openCountText;
    private TextView inProgressCountText;
    private TextView resolvedCountText;
    private TextView avgRatingText;
    private LinearLayout priorityQueueContainer;
    private LinearLayout recentActivityContainer;
    private BarChart weeklyActivityChart;
    private String statusFilter;

    public AuthorityDashboardFragment() {
        // Required empty public constructor
    }

    public static AuthorityDashboardFragment newInstance(String statusFilter) {
        AuthorityDashboardFragment fragment = new AuthorityDashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS_FILTER, statusFilter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        complaintViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
                .create(ComplaintViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        if (getArguments() != null) {
            statusFilter = getArguments().getString(ARG_STATUS_FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authority_dashboard, container, false);

        initViews(view);
        setupChart();
        observeViewModels();
        loadData();

        return view;
    }

    private void initViews(View view) {
        openCountText = view.findViewById(R.id.openCount);
        inProgressCountText = view.findViewById(R.id.inProgressCount);
        resolvedCountText = view.findViewById(R.id.resolvedCount);
        avgRatingText = view.findViewById(R.id.avgRatingText);
        priorityQueueContainer = view.findViewById(R.id.priorityQueueContainer);
        recentActivityContainer = view.findViewById(R.id.recentActivityContainer);
        weeklyActivityChart = view.findViewById(R.id.weeklyActivityChart);
    }

    private void setupChart() {
        weeklyActivityChart.getDescription().setEnabled(false);
        weeklyActivityChart.setDrawValueAboveBar(true);
        weeklyActivityChart.setDrawGridBackground(false);
        weeklyActivityChart.getAxisLeft().setDrawGridLines(false);
        weeklyActivityChart.getAxisRight().setDrawGridLines(false);
        weeklyActivityChart.getXAxis().setDrawGridLines(false);
        weeklyActivityChart.getAxisRight().setEnabled(false);

        XAxis xAxis = weeklyActivityChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void observeViewModels() {
        complaintViewModel.getComplaintsLiveData().observe(getViewLifecycleOwner(), complaints -> {
            if (complaints != null) {
                updateStatistics(complaints);
            } else {
                updateStatistics(Collections.emptyList());
            }
        });

        complaintViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadData() {
        User currentUser = authViewModel.getCurrentUser();
        complaintViewModel.getComplaintsForAuthority(currentUser);
    }

    private void updateStatistics(List<Complaint> complaints) {
        List<Complaint> filteredComplaints = filterComplaintsByStatus(complaints);
        int openCount = 0;
        int inProgressCount = 0;
        int resolvedCount = 0;
        int criticalCount = 0;
        
        for (Complaint complaint : filteredComplaints) {
            String status = complaint.getStatus();
            if (status == null) {
                status = "open";
            }
            switch (status) {
                case "open":
                    openCount++;
                    break;
                case "in_progress":
                    inProgressCount++;
                    break;
                case "resolved":
                    resolvedCount++;
                    break;
            }

            if ("critical".equalsIgnoreCase(complaint.getPriority())) {
                criticalCount++;
            }
        }
        
        openCountText.setText(String.valueOf(openCount));
        inProgressCountText.setText(String.valueOf(inProgressCount));
        resolvedCountText.setText(String.valueOf(resolvedCount));

        avgRatingText.setText(String.valueOf(filteredComplaints.size()));
        setupChartData(filteredComplaints);
        populatePriorityQueue(filteredComplaints);
        populateRecentActivity(filteredComplaints);
    }

    private List<Complaint> filterComplaintsByStatus(List<Complaint> complaints) {
        if (statusFilter == null || statusFilter.trim().isEmpty()) {
            return new ArrayList<>(complaints);
        }

        ArrayList<Complaint> filtered = new ArrayList<>();
        for (Complaint complaint : complaints) {
            if (statusFilter.equalsIgnoreCase(complaint.getStatus())) {
                filtered.add(complaint);
            }
        }
        return filtered;
    }

    private void setupChartData(List<Complaint> complaints) {
        ArrayList<BarEntry> resolvedEntries = new ArrayList<>();
        ArrayList<BarEntry> receivedEntries = new ArrayList<>();
        String[] days = new String[7];

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        for (int i = 6; i >= 0; i--) {
            Calendar dayStart = (Calendar) today.clone();
            dayStart.add(Calendar.DAY_OF_YEAR, -i);

            Calendar dayEnd = (Calendar) dayStart.clone();
            dayEnd.add(Calendar.DAY_OF_YEAR, 1);

            int index = 6 - i;
            days[index] = dayStart.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());

            int createdCount = 0;
            int resolvedCount = 0;
            for (Complaint complaint : complaints) {
                Date createdAt = complaint.getCreatedAt();
                if (createdAt != null &&
                        !createdAt.before(dayStart.getTime()) &&
                        createdAt.before(dayEnd.getTime())) {
                    createdCount++;
                }

                Date resolvedAt = complaint.getResolvedAt();
                if (resolvedAt != null &&
                        !resolvedAt.before(dayStart.getTime()) &&
                        resolvedAt.before(dayEnd.getTime())) {
                    resolvedCount++;
                }
            }

            receivedEntries.add(new BarEntry(index, createdCount));
            resolvedEntries.add(new BarEntry(index, resolvedCount));
        }

        BarDataSet resolvedSet = new BarDataSet(resolvedEntries, "Resolved");
        resolvedSet.setColor(Color.parseColor("#2E7D32"));

        BarDataSet receivedSet = new BarDataSet(receivedEntries, "Received");
        receivedSet.setColor(Color.parseColor("#1565C0"));

        weeklyActivityChart.setData(new BarData(resolvedSet, receivedSet));
        weeklyActivityChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(days));
        weeklyActivityChart.invalidate();
    }

    private void populatePriorityQueue(List<Complaint> complaints) {
        priorityQueueContainer.removeAllViews();

        List<Complaint> prioritizedComplaints = new ArrayList<>(complaints);
        prioritizedComplaints.sort((left, right) -> {
            int priorityComparison = Integer.compare(priorityRank(right.getPriority()), priorityRank(left.getPriority()));
            if (priorityComparison != 0) {
                return priorityComparison;
            }
            Date leftDate = left.getCreatedAt();
            Date rightDate = right.getCreatedAt();
            if (leftDate == null && rightDate == null) return 0;
            if (leftDate == null) return 1;
            if (rightDate == null) return -1;
            return rightDate.compareTo(leftDate);
        });

        int count = 0;
        for (Complaint complaint : prioritizedComplaints) {
            if ("resolved".equalsIgnoreCase(complaint.getStatus()) || "rejected".equalsIgnoreCase(complaint.getStatus())) {
                continue;
            }
            View item = LayoutInflater.from(getContext()).inflate(R.layout.priority_queue_item, priorityQueueContainer, false);
            ((TextView) item.findViewById(R.id.priorityQueueTitle)).setText(valueOrDefault(complaint.getTitle(), "Untitled complaint"));
            ((TextView) item.findViewById(R.id.priorityQueueSubtitle)).setText(
                    valueOrDefault(complaint.getLocationText(), "Location unavailable") + " | " + valueOrDefault(complaint.getWard(), "-")
            );
            TextView badge = item.findViewById(R.id.priorityQueueBadge);
            String priority = valueOrDefault(complaint.getPriority(), "medium");
            badge.setText(capitalize(priority));
            badge.setBackgroundResource(priorityBadge(priority));
            ImageView icon = item.findViewById(R.id.priorityQueueIcon);
            icon.setImageResource(priorityIcon(priority));
            priorityQueueContainer.addView(item);
            count++;
            if (count == 3) {
                break;
            }
        }

        if (count == 0) {
            addEmptyState(priorityQueueContainer, "No active complaints need attention");
        }
    }

    private void populateRecentActivity(List<Complaint> complaints) {
        recentActivityContainer.removeAllViews();

        List<Complaint> recentComplaints = new ArrayList<>(complaints);
        recentComplaints.sort((left, right) -> {
            Date leftDate = left.getUpdatedAt() != null ? left.getUpdatedAt() : left.getCreatedAt();
            Date rightDate = right.getUpdatedAt() != null ? right.getUpdatedAt() : right.getCreatedAt();
            if (leftDate == null && rightDate == null) return 0;
            if (leftDate == null) return 1;
            if (rightDate == null) return -1;
            return rightDate.compareTo(leftDate);
        });

        int count = 0;
        for (Complaint complaint : recentComplaints) {
            View item = LayoutInflater.from(getContext()).inflate(R.layout.recent_activity_item, recentActivityContainer, false);
            ((TextView) item.findViewById(R.id.recentActivityTitle)).setText(activityTitle(complaint));
            ((TextView) item.findViewById(R.id.recentActivitySubtitle)).setText(valueOrDefault(complaint.getTitle(), "Untitled complaint"));
            Date activityDate = complaint.getUpdatedAt() != null ? complaint.getUpdatedAt() : complaint.getCreatedAt();
            ((TextView) item.findViewById(R.id.recentActivityTime)).setText(
                    activityDate != null ? DateUtils.getTimeAgo(activityDate, new Date()) : "Just now"
            );
            ImageView icon = item.findViewById(R.id.recentActivityIcon);
            icon.setImageResource(activityIcon(complaint.getStatus()));
            recentActivityContainer.addView(item);
            count++;
            if (count == 3) {
                break;
            }
        }

        if (count == 0) {
            addEmptyState(recentActivityContainer, "No recent activity yet");
        }
    }

    private void addEmptyState(LinearLayout container, String text) {
        TextView emptyView = new TextView(getContext());
        emptyView.setText(text);
        emptyView.setTextColor(getResources().getColor(R.color.text_secondary, null));
        emptyView.setPadding(0, 8, 0, 8);
        container.addView(emptyView);
    }

    private int priorityRank(String priority) {
        if ("critical".equalsIgnoreCase(priority)) return 4;
        if ("high".equalsIgnoreCase(priority)) return 3;
        if ("medium".equalsIgnoreCase(priority)) return 2;
        if ("low".equalsIgnoreCase(priority)) return 1;
        return 0;
    }

    private int priorityBadge(String priority) {
        if ("critical".equalsIgnoreCase(priority)) return R.drawable.priority_critical_background;
        if ("high".equalsIgnoreCase(priority)) return R.drawable.priority_high_background;
        if ("low".equalsIgnoreCase(priority)) return R.drawable.priority_low_background;
        return R.drawable.priority_medium_background;
    }

    private int priorityIcon(String priority) {
        if ("critical".equalsIgnoreCase(priority) || "high".equalsIgnoreCase(priority)) {
            return R.drawable.ic_warning;
        }
        return R.drawable.ic_info;
    }

    private String activityTitle(Complaint complaint) {
        String status = complaint.getStatus();
        if ("resolved".equalsIgnoreCase(status)) {
            return "Complaint resolved";
        }
        if ("in_progress".equalsIgnoreCase(status)) {
            return "Complaint in progress";
        }
        if ("rejected".equalsIgnoreCase(status)) {
            return "Complaint rejected";
        }
        return "Complaint reported";
    }

    private int activityIcon(String status) {
        if ("resolved".equalsIgnoreCase(status)) return R.drawable.ic_check_circle;
        if ("in_progress".equalsIgnoreCase(status)) return R.drawable.ic_in_progress;
        if ("rejected".equalsIgnoreCase(status)) return R.drawable.ic_rejected;
        return R.drawable.ic_pending;
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.substring(0, 1).toUpperCase(Locale.getDefault()) + value.substring(1).toLowerCase(Locale.getDefault());
    }

    private String valueOrDefault(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }
}
