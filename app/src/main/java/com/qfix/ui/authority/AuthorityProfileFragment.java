package com.qfix.ui.authority;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.qfix.R;
import com.qfix.data.model.Complaint;
import com.qfix.data.model.User;
import com.qfix.ui.auth.LoginActivity;
import com.qfix.utils.ThemeHelper;
import com.qfix.viewmodel.AuthViewModel;
import com.qfix.viewmodel.ComplaintViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Locale;

public class AuthorityProfileFragment extends Fragment {
    private AuthViewModel authViewModel;
    private ComplaintViewModel complaintViewModel;
    private SwitchMaterial darkModeSwitch;
    private TextView nameText;
    private TextView departmentText;
    private TextView complaintsCountText;
    private TextView resolvedCountText;
    private TextView pendingCountText;
    private static final String PREFS_NAME = "qfix_prefs";
    private static final String KEY_LANGUAGE = "language_code";

    public AuthorityProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get ViewModel from parent activity
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        complaintViewModel = new ViewModelProvider(requireActivity()).get(ComplaintViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authority_profile, container, false);

        initViews(view);
        setupClickListeners(view);

        return view;
    }

    private void initViews(View view) {
        darkModeSwitch = view.findViewById(R.id.darkModeSwitch);
        nameText = view.findViewById(R.id.nameText);
        departmentText = view.findViewById(R.id.departmentText);
        complaintsCountText = view.findViewById(R.id.complaintsCount);
        resolvedCountText = view.findViewById(R.id.resolvedCount);
        pendingCountText = view.findViewById(R.id.pendingCount);

        // Set initial state of dark mode switch
        int currentTheme = ThemeHelper.getTheme(requireContext());
        darkModeSwitch.setChecked(currentTheme == ThemeHelper.THEME_DARK ||
                (currentTheme == ThemeHelper.THEME_SYSTEM &&
                 AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES));
    }

    private void setupClickListeners(View view) {
        if (darkModeSwitch != null) {
            darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    ThemeHelper.setTheme(requireContext(), ThemeHelper.THEME_DARK);
                } else {
                    ThemeHelper.setTheme(requireContext(), ThemeHelper.THEME_LIGHT);
                }

                // Apply theme changes immediately
                requireActivity().recreate();
            });
        }

        // Set up other button click listeners
        View editProfileButton = view.findViewById(R.id.editProfileButton);
        if (editProfileButton != null) {
            editProfileButton.setOnClickListener(v -> showEditProfileDialog());
        }

        View changePasswordButton = view.findViewById(R.id.changePasswordButton);
        if (changePasswordButton != null) {
            changePasswordButton.setOnClickListener(v -> {
                // TODO: Open change password screen
                Toast.makeText(requireContext(), "Change password clicked", Toast.LENGTH_SHORT).show();
            });
        }

        View languageButton = view.findViewById(R.id.languageButton);
        if (languageButton != null) {
            languageButton.setOnClickListener(v -> {
                showLanguageSelectionDialog();
            });
        }

        View logoutButton = view.findViewById(R.id.logoutButton);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> {
                // Handle logout
                handleLogout();
            });
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel.getUserLiveData().observe(getViewLifecycleOwner(), this::bindUser);
        complaintViewModel.getComplaintsLiveData().observe(getViewLifecycleOwner(), this::bindComplaintStats);
        bindUser(authViewModel.getCurrentUser());
        loadComplaintStats(authViewModel.getCurrentUser());
    }

    private void bindUser(User user) {
        if (user == null) {
            return;
        }
        nameText.setText(defaultValue(user.getName(), "Authority Name"));
        String department = defaultValue(user.getDepartment(), "Department");
        String workArea = defaultValue(
                user.getWorkArea() != null && !user.getWorkArea().trim().isEmpty() ? user.getWorkArea() : user.getWard(),
                ""
        );
        departmentText.setText(workArea.isEmpty() ? department : department + " | " + workArea);
        loadComplaintStats(user);
    }

    private void loadComplaintStats(User user) {
        if (user == null) {
            bindComplaintStats(null);
            return;
        }
        complaintViewModel.getComplaintsForAuthority(user);
    }

    private void bindComplaintStats(List<Complaint> complaints) {
        int total = 0;
        int resolved = 0;
        int pending = 0;

        if (complaints != null) {
            total = complaints.size();
            for (Complaint complaint : complaints) {
                String status = complaint.getStatus();
                if ("resolved".equalsIgnoreCase(status)) {
                    resolved++;
                } else if (!"rejected".equalsIgnoreCase(status)) {
                    pending++;
                }
            }
        }

        complaintsCountText.setText(String.valueOf(total));
        resolvedCountText.setText(String.valueOf(resolved));
        pendingCountText.setText(String.valueOf(pending));
    }

    private void showEditProfileDialog() {
        User currentUser = authViewModel.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User details are not available", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, 0);

        EditText nameInput = new EditText(requireContext());
        nameInput.setHint("Full name");
        nameInput.setText(defaultValue(currentUser.getName(), ""));
        container.addView(nameInput);

        EditText departmentInput = new EditText(requireContext());
        departmentInput.setHint("Department");
        departmentInput.setText(defaultValue(currentUser.getDepartment(), ""));
        container.addView(departmentInput);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Profile")
                .setView(container)
                .setPositiveButton("Save", (dialog, which) -> {
                    String updatedName = nameInput.getText().toString().trim();
                    String updatedDepartment = departmentInput.getText().toString().trim();

                    if (updatedName.isEmpty() || updatedDepartment.isEmpty()) {
                        Toast.makeText(requireContext(), "Name and department are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    currentUser.setName(updatedName);
                    currentUser.setDepartment(updatedDepartment);

                    if (authViewModel.saveUser(currentUser)) {
                        bindUser(currentUser);
                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLanguageSelectionDialog() {
        String[] languages = {"English", "हिंदी"};
        int currentLanguageIndex = getCurrentLanguageIndex();

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Language")
                .setSingleChoiceItems(languages, currentLanguageIndex, (dialog, which) -> {
                    setLocale(which == 0 ? "en" : "hi");
                    dialog.dismiss();
                    // Restart the activity to apply language changes
                    restartActivity();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int getCurrentLanguageIndex() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedLanguage = prefs.getString(KEY_LANGUAGE, Locale.getDefault().getLanguage());
        return savedLanguage.equals("hi") ? 1 : 0;
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, dm);

        // Save the selected language preference
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }

    private void restartActivity() {
        Intent intent = requireActivity().getIntent();
        requireActivity().finish();
        startActivity(intent);
    }

    private void handleLogout() {
        if (authViewModel != null) {
            authViewModel.signOut();
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }
}
