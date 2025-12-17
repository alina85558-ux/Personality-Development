package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.UserAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.databinding.ActivityLikeUserListBinding;
import com.mayur.personalitydevelopment.models.LikeUserListResponse;

import java.util.ArrayList;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class LikeUserListActivity extends BaseActivity {
    ArrayList<LikeUserListResponse.UserDetail> userList = new ArrayList<>();
    private ActivityLikeUserListBinding binding;
    private String postId = "";
    private RelativeLayout remove_ad_rl;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(LikeUserListActivity.this, R.layout.activity_like_user_list);
        remove_ad_rl = findViewById(R.id.remove_ad);
        mAdView = findViewById(R.id.adView);


        binding.rvUser.setHasFixedSize(true);
        binding.rvUser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        binding.rvUser.setNestedScrollingEnabled(false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        init();
        changeReadingMode();
    }

    private void init() {
        if (getIntent().hasExtra(Constants.POST_ID)) {
            postId = getIntent().getStringExtra(Constants.POST_ID);
            getLikeList();
        }
        else if (getIntent().hasExtra("commentId")) {
            likeUserInfoComments(getIntent().getStringExtra("commentId"));
        }
    }

    private void getLikeList() {
        try {
            Utils.showDialog(this);
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, ApiCallBack.getLikeList(BaseActivity.getKYC(),
                    authToken,
                    sp.getBoolean("guest_entry", false),
                    Constants.getV6Value(),
                    postId), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    try {
                        LikeUserListResponse.UserListData userListData = new Gson().fromJson(response, LikeUserListResponse.UserListData.class);
                        userList.addAll(userListData.getUsers());
                        setAdapter();
                        Utils.hideDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    try {
                        Utils.hideDialog();
                        Toast.makeText(LikeUserListActivity.this, "Failure", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    Toast.makeText(LikeUserListActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(LikeUserListActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void likeUserInfoComments(String commentId) {
//        Utils.showDialog(LikeUserListActivity.this);
        String authToken = "";
        if (Constants.getUserData(LikeUserListActivity.this) != null) {
            authToken = Constants.getUserData(LikeUserListActivity.this).getAuthentication_token();
        }

        connectPost(LikeUserListActivity.this, null, ApiCallBack.likeUserInfoComments(
                BaseActivity.getKYC(),
                authToken,
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value(), commentId),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        try {
                            LikeUserListResponse.UserListData userListData = new Gson().fromJson(response, LikeUserListResponse.UserListData.class);
                            if (userList != null){
                                userList.clear();
                            }
                            userList.addAll(userListData.getUsers());
                            setAdapter();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Utils.hideDialog();
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(LikeUserListActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onConnectionFailure() {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(LikeUserListActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(LikeUserListActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }
                });

    }

    private void setAdapter() {
        UserAdapter userAdapter = new UserAdapter(this, userList);
        binding.rvUser.setAdapter(userAdapter);
    }

    @Override
    protected void onResume() {
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
                remove_ad_rl.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void changeReadingMode() {
        if (sp.getBoolean("light", false)) {
//            binding.llMain.setBackgroundColor(getResources().getColor(R.color.dark_grey));
            binding.rlMain.setBackgroundColor(getResources().getColor(R.color.dark_grey));
            binding.adsRem.setTextColor(getResources().getColor(R.color.white));
            binding.adsRem.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_white));
        } else {
//            binding.llMain.setBackgroundColor(getResources().getColor(android.R.color.background_light));
//            binding.rlMain.setBackgroundColor(getResources().getColor(R.color.white));
            binding.rlMain.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            binding.adsRem.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.adsRem.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
        }
    }

    public void Premium_fun(View v) {
        try {
            boolean isAvailable = Utils.isIabServiceAvailable(this);
            if (isAvailable) {
                Intent purchase = new Intent(LikeUserListActivity.this, RemoveAdActivity.class);
                startActivity(purchase);
            } else {
                Toast.makeText(LikeUserListActivity.this, "In-App Subscription not supported", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
