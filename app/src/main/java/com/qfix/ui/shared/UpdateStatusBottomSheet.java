package com.qfix.ui.shared;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qfix.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class UpdateStatusBottomSheet extends BottomSheetDialogFragment {
    private static final String ARG_CURRENT_STATUS = "current_status";
    private static final String[] STATUS_OPTIONS = new String[]{"Open", "In Progress", "Resolved", "Rejected"};

    private OnStatusUpdateListener listener;

    public UpdateStatusBottomSheet() {
        // Required empty public constructor
    }

    public static UpdateStatusBottomSheet newInstance(String currentStatus) {
        UpdateStatusBottomSheet bottomSheet = new UpdateStatusBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT_STATUS, currentStatus);
        bottomSheet.setArguments(args);
        return bottomSheet;
    }

    public void setOnStatusUpdateListener(OnStatusUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bottom_sheet_update_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AutoCompleteTextView statusDropdown = view.findViewById(R.id.statusDropdown);
        TextInputEditText commentsEditText = view.findViewById(R.id.commentsEditText);
        View cancelButton = view.findViewById(R.id.cancelButton);
        View updateButton = view.findViewById(R.id.updateButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                STATUS_OPTIONS
        );
        statusDropdown.setAdapter(adapter);

        String currentStatus = getArguments() != null ? getArguments().getString(ARG_CURRENT_STATUS, "open") : "open";
        statusDropdown.setText(formatStatus(currentStatus), false);

        cancelButton.setOnClickListener(v -> dismiss());
        updateButton.setOnClickListener(v -> {
            String selectedStatus = statusDropdown.getText() != null
                    ? statusDropdown.getText().toString().trim()
                    : "";
            String comments = commentsEditText.getText() != null
                    ? commentsEditText.getText().toString().trim()
                    : "";

            if (selectedStatus.isEmpty()) {
                statusDropdown.setError(getString(R.string.error_field_required));
                return;
            }

            if (listener != null) {
                listener.onStatusUpdated(normalizeStatus(selectedStatus), comments);
            }
            dismiss();
        });
    }

    private String formatStatus(String value) {
        if ("in_progress".equalsIgnoreCase(value)) {
            return "In Progress";
        }
        if (value == null || value.trim().isEmpty()) {
            return "Open";
        }
        String normalized = value.replace('_', ' ').trim();
        return normalized.substring(0, 1).toUpperCase() + normalized.substring(1).toLowerCase();
    }

    private String normalizeStatus(String value) {
        if ("In Progress".equalsIgnoreCase(value)) {
            return "in_progress";
        }
        return value == null ? "open" : value.trim().toLowerCase().replace(' ', '_');
    }

    public interface OnStatusUpdateListener {
        void onStatusUpdated(String status, String comments);
    }
}
