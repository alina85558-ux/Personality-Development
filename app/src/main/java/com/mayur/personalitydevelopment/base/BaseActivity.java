package com.mayur.personalitydevelopment.base;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.doneCourse;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.gson.Gson;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.activity.CoursesCategoriesListActivity;
import com.mayur.personalitydevelopment.connection.API;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.connection.ConnectionDetector;
import com.mayur.personalitydevelopment.models.SubscriptionResponse;

import java.util.List;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.ResponseBody;
/**
 * Created by Admin on 10/15/2017.
 */

public class BaseActivity extends AppCompatActivity {

    private final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg2RciyklkPme5MJ4IZUa0/XhQdZvThkJVnLWQib4AHWeBRN9UKU5PY4khMZLIsoEESShh09QY2LoxpfvC1N26N8/GKIFuL5fhZ47X7zMq+9HlrFE6Yv0eTr0Pr6UfZ0GJXosPddZp2Ed7ybCjERSmdzL0IL3CYTF2ZY6+zIlBPvpQd/1aeM61VrDjPf1n9ba0v/O38sLOmmYf3CFBLbMjvlX2Hg1LfArA0MFXbaPtXuE9MXMEyx3Vsbg+qP/dpE/JOa3OKR75hSMM4+qumTZ2nCkgVyrMyt49XR7FFFXfW6rf84AzfO+isGe/WtG5oBtX92UYG71IlI1gO67Fz8bjQIDAQAB";
    private final String MERCHANT_ID = null;
    private final String TAG = BaseActivity.class.getSimpleName();
    public boolean isLifetimeActive = false;
    public ConnectionDetector cd;
    public Boolean isInternetPresent = false;
    public API api;
    public Boolean restored_Issubscribed;
    public Constants constants;
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    public int courseCategoryId;
    public String APP_KEY = "c04f42a073dfa7b99e4d788d72f1bbce7333090e523ddb43";
    private boolean subscribed = false;
    private String subscriptionType = "";
    private boolean isLoadingSubscription;
    private BillingClient billingClient;
    private String inAppPurchaseToken = "";

    public static int getKYC() {
        return 1021068286;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        constants = new Constants(this);
        courseCategoryId = getIntent().getIntExtra("courseCategoryId", 0);
        SharedPreferences prefs = this.getSharedPreferences("Purchase", MODE_PRIVATE);
        restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        editor = sp.edit();
        cd = new ConnectionDetector(this);
    }

    public void newActivity(Class aClass, Bundle bundle, boolean isFinish) {
        Utils.hideDialog();
        Intent intent = new Intent(this, aClass);
        if (bundle != null) {
            intent.putExtra("data", bundle);
        }
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    public void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void displayMessage(int message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onDoneClick(View view) {
        try {
//            Utils.showDialog(this);
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, doneCourse(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(),
                    courseCategoryId, Utils.getCurrentDateWithTime()), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
//                    Utils.hideDialog();
                    Log.d("tag", "response:" + response);
                    CoursesCategoriesListActivity.popUntil(BaseActivity.this);
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
//                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Headers headers) {
//                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
//                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
//                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    public void updateToken() {
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }
        connectPost(this, null, ApiCallBack.updateToken(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), Utils.getFcmToken(this), sp.getString("UUID", "")), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                try {
                    Log.i("BaseActivity", "onResponseSuccess: UPDATE TOKEN SUCCESSFULLY");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                Log.i("BaseActivity", "onResponseFailure: ");
            }

            @Override
            public void onFailure(Headers headers) {
                Log.i("BaseActivity", "onFailure: ");
            }

            @Override
            public void onConnectionFailure() {
                Log.i("BaseActivity", "onConnectionFailure: ");
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                Log.i("BaseActivity", "onException: ");
            }
        });
    }

