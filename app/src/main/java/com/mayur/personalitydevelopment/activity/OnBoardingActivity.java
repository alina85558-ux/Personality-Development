package com.mayur.personalitydevelopment.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.databinding.ActivityOnBoardingBinding;
import com.mayur.personalitydevelopment.fragment.FocusFragment;
import com.mayur.personalitydevelopment.fragment.GoalsFragment;
import com.mayur.personalitydevelopment.fragment.InterestsFragment;
import com.mayur.personalitydevelopment.fragment.WelcomeScreenFragment;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {

    private ActivityOnBoardingBinding binding;
    private FragmentManager fragmentManager;
    private int currentFragmentIndex = 0;
    private static final int TOTAL_FRAGMENTS = 4;
    
    // Store selected data
    private List<String> selectedGoals = new ArrayList<>();
    private List<String> selectedInterests = new ArrayList<>();
    private String selectedFocus = "";

    private WelcomeScreenFragment welcomeScreenFragment;
    private GoalsFragment goalsFragment;
    private InterestsFragment interestsFragment;
    private FocusFragment focusFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide action bar for full screen
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding = DataBindingUtil.setContentView(OnBoardingActivity.this, R.layout.activity_on_boarding);
        fragmentManager = getSupportFragmentManager();

        // Initialize fragments
        welcomeScreenFragment = new WelcomeScreenFragment();
        goalsFragment = new GoalsFragment();
        interestsFragment = new InterestsFragment();
        focusFragment = new FocusFragment();

        // Set up listeners
        setupFragmentListeners();

        // Load first fragment
        updateProgressBar();
        loadFragment(welcomeScreenFragment, false);
    }

    private void setupFragmentListeners() {
        // Welcome screen listener
        welcomeScreenFragment.setOnContinueClickListener(() -> {
            currentFragmentIndex = 1;
            updateProgressBar();
            loadFragment(goalsFragment, true);
        });

        // Goals fragment listener
        goalsFragment.setOnContinueClickListener((goals) -> {
            selectedGoals = goals;
            currentFragmentIndex = 2;
            updateProgressBar();
            loadFragment(interestsFragment, true);
        });

        // Interests fragment listener
        interestsFragment.setOnContinueClickListener((interests) -> {
            selectedInterests = interests;
            currentFragmentIndex = 3;
            updateProgressBar();
            loadFragment(focusFragment, true);
        });

        // Focus fragment listener
        focusFragment.setOnSubmitClickListener((focus) -> {
            selectedFocus = focus;
            completeOnBoarding();
        });
    }

    private void updateProgressBar() {
        if (currentFragmentIndex == 0) {
            binding.progressBar.setVisibility(android.view.View.GONE);
        } else {
            binding.progressBar.setVisibility(android.view.View.VISIBLE);
            int progress = (currentFragmentIndex * 100) / (TOTAL_FRAGMENTS - 1); // 33%, 66%, 100%
            binding.progressBar.setProgress(progress);
        }
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (currentFragmentIndex > 0) {
            currentFragmentIndex--;
            updateProgressBar();
            Fragment fragment = getFragmentForIndex(currentFragmentIndex);
            if (fragment != null) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragmentContainer, fragment);
                transaction.commit();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private Fragment getFragmentForIndex(int index) {
        switch (index) {
            case 0:
                return welcomeScreenFragment;
            case 1:
                return goalsFragment;
            case 2:
                return interestsFragment;
            case 3:
                return focusFragment;
            default:
                return null;
        }
    }

    private void completeOnBoarding() {
        Intent intent = new Intent(OnBoardingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}