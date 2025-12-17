package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.favArticle;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.getRelatedArticle;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.likeArticle;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.signIn;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.unlockArticle;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.CustomTabActivityHelper;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.Utils.WebviewFallback;
import com.mayur.personalitydevelopment.adapter.CustomAdapter;
import com.mayur.personalitydevelopment.adapter.RelatedArticleListAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.database.ArticleRoomDatabase;
import com.mayur.personalitydevelopment.databinding.ActivityDeatilBinding;
import com.mayur.personalitydevelopment.fragment.Tab1;
import com.mayur.personalitydevelopment.models.ArticleDetailResponse;
import com.mayur.personalitydevelopment.models.Articles;
import com.mayur.personalitydevelopment.models.DATAHTML;
import com.mayur.personalitydevelopment.models.RelatedArticlesRequestResponse;
import com.mayur.personalitydevelopment.viewholder.ImageViewHolder;
import com.mayur.personalitydevelopment.viewholder.TextView2Holder;
import com.mayur.personalitydevelopment.viewholder.TextViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class ArticleDetailActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = ArticleDetailActivity.class.getSimpleName();
    private final String email = "mayur.68268@gmail.com";
    private final ArrayList<DATAHTML> datahtmls = new ArrayList<>();
    private final boolean isRunning = false;
    private final ArrayList<RelatedArticlesRequestResponse.Article> relatedArticleList = new ArrayList<>();
    public RelatedArticleListAdapter relatedArticleListAdapter;
    int fromWhere = 0;
    MediaPlayer mMediaPlayer;
    CallbackManager callbackManager;
    private Dialog dialog2;
    private String name;
    private String description;
    private Articles articlesBean;
    private ActivityDeatilBinding binding;
    private RelativeLayout remove_ad_rl;
    private CustomAdapter adapter;
    private ArrayList<DATAHTML> tempDatahtmls = new ArrayList<>();
    private String temphtmlSortString = "";
    private String temphtmlFullString = "";
    private boolean isFound = true;
    private int isLoginFrom = 1;
    private int isFrom = 1;
    private boolean isOkWithAdv = false;
    private GoogleSignInClient googleSignInClient;
    private boolean isLoading;
    private String articleId = "";
    private RewardedAd rewardedAd;
    private AdView mAdView;
    private AdView mAdViewSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(ArticleDetailActivity.this, R.layout.activity_deatil);
        remove_ad_rl = findViewById(R.id.remove_ad);
        mAdView = findViewById(R.id.adView);
        mAdViewSecond = findViewById(R.id.adView1);

        Toolbar toolbar = findViewById(R.id.maintoolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        Bundle bundle = getIntent().getExtras();
        if (getIntent().hasExtra(Constants.ARTICLE_ID)) {
            articleId = getIntent().getStringExtra(Constants.ARTICLE_ID);
            callArticleDetail(articleId);
            getRelatedArticlesList(articleId);
        } else {
            assert bundle != null;
            if (bundle.containsKey("Message")) {
                articlesBean = new Gson().fromJson(bundle.getString("Message"), Articles.class);
                setArticleData(articlesBean);
                isFrom = bundle.getInt("IS_FROM");
                getRelatedArticlesList(articlesBean.getId() + "");
            }
        }

        if (sp.getBoolean("guest_entry", false)) {
            init();
        }

        binding.btnReadMore.setOnClickListener(v -> {
            try {
                if (articlesBean.isArticle_is_locked()) {
                    if (sp.getBoolean("guest_entry", false)) {
                        //Open login dialog
//                        showLoginDialog(3);

                        //Open Dialog for view adv.
                        SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
                        isOkWithAdv = prefs.getBoolean("articleViewChoice", false);
                        if (isOkWithAdv) {
                            initRewardVideo(articlesBean.getId());
                        } else {
                            showArticleOptionToWatchDialog(articlesBean.getId());
                        }
                    } else {
                        if (articlesBean.isUser_article_is_locked()) {
                            if (restored_Issubscribed) {
                                //Update service status.
                                updateWatchedVideoStatus(articlesBean.getId(), false);
                            } else {
                                //Open Dialog for view adv.
                                SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
                                isOkWithAdv = prefs.getBoolean("articleViewChoice", false);
                                if (isOkWithAdv) {
                                    initRewardVideo(articlesBean.getId());
                                } else {
                                    showArticleOptionToWatchDialog(articlesBean.getId());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.linearLike.setOnClickListener(view -> {
            try {
                if (!sp.getBoolean("guest_entry", false)) {
                    if (!articlesBean.isIs_like()) {
                        play(R.raw.like_click_sound);
                        binding.tvLikes.setText(Utils.convertNumberToCount(articlesBean.getTotal_likes() + 1) + getResources().getString(R.string.likes));
                    } else {
                        binding.tvLikes.setText(Utils.convertNumberToCount(articlesBean.getTotal_likes() - 1) + getResources().getString(R.string.likes));
                    }

                    binding.likeIcon.setChecked(!articlesBean.isIs_like());
                    binding.linearLike.setClickable(false);

                    if (!Utils.isNetworkAvailable(ArticleDetailActivity.this)) {
                        if (restored_Issubscribed) {
                            ArticleRoomDatabase articleRoomDatabase = ArticleRoomDatabase.getDatabase(ArticleDetailActivity.this);
                            int totalLike = 0;
                            boolean isLike = false;
                            if (articlesBean.isIs_like()) {
                                totalLike = articlesBean.getTotal_likes() - 1;
                                isLike = false;
                            } else {
                                isLike = true;
                                totalLike = articlesBean.getTotal_likes() + 1;
                            }

                            articlesBean.setTotal_likes(totalLike);
                            articlesBean.setIs_like(isLike);
                            binding.linearLike.setClickable(true);
                            articleRoomDatabase.articleDao().setLikes(totalLike, isLike, articlesBean.getId(), System.currentTimeMillis());
                            articleRoomDatabase.articleDao().setSynch(true, articlesBean.getId());
                        } else {
                            Utils.showToast(getString(R.string.no_internet_connection));
                        }
                    } else {
                        actionLikeArticle(!articlesBean.isIs_like());
                    }

                } else {
                    binding.linearLike.setClickable(true);
                    fromWhere = 1;
                    showLoginDialog(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            if (!restored_Issubscribed) {
                MobileAds.initialize(this, initializationStatus -> {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    if (mAdView != null) {
                        mAdView.loadAd(adRequest);
                        mAdView.setVisibility(View.VISIBLE);
                    }
                    if (mAdViewSecond != null) {
                        AdRequest adRequestSecond = new AdRequest.Builder().build();
                        mAdViewSecond.loadAd(adRequestSecond);
                        mAdViewSecond.setVisibility(View.VISIBLE);
                    }
                });
                initAppoDealNative();
            } else {
                if (mAdView != null) {
                    mAdView.setVisibility(View.GONE);
                }
                ConstraintLayout v = findViewById(R.id.nativeview);
                v.setVisibility(View.GONE);
                remove_ad_rl.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initAppoDealNative() {
        AdLoader adLoader = new AdLoader.Builder(this, getString(R.string.native_adv_ids))
                .forNativeAd(nativeAd -> {
                    // Show the ad.
                    showAd(nativeAd);
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                        ConstraintLayout v = findViewById(R.id.nativeview);
                        v.setVisibility(View.GONE);
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void showAd(NativeAd nativeAd) {
        if (nativeAd != null) {
            ConstraintLayout view = findViewById(R.id.nativeview);
            view.removeAllViews();
            NativeAdView nativeAdView = (NativeAdView) LayoutInflater.from(this).inflate(R.layout.include_native_adsarticaldettails, null, false);

            TextView tvTitle = nativeAdView.findViewById(R.id.tv_title);
            tvTitle.setText(nativeAd.getHeadline());
            nativeAdView.setHeadlineView(tvTitle);

            TextView tvDescription = nativeAdView.findViewById(R.id.tv_description);
            tvDescription.setText(nativeAd.getBody());
            nativeAdView.setBodyView(tvDescription);

            Button ctaButton = nativeAdView.findViewById(R.id.b_cta);
            ctaButton.setVisibility(View.VISIBLE);
            ctaButton.setText(nativeAd.getCallToAction());
            nativeAdView.setCallToActionView(ctaButton);
            MediaView nativeMediaView = nativeAdView.findViewById(R.id.appodeal_media_view_content);
            nativeAdView.setMediaView(nativeMediaView);
            RatingBar ratingBar = nativeAdView.findViewById(R.id.rb_rating);
            try {
                if (nativeAd.getStarRating() > 0) {
                    ratingBar.setRating(nativeAd.getStarRating().floatValue());
                    ratingBar.setStepSize(0.1f);
                    ratingBar.setVisibility(View.VISIBLE);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            nativeAdView.setStarRatingView(ratingBar);
            nativeAdView.setNativeAd(nativeAd);
            nativeAdView.setVisibility(View.VISIBLE);
            view.addView(nativeAdView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setArticleData(final Articles articlesBean) {
        try {
            name = articlesBean.getTopic();
            binding.tvTitle.setText(name);
            binding.tvTitle.setTextSize(sp.getInt("textSize", 18) + 4);
            binding.tvTime.setReferenceTime(articlesBean.getCreated_at());
            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MRegular.ttf");
            binding.tvTitle.setTypeface(font);
            binding.tvTime.setTypeface(font);
            binding.webview.setVisibility(View.GONE);
            binding.pb.setVisibility(View.GONE);
            description = (articlesBean.getDescription());
            binding.maincollapsing.setTitle(articlesBean.getTopic());

            RequestOptions options = new RequestOptions();
            final RequestOptions placeholder_error = options.error(R.drawable.temo)
                    .placeholder(R.drawable.temo).diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(getApplicationContext())
                    .load(articlesBean.getPhoto())
                    .apply(placeholder_error)
                    .into(binding.ivicon);

            binding.ivToggle.setActivated(articlesBean.isIs_favourite());

            if (!sp.getBoolean("light", false)) {
                binding.tvDes.setTextColor(Color.parseColor("#000000"));
                binding.tvLikes.setTextColor(Color.parseColor("#000000"));
                binding.tvTime.setTextColor(getResources().getColor(R.color.grey3));
                binding.tvTitle.setTextColor(Color.parseColor("#000000"));
                binding.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                binding.adsRem.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.adsRem.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.border));
                binding.linear.setBackgroundColor(getResources().getColor(R.color.white));
                binding.shareCard.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                binding.webview.setBackgroundColor(getResources().getColor(R.color.white));
            } else {
                binding.tvDes.setTextColor(getResources().getColor(R.color.white));
                binding.tvLikes.setTextColor(getResources().getColor(R.color.white));
                binding.tvTime.setTextColor(getResources().getColor(R.color.white));
                binding.tvTitle.setTextColor(getResources().getColor(R.color.white));
                binding.cardView.setCardBackgroundColor(Color.parseColor("#363636"));
                binding.adsRem.setTextColor(getResources().getColor(R.color.white));
                binding.adsRem.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.border_white));
                binding.linear.setBackgroundColor(Color.parseColor("#464646"));
                binding.shareCard.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                binding.webview.setBackgroundColor(Color.parseColor("#363636"));
            }

            if (articlesBean.isArticle_is_locked()) {
                if (restored_Issubscribed) {
                    binding.btnReadMore.setVisibility(View.GONE);
                    updateWatchedVideoStatus(articlesBean.getId(), false);
                    getFullWebsite(articlesBean.getDescription());
                } else if (!articlesBean.isUser_article_is_locked()) {
                    binding.btnReadMore.setVisibility(View.GONE);
                    getFullWebsite(articlesBean.getDescription());
                } else {
                    binding.btnReadMore.setVisibility(View.VISIBLE);
                    binding.btnReadMore.setClickable(true);
                    getWebsite(articlesBean.getDescription());
                }
            } else {
                getFullWebsite(articlesBean.getDescription());
            }

            binding.ivToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!sp.getBoolean("guest_entry", false)) {
                        binding.ivToggle.setClickable(false);
                        binding.ivToggle.setActivated(!binding.ivToggle.isActivated());
                        if (restored_Issubscribed) {
                            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(ArticleDetailActivity.this);
                            db.articleDao().setBookMark(binding.ivToggle.isActivated(), articlesBean.getId(), System.currentTimeMillis());
                            articlesBean.setIs_favourite(binding.ivToggle.isActivated());
                            db.articleDao().setSynch(true, articlesBean.getId());
                            binding.ivToggle.setClickable(true);

                            if (Utils.isNetworkAvailable(ArticleDetailActivity.this)) {
                                actionFavArticle();
                            } else {
                                if (binding.ivToggle.isActivated()) {
                                    Toast.makeText(ArticleDetailActivity.this, "Added to bookmarks", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ArticleDetailActivity.this, "Removed from bookmarks", Toast.LENGTH_LONG).show();
                                }
                            }

                        } else {
                            actionFavArticle();
                        }

                    } else {
                        binding.ivToggle.setClickable(true);
                        fromWhere = 2;
                        showLoginDialog(2);
                    }
                }
            });

            binding.likeIcon.setChecked(articlesBean.isIs_like());
            binding.tvLikes.setText(Utils.convertNumberToCount(articlesBean.getTotal_likes()) + getResources().getString(R.string.likes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(int rid) {
        try {
            stop();
            mMediaPlayer = MediaPlayer.create(ArticleDetailActivity.this, rid);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                }
            });
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("On backpressed", "Called");
        if (Constants.IS_FROM_NOTIFICATION_ACT && Constants.RELATED_ARTICLE_ACTIVITY_INSTANCE_COUNT == 0) {

            Constants.IS_FROM_NOTIFICATION_ACT = false;
            Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }
        if (getIntent().hasExtra(Constants.ARTICLE_ID)) {
            if (getIntent().hasExtra(Constants.FROM) && getIntent().getStringExtra(Constants.FROM).equalsIgnoreCase(Constants.FROM_NOTIFICATION)) {
                Intent intent = new Intent(ArticleDetailActivity.this, MainActivity.class);
                // Get the component name of the nested intent.
                ComponentName name = intent.resolveActivity(getPackageManager());
                // Check that the package name and class name contain the expected values.
                if (name.getPackageName().equals("com.mayur.personalitydevelopment")
//                        && name.getClassName().equals("com.mayur.personalitydevelopment.activity.ArticleDetailActivity")
                ) {
                    // Redirect the nested intent.
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            } else {

                Constants.RELATED_ARTICLE_ACTIVITY_INSTANCE_COUNT--;
                finish();
            }
        } else {
            try {
                Intent intent = getIntent();
                intent.putExtra("data", new Gson().toJson(articlesBean));
                intent.putExtra("IS_FROM", isFrom);
                // Get the component name of the nested intent.
                ComponentName name = intent.resolveActivity(getPackageManager());
                // Check that the package name and class name contain the expected values.
                if (name.getPackageName().equals("com.mayur.personalitydevelopment") &&
                        name.getClassName().equals("com.mayur.personalitydevelopment.activity.ArticleDetailActivity")) {
                    // Redirect the nested intent.
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getRelatedArticlesList(final String articleID) {
        try {
            String authToken = "";
            if (Constants.getUserData(ArticleDetailActivity.this) != null) {
                authToken = Constants.getUserData(ArticleDetailActivity.this).getAuthentication_token();
            }

            SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);

            Log.e("authToken: ", authToken + " token");

            connectPost(ArticleDetailActivity.this, null, getRelatedArticle(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), articleID + "", Constants.getV6Value(), Utils.getArticleLang(this)),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                ArrayList<RelatedArticlesRequestResponse.Article> articles = new ArrayList<>();
                                JSONArray jsonObjects = jsonObject.getJSONArray("articles");

                                for (int i = 0; i < jsonObjects.length(); i++) {
                                    RelatedArticlesRequestResponse.Article article = new RelatedArticlesRequestResponse().new Article();
                                    JSONObject jsonObjectData = jsonObjects.getJSONObject(i);
                                    article.setId(jsonObjectData.getInt("id"));
                                    article.setPhoto(jsonObjectData.getString("photo"));
                                    article.setTopic(jsonObjectData.getString("topic"));
                                    articles.add(article);
                                }

                                if (articles != null && !articles.isEmpty()) {
                                    binding.rvRelatedArticle.setVisibility(View.VISIBLE);
                                    relatedArticleList.addAll(articles);
                                    setDataForRelatedArticle();
                                } else {
                                    binding.rvRelatedArticle.setVisibility(View.GONE);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                            //Toast.makeText(ArticleDetailActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Headers headers) {
                            Toast.makeText(ArticleDetailActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onConnectionFailure() {
                            //Toast.makeText(ArticleDetailActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onException(Headers headers, int StatusCode) {
                            Log.e("onException: ", headers.toString() + "");
                            Toast.makeText(ArticleDetailActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDataForRelatedArticle() {
        relatedArticleListAdapter = new RelatedArticleListAdapter(relatedArticleList, ArticleDetailActivity.this);
        binding.rvRelatedArticle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvRelatedArticle.setAdapter(relatedArticleListAdapter);
    }

    public void onRelatedArticleClick(int position, RelatedArticlesRequestResponse.Article article) {
        Log.i(TAG, "onRelatedArticleClick: ");
        Intent intent = new Intent(ArticleDetailActivity.this, ArticleDetailActivity.class);
        intent.putExtra(Constants.ARTICLE_ID, article.getId() + "");
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void getWebsite(final String html) {
        try {
            temphtmlFullString = html;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final StringBuilder builder = new StringBuilder();

                    try {
                        Document doc = Jsoup.parse(html.replace("<center>", "").replace("</center>", ""));
                        String title = doc.title();
                        Elements links = doc.getAllElements();

                        for (int i = 0; i < links.size(); i++) {
                            Element link = links.get(i);
                            if (link.tagName().equals("#root") || link.tagName().equals("html") || link.tagName().equals("body")
                                    || link.tagName().equals("head") || link.tagName().equals("strong") || link.tagName().equals("a")
                                    || link.tagName().equals("em")) {

                            } else if (link.tagName().equals("img")) {
                                datahtmls.add(new DATAHTML("image", link.attr("src")));
                                if (isFound) {
                                    Log.i(TAG, "run: POSITION 1" + i + "  " + link.tagName());
                                    isFound = false;
                                    temphtmlSortString = html;
                                    tempDatahtmls = datahtmls;
                                    break;
                                }
                            } else {
                                if (link.html().contains("<img")) {
                                } else {
                                    datahtmls.add(new DATAHTML("textview",/*"<"+link.tagName()+">"+*/link.html()/*+"</"+link.tagName()+">"*/));
                                }
                            }
                        }
                    } catch (Exception e) {
                        builder.append("Error : ").append(e.getMessage()).append("\n");
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initSortAdapter();
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFullWebsite(final String html) {
        try {
            datahtmls.clear();
            binding.btnReadMore.setVisibility(View.GONE);
            temphtmlFullString = html;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final StringBuilder builder = new StringBuilder();

                    try {
                        Document doc = Jsoup.parse(html.replace("<center>", "").replace("</center>", ""));
                        String title = doc.title();
                        Elements links = doc.getAllElements();

                        for (int i = 0; i < links.size(); i++) {
                            Element link = links.get(i);
                            if (link.tagName().equals("#root") || link.tagName().equals("html") || link.tagName().equals("body")
                                    || link.tagName().equals("head") || link.tagName().equals("strong") || link.tagName().equals("a")
                                    || link.tagName().equals("em")) {

                            } else if (link.tagName().equals("img")) {
                                datahtmls.add(new DATAHTML("image", link.attr("src")));
                            } else {
                                if (link.html().contains("<img")) {
                                } else {
                                    datahtmls.add(new DATAHTML("textview",/*"<"+link.tagName()+">"+*/link.html()/*+"</"+link.tagName()+">"*/));
                                }
                            }
                        }
                    } catch (Exception e) {
                        builder.append("Error : ").append(e.getMessage()).append("\n");
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initFullAdapter();
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showArticleOptionToWatchDialog(final int articleID) {
        try {
            final Dialog dialog = new Dialog(ArticleDetailActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_rectangle_white_big_no_stroke));
            dialog.setContentView(R.layout.dialog_premium_dialog);
            dialog.setCanceledOnTouchOutside(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = getWindow().getWindowManager().getDefaultDisplay().getWidth() * 85 / 100;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);
            CheckBox ck_select_default_choice = dialog.findViewById(R.id.ck_select_default_choice);
            TextView btn_subscribe = dialog.findViewById(R.id.btn_subscribe);
            TextView btn_watch_ad = dialog.findViewById(R.id.btn_watch_ad);
            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MRegular.ttf");
            TextView txt_titile = dialog.findViewById(R.id.txt_titile);
            TextView txt_content = dialog.findViewById(R.id.txt_content);
            TextView txt_ck_content = dialog.findViewById(R.id.txt_ck_content);
            txt_titile.setTypeface(font);
            txt_content.setTypeface(font);
            txt_ck_content.setTypeface(font);
            ck_select_default_choice.setOnCheckedChangeListener((compoundButton, b) -> {
                SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("articleViewChoice", b);
                editor.commit();
            });

            btn_subscribe.setOnClickListener(view -> {
                dialog.dismiss();
                startActivity(new Intent(ArticleDetailActivity.this, RemoveAdActivity.class));
            });

            btn_watch_ad.setOnClickListener(view -> {
                dialog.dismiss();
                initRewardVideo(articleID);
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initRewardVideo(int articleID) {
        Utils.showDialog(this);
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, getString(R.string.reward_adv_ids),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                        Utils.hideDialog();
                        /*Handler handler = new Handler();
                        handler.postDelayed(() -> updateWatchedVideoStatus(articleID, true), 250);*/
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                        Utils.hideDialog();
                        if (rewardedAd != null) {
                            rewardedAd.show(ArticleDetailActivity.this, new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    // Handle the reward.
                                    Log.d(TAG, "The user earned the reward.");
                                   if(sp.getBoolean("guest_entry", false)){
                                        getFullWebsite(articlesBean.getDescription());
                                    }else {
                                       Handler handler = new Handler();
                                       handler.postDelayed(() -> updateWatchedVideoStatus(articleID, true), 250);
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "The rewarded ad wasn't ready yet.");
                        }

                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                /*Handler handler = new Handler();
                                handler.postDelayed(() -> updateWatchedVideoStatus(articleID, true), 250);*/
                                rewardedAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                rewardedAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });
                    }
                });

    }

    public void updateWatchedVideoStatus(final int articleID, final boolean isFromVideoComplete) {
        try {
            String authToken = "";
            if (Constants.getUserData(ArticleDetailActivity.this) != null) {
                authToken = Constants.getUserData(ArticleDetailActivity.this).getAuthentication_token();
            }

            Log.e("authToken: ", authToken + " token");

            connectPost(ArticleDetailActivity.this, null, unlockArticle(BaseActivity.getKYC(), authToken, false, Constants.getV6Value(), articleID, true),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                            // callArticleIntent();
                            Handler h = new Handler();
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //TODO need to update status.
                                    if (isFromVideoComplete && !restored_Issubscribed) {
                                        articlesBean.setUser_article_is_locked(false);
                                        getFullWebsite(articlesBean.getDescription());
                                    } else if (restored_Issubscribed) {
                                        articlesBean.setUser_article_is_locked(false);
                                    }
                                }
                            }, 150);
                        }

                        @Override
                        public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                            //Toast.makeText(ArticleDetailActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Headers headers) {
                            Toast.makeText(ArticleDetailActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onConnectionFailure() {
                            //Toast.makeText(ArticleDetailActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onException(Headers headers, int StatusCode) {
                            Log.e("onException: ", headers.toString() + "");
                            Toast.makeText(ArticleDetailActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initSortAdapter() {
        try {
            final TextViewHolder textViewHolder = new TextViewHolder();
            final ImageViewHolder imageViewHolder = new ImageViewHolder();

            adapter = new CustomAdapter(new CustomAdapter.AdapterListener() {
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    if (viewType == 0) {
                        imageViewHolder.setItemBinding(getApplicationContext(), parent);
                        return imageViewHolder.getHolder();

                    } else {

                        textViewHolder.setItemBinding(getApplicationContext(), parent);
                        return textViewHolder.getHolder();
                    }

                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

                    final DATAHTML datahtml = tempDatahtmls.get(position);

                    if (datahtml.type.equals("image")) {

                        final ImageViewHolder.MyHolder myHolder = imageViewHolder.castHolder(holder);
                        RequestOptions options = new RequestOptions();

                        final RequestOptions placeholder_error = options.error(R.drawable.temo)
                                .placeholder(R.drawable.temo).diskCacheStrategy(DiskCacheStrategy.ALL);

                        Glide.with(ArticleDetailActivity.this)
                                .load(datahtml.getValue())
                                .apply(placeholder_error).into(myHolder.image);

                    } else {

                        final TextViewHolder.MyHolder myHolder = textViewHolder.castHolder(holder);

                        myHolder.textView.setText(Html.fromHtml(datahtml.getValue()));

                        Log.e("HTMLMLML: ", datahtml.getValue());

                        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MRegular.ttf");

                        myHolder.textView.setTypeface(font);
                        myHolder.textView.setTextSize(sp.getInt("textSize", 18));
                        myHolder.textView.setOnClickListener(view -> {
                            if (datahtml.getValue().contains("\"")) {
                                String tempString = datahtml.getValue().substring(datahtml.getValue().indexOf("\""));
                                String urlString = tempString.substring(1, tempString.indexOf(">")).replace("\"", "");
                                Log.e("HTML URL: ", urlString);

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(urlString));
                                startActivity(i);
                            }
                        });

                        if (!sp.getBoolean("light", false)) {
                            myHolder.textView.setTextColor(Color.parseColor("#000000"));
                            myHolder.textView2.setTextColor(Color.parseColor("#000000"));
                        } else {
                            myHolder.textView.setTextColor(getResources().getColor(R.color.white));
                            myHolder.textView2.setTextColor(getResources().getColor(R.color.white));
                        }

                    }

                }

                @Override
                public int getItemCount() {
                    return tempDatahtmls.size();
                }

                @Override
                public int getItemViewType(int position) {

                    if (tempDatahtmls.get(position).type.equals("image")) {
                        return 0;
                    } else {
                        return 1;
                    }

                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

            });

            binding.rv.setNestedScrollingEnabled(false);
            binding.rv.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initFullAdapter() {

        try {
            final TextViewHolder textViewHolder = new TextViewHolder();
            final TextView2Holder textView2Holder = new TextView2Holder();
            final ImageViewHolder imageViewHolder = new ImageViewHolder();

            adapter = new CustomAdapter(new CustomAdapter.AdapterListener() {
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                    if (viewType == 0) {
                        imageViewHolder.setItemBinding(getApplicationContext(), parent);
                        return imageViewHolder.getHolder();

                    } else {

                        textViewHolder.setItemBinding(getApplicationContext(), parent);
                        return textViewHolder.getHolder();
                    }

                }

                String[] linktyp = {"Play & Win Coins Daily.",
                        "Come play Cricket contests running for 50,000 Coins daily.",
                        " Play Quiz & Win Upto 50,000 Coins",
                        "No install required! Play Now",
                        "Quiz for 15,000 Coins calling you!📞",
                        "Play Qureka GK quiz to win now",
                        "Pool prize is 50,000 coins",
                        "Sharpen your Cricket knowledge & win now 📖📚",
                        "Tap here & grab your share of 15,000 coins now! 😍",
                        "Play GK quiz, increase your knowledge & WIN 📚",
                        "Aaj Math Quiz khela kya? \nPlay and win coins now",
                        "Who was the first president of India?",
                        "Play History Quiz & win coins now",
                        "Time to Win Now!",
                        "Play Bollywood Quizzes  & win coins daily",
                        "Innings for 50,000 coins",
                        "IPL Quiz Khelo aur Jeeto coins. No install required.",
                        "Prize Pool: 50,000 Coins| No install required",
                        "Play IPL contest! Khelo aur jeeto.", "Jeeto 10,000 Coins",
                        "Show your Math knowledge & Win Now",
                        "Tech quiz for 50,000 coins open",
                        "Test your tech skills & win now",
                        "Mega quiz for 5,00,000 coins live",
                        "Play now & win some coins. No install required.",
                        "Mega quiz for 5,00,000 coins open",
                        "Your chance of winning is high here! Play Now",
                        "Jeeto 10,000 Coins Abhi!",
                        "Play GK, Math & other quizzes & Win Now",
                        "Learn and Win daily.",
                        " SSC, Bank PO clear karna hai? Exam ki tyaari karo aur jeeto.",
                        "Time to Win Now!",
                        "Play SSC quiz, increase your knowledge & WIN",
                        "Are You Ready to learn & Win?",
                        "A Special Bank PO & Clerk Quiz for Exams will help you learn more!",
                        "SSC Exam Quiz for 50,000 Coins is Live",
                        "Play Quiz & Win Now",
                        "Play SSC Exam Quiz & Win Upto 50,000 Coins",
                        "Play Quiz & Win Now",
                        "Bank PO/Clerk Exam Quiz for 50,000 Coins is Live Play Quiz & Win Now",
                        "Play Bank PO/Clerk Exam Quiz & Win Upto 50,000 Coins",
                        "Play Quiz & Win Now 10+2 Exam Quiz for 50,000 Coins is Live",
                        "Play Quiz & Win Now",
                        "Play 10+2 Exam Quiz & Win Upto 50,000 Coins",
                        "Play Quiz & Win Now"};
                int cts = 0;
                int tot = 1;

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    final DATAHTML datahtml = datahtmls.get(position);
                    int arrsize = linktyp.length;
                  /*  for (int i=0;i<datahtmls.get(position).getValue().length();i++){
                        if (datahtml.type.equals("image")) {
                            cts++;
                            System.out.println("ctss/////"+cts);
                        }
                    }*/
                    if (datahtml.type.equals("image")) {
                        cts++;
                        System.out.println("imgc/////" + cts);
                        final ImageViewHolder.MyHolder myHolder = imageViewHolder.castHolder(holder);
                        RequestOptions options = new RequestOptions();

                        final RequestOptions placeholder_error = options.error(R.drawable.temo)
                                .placeholder(R.drawable.temo).diskCacheStrategy(DiskCacheStrategy.ALL);

                        Glide.with(ArticleDetailActivity.this).load(datahtml.getValue()).apply(placeholder_error).into(myHolder.image);

                    } else {
                        // int random = ThreadLocalRandom.current().nextInt(0, cts);
                        int textind = ThreadLocalRandom.current().nextInt(0, arrsize);
                        final TextViewHolder.MyHolder myHolder = textViewHolder.castHolder(holder);
                        //  System.out.println("inside if////random//"+random);
                        if (!restored_Issubscribed) {
                            if (cts == 2) {
                                cts++;
                                myHolder.textView2.setVisibility(View.VISIBLE);
                                myHolder.textView2.setPaintFlags(myHolder.textView2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                System.out.println("inside if////afterimg" + cts);
                                myHolder.textView2.setText(linktyp[textind]);
                                myHolder.textView2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
                                        CustomTabActivityHelper.openCustomTab(
                                                ArticleDetailActivity.this,// activity
                                                customTabsIntent,
                                                Uri.parse("https://391.live.qureka.co/intro/question"),
                                                new WebviewFallback()
                                        );
                                    }
                                });
                            }
                        }
                        //cts=0;
                        myHolder.textView.setText(Html.fromHtml(datahtml.getValue()));
                        Log.e("HTMLMLML: ", datahtml.getValue());

                        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MRegular.ttf");

                        myHolder.textView.setTypeface(font);
                        myHolder.textView.setTextSize(sp.getInt("textSize", 18));
                        myHolder.textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (datahtml.getValue().contains("\"")) {
                                    String tempString = datahtml.getValue().substring(datahtml.getValue().indexOf("\""));
                                    String urlString = tempString.substring(1, tempString.indexOf(">")).replace("\"", "");
                                    Log.e("HTML URL: ", urlString);

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(urlString));
                                    startActivity(i);
                                }
                            }
                        });

                        if (!sp.getBoolean("light", false)) {
                            myHolder.textView.setTextColor(Color.parseColor("#000000"));
                            myHolder.textView2.setTextColor(Color.parseColor("#000000"));
                        } else {
                            myHolder.textView.setTextColor(getResources().getColor(R.color.white));
                            myHolder.textView2.setTextColor(getResources().getColor(R.color.white));
                        }

                    }

                }

                @Override
                public int getItemCount() {
                    return datahtmls.size();
                }

                @Override
                public int getItemViewType(int position) {

                    if (datahtmls.get(position).type.equals("image")) {
                        return 0;
                    } else {
                        return 1;
                    }

                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

            });

            binding.rv.setNestedScrollingEnabled(false);
            binding.rv.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Premium_fun(View v) {
        try {
            boolean isAvailable = Utils.isIabServiceAvailable(this);
            if (isAvailable) {
                Intent purchase = new Intent(ArticleDetailActivity.this, RemoveAdActivity.class);
                startActivity(purchase);
            } else {
                Toast.makeText(ArticleDetailActivity.this, "In-App Subscription not supported", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Share(View v) {
        try {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            description = getFirst10Words(description);
            description = description.replaceAll("\\<[^>]*>", "").replaceAll("&nbsp;", "").replaceAll("&rdquo;", "").replaceAll("&rsquo;", "").replaceAll("&lsquo;", "").replaceAll("&", "").replaceAll("ldquo;", "").replaceAll("&#39;", "");
            String shareBody = name + "\n" + description + "\nFor more download Personality Development(BestifyMe) application: " +
                    "https://play.google.com/store/apps/details?id=com.mayur.personalitydevelopment";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, name);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFirst10Words(String arg) {
        Pattern pattern = Pattern.compile("([\\S]+\\s*){1,300}");
        Matcher matcher = pattern.matcher(arg);
        matcher.find();
        return matcher.group();
    }

    public void Email(View v) {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Personality Development App");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi Mayur!\nHere are few suggestions/complaints/feature request about the BestifyMe app:");
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public void actionFavArticle() {
        try {
            String authToken = "";
            if (Constants.getUserData(ArticleDetailActivity.this) != null) {
                authToken = Constants.getUserData(ArticleDetailActivity.this).getAuthentication_token();
            }

            connectPost(ArticleDetailActivity.this, null, favArticle(BaseActivity.getKYC(),
                            authToken,
                            sp.getBoolean("guest_entry", false),
                            Constants.getV6Value(),
                            articlesBean.getId(),
                            binding.ivToggle.isActivated()),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                            articlesBean.setIs_favourite(binding.ivToggle.isActivated());
                            binding.ivToggle.setClickable(true);
                            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(ArticleDetailActivity.this);
                            db.articleDao().setBookMark(binding.ivToggle.isActivated(), articlesBean.getId(), System.currentTimeMillis());
                            db.articleDao().setSynch(true, articlesBean.getId());
                            if (binding.ivToggle.isActivated()) {
                                Toast.makeText(ArticleDetailActivity.this, "Added to bookmarks", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ArticleDetailActivity.this, "Removed from bookmarks", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                            binding.ivToggle.setActivated(!binding.ivToggle.isActivated());
                            binding.ivToggle.setClickable(true);
                        }

                        @Override
                        public void onFailure(Headers headers) {
                            binding.ivToggle.setActivated(!binding.ivToggle.isActivated());
                            binding.ivToggle.setClickable(true);
                        }

                        @Override
                        public void onConnectionFailure() {
                            binding.ivToggle.setActivated(!binding.ivToggle.isActivated());
                            binding.ivToggle.setClickable(true);
                        }

                        @Override
                        public void onException(Headers headers, int StatusCode) {
                            binding.ivToggle.setActivated(!binding.ivToggle.isActivated());
                            binding.ivToggle.setClickable(true);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionLikeArticle(final boolean status) {

        try {
            String authToken = "";
            if (Constants.getUserData(ArticleDetailActivity.this) != null) {
                authToken = Constants.getUserData(ArticleDetailActivity.this).getAuthentication_token();
            }

            connectPost(ArticleDetailActivity.this, null, likeArticle(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), articlesBean.getId(), status),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                            binding.linearLike.setClickable(true);
                            articlesBean.setIs_like(status);
                            ArticleRoomDatabase articleRoomDatabase = ArticleRoomDatabase.getDatabase(ArticleDetailActivity.this);

                            if (status) {
                                articleRoomDatabase.articleDao().setLikes(articlesBean.getTotal_likes() + 1, status, articlesBean.getId(), System.currentTimeMillis());
                                articleRoomDatabase.articleDao().setSynch(true, articlesBean.getId());
                                articlesBean.setTotal_likes(articlesBean.getTotal_likes() + 1);
                                Utils.likeCounter++;
                                if (Utils.likeCounter == 4) {
                                    showSharePopUp();
                                } else if (Utils.likeCounter == 10) {
                                    showSharePopUp();
                                }
                            } else {
                                articleRoomDatabase.articleDao().setLikes(articlesBean.getTotal_likes() - 1, status, articlesBean.getId(), System.currentTimeMillis());
                                articleRoomDatabase.articleDao().setSynch(true, articlesBean.getId());
                                articlesBean.setTotal_likes(articlesBean.getTotal_likes() - 1);
                            }
                        }

                        @Override
                        public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                            binding.linearLike.setClickable(true);
                            binding.likeIcon.setChecked(articlesBean.isIs_like());
                            binding.tvLikes.setClickable(true);
                            binding.tvLikes.setText(Utils.convertNumberToCount((articlesBean.getTotal_likes())) + getResources().getString(R.string.likes));
                        }

                        @Override
                        public void onFailure(Headers headers) {
                            binding.linearLike.setClickable(true);
                            binding.likeIcon.setChecked(articlesBean.isIs_like());
                            binding.tvLikes.setText(Utils.convertNumberToCount((articlesBean.getTotal_likes())) + getResources().getString(R.string.likes));
                            binding.tvLikes.setClickable(true);
                        }

                        @Override
                        public void onConnectionFailure() {
                            binding.linearLike.setClickable(true);
                            binding.likeIcon.setChecked(articlesBean.isIs_like());
                            binding.tvLikes.setText(Utils.convertNumberToCount((articlesBean.getTotal_likes())) + getResources().getString(R.string.likes));
                            binding.tvLikes.setClickable(true);
                        }

                        @Override
                        public void onException(Headers headers, int StatusCode) {
                            binding.linearLike.setClickable(true);
                            binding.likeIcon.setChecked(articlesBean.isIs_like());
                            binding.tvLikes.setText(Utils.convertNumberToCount((articlesBean.getTotal_likes())) + getResources().getString(R.string.likes));
                            binding.tvLikes.setClickable(true);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSharePopUp() {
        final LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_share_app, null);

        TextView txtShare = promptsView.findViewById(R.id.txtShare);
        ImageView imgPhoto = promptsView.findViewById(R.id.imgPhoto);

        if (Build.VERSION.SDK_INT >= 28) {
            imgPhoto.setScaleType(ImageView.ScaleType.MATRIX);
        } else {
            imgPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "A guide for all those who seek improvements in their personality and willing to accept the change in them according to modern world. " +
                        "This app will help you to enhance your personality with some unique tips along with expert advice   " +
                        "Android app: http://bit.ly/pd_app, " +
                        "IOS app: http://bit.ly/pd_ios_app";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Personality Development");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
        alertDialog.show();
    }

    private void showLoginDialog(final int isFrom) { // LIKE = 1 , FAVORITE = 2 , READ MORE.
        try {
            final Dialog dialog = new Dialog(ArticleDetailActivity.this);

            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_custom_login_2);

            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_rectangle_white_big_no_stroke));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = getWindow().getWindowManager().getDefaultDisplay().getWidth() * 85 / 100;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            dialog.getWindow().setAttributes(lp);

            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MRegular.ttf");
            TextView title = dialog.findViewById(R.id.title);
            TextView msg = dialog.findViewById(R.id.msg);

            title.setTypeface(font);
            msg.setTypeface(font);

            ImageView fb = dialog.findViewById(R.id.facebook);
            ImageView google = dialog.findViewById(R.id.google);

            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    onFacebook(isFrom);
                }
            });

            google.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    isLoginFrom = isFrom;

                    googleSignInClient.signOut();
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, Constants.LOGIN_TYPE.GOOGLE);

                    //Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    //startActivityForResult(signInIntent, Constants.LOGIN_TYPE.GOOGLE);
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    void init() {
        try {
            prepareGoogle();
            FacebookSdk.sdkInitialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void onFacebook(final int isFrom) { // LIKE = 1 , FAVORITE = 2 , READ MORE.
        try {
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile, email"));

            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {

                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            try {
                                JSONObject fbResponse = new JSONObject(String.valueOf(response.getJSONObject()));
                                if (fbResponse.getString("email") != null && !fbResponse.getString("email").equals("")) {
                                    Map<String, Object> params = new HashMap<>();
                                    params.put("email", fbResponse.getString("email").trim());
                                    params.put("first_name", fbResponse.getString("name").split("\\s+")[0].trim());
                                    params.put("last_name", fbResponse.getString("name").split("\\s+")[1].trim());
                                    params.put("user_profile_photo", "https://graph.facebook.com/" + fbResponse.getString("id") + "/picture?type=large".trim());
                                    params.put("social_id", fbResponse.getString("id"));
                                    params.put("login_type", Constants.LOGIN_TYPE.FACEBOOK);
                                    onSignin(params, isFrom); // LIKE = 1 , FAVORITE = 2 , READ MORE.
                                }

                                LoginManager.getInstance().logOut();

                            } catch (JSONException e) {
                                if (e != null) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id, name, email, link");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException e) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void onSignin(Map<String, Object> params, final int isFromWhere) { // LIKE = 1 , FAVORITE = 2 , READ MORE.
        try {
            Utils.showDialog(this);
            params.put("platform", "android");
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            String token = sp.getString("FCM_TOKEN", "");
            if (token != null && token.length() > 0) {
                params.put("device_token", token);
            } else {
                params.put("device_token", "test");
            }

            //params.put("device_token", FirebaseInstanceId.getInstance().getToken());
            params.put("uuid", sp.getString("UUID", ""));

            connectPost(this, null, signIn(params), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                    editor.putBoolean("guest_entry", false);
                    editor.commit();

                    Constants.setUserData(ArticleDetailActivity.this, response);
                    initializeBilling();
                    updateToken();
                    displayMessage(getString(R.string.msg_logged_in));

                    if (isFromWhere == 1) { // LIKE = 1 , FAVORITE = 2 , READ MORE.
                        play(R.raw.like_click_sound);
                        binding.tvLikes.setText((articlesBean.getTotal_likes() + 1) + getResources().getString(R.string.likes));
                        binding.likeIcon.setChecked(!articlesBean.isIs_like());
                        binding.linearLike.setClickable(false);
                        actionLikeArticle(!articlesBean.isIs_like());
                    } else if (isFromWhere == 2) {
                        binding.ivToggle.setClickable(false);
                        binding.ivToggle.setActivated(!binding.ivToggle.isActivated());
                        actionFavArticle();
                    } else if (isFromWhere == 3) { // TODO NEED TO SHOW ADVERTISEMENT THEN TO READ MORE ACTIVE
                        SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
                        isOkWithAdv = prefs.getBoolean("articleViewChoice", false);
                        if (isOkWithAdv) {
                            //initRewardVideo(true, articlesBean.getId());
                        } else {
                            showArticleOptionToWatchDialog(articlesBean.getId());
                        }
                        //getFullWebsite(articlesBean.getDescription());
                    }
                    if (isFrom == 1) {
                        Tab1.isFromLogin = true;
                    } else if (isFrom == 2) {
                        FilterResultActivity.isFromLogin = true;
                    } else if (isFrom == 3) {
                        SearchActivity.isFromLogin = true;
                    }

                    Utils.hideDialog();
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    //Toast.makeText(getApplicationContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    Toast.makeText(getApplicationContext(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(getApplicationContext(), "EE Failure", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == Constants.LOGIN_TYPE.GOOGLE) {

               /* GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if (result.isSuccess()) {

                    GoogleSignInAccount acct = result.getSignInAccount();

                    if (acct.getEmail() == null && acct.getEmail().trim().isEmpty()) {
                        googleApiClient.clearDefaultAccountAndReconnect();

                        Toast.makeText(ArticleDetailActivity.this, "null", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Map<String, Object> params = new HashMap<>();
                    params.put("email", acct.getEmail().trim());
                    params.put("first_name", acct.getDisplayName().split("\\s+")[0].trim());
                    params.put("last_name", acct.getDisplayName().split("\\s+")[1].trim());
                    params.put("user_profile_photo", String.valueOf(acct.getPhotoUrl()).trim());
                    params.put("social_id", acct.getId());
                    params.put("login_type", Constants.LOGIN_TYPE.GOOGLE);
                    googleApiClient.clearDefaultAccountAndReconnect();
                    onSignin(params,isLoginFrom);
                }*/

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount acct = task.getResult(ApiException.class);

                // GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                //GoogleSignInAccount acct = result.getSignInAccount();

                if (acct.getEmail() == null && acct.getEmail().trim().isEmpty()) {
                    Toast.makeText(ArticleDetailActivity.this, "null", Toast.LENGTH_LONG).show();
                    return;
                }

                Map<String, Object> params = new HashMap<>();
                params.put("email", acct.getEmail().trim());
                params.put("first_name", acct.getDisplayName().split("\\s+")[0].trim());
                params.put("last_name", acct.getDisplayName().split("\\s+")[1].trim());
                if (acct.getPhotoUrl() != null && String.valueOf(acct.getPhotoUrl()).trim().length() > 0) {
                    params.put("user_profile_photo", String.valueOf(acct.getPhotoUrl()).trim());
                } else {
                    params.put("user_profile_photo", "");
                }
                params.put("social_id", acct.getId());
                params.put("login_type", Constants.LOGIN_TYPE.GOOGLE);

                onSignin(params, isLoginFrom);

            } else if (FacebookSdk.isFacebookRequestCode(requestCode)) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.somehing_want_wrong), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    void prepareGoogle() {
       /* GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();*/

        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(ArticleDetailActivity.this, gso);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void callArticleDetail(String articleID) {
        try {
            isLoading = true;

            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, ApiCallBack.articleDetail(BaseActivity.getKYC(),
                            authToken,
                            sp.getBoolean("guest_entry", false),
                            Constants.getV6Value(),
                            articleID,
                            1),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                            ArticleDetailResponse.ArticleDetailData articleDetailData = new Gson().fromJson(response, ArticleDetailResponse.ArticleDetailData.class);
                            isLoading = false;
                            Utils.hideDialog();
                            articlesBean = articleDetailData.getArticles();
                            setArticleData(articlesBean);
                        }

                        @Override
                        public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                            isLoading = false;
                            Utils.hideDialog();
                        }

                        @Override
                        public void onFailure(Headers headers) {
                            try {
                                isLoading = false;
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
                                isLoading = false;
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
                                isLoading = false;
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
