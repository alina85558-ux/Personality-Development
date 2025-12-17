package com.mayur.personalitydevelopment.Utils;

import androidx.browser.customtabs.CustomTabsClient;

public interface ServiceConnectionCallback {
    void onServiceConnected(CustomTabsClient client);

    /**
     * Called when the service is disconnected.
     */
    void onServiceDisconnected();
}
