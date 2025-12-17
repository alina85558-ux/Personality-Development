package com.mayur.personalitydevelopment.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.mayur.personalitydevelopment.R;

public class Luckyprizewebview extends AppCompatActivity {
    public static final String EXTRA_URL = "extra.url";
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luckyprizewebview);
        String url = getIntent().getStringExtra(EXTRA_URL);
         webView = (WebView)findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        setTitle(url);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
           onBackPressed();
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}