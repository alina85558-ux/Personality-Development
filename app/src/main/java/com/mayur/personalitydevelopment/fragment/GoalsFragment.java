package com.mayur.personalitydevelopment.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mayur.personalitydevelopment.R;

import java.util.ArrayList;
import java.util.List;

public class GoalsFragment extends Fragment {

    private OnContinueClickListener listener;
    private List<CheckBox> goalCheckBoxes;
    private List<String> goalTexts;
    private static final int MAX_SELECTION = 3;

    public interface OnContinueClickListener {
        void onContinueClicked(List<String> selectedGoals);
    }

    public void setOnContinueClickListener(OnContinueClickListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_goals, container, false);

        goalCheckBoxes = new ArrayList<>();
        goalTexts = new ArrayList<>();
        LinearLayout goalsContainer = rootView.findViewById(R.id.goalsContainer);
        Button btnContinue = rootView.findViewById(R.id.btnContinue);

        // Goals list with icons (using drawable resource IDs - you can customize these)
        int[] goalIcons = {
            R.drawable.ic_user, // Increase productivity - clock icon
            R.drawable.ic_user, // Have a successful career - mountain icon
            R.drawable.ic_user, // Be a better parent - family icon
            R.drawable.ic_user, // Become confident - trophy icon
            R.drawable.ic_user, // Achieve life balance - scales icon
            R.drawable.ic_user, // Boost intelligence - brain icon
            R.drawable.ic_user, // Develop healthy relationships - heart icon
            R.drawable.ic_user, // Create wealth - money icon
            R.drawable.ic_user, // Improve sex life
            R.drawable.ic_user, // Lead a healthy lifestyle - apple icon
            R.drawable.ic_user  // Reach happiness - smiley icon
        };

        String[] goals = {
            "Increase productivity",
            "Have a successful career",
            "Be a better parent",
            "Become confident",
            "Achieve life balance",
            "Boost intelligence",
            "Develop healthy relationships",
            "Create wealth",
            "Improve sex life",
            "Lead a healthy lifestyle",
            "Reach happiness"
        };

        // Create goal items with icons
        for (int i = 0; i < goals.length; i++) {
            View goalItemView = inflater.inflate(R.layout.item_goal, goalsContainer, false);
            ImageView ivIcon = goalItemView.findViewById(R.id.ivIcon);
            TextView tvGoal = goalItemView.findViewById(R.id.tvGoal);
            CheckBox checkBox = goalItemView.findViewById(R.id.checkbox);

            ivIcon.setImageResource(goalIcons[i]);
            tvGoal.setText(goals[i]);
            
            goalTexts.add(goals[i]);
            goalCheckBoxes.add(checkBox);
            
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int selectedCount = getSelectedGoals().size();
                if (isChecked && selectedCount >= MAX_SELECTION) {
                    checkBox.setChecked(false);
                    Toast.makeText(getContext(), "You can select up to 3 goals", Toast.LENGTH_SHORT).show();
                }
            });

            goalsContainer.addView(goalItemView);
        }

        btnContinue.setOnClickListener(v -> {
            List<String> selectedGoals = getSelectedGoals();
            if (selectedGoals.isEmpty()) {
                Toast.makeText(getContext(), "Please select at least one goal", Toast.LENGTH_SHORT).show();
            } else if (listener != null) {
                listener.onContinueClicked(selectedGoals);
            }
        });

        return rootView;
    }

    private List<String> getSelectedGoals() {
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < goalCheckBoxes.size(); i++) {
            if (goalCheckBoxes.get(i).isChecked()) {
                selected.add(goalTexts.get(i));
            }
        }
        return selected;
    }
}
