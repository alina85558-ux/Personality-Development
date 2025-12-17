package com.mayur.personalitydevelopment.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mayur.personalitydevelopment.R;

public class FocusFragment extends Fragment {

    private OnSubmitClickListener listener;
    private RadioGroup radioGroup;

    public interface OnSubmitClickListener {
        void onSubmitClicked(String selectedFocus);
    }

    public void setOnSubmitClickListener(OnSubmitClickListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_focus, container, false);

        radioGroup = rootView.findViewById(R.id.radioGroupFocus);
        Button btnSubmit = rootView.findViewById(R.id.btnSubmit);

        // Focus options
        String[] focusOptions = {
            "Develop my career skills",
            "Broaden my knowledge",
            "Practice self-discipline & consistency",
            "Understand myself & others better",
            "I don't relate to any of these"
        };

        // Create radio buttons for each focus option
        for (String focus : focusOptions) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(focus);
            radioButton.setTextSize(16);
            radioButton.setTextColor(getResources().getColor(android.R.color.black));
            radioButton.setPadding(16, 16, 16, 16);
            radioGroup.addView(radioButton);
        }

        btnSubmit.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Please select a focus area", Toast.LENGTH_SHORT).show();
            } else {
                RadioButton selectedRadioButton = rootView.findViewById(selectedId);
                String selectedFocus = selectedRadioButton.getText().toString();
                if (listener != null) {
                    listener.onSubmitClicked(selectedFocus);
                }
            }
        });

        return rootView;
    }
}
