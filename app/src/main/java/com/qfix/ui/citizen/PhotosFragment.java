package com.qfix.ui.citizen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.qfix.R;
import com.qfix.utils.ImageUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class PhotosFragment extends Fragment {
    private static final int MAX_PHOTOS = 5;
    private GridLayout photosGrid;
    private List<Uri> photoUris;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    private Uri pendingCameraPhotoUri;

    public PhotosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoUris = new ArrayList<>();
        
        // Initialize activity result launchers
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && pendingCameraPhotoUri != null) {
                        addPhoto(pendingCameraPhotoUri);
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        if (selectedImage != null) {
                            addPhoto(selectedImage);
                        }
                    }
                });

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(getContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        initViews(view);
        setupInitialGrid();

        return view;
    }

    private void initViews(View view) {
        photosGrid = view.findViewById(R.id.photosGrid);
        view.findViewById(R.id.nextButton).setOnClickListener(v -> {
            if (getParentFragment() instanceof ReportComplaintFragment) {
                ((ReportComplaintFragment) getParentFragment()).navigateToNext();
            }
        });
    }

    private void setupInitialGrid() {
        photosGrid.removeAllViews();
        
        // Add photo cells
        for (int i = 0; i < MAX_PHOTOS; i++) {
            if (i < photoUris.size()) {
                addPhotoCell(photoUris.get(i));
            } else {
                addEmptyPhotoCell();
            }
        }
    }

    private void addEmptyPhotoCell() {
        MaterialCardView cardView = (MaterialCardView) LayoutInflater.from(getContext())
                .inflate(R.layout.photo_cell_empty, photosGrid, false);
        
        ImageButton addButton = cardView.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> showImageSourceDialog());
        
        photosGrid.addView(cardView);
    }

    private void addPhotoCell(Uri uri) {
        MaterialCardView cardView = (MaterialCardView) LayoutInflater.from(getContext())
                .inflate(R.layout.photo_cell_filled, photosGrid, false);
        
        // Load image using Glide or similar library
        // For now, we'll use a placeholder
        cardView.findViewById(R.id.photoImage);
        
        ImageButton removeButton = cardView.findViewById(R.id.removeButton);
        removeButton.setOnClickListener(v -> removePhoto(uri));
        
        photosGrid.addView(cardView);
    }

    private void showImageSourceDialog() {
        if (getContext() == null) return;
        
        // Create dialog for choosing image source
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Choose Image Source")
                .setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermission();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) 
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                openCamera();
            }
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                java.io.File photoFile = ImageUtils.createImageFile(requireContext());
                pendingCameraPhotoUri = ImageUtils.getFileUri(requireContext(), photoFile);
            } catch (java.io.IOException e) {
                Toast.makeText(getContext(), "Unable to create image file", Toast.LENGTH_SHORT).show();
                pendingCameraPhotoUri = null;
            }

            if (pendingCameraPhotoUri != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, pendingCameraPhotoUri);
                cameraLauncher.launch(cameraIntent);
            }
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private void addPhoto(Uri uri) {
        if (photoUris.size() < MAX_PHOTOS) {
            photoUris.add(uri);
            setupInitialGrid();
        }
    }

    private void removePhoto(Uri uri) {
        photoUris.remove(uri);
        setupInitialGrid();
    }

    public List<Uri> getPhotoUris() {
        return new ArrayList<>(photoUris);
    }
}
