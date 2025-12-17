package com.mayur.personalitydevelopment.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.connection.ConnectionDetector;

import java.util.Calendar;
import java.util.UUID;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setTheme(R.style.Theme_App_Starting);
            super.onCreate(savedInstanceState);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
            SharedPreferences.Editor editor = sp.edit();

            if (sp.getString("UUID", "").equals("")) {
                editor.putString("UUID", UUID.randomUUID().toString());
                editor.apply();
            }

            editor.putBoolean("light", getMode());
            editor.commit();

           /* Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();*/


            Intent intent = new Intent(SplashActivity.this, OnBoardingActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }else {
            super.onCreate(savedInstanceState);
            setTheme(R.style.splashTheme);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
            SharedPreferences.Editor editor = sp.edit();

            if (sp.getString("UUID", "").equals("")) {
                editor.putString("UUID", UUID.randomUUID().toString());
                editor.apply();
            }

            editor.putBoolean("light", getMode());
            editor.commit();

            /*Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();*/

            Intent intent = new Intent(SplashActivity.this, OnBoardingActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

/*    void generateSHAKey() {
        try {
            Context context = getApplicationContext();
            PackageInfo info = context.getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }*/

    boolean getMode() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        return hour <= 6 || hour >= 17;
    }
}
