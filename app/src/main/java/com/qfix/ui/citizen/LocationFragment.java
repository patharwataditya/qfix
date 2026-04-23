package com.qfix.ui.citizen;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.qfix.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LocationFragment extends Fragment {
    private TextInputLayout nearInputLayout;
    private TextInputLayout streetInputLayout;
    private TextInputLayout landmarkInputLayout;
    private TextInputLayout areaInputLayout;
    private TextInputLayout cityInputLayout;
    private TextInputLayout wardInputLayout;
    private TextInputLayout pincodeInputLayout;

    private ActivityResultLauncher<String[]> locationPermissionRequest;
    
    // Mock location for local implementation
    private double currentLatitude = 12.9716; // Bangalore coordinates as example
    private double currentLongitude = 77.5946;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        locationPermissionRequest = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                
                if (fineLocationGranted != null && fineLocationGranted) {
                    getCurrentLocation();
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        nearInputLayout = view.findViewById(R.id.nearInputLayout);
        streetInputLayout = view.findViewById(R.id.streetInputLayout);
        landmarkInputLayout = view.findViewById(R.id.landmarkInputLayout);
        areaInputLayout = view.findViewById(R.id.areaInputLayout);
        cityInputLayout = view.findViewById(R.id.cityInputLayout);
        wardInputLayout = view.findViewById(R.id.wardInputLayout);
        pincodeInputLayout = view.findViewById(R.id.pincodeInputLayout);

        view.findViewById(R.id.backButton).setOnClickListener(v -> {
            if (getParentFragment() instanceof ReportComplaintFragment) {
                ((ReportComplaintFragment) getParentFragment()).navigateToPrevious();
            }
        });

        view.findViewById(R.id.submitButton).setOnClickListener(v -> {
            if (validateInputs() && getParentFragment() instanceof ReportComplaintFragment) {
                ((ReportComplaintFragment) getParentFragment()).submitComplaint();
            }
        });
    }

    private void getCurrentLocation() {
        // For local implementation, we'll use mock coordinates
        // In a real implementation, you would use the device's location services
        // For now, we're just using predetermined coordinates
        Toast.makeText(getContext(), "Using mock location for demo", Toast.LENGTH_SHORT).show();
    }

    public String getLocationText() {
        StringBuilder locationBuilder = new StringBuilder();
        
        String near = nearInputLayout.getEditText().getText().toString().trim();
        String street = streetInputLayout.getEditText().getText().toString().trim();
        String landmark = landmarkInputLayout.getEditText().getText().toString().trim();
        String area = areaInputLayout.getEditText().getText().toString().trim();
        String city = cityInputLayout.getEditText().getText().toString().trim();
        String pincode = pincodeInputLayout.getEditText().getText().toString().trim();
        
        if (!near.isEmpty()) locationBuilder.append(near).append(", ");
        if (!street.isEmpty()) locationBuilder.append(street).append(", ");
        if (!landmark.isEmpty()) locationBuilder.append(landmark).append(", ");
        if (!area.isEmpty()) locationBuilder.append(area).append(", ");
        if (!city.isEmpty()) locationBuilder.append(city).append(" - ");
        if (!pincode.isEmpty()) locationBuilder.append(pincode);
        
        return locationBuilder.toString();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String near = getNear();
        String street = getStreet();
        String area = getArea();
        String city = getCity();
        String ward = getWard();
        String pincode = getPincode();

        if (near.isEmpty()) {
            nearInputLayout.setError("Location reference is required");
            isValid = false;
        } else {
            nearInputLayout.setError(null);
        }

        if (street.isEmpty()) {
            streetInputLayout.setError("Street is required");
            isValid = false;
        } else {
            streetInputLayout.setError(null);
        }

        if (area.isEmpty()) {
            areaInputLayout.setError("Area is required");
            isValid = false;
        } else {
            areaInputLayout.setError(null);
        }

        if (city.isEmpty()) {
            cityInputLayout.setError("City is required");
            isValid = false;
        } else {
            cityInputLayout.setError(null);
        }

        if (ward.isEmpty()) {
            wardInputLayout.setError("Ward is required");
            isValid = false;
        } else {
            wardInputLayout.setError(null);
        }

        if (pincode.isEmpty()) {
            pincodeInputLayout.setError("Pincode is required");
            isValid = false;
        } else {
            pincodeInputLayout.setError(null);
        }

        return isValid;
    }

    public String getNear() {
        return nearInputLayout.getEditText().getText().toString().trim();
    }

    public String getStreet() {
        return streetInputLayout.getEditText().getText().toString().trim();
    }

    public String getArea() {
        return areaInputLayout.getEditText().getText().toString().trim();
    }

    public String getCity() {
        return cityInputLayout.getEditText().getText().toString().trim();
    }

    public String getPincode() {
        return pincodeInputLayout.getEditText().getText().toString().trim();
    }

    public String getWard() {
        return wardInputLayout.getEditText().getText().toString().trim();
    }
}
