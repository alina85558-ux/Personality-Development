package com.mayur.personalitydevelopment.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mayur.personalitydevelopment.R;

public class WelcomeScreenFragment extends Fragment {

    private OnContinueClickListener listener;

    public interface OnContinueClickListener {
        void onContinueClicked();
    }

    public void setOnContinueClickListener(OnContinueClickListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome_screen, container, false);

        Button btnContinue = rootView.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContinueClicked();
            }
        });

        return rootView;
    }
}