    public void initializeBilling() {
        billingClient = BillingClient.newBuilder(this)
                .setListener((billingResult, purchases) -> Log.d(TAG, "onPurchasesUpdated: "))
                .enablePendingPurchases(PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts()
                        .build())
                .enableAutoServiceReconnection()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "onBillingSetupFinished: ");
                    checkUserSubscription();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d(TAG, "onBillingServiceDisconnected: ");
            }
        });
    }


    private void checkUserSubscription() {
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                (billingResult, purchases) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK &&
                            !purchases.isEmpty()) {
                        String purchasedItem = purchases.get(0).getProducts().get(0);

                        inAppPurchaseToken = purchases.get(0).getPurchaseToken();

                        switch (purchasedItem) {
                            case "3_months" -> {
                                subscribed = true;
                                subscriptionType = "3_months";
                            }
                            case "6_months" -> {
                                subscribed = true;
                                subscriptionType = "6_months";
                            }
                            case "yearly" -> {
                                subscribed = true;
                                subscriptionType = "yearly";
                            }
                            case "six_months_v2" -> {
                                subscribed = true;
                                subscriptionType = "six_months_v2";
                            }
                            case "twelve_months_v2" -> {
                                subscribed = true;
                                subscriptionType = "twelve_months_v2";
                            }
                            case "one_month_v2" -> {
                                subscribed = true;
                                subscriptionType = "one_month_v2";
                            }
                            case "offer_twelve_months_v2" -> {
                                subscribed = true;
                                subscriptionType = "offer_twelve_months_v2";
                            }
                        }
                        BaseActivity.this.runOnUiThread(() -> {
                            if (subscribed) {
                                SharedPreferences pref = getSharedPreferences("Purchase", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putBoolean("Issubscribed", subscribed);
                                editor.apply();
                                restored_Issubscribed = pref.getBoolean("Issubscribed", false);
                                callSetSubscriptionAPI();
                            }
                        });
                    } else {
                        getSubscriptionAPI();
                    }
                }
        );
    }

    private void callSetSubscriptionAPI() {
        try {
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            Utils.showDialog(this);
            connectPost(this, null, ApiCallBack.setSubscriptionDetail(BaseActivity.getKYC(),
                    authToken,
                    sp.getBoolean("guest_entry", false),
                    Constants.getV6Value(),
                    subscribed,
                    subscriptionType,
                    inAppPurchaseToken),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {
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
                                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                            }
                        }

                        @Override
                        public void onConnectionFailure() {
                            try {
                                Utils.hideDialog();
                                Toast.makeText(getApplicationContext(), "CC Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                            }
                        }

                        @Override
                        public void onException(Headers headers, int StatusCode) {
                            try {
                                isLoadingSubscription = false;
                                Utils.hideDialog();
                                Toast.makeText(getApplicationContext(), "EE Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                                Log.e("onException 2", e.getMessage() + "");
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    private void getSubscriptionAPI() {
        try {
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            Utils.showDialog(this);
            connectPost(this, null, ApiCallBack.getSubscriptionDetail(BaseActivity.getKYC(),
                    authToken,
                    sp.getBoolean("guest_entry", false),
                    Constants.getV6Value()),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                            Utils.hideDialog();
                            SubscriptionResponse.SubscriptionData subscriptionData = new Gson().fromJson(response, SubscriptionResponse.SubscriptionData.class);
                            SharedPreferences.Editor editor = getSharedPreferences("Purchase", MODE_PRIVATE).edit();
                            if (subscriptionData.getSubscriptionType() != null &&
                                    subscriptionData.getSubscriptionType().equalsIgnoreCase("lifetime") && subscriptionData.isIsSubscriptionActive()) {
                                editor.putBoolean("Issubscribed", true);
                                if (subscriptionData.isLifetimeActive()) {
                                    if (subscriptionData.getLifetimeSubscriptionDetails() != null) {
                                        isLifetimeActive = true;
                                        editor.putString("LIFETIME_DETAIL", subscriptionData.getLifetimeSubscriptionDetails());
//                                        tab1.setLifeTimeDetail(subscriptionData.getLifetimeSubscriptionDetails());
                                    }
                                }
                            } else {
                                editor.putBoolean("Issubscribed", false);
                                subscribed = false;
                                subscriptionType = "";
                                callSetSubscriptionAPI();
                            }
                            editor.apply();
                        }

                        @Override
                        public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                            isLoadingSubscription = false;
                            Utils.hideDialog();
                        }

                        @Override
                        public void onFailure(Headers headers) {
                            try {
                                Utils.hideDialog();
                                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                            }
                        }

                        @Override
                        public void onConnectionFailure() {
                            try {
                                isLoadingSubscription = false;
                                Utils.hideDialog();
                                Toast.makeText(getApplicationContext(), "CC Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                            }
                        }

                        @Override
                        public void onException(Headers headers, int StatusCode) {
                            try {
                                isLoadingSubscription = false;
                                Utils.hideDialog();
                                Toast.makeText(getApplicationContext(), "EE Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

}
