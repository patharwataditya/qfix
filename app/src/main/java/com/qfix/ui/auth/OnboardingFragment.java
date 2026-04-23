package com.qfix.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.qfix.R;
import com.airbnb.lottie.LottieAnimationView;

public class OnboardingFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_ANIMATION = "animation";
    private static final String ARG_COLOR = "color";

    private String title;
    private String description;
    private int animation;
    private int color;

    public OnboardingFragment() {
        // Required empty public constructor
    }

    public static OnboardingFragment newInstance(String title, String description, int animation, int color) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        args.putInt(ARG_ANIMATION, animation);
        args.putInt(ARG_COLOR, color);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            description = getArguments().getString(ARG_DESCRIPTION);
            animation = getArguments().getInt(ARG_ANIMATION);
            color = getArguments().getInt(ARG_COLOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.onboarding_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View topBackground = view.findViewById(R.id.topBackground);
        LottieAnimationView animationView = view.findViewById(R.id.animationView);
        TextView titleText = view.findViewById(R.id.titleText);
        TextView descriptionText = view.findViewById(R.id.descriptionText);

        topBackground.setBackgroundColor(color);
        animationView.setAnimation(animation);
        titleText.setText(title);
        descriptionText.setText(description);
    }
}