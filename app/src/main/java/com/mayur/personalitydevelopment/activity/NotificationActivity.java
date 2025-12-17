package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.NotificationAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.databinding.ActivityNotificationBinding;
import com.mayur.personalitydevelopment.listener.NotificationClickListener;
import com.mayur.personalitydevelopment.models.NotificationDataRes;
import com.mayur.personalitydevelopment.models.NotificationResponse;

import java.util.ArrayList;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class NotificationActivity extends BaseActivity implements NotificationClickListener {

    private ActivityNotificationBinding binding;
    private boolean isDarkTheme = false;
    private int pageCount = 0;
    private LinearLayoutManager llm;
    private ArrayList<NotificationDataRes> notificationList;
    private NotificationAdapter adapter;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        mAdView = findViewById(R.id.adView);
        setToolbar();
        changeReadingMode();
        llm = new LinearLayoutManager(this);
        onLoadMore();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void onLoadMore() {
        binding.notificationRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (llm.findLastCompletelyVisibleItemPosition() == notificationList.size() - 1) {
                    getData();
                }
            }
        });
    }

    private void getData() {
        if (!sp.getBoolean("guest_entry", false)) {
            getNotification();
        } else {
            getNotificationGuest();
        }
    }

    private void getNotification() {
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showToast(getString(R.string.no_internet_connection));
            return;
        }
        Utils.showDialog(this);
        pageCount = pageCount + 1;
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.getNotification(
                BaseActivity.getKYC(),
                authToken,
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value(), pageCount),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        setRecyclerViewData(response);
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        Utils.hideDialog();
                        try {
                            Toast.makeText(NotificationActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onConnectionFailure() {
                        Utils.hideDialog();
                        try {
                            Toast.makeText(NotificationActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        Utils.hideDialog();
                        try {
                            Toast.makeText(NotificationActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getNotificationGuest() {
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showToast(getString(R.string.no_internet_connection));
            return;
        }
        Utils.showDialog(this);
        pageCount = pageCount + 1;
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.getNotificationGuest(
                BaseActivity.getKYC(),
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value(), pageCount, sp.getString(Constants.GUEST_ID, "")),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        setRecyclerViewData(response);
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        Utils.hideDialog();
                        try {
                            Toast.makeText(NotificationActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onConnectionFailure() {
                        Utils.hideDialog();
                        try {
                            Toast.makeText(NotificationActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        Utils.hideDialog();
                        try {
                            Toast.makeText(NotificationActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setRecyclerViewData(String response) {
        NotificationResponse notificationResponse = new Gson().fromJson(response, NotificationResponse.class);
        if (notificationList != null && notificationList.size() > 0) {
            notificationList.addAll(notificationResponse.getNotificationsList());
            adapter.notifyDataSetChanged();
        } else {
            notificationList = notificationResponse.getNotificationsList();
            adapter = new NotificationAdapter(NotificationActivity.this, isDarkTheme, notificationList, NotificationActivity.this);
            binding.notificationRecyclerView.setHasFixedSize(true);
            binding.notificationRecyclerView.setLayoutManager(llm);
            binding.notificationRecyclerView.setAdapter(adapter);
        }
        Utils.hideDialog();
    }

    @Override
    public void onNotificationClick(int position, NotificationDataRes notificationDataRes) {
        Constants.IS_FROM_NOTIFICATION_ACT = true;
        Constants.IS_RELATED_ARTICLE_CLICK = false;
        Constants.RELATED_ARTICLE_ACTIVITY_INSTANCE_COUNT = 0;
        Intent intent = null;
        String redirectTo = notificationDataRes.getRedirectTo();
        if (redirectTo != null && !redirectTo.isEmpty()) {
            if (redirectTo.equalsIgnoreCase("article_detail")) {
                String passArticleId = notificationDataRes.getArticleId() + "";
                if (passArticleId != null && !passArticleId.equalsIgnoreCase("")) {
                    intent = new Intent(this, ArticleDetailActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, passArticleId);
                    intent.putExtra(Constants.FROM, Constants.FROM_NOTIFICATION);
                } else {
                    intent = new Intent(this, MainActivity.class);
                }
            } else if (redirectTo.equalsIgnoreCase("premium_page")) {
                intent = new Intent(this, RemoveAdActivity.class);
            } else if (redirectTo.equalsIgnoreCase("url")) {
                String redirectUrl = notificationDataRes.getRedirectUrl();
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(redirectUrl));
            } else if (redirectTo.equalsIgnoreCase("post")) {
                String passPostId = notificationDataRes.getPostId() + "";
                if (passPostId != null && !passPostId.equalsIgnoreCase("")) {
                    intent = new Intent(this, PostDetailActivity.class);
                    intent.putExtra(Constants.POST_ID, passPostId);
                    intent.putExtra(Constants.FROM, Constants.FROM_NOTIFICATION);
                } else {
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra(Constants.FROM, Constants.FROM_NOTIFICATION_POST);
                }
            } else if (redirectTo.equalsIgnoreCase("savers")) {
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("fromSaverNotification", "savers");
            }
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        assert intent != null;
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void changeReadingMode() {
        if (sp.getBoolean("light", false)) {
            isDarkTheme = true;
            binding.notificationActLinLay.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        } else {
            isDarkTheme = false;
            binding.notificationActLinLay.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    private void handleData() {
        if (Constants.IS_NEW_ACT) {
            if (notificationList != null && notificationList.size() > 0) {
                notificationList.clear();
                adapter = null;
            }
            Constants.IS_NEW_ACT = false;
            pageCount = 0;
            getData();
        }
    }

    @Override
    protected void onResume() {
        handleData();
        try {
            super.onResume();
            if (!restored_Issubscribed) {
                AdRequest adRequest = new AdRequest.Builder().build();
                if (mAdView != null) {
                    mAdView.loadAd(adRequest);
                    mAdView.setVisibility(View.VISIBLE);
                }
            } else {
                if (mAdView != null) {
                    mAdView.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Premium_fun(View v) {
        try {
            boolean isAvailable = Utils.isIabServiceAvailable(this);
            if (isAvailable) {
                Intent purchase = new Intent(NotificationActivity.this, RemoveAdActivity.class);
                startActivity(purchase);
            } else {
                Toast.makeText(NotificationActivity.this, "In-App Subscription not supported", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}