package com.mayur.personalitydevelopment.Utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.mayur.personalitydevelopment.activity.Luckyprizewebview;

public class WebviewFallback implements CustomTabActivityHelper.CustomTabFallback {
    @Override
    public void openUri(Activity activity, Uri uri) {
        Intent intent = new Intent(activity, Luckyprizewebview.class);
        intent.putExtra(Luckyprizewebview.EXTRA_URL, uri.toString());
        activity.startActivity(intent);
    }
}
