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

public class InterestsFragment extends Fragment {

    private OnContinueClickListener listener;
    private List<CheckBox> interestCheckBoxes;
    private List<String> interestTexts;
    private static final int MAX_SELECTION = 3;

    public interface OnContinueClickListener {
        void onContinueClicked(List<String> selectedInterests);
    }

    public void setOnContinueClickListener(OnContinueClickListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_interests, container, false);

        interestCheckBoxes = new ArrayList<>();
        interestTexts = new ArrayList<>();
        LinearLayout interestsContainer = rootView.findViewById(R.id.interestsContainer);
        Button btnContinue = rootView.findViewById(R.id.btnContinue);

        // Interests list (similar to goals, you can customize these)
        int[] interestIcons = {
            R.drawable.ic_user,
            R.drawable.ic_user,
            R.drawable.ic_user,
            R.drawable.ic_user,
            R.drawable.ic_user,
            R.drawable.ic_user,
            R.drawable.ic_user,
            R.drawable.ic_user,
            R.drawable.ic_user,
            R.drawable.ic_user,
            R.drawable.ic_user,
            R.drawable.ic_user
        };

        String[] interests = {
            "Personal Development",
            "Career Growth",
            "Health & Fitness",
            "Relationships",
            "Finance & Wealth",
            "Creativity",
            "Spirituality",
            "Education",
            "Travel",
            "Technology",
            "Sports",
            "Arts & Culture"
        };

        // Create interest items with icons
        for (int i = 0; i < interests.length; i++) {
            View interestItemView = inflater.inflate(R.layout.item_interest, interestsContainer, false);
            ImageView ivIcon = interestItemView.findViewById(R.id.ivIcon);
            TextView tvInterest = interestItemView.findViewById(R.id.tvInterest);
            CheckBox checkBox = interestItemView.findViewById(R.id.checkbox);

            ivIcon.setImageResource(interestIcons[i]);
            tvInterest.setText(interests[i]);
            
            interestTexts.add(interests[i]);
            interestCheckBoxes.add(checkBox);
            
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int selectedCount = getSelectedInterests().size();
                if (isChecked && selectedCount >= MAX_SELECTION) {
                    checkBox.setChecked(false);
                    Toast.makeText(getContext(), "You can select up to 3 interests", Toast.LENGTH_SHORT).show();
                }
            });

            interestsContainer.addView(interestItemView);
        }

        btnContinue.setOnClickListener(v -> {
            List<String> selectedInterests = getSelectedInterests();
            if (selectedInterests.isEmpty()) {
                Toast.makeText(getContext(), "Please select at least one interest", Toast.LENGTH_SHORT).show();
            } else if (listener != null) {
                listener.onContinueClicked(selectedInterests);
            }
        });

        return rootView;
    }

    private List<String> getSelectedInterests() {
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < interestCheckBoxes.size(); i++) {
            if (interestCheckBoxes.get(i).isChecked()) {
                selected.add(interestTexts.get(i));
            }
        }
        return selected;
    }
}
