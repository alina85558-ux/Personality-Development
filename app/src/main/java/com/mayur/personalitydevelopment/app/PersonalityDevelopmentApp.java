package com.mayur.personalitydevelopment.app;

import android.media.MediaPlayer;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.ads.RequestConfiguration;
import com.mayur.personalitydevelopment.connection.ConnectivityReceiver;

import java.util.Arrays;


public class PersonalityDevelopmentApp extends MultiDexApplication {

    private static PersonalityDevelopmentApp mInstance;
    private MediaPlayer mp;

    public static synchronized PersonalityDevelopmentApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        //FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("5B1CFFA65489B11B2DCADE83EA178613"));
        mInstance = this;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public MediaPlayer getMediaPlayer() {
        if (mp == null) {
            mp = new MediaPlayer();
        }
        return mp;
    }

    public void releaseMediaPlayer() {
        if (mp != null) {
            if (mp.isPlaying())
                mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
    }

}
