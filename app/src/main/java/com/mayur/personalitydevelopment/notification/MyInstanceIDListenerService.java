package com.mayur.personalitydevelopment.notification;

import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class MyInstanceIDListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseIIDService";
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d(TAG, "FCM Token: " + s);

        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance();

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        if (sp.getString("email", "") != null && sp.getString("email", "").trim().length() > 0) {
            updateToken(s);
        }

        super.onNewToken(s);
    }



    public void updateToken(String token) {
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.updateToken(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), token, sp.getString("UUID", "")), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                try {
                    Log.i(TAG, "onResponseSuccess: UPDATE TOKEN SUCCESSFULLY");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                Log.i(TAG, "onResponseFailure: ");
            }

            @Override
            public void onFailure(Headers headers) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onConnectionFailure() {
                Log.i(TAG, "onConnectionFailure: ");
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                Log.i(TAG, "onException: ");
            }
        });

    }

}
