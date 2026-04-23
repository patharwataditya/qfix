package com.qfix.ui.authority;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qfix.R;
import com.qfix.data.model.Complaint;
import com.qfix.utils.DateUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.Locale;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ComplaintViewHolder> {
    private List<Complaint> complaints;
    private OnComplaintClickListener listener;
    private int lastPosition = -1;

    public interface OnComplaintClickListener {
        void onComplaintClick(Complaint complaint);
    }

    public ComplaintsAdapter(List<Complaint> complaints, OnComplaintClickListener listener) {
        this.complaints = complaints;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.complaint_item, parent, false);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        Complaint complaint = complaints.get(position);
        holder.bind(complaint);
        
        // Add animation to the item
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return complaints.size();
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.slide_in_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    class ComplaintViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private View statusStrip;
        private TextView titleText;
        private TextView categoryText;
        private TextView descriptionText;
        private TextView locationText;
        private TextView dateText;
        private TextView priorityText;
        private Chip statusChip;
        private TextView wardText;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            statusStrip = itemView.findViewById(R.id.statusStrip);
            titleText = itemView.findViewById(R.id.titleText);
            categoryText = itemView.findViewById(R.id.categoryText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            locationText = itemView.findViewById(R.id.locationText);
            dateText = itemView.findViewById(R.id.dateText);
            priorityText = itemView.findViewById(R.id.priorityText);
            statusChip = itemView.findViewById(R.id.statusChip);
            wardText = itemView.findViewById(R.id.wardText);

            itemView.setOnClickListener(v -> {
                // Add scale animation on click
                Animation scaleUp = AnimationUtils.loadAnimation(v.getContext(), R.anim.scale_up);
                v.startAnimation(scaleUp);
                
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onComplaintClick(complaints.get(position));
                }
            });
        }

        public void bind(Complaint complaint) {
            String title = fallback(complaint.getTitle(), "Untitled complaint");
            String category = formatCategoryLabel(fallback(complaint.getCategory(), "Uncategorized"));
            String description = fallback(complaint.getDescription(), "No description provided");
            String location = fallback(complaint.getLocationText(), "Location not provided");
            String priority = fallback(complaint.getPriority(), "medium");
            String status = fallback(complaint.getStatus(), "open");
            String ward = fallback(complaint.getWard(), "-");

            titleText.setText(title);
            categoryText.setText(category);
            descriptionText.setText(description);
            locationText.setText(location);
            dateText.setText(DateUtils.formatDate(complaint.getCreatedAt()));
            priorityText.setText(capitalizeFirstLetter(priority));
            statusChip.setText(capitalizeFirstLetter(status.replace("_", " ")));
            wardText.setText("Ward " + ward);

            // Set status strip color
            int statusColorRes = getStatusColor(status);
            statusStrip.setBackgroundResource(statusColorRes);

            // Set priority text color
            int priorityColorRes = getPriorityColor(priority);
            priorityText.setBackgroundResource(priorityColorRes);
        }

        private String fallback(String value, String defaultValue) {
            return value == null || value.trim().isEmpty() ? defaultValue : value;
        }

        private String capitalizeFirstLetter(String text) {
            if (text == null || text.isEmpty()) {
                return text;
            }
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }

        private String formatCategoryLabel(String category) {
            if (category == null || category.trim().isEmpty()) {
                return "Uncategorized";
            }

            String normalized = category.trim().replace('_', ' ');
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

        private int getStatusColor(String status) {
            switch (status) {
                case "open":
                    return R.color.status_open;
                case "in_progress":
                    return R.color.status_in_progress;
                case "resolved":
                    return R.color.status_resolved;
                case "rejected":
                    return R.color.status_rejected;
                default:
                    return R.color.status_open;
            }
        }

        private int getPriorityColor(String priority) {
            switch (priority) {
                case "low":
                    return R.drawable.priority_low_background;
                case "medium":
                    return R.drawable.priority_medium_background;
                case "high":
                    return R.drawable.priority_high_background;
                case "critical":
                    return R.drawable.priority_critical_background;
                default:
                    return R.drawable.priority_chip_background;
            }
        }
    }
}
