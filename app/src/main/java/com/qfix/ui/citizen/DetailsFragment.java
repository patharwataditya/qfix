package com.qfix.ui.citizen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.fragment.app.Fragment;

import com.qfix.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.LinkedHashMap;
import java.util.Map;

public class DetailsFragment extends Fragment {
    private GridLayout categoryGrid;
    private TextInputLayout titleInputLayout;
    private TextInputLayout descriptionInputLayout;
    private com.google.android.material.button.MaterialButtonToggleGroup priorityToggleGroup;
    private com.google.android.material.switchmaterial.SwitchMaterial publicSwitch;

    // Category mapping
    private Map<String, String> categoryMap;
    private String selectedCategory = "";

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeCategories();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        initViews(view);
        setupCategories();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        categoryGrid = view.findViewById(R.id.categoryGrid);
        titleInputLayout = view.findViewById(R.id.titleInputLayout);
        descriptionInputLayout = view.findViewById(R.id.descriptionInputLayout);
        priorityToggleGroup = view.findViewById(R.id.priorityToggleGroup);
        publicSwitch = view.findViewById(R.id.publicSwitch);

        view.findViewById(R.id.backButton).setOnClickListener(v -> {
            if (getParentFragment() instanceof ReportComplaintFragment) {
                ((ReportComplaintFragment) getParentFragment()).navigateToPrevious();
            }
        });

        view.findViewById(R.id.nextButton).setOnClickListener(v -> {
            if (validateInputs() && getParentFragment() instanceof ReportComplaintFragment) {
                ((ReportComplaintFragment) getParentFragment()).navigateToNext();
            }
        });
    }

    private void initializeCategories() {
        categoryMap = new LinkedHashMap<>();
        categoryMap.put("pothole", "Pothole");
        categoryMap.put("drainage", "Drainage");
        categoryMap.put("garbage", "Garbage Collection");
        categoryMap.put("street_light", "Street Light");
        categoryMap.put("water_leakage", "Water Leakage");
        categoryMap.put("electric_pole", "Electric Pole");
        categoryMap.put("fire_hazard", "Fire Hazard");
        categoryMap.put("public_park", "Public Park");
        categoryMap.put("traffic_signal", "Traffic Signal");
        categoryMap.put("other", "Other");
    }

    private void setupCategories() {
        categoryGrid.removeAllViews();

        int index = 0;
        for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
            MaterialCardView categoryCard = (MaterialCardView) LayoutInflater.from(getContext())
                    .inflate(R.layout.category_item, categoryGrid, false);

            MaterialButton categoryButton = categoryCard.findViewById(R.id.categoryButton);
            categoryButton.setText(entry.getValue());
            categoryButton.setTag(entry.getKey());

            categoryButton.setOnClickListener(v -> {
                selectedCategory = (String) v.getTag();
                updateCategorySelection();
            });

            // Add to grid
            categoryGrid.addView(categoryCard);

            index++;
        }
    }

    private void updateCategorySelection() {
        for (int i = 0; i < categoryGrid.getChildCount(); i++) {
            MaterialCardView cardView = (MaterialCardView) categoryGrid.getChildAt(i);
            MaterialButton button = cardView.findViewById(R.id.categoryButton);
            
            if (button.getTag().equals(selectedCategory)) {
                cardView.setStrokeColor(getResources().getColor(R.color.electric_blue, null));
                cardView.setStrokeWidth(2);
            } else {
                cardView.setStrokeColor(getResources().getColor(android.R.color.transparent, null));
                cardView.setStrokeWidth(0);
            }
        }
    }

    private void setupClickListeners() {
        // The toggle group is already set up in XML with single selection
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate category
        if (selectedCategory.isEmpty()) {
            // In a real app, we would show an error
            isValid = false;
        }

        // Validate title
        String title = ((TextInputEditText) titleInputLayout.getEditText()).getText().toString().trim();
        if (title.isEmpty()) {
            titleInputLayout.setError("Title is required");
            isValid = false;
        } else if (title.length() < 10) {
            titleInputLayout.setError("Title must be at least 10 characters");
            isValid = false;
        } else {
            titleInputLayout.setError(null);
        }

        // Validate description
        String description = ((TextInputEditText) descriptionInputLayout.getEditText()).getText().toString().trim();
        if (description.isEmpty()) {
            descriptionInputLayout.setError("Description is required");
            isValid = false;
        } else if (description.length() < 20) {
            descriptionInputLayout.setError("Description must be at least 20 characters");
            isValid = false;
        } else {
            descriptionInputLayout.setError(null);
        }

        return isValid;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public String getTitle() {
        TextInputEditText editText = (TextInputEditText) titleInputLayout.getEditText();
        return editText != null ? editText.getText().toString().trim() : "";
    }

    public String getDescription() {
        TextInputEditText editText = (TextInputEditText) descriptionInputLayout.getEditText();
        return editText != null ? editText.getText().toString().trim() : "";
    }

    public String getPriority() {
        int selectedId = priorityToggleGroup.getCheckedButtonId();
        if (selectedId == R.id.lowButton) return "low";
        if (selectedId == R.id.mediumButton) return "medium";
        if (selectedId == R.id.highButton) return "high";
        if (selectedId == R.id.criticalButton) return "critical";
        return "medium"; // Default
    }

    public boolean isPublic() {
        return publicSwitch.isChecked();
    }
}
