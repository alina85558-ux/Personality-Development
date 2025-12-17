package com.mayur.personalitydevelopment.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.connection.ConnectivityReceiver;

public class NoInternetConnectionFragment extends Fragment {

    private static final String TAG = NoInternetConnectionFragment.class.getSimpleName();
    public static OnInterNetConnectionListener connectedListner;

    public static void setInterNetConnectionListner(OnInterNetConnectionListener listner) {
        connectedListner = listner;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.no_internet_connections, container, false);

        TextView btnTryAgain = rootView.findViewById(R.id.btnTryAgain);
        TextView btnSetting = rootView.findViewById(R.id.btnSetting);
        btnSetting.setVisibility(View.GONE);

        btnTryAgain.setOnClickListener(v -> {
            if (ConnectivityReceiver.isConnected()) {
                connectedListner.onInterNetConnected();
            } else {
                Utils.showToast(getString(R.string.no_internet_connection));
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    public interface OnInterNetConnectionListener {
        void onInterNetConnected();
    }
}
