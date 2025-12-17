//Jay BHAGWANJI
package com.mayur.personalitydevelopment.activity;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;

import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.databinding.ActivityRemoveAd1Binding;
import com.mayur.personalitydevelopment.models.OfferResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class RemoveAdActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private static final String TAG = RemoveAdActivity.class.getSimpleName();
//    ArrayList<String> packageList;
    private BillingClient billingClient;
    private ActivityRemoveAd1Binding binding;
    private SharedPreferences sp;
    private Handler handler;
    private boolean isOfferActive = false;

    ImmutableList<QueryProductDetailsParams.Product> productList;
    private ArrayList<String> packageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_remove_ad_1);
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

//        packageList = new ArrayList<>();
//        packageList.add("3_months");
//        packageList.add("6_months");
//        packageList.add("yearly");
//        packageList.add("six_months_v2");
//        packageList.add("twelve_months_v2");
//        packageList.add("one_month_v2");
//        packageList.add("offer_twelve_months_v2");
//        packageList.add("six_months_v2_disp_2");
//        packageList.add("offer_twelve_months_v2_disp_2");
//        packageList.add("twelve_months_v2_disp_2");

        QueryProductDetailsParams.Product builder = QueryProductDetailsParams.Product.newBuilder()
                .setProductId("3_months")
                .setProductType(BillingClient.ProductType.SUBS).build();

        QueryProductDetailsParams.Product builder2 = QueryProductDetailsParams.Product.newBuilder()
                .setProductId("6_months")
                .setProductType(BillingClient.ProductType.SUBS).build();

        QueryProductDetailsParams.Product builder3 = QueryProductDetailsParams.Product.newBuilder()
                .setProductId("yearly")
                .setProductType(BillingClient.ProductType.SUBS).build();

        QueryProductDetailsParams.Product builder4 = QueryProductDetailsParams.Product.newBuilder()
                .setProductId("six_months_v2")
                .setProductType(BillingClient.ProductType.SUBS).build();

        QueryProductDetailsParams.Product builder5 = QueryProductDetailsParams.Product.newBuilder()
                .setProductId("twelve_months_v2")
                .setProductType(BillingClient.ProductType.SUBS).build();

        QueryProductDetailsParams.Product builder6 = QueryProductDetailsParams.Product.newBuilder()
                .setProductId("one_month_v2")
                .setProductType(BillingClient.ProductType.SUBS).build();

        QueryProductDetailsParams.Product builder7 = QueryProductDetailsParams.Product.newBuilder()
                .setProductId("offer_twelve_months_v2")
                .setProductType(BillingClient.ProductType.SUBS).build();

        QueryProductDetailsParams.Product builder8 = QueryProductDetailsParams.Product.newBuilder()
                .setProductId("six_months_v2_disp_2")
                .setProductType(BillingClient.ProductType.SUBS).build();

        QueryProductDetailsParams.Product builder9 = QueryProductDetailsParams.Product.newBuilder()
                .setProductId("offer_twelve_months_v2_disp_2")
                .setProductType(BillingClient.ProductType.SUBS).build();

        QueryProductDetailsParams.Product builder10 = QueryProductDetailsParams.Product.newBuilder()
                .setProductId("twelve_months_v2_disp_2")
                .setProductType(BillingClient.ProductType.SUBS).build();

         productList = ImmutableList.of(
//                 builder,
//                 builder2,
//                 builder3,
                 builder4,
                 builder5,
                 builder6,
//                 builder7,
                 builder8,
//                 builder9,
                 builder10);

        initializeBilling();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (Constants.IS_FROM_NOTIFICATION_ACT) {
                Constants.IS_FROM_NOTIFICATION_ACT = false;
                Intent i = new Intent(RemoveAdActivity.this, NotificationActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return;
            }
            onBackPressed();
        });

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MRegular.ttf");
        binding.t1.setTypeface(font);
        binding.t5.setTypeface(font);

        handler = new Handler();
        callGetOfferFlagAPI();
        setColorData();
    }

    private void callGetOfferFlagAPI() {
        try {

            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, ApiCallBack.getOfferFlagAPI(BaseActivity.getKYC(),
                    authToken,
                    sp.getBoolean("guest_entry", false),
                    Constants.getV6Value()),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                            OfferResponse.OfferData result = new Gson().fromJson(response, OfferResponse.OfferData.class);
                            setLifeTimeButtonView(result);
                            isOfferActive = result.isOfferActive();
                            setOfferLabel(result.isOfferActive());
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
                                setOfferLabel(false);
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
                                setOfferLabel(false);
                                Toast.makeText(getApplicationContext(), "CC Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                            }
                        }

                        @Override
                        public void onException(Headers headers, int StatusCode) {
                            try {
                                Utils.hideDialog();
                                setOfferLabel(false);
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

    private void setLifeTimeButtonView(OfferResponse.OfferData data) {
        if (data.isLifetimeActive()) {
            binding.lifetimeLinLay.setVisibility(View.VISIBLE);
            binding.headerTextView.setText(data.getLabel());
            binding.subHeaderTextView.setText(data.getSmallLabel());
            binding.lifetimeLinLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data.getUrl())));
                }
            });
        } else {
            binding.lifetimeLinLay.setVisibility(View.GONE);
        }
    }

    void setColorData() {
        try {
            if (sp.getBoolean("light", false)) {
                binding.main.setBackgroundColor(Color.parseColor("#464646"));
                binding.badge.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.MULTIPLY);
                binding.noad.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.MULTIPLY);
                binding.unlock.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.MULTIPLY);
                binding.t1.setTextColor(Color.parseColor("#ffffff"));
                binding.t2.setTextColor(Color.parseColor("#ffffff"));
                binding.t5.setTextColor(Color.parseColor("#ffffff"));
                binding.txtOffline.setTextColor(Color.parseColor("#ffffff"));
                binding.txtAllFunction.setTextColor(Color.parseColor("#ffffff"));
                binding.savers.setImageResource(R.drawable.courses_active_white);
                binding.txtSavers.setTextColor(Color.parseColor("#ffffff"));
                binding.offline.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_offline_access_whie));
                binding.offline.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_offline_access_whie));
                binding.allFunction.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_premium_benfis_whie));
            } else {
                binding.savers.setImageResource(R.drawable.courses_active);
                binding.offline.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_offline_access));
                binding.allFunction.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_premium_benfis));
                binding.main.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeBilling() {
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases(PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts()   // enable pending for one-time products
                        .build())
                .setListener((billingResult, purchases) -> {
                    if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK && purchases != null){
                        Log.d(TAG, "onPurchasesUpdated: ");
                        AlertDialog.Builder builder = new AlertDialog.Builder(RemoveAdActivity.this);
                        LayoutInflater inflater = RemoveAdActivity.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog, null);
                        builder.setView(dialogView).setPositiveButton("OK", (dialog, which) -> {
                                    Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                                    assert i != null;
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                }).create().show();
                    }

                })
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "onBillingSetupFinished: ");
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



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void buy(View view) {
        buyPackage("six_months_v2");
    }

    public void buy2(View view) {
        if (isOfferActive) {
            buyPackage("offer_twelve_months_v2");
        } else {
            buyPackage("twelve_months_v2");
        }
    }

    public void buy4(View view) {
        buyPackage("one_month_v2");
    }

  /*  private void buyPackage(String selectedPackage) {
        if (billingClient.isReady()) {
            SkuDetailsParams skuDetailsParams = SkuDetailsParams
                    .newBuilder()
                    .setSkusList(packageList)
                    .setType(BillingClient.SkuType.SUBS)
                    .build();
            billingClient.querySkuDetailsAsync(skuDetailsParams, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null && list.size() > 0) {
                        for (int i = 0; list.size() > i; i++) {
                            if (list.get(i).getSku().equalsIgnoreCase(selectedPackage)) {
                                BillingFlowParams billingFlowParams = BillingFlowParams
                                        .newBuilder()
                                        .setSkuDetails(list.get(i))
                                        .build();
                                billingClient.launchBillingFlow(RemoveAdActivity.this, billingFlowParams);
                            }
                        }
                    }
                }

            });
        }
    }*/


    private void buyPackage(String  selectedPackage){
        Log.e("buyPackage", "Called");
        if (billingClient.isReady()) {
            Log.e("isReady", "Called");
            QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                    .setProductList(productList)
                    .build();

            billingClient.queryProductDetailsAsync(params, (billingResult, queryProductDetailsResult) -> {
                List<ProductDetails> productDetailsList = queryProductDetailsResult.getProductDetailsList();
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !productDetailsList.isEmpty()) {
                    for (int i = 0; productDetailsList.size() > i; i++) {
                        if (productDetailsList.get(i).getProductId().equalsIgnoreCase(selectedPackage)) {
                            assert productDetailsList.get(i).getSubscriptionOfferDetails() != null;
                            String offerToken = Objects.requireNonNull(productDetailsList.get(i).getSubscriptionOfferDetails())
                                    .get(0)
                                    .getOfferToken();

                            ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                                    ImmutableList.of(
                                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                                    .setProductDetails(productDetailsList.get(i))
                                                    .setOfferToken(offerToken)
                                                    .build()
                                    );
                            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                    .setProductDetailsParamsList(productDetailsParamsList)
                                    .build();

                            billingClient.launchBillingFlow(RemoveAdActivity.this, billingFlowParams);

                        }
                    }
                }
                    });

            /*billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList.size() > 0) {
                            for (int i = 0; productDetailsList.size() > i; i++) {

                                if (productDetailsList.get(i).getProductId().equalsIgnoreCase(selectedPackage)) {

                                    String offerToken = productDetailsList.get(i).getSubscriptionOfferDetails()
                                            .get(0)
                                            .getOfferToken();

                                    ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                                            ImmutableList.of(
                                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                                            .setProductDetails(productDetailsList.get(i))
                                                            .setOfferToken(offerToken)
                                                            .build()
                                            );
                                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                            .setProductDetailsParamsList(productDetailsParamsList)
                                            .build();

                                    billingClient.launchBillingFlow(RemoveAdActivity.this, billingFlowParams);

                                }
                            }
                        }
                    }
            );*/

        }

    }

    private void setOfferLabel(final boolean offerActive) {
        if (offerActive) {
            binding.txtLimitedOffer.setVisibility(View.VISIBLE);
            binding.yearDescount.setText("Save 60%");
            binding.rlTwelveBg.setBackgroundResource(R.drawable.back_ground_12_offer);
            binding.yearDescount.setTextColor(ContextCompat.getColor(this, R.color.red1));
            binding.year.setTextColor(ContextCompat.getColor(this, R.color.red1));
        } else {
            binding.txtLimitedOffer.setVisibility(View.GONE);
            binding.yearDescount.setText("Save 51%");
            binding.rlTwelveBg.setBackgroundResource(R.drawable.back_ground_12);
            binding.yearDescount.setTextColor(ContextCompat.getColor(this, R.color.white));
            binding.year.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

         handler.postDelayed(() -> {
            try {
                if (billingClient.isReady()) {
                    QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                            .setProductList(productList)
                            .build();

                    billingClient.queryProductDetailsAsync(params, (billingResult, queryProductDetailsResult) -> {

                        List<ProductDetails> productDetailsList = queryProductDetailsResult.getProductDetailsList();
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !productDetailsList.isEmpty()) {
                            for (int i = 0; productDetailsList.size() > i; i++) {
                                List<ProductDetails.SubscriptionOfferDetails> subscriptionPlans = productDetailsList.get(i).getSubscriptionOfferDetails();
                                assert subscriptionPlans != null;
                                ProductDetails.SubscriptionOfferDetails pricingPlan = subscriptionPlans.get(0);
                                ProductDetails.PricingPhase firstPricingPhase = pricingPlan.getPricingPhases().getPricingPhaseList().get(0);

                                String productId = productDetailsList.get(i).getProductId();
                                String formattedPrice = String.format("%s/mo", firstPricingPhase.getFormattedPrice());

                                if (productId.equalsIgnoreCase("one_month_v2")) {
                                    binding.threeMonth.setText(formattedPrice);
                                } else if (productId.equalsIgnoreCase("six_months_v2_disp_2")) {
                                    binding.sixMonth.setText(formattedPrice);
                                } else {
                                    Log.e("Check Id", productDetailsList.get(i).getProductId());
                                    if (isOfferActive) {
                                        if (productId.equalsIgnoreCase("offer_twelve_months_v2_disp_2")) {
                                            binding.year.setText(formattedPrice);
                                        }
                                    } else {
                                        if (productId.equalsIgnoreCase("twelve_months_v2_disp_2")) {
                                            binding.year.setText(formattedPrice);
                                        }
                                    }
                                }
                            } // for loop end
                        }
                    });

                  /*  billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList.size() > 0) {
                                    for (int i = 0; productDetailsList.size() > i; i++) {
                                        List<ProductDetails.SubscriptionOfferDetails> subscriptionPlans = productDetailsList.get(i).getSubscriptionOfferDetails();
                                        assert subscriptionPlans != null;
                                        ProductDetails.SubscriptionOfferDetails pricingPlan = subscriptionPlans.get(0);
                                        ProductDetails.PricingPhase firstPricingPhase = pricingPlan.getPricingPhases().getPricingPhaseList().get(0);

//                                        Log.e("Product Price", productDetailsList.get(i).getProductId()+" =  "+firstPricingPhase.getBillingPeriod()+" Plan = "+firstPricingPhase.getFormattedPrice());

                                        if (productDetailsList.get(i).getProductId().equalsIgnoreCase("one_month_v2")) {
                                            binding.threeMonth.setText(String.format("%s/mo", firstPricingPhase.getFormattedPrice()));
                                        } else if (productDetailsList.get(i).getProductId().equalsIgnoreCase("six_months_v2_disp_2")) {
                                            binding.sixMonth.setText(String.format("%s/mo", firstPricingPhase.getFormattedPrice()));
                                        } else {
                                            Log.e("Check Id", productDetailsList.get(i).getProductId());
                                            if (isOfferActive) {
                                                if (productDetailsList.get(i).getProductId().equalsIgnoreCase("offer_twelve_months_v2_disp_2")) {
                                                    binding.year.setText(String.format("%s/mo", firstPricingPhase.getFormattedPrice()));
                                                }
                                            } else {
                                                if (productDetailsList.get(i).getProductId().equalsIgnoreCase("twelve_months_v2_disp_2")) {
                                                    binding.year.setText(String.format("%s/mo", firstPricingPhase.getFormattedPrice()));
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                    );*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 200);

    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

            AlertDialog.Builder builder = new AlertDialog.Builder(RemoveAdActivity.this);
            LayoutInflater inflater = RemoveAdActivity.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog, null);
            builder.setView(dialogView)
                    .setPositiveButton("OK", (dialog, which) -> {
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        assert i != null;
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }).create().show();
        }

}
