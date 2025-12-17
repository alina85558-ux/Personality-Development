package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.importFavArticles;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.signIn;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.updateArticleLang;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.updateArticleLangGuest;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.QueryPurchasesParams;
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
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.CategoriesListAdapter;
import com.mayur.personalitydevelopment.app.PersonalityDevelopmentApp;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.connection.ConnectivityReceiver;
import com.mayur.personalitydevelopment.database.Article;
import com.mayur.personalitydevelopment.database.ArticleRoomDatabase;
import com.mayur.personalitydevelopment.database.Category;
import com.mayur.personalitydevelopment.databinding.ActivityMainBinding;
import com.mayur.personalitydevelopment.fragment.CourseListingFragment;
import com.mayur.personalitydevelopment.fragment.NoInternetConnectionFragment;
import com.mayur.personalitydevelopment.fragment.PostFragment;
import com.mayur.personalitydevelopment.fragment.QuotesFragment;
import com.mayur.personalitydevelopment.fragment.Tab1;
import com.mayur.personalitydevelopment.models.CategoriesData;
import com.mayur.personalitydevelopment.models.MusicItem;
import com.mayur.personalitydevelopment.models.SubscriptionResponse;
import com.mayur.personalitydevelopment.models.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import okhttp3.Headers;
import okhttp3.ResponseBody;

public class MainActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener,
        NoInternetConnectionFragment.OnInterNetConnectionListener {

    //    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG = "MainActivity TAG";
    //    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg2RciyklkPme5MJ4IZUa0/XhQdZvThkJVnLWQib4AHWeBRN9UKU5PY4khMZLIsoEESShh09QY2LoxpfvC1N26N8/GKIFuL5fhZ47X7zMq+9HlrFE6Yv0eTr0Pr6UfZ0GJXosPddZp2Ed7ybCjERSmdzL0IL3CYTF2ZY6+zIlBPvpQd/1aeM61VrDjPf1n9ba0v/O38sLOmmYf3CFBLbMjvlX2Hg1LfArA0MFXbaPtXuE9MXMEyx3Vsbg+qP/dpE/JOa3OKR75hSMM4+qumTZ2nCkgVyrMyt49XR7FFFXfW6rf84AzfO+isGe/WtG5oBtX92UYG71IlI1gO67Fz8bjQIDAQAB";
//    private static final String MERCHANT_ID = null;
    public static boolean allowLoader = false;
    private final List<CategoriesData.CategoriesBean> categoriesDataList = new ArrayList<>();
    private final PostFragment post = new PostFragment();
    public Menu menu;
    public boolean isLifetimeActive = false;
    boolean doubleBackToExitPressedOnce = false;
    private boolean subscribed = false;
    private SharedPreferences sharedPreferences;
    private ActivityMainBinding binding;
    private CategoriesListAdapter categoriesListAdapter;
    private int sltPosition = 1;
    private CallbackManager callbackManager;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Tab1 tab1 = new Tab1();
    private LinearLayout llIn;
    private Menu bottomNavigationMenu;
    private boolean isSynchRunning = false;
    private boolean isRunning = false;
    private GoogleSignInClient googleSignInClient;
    private boolean isLoading;
    private String subscriptionType = "";
    private String inAppPurchaseToken = "";
    private boolean isFromNotification = false;
    private Context mContext;
    private BottomNavigationView bottomNavigationView;
    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeBillingInMainAct();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mContext = this;
        sharedPreferences = getSharedPreferences("yourKey", Context.MODE_PRIVATE);
        fragmentManager = getSupportFragmentManager();

        MobileAds.initialize(this, initializationStatus -> {

        });

        showAppRateDialog();
        drawerAndNavigation();

        checkNotificationPermission();

        NoInternetConnectionFragment.setInterNetConnectionListner(this);

        if (constants.getInstalledAt() == -1) {
            constants.setInstallDate();
        }

        if (!restored_Issubscribed && constants.showP()) {
            showPremiumDialog();
        }

        hideItem();

        if (sp.getBoolean("guest_entry", false)) {
            init();
        } else {
            List<String> samples = new ArrayList<>();
            Set<String> setx = sharedPreferences.getStringSet("yourKey", null);

            if (setx != null) {
                samples = new ArrayList<>(setx);
            }

            if (samples.size() > 0) {
                favBackUp(samples.toString().replace("[", "").replace("]", "").replace(" ", ""));
            }
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.white));

        if (getIntent().hasExtra(Constants.FROM)) {
            isFromNotification = false;
            if (Objects.requireNonNull(getIntent().getStringExtra(Constants.FROM)).equalsIgnoreCase(Constants.FROM_NOTIFICATION_POST)) {
                sltPosition = 0;
                bottomNavigationView.setSelectedItemId(R.id.action_post_list);
                bottomNavigationView.setItemIconTintList(null);
                bottomNavigationMenu = bottomNavigationView.getMenu();
                selectMenu(sltPosition, bottomNavigationMenu);
                binding.rvCategories.setVisibility(View.GONE);
            }
        } else if (getIntent().hasExtra("fromMusicService")) {
            isFromNotification = false;
            if (Objects.requireNonNull(getIntent().getExtras()).getBoolean("fromMusicService")) {
                if (menu != null) {
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(false);
                }
                MusicItem musicItem = new MusicItem();
                musicItem.setUrl(Constants.music_url);
                musicItem.setImage_url(Constants.music_image_url);
                Intent intent = new Intent(this, MusicPlayActivity.class);
                intent.putExtra("fromNotification", true);
                intent.putExtra("title", Constants.music_title);
                intent.putExtra("musicItem", musicItem);
                intent.putExtra("categoryId", Constants.music_category_id);
                intent.putExtra("courseCategoryId", Constants.music_course_category_id);
                startActivity(intent);
                finish();
            }
        } else if (getIntent().hasExtra("isFromNotification")) {
            if (Objects.requireNonNull(getIntent().getExtras()).getBoolean("isFromNotification")) {
                binding.rvCategories.setVisibility(View.GONE);
                isFromNotification = true;
                sltPosition = 3;
                if (menu != null) {
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(false);
                }
                bottomNavigationView.setSelectedItemId(R.id.coursesList);
                bottomNavigationView.setItemIconTintList(null);
                bottomNavigationMenu = bottomNavigationView.getMenu();
            }
        }
//        else if (getIntent().hasExtra("fromSaverNotification")) {
//            isFromSaverNotification = true;
//            binding.rvCategories.setVisibility(View.GONE);
//            if (sltPosition != 3) {
//                sltPosition = 3;
//                if (menu != null) {
//                    menu.getItem(0).setVisible(false);
//                    menu.getItem(1).setVisible(false);
//                    menu.getItem(2).setVisible(false);
//                }
//                bottomNavigationView.setSelectedItemId(R.id.coursesList);
//                bottomNavigationView.setItemIconTintList(null);
//            }
//            bottomNavigationMenu = bottomNavigationView.getMenu();
//        }
        else {
            isFromNotification = false;
            bottomNavigationView.setSelectedItemId(R.id.action_articles_list);
            bottomNavigationView.setItemIconTintList(null);
            bottomNavigationMenu = bottomNavigationView.getMenu();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_post_list -> {
                    Constants.IS_FROM_NOTIFICATION_ACT = false;
                    try {
                        if (sltPosition != 0) {
                            sltPosition = 0;
                            selectMenu(0, bottomNavigationMenu);
                            if (menu != null) {
                                menu.getItem(0).setVisible(false);
                                menu.getItem(1).setVisible(false);
                                menu.getItem(2).setVisible(false);
                            }
                            binding.rvCategories.setVisibility(View.GONE);

                            SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
                            restored_Issubscribed = prefs.getBoolean("Issubscribed", false);

                            if (sp.getBoolean("guest_entry", false)) {
                                if (Utils.isNetworkAvailable(MainActivity.this)) {
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.llContainer, post);
                                    fragmentTransaction.commit();
                                } else {
                                    if (!restored_Issubscribed) {
                                        noInterNetView();
                                    } else {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.llContainer, post);
                                        fragmentTransaction.commit();
                                    }
                                }
                            } else {
                                if (restored_Issubscribed && Utils.isNetworkAvailable(MainActivity.this)) {
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.llContainer, post);
                                    fragmentTransaction.commit();
                                } else if (Utils.isNetworkAvailable(MainActivity.this)) {
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.llContainer, post);
                                    fragmentTransaction.commit();
                                } else {
                                    if (!restored_Issubscribed) {
                                        noInterNetView();
                                    } else {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.llContainer, post);
                                        fragmentTransaction.commit();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case R.id.action_articles_list -> {
                    Constants.IS_FROM_NOTIFICATION_ACT = false;
                    try {
                        if (sltPosition != 1) {
                            menu.getItem(0).setVisible(true);
                            menu.getItem(1).setVisible(true);
                            menu.getItem(2).setVisible(true);
                            binding.rvCategories.setVisibility(View.VISIBLE);
                            sltPosition = 1;

                            SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
                            restored_Issubscribed = prefs.getBoolean("Issubscribed", false);

                            selectMenu(1, bottomNavigationMenu);
                            if (sp.getBoolean("guest_entry", false)) {
                                if (Utils.isNetworkAvailable(MainActivity.this)) {
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.llContainer, tab1);
                                    fragmentTransaction.commit();
                                } else {
                                    if (!restored_Issubscribed) {
                                        noInterNetView();
                                    } else {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.llContainer, tab1);
                                        fragmentTransaction.commit();
                                    }
                                }
                            } else {
                                if (restored_Issubscribed && Utils.isNetworkAvailable(MainActivity.this)) {
                                    syncData();
                                } else if (Utils.isNetworkAvailable(MainActivity.this)) {
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.llContainer, tab1);
                                    fragmentTransaction.commit();
                                } else {
                                    if (!restored_Issubscribed) {
                                        noInterNetView();
                                    } else {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.llContainer, tab1);
                                        fragmentTransaction.commit();
                                    }
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case R.id.action_quotes -> {
                    Constants.IS_FROM_NOTIFICATION_ACT = false;
                    try {
                        if (sltPosition != 2) {
                            sltPosition = 2;

                            SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
                            restored_Issubscribed = prefs.getBoolean("Issubscribed", false);

                            selectMenu(2, bottomNavigationMenu);
                            if (menu != null) {
                                menu.getItem(0).setVisible(false);
                                menu.getItem(1).setVisible(false);
                                menu.getItem(2).setVisible(false);
                            }
                            binding.rvCategories.setVisibility(View.GONE);
                            if (sp.getBoolean("guest_entry", false)) {
                                if (Utils.isNetworkAvailable(MainActivity.this)) {
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    QuotesFragment quotesFragment = new QuotesFragment();
                                    fragmentTransaction.replace(R.id.llContainer, quotesFragment);
                                    fragmentTransaction.commit();
                                } else {
                                    if (!restored_Issubscribed) {
                                        noInterNetView();
                                    } else {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        QuotesFragment quotesFragment = new QuotesFragment();
                                        fragmentTransaction.replace(R.id.llContainer, quotesFragment);
                                        fragmentTransaction.commit();
                                    }
                                }
                            } else {
                                if (restored_Issubscribed && Utils.isNetworkAvailable(MainActivity.this)) {
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    QuotesFragment quotesFragment = new QuotesFragment();
                                    fragmentTransaction.replace(R.id.llContainer, quotesFragment);
                                    fragmentTransaction.commit();
                                } else if (Utils.isNetworkAvailable(MainActivity.this)) {
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    QuotesFragment quotesFragment = new QuotesFragment();
                                    fragmentTransaction.replace(R.id.llContainer, quotesFragment);
                                    fragmentTransaction.commit();
                                } else {
                                    if (!restored_Issubscribed) {
                                        noInterNetView();
                                    } else {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        QuotesFragment quotesFragment = new QuotesFragment();
                                        fragmentTransaction.replace(R.id.llContainer, quotesFragment);
                                        fragmentTransaction.commit();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Exception", e.getMessage());
                    }
                }
                case R.id.coursesList -> {
                    if (sltPosition != 3) {
                        sltPosition = 3;
                        if (menu != null) {
                            menu.getItem(0).setVisible(false);
                            menu.getItem(1).setVisible(false);
                            menu.getItem(2).setVisible(false);
                        }
                        binding.rvCategories.setVisibility(View.GONE);
                        selectMenu(3, bottomNavigationMenu);
                        fragmentTransaction = fragmentManager.beginTransaction();
                        CourseListingFragment courseListingFragment = new CourseListingFragment();
                        fragmentTransaction.replace(R.id.llContainer, courseListingFragment);
                        fragmentTransaction.commit();
                    }
                }
            }
            return true;
        });


        setAdapter();
//        setUpFragment();
    }

    private void checkNotificationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.e("Notification ","permission is isGranted");
                }else  {
                    Toast.makeText(this, "Please grant permission for Notification", Toast.LENGTH_LONG).show();
                }
            });


    private void setUpFragment() {
        try {
            if (getIntent().hasExtra(Constants.FROM)) {
                if (Objects.requireNonNull(getIntent().getStringExtra(Constants.FROM)).equalsIgnoreCase(Constants.FROM_NOTIFICATION_POST)) {
                    if (Utils.isNetworkAvailable(MainActivity.this)) {
                        getCategories(Constants.POST);
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.llContainer, post);
                        fragmentTransaction.commit();
                        binding.rvCategories.setVisibility(View.VISIBLE);
                        Log.e("Article Fragment", "Called");
                    } else {
                        if (restored_Issubscribed) {
                            getOfflineCategory(Constants.POST);
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.llContainer, post);
                            fragmentTransaction.commit();
                            binding.rvCategories.setVisibility(View.VISIBLE);
                        } else {
                            noInterNetView();
                        }
                    }
                }
            } else if (getIntent().hasExtra("isFromNotification")) {
                if (Objects.requireNonNull(getIntent().getExtras()).getBoolean("isFromNotification")) {
                    if (menu != null) {
                        menu.getItem(0).setVisible(false);
                        menu.getItem(1).setVisible(false);
                        menu.getItem(2).setVisible(false);
                    }
                    getCategories(Constants.ARTICLE);
                    binding.rvCategories.setVisibility(View.GONE);
                    selectMenu(sltPosition, bottomNavigationMenu);
                    fragmentTransaction = fragmentManager.beginTransaction();
                    CourseListingFragment courseListingFragment = new CourseListingFragment();
                    fragmentTransaction.replace(R.id.llContainer, courseListingFragment);
                    fragmentTransaction.commit();
                }
            } else if (getIntent().hasExtra("fromSaverNotification")) {
                sltPosition = 3;
                if (menu != null) {
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(false);
                }
                binding.rvCategories.setVisibility(View.GONE);
                selectMenu(3, bottomNavigationMenu);
                fragmentTransaction = fragmentManager.beginTransaction();
                CourseListingFragment courseListingFragment = new CourseListingFragment();
                fragmentTransaction.replace(R.id.llContainer, courseListingFragment);
                fragmentTransaction.commit();
                bottomNavigationMenu = bottomNavigationView.getMenu();
                bottomNavigationView.setSelectedItemId(R.id.coursesList);
                bottomNavigationView.setItemIconTintList(null);
            } else {
                if (Utils.isNetworkAvailable(MainActivity.this)) {
                    getCategories(Constants.ARTICLE);
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.llContainer, tab1);
                    fragmentTransaction.commit();
                    binding.rvCategories.setVisibility(View.VISIBLE);
                    Log.e("Article Fragment", "Called");
                } else {
                    if (restored_Issubscribed) {
                        getOfflineCategory(Constants.ARTICLE);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.llContainer, tab1);
                        ft.commit();
                        binding.rvCategories.setVisibility(View.VISIBLE);
                    } else {
                        noInterNetView();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void noInterNetView() {
        try {
            if (!restored_Issubscribed) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                NoInternetConnectionFragment noInterNetFragment = new NoInternetConnectionFragment();
                ft.replace(R.id.llContainer, noInterNetFragment);
                ft.commit();
                binding.bottomNavigation.setVisibility(View.GONE);
                binding.rvCategories.setVisibility(View.GONE);
            } else {
                currentFragment();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectMenu(int sltPosition, Menu menu) {
        hideSubscriptionMenu();
        if (sltPosition == 0) {
            menu.getItem(0).setIcon(R.drawable.ic_article_blue_new);
            menu.getItem(1).setIcon(R.drawable.ic_home);
            menu.getItem(2).setIcon(R.drawable.ic_quotes_slt);
            menu.getItem(3).setIcon(R.drawable.courses);
        } else if (sltPosition == 1) {
            binding.rvCategories.setVisibility(View.VISIBLE);
            menu.getItem(0).setIcon(R.drawable.ic_article_grey_new);
            menu.getItem(1).setIcon(R.drawable.ic_home_slt);
            menu.getItem(2).setIcon(R.drawable.ic_quotes_slt);
            menu.getItem(3).setIcon(R.drawable.courses);
        } else if (sltPosition == 3) {
            binding.rvCategories.setVisibility(View.GONE);
            menu.getItem(0).setIcon(R.drawable.ic_article_grey_new);
            menu.getItem(1).setIcon(R.drawable.ic_home);
            menu.getItem(2).setIcon(R.drawable.ic_quotes_slt);
            menu.getItem(3).setIcon(R.drawable.courses_active);
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_article_grey_new);
            menu.getItem(1).setIcon(R.drawable.ic_home);
            menu.getItem(2).setIcon(R.drawable.ic_qoutes);
            menu.getItem(3).setIcon(R.drawable.courses);
        }
    }

    public void favBackUp(String articles_id) {

        String authToken = "";
        if (Constants.getUserData(MainActivity.this) != null) {
            authToken = Constants.getUserData(MainActivity.this).getAuthentication_token();
        }

        connectPost(MainActivity.this, null, importFavArticles(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), articles_id), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                SharedPreferences.Editor putStringSet = sharedPreferences.edit();
                putStringSet.putStringSet("yourKey", null);
                putStringSet.apply();

            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                //Toast.makeText(MainActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Headers headers) {
                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionFailure() {
                Toast.makeText(MainActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                Toast.makeText(MainActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showPremiumDialog() {

        try {
            final Dialog dialog = new Dialog(MainActivity.this);

            Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_custom);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = getWindow().getWindowManager().getDefaultDisplay().getWidth() * 85 / 100;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            dialog.getWindow().setAttributes(lp);

            TextView title = dialog.findViewById(R.id.title);
            TextView description = dialog.findViewById(R.id.description);
            TextView enable = dialog.findViewById(R.id.enable);
            TextView later = dialog.findViewById(R.id.later);
            RelativeLayout relativeLayout = dialog.findViewById(R.id.mainRel);

            ImageView imageView = dialog.findViewById(R.id.image);

            if (sp.getBoolean("light", false)) {
                relativeLayout.setBackgroundColor(Color.parseColor("#464646"));
                description.setTextColor(Color.parseColor("#ffffff"));
                later.setTextColor(Color.parseColor("#48bdcf"));
                enable.setTextColor(Color.parseColor("#48bdcf"));
                title.setTextColor(Color.parseColor("#48bdcf"));
            } else {
                relativeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                description.setTextColor(Color.parseColor("#186673"));
            }

            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MRegular.ttf");

            title.setTypeface(font);
            description.setTypeface(font);
            enable.setTypeface(font);
            later.setTypeface(font);

            later.setOnClickListener(view -> dialog.dismiss());

            enable.setOnClickListener(view -> {
                dialog.dismiss();
                boolean isAvailable = Utils.isIabServiceAvailable(getApplication());
                if (isAvailable) {
                    Intent purchase = new Intent(MainActivity.this, RemoveAdActivity.class);
                    startActivity(purchase);
                } else {
                    Toast.makeText(MainActivity.this, "In-App Subscription not supported", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void hideItem() {
        Menu nav_Menu = binding.navigationView.getMenu();
        nav_Menu.findItem(R.id.signout).setVisible(!sp.getBoolean("guest_entry", false));
    }

    public void drawerAndNavigation() {
        try {
            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MRegular.ttf");

            View headerLayout = binding.navigationView.getHeaderView(0);
            TextView signUp = headerLayout.findViewById(R.id.signUp);
            TextView name = headerLayout.findViewById(R.id.name);
            TextView email = headerLayout.findViewById(R.id.email);
            LinearLayout llIn = headerLayout.findViewById(R.id.llIn);
            RelativeLayout rlProfile = headerLayout.findViewById(R.id.rlProfile);
            ImageView ivProfileTop = headerLayout.findViewById(R.id.ivProfileTop);
            ImageView imgArrow = headerLayout.findViewById(R.id.imgArrow);

            signUp.setTypeface(font);
            name.setTypeface(font);
            email.setTypeface(font);

            rlProfile.setOnClickListener(v -> {
                ///binding.drawer.closeDrawers();
                if (sp.getBoolean("guest_entry", false)) {
                    String uuid = sp.getString("UUID", "");
                    editor.clear();
                    editor.commit();
                    editor.putString("UUID", uuid);
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    if (Utils.isNetworkAvailable(MainActivity.this)) {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else {
                        Utils.showToast(getString(R.string.no_internet_connection));
                    }
                }
            });

            if (sp.getBoolean("guest_entry", false)) {
                signUp.setVisibility(View.VISIBLE);
                llIn.setVisibility(View.GONE);
                imgArrow.setVisibility(View.GONE);
                ivProfileTop.setVisibility(View.GONE);

                signUp.setOnClickListener(view -> {
                    String uuid = sp.getString("UUID", "");
                    editor.clear();
                    editor.commit();
                    editor.putString("UUID", uuid);
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });

            } else {
                signUp.setVisibility(View.GONE);
                llIn.setVisibility(View.VISIBLE);
                imgArrow.setVisibility(View.VISIBLE);
                ivProfileTop.setVisibility(View.VISIBLE);

                UserData userData = Constants.getUserData(MainActivity.this);
                name.setText((userData.getFirst_name() == (null) ? "" : userData.getFirst_name()) + " " + (userData.getLast_name() == (null) ? "" : userData.getLast_name()));
                email.setText(userData.getUser_email());

                RequestOptions options = new RequestOptions();
                final RequestOptions placeholder_error = options.error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                if (userData.getProfilePic() != null && userData.getProfilePic().length() > 0) {
                    Glide.with(MainActivity.this)
                            .load(userData.getProfilePic()).apply(placeholder_error).into(ivProfileTop);
                }
                signUp.setVisibility(View.GONE);
                llIn.setVisibility(View.VISIBLE);
                ivProfileTop.setVisibility(View.VISIBLE);
            }

            setSupportActionBar(binding.toolbar);
            sharedPreferences = getSharedPreferences("yourKey", Context.MODE_PRIVATE);

            // This method will trigger on item Click of navigation menu
            binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
                        if (menuItem.isChecked()) {
                            menuItem.setChecked(false);
                        }

                        binding.drawer.closeDrawers();
                        Intent intent;
                        Constants.IS_FROM_NOTIFICATION_ACT = false;
                        if (menuItem.getItemId() == R.id.home) {
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.feedback) {
                            if (Utils.isNetworkAvailable(MainActivity.this)) {
                                intent = new Intent(MainActivity.this, FeedbackActivity.class);
                                startActivity(intent);
                            } else {
                                Utils.showToast(getString(R.string.no_internet_connection));
                            }
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.request) {
                            if (Utils.isNetworkAvailable(MainActivity.this)) {
                                intent = new Intent(MainActivity.this, RequestActivity.class);
                                startActivity(intent);
                            } else {
                                Utils.showToast(getString(R.string.no_internet_connection));
                            }
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.purchase) {
                            if (Utils.isNetworkAvailable(MainActivity.this)) {
                                boolean isAvailable = Utils.isIabServiceAvailable(getApplication());
                                if (isAvailable) {
                                    Intent purchase = new Intent(MainActivity.this, RemoveAdActivity.class);
                                    startActivity(purchase);
                                } else {
                                    Toast.makeText(MainActivity.this, "In-App Subscription not supported", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Utils.showToast(getString(R.string.no_internet_connection));
                            }
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.settings) {
                            intent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivityForResult(intent, 101);
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.favourite) {
                            if (restored_Issubscribed || Utils.isNetworkAvailable(MainActivity.this)) {
                                if (sp.getBoolean("guest_entry", false)) {
                                    showLoginDialog();
                                } else {
                                    intent = new Intent(MainActivity.this, FavouriteActivity.class);
                                    startActivity(intent);
                                }
                            }
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.likes) {
                            if (restored_Issubscribed || Utils.isNetworkAvailable(MainActivity.this)) {
                                if (sp.getBoolean("guest_entry", false)) {
                                    showLoginDialog();
                                } else {
                                    intent = new Intent(MainActivity.this, LikesActivity.class);
                                    startActivity(intent);
                                }
                            }
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.notification) {
                            intent = new Intent(MainActivity.this, NotificationActivity.class);
                            Constants.IS_NEW_ACT = true;
                            startActivity(intent);
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.rate) {
                            rate_app();
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.share) {
                            ShareApp();
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.about) {
                            intent = new Intent(MainActivity.this, AboutAppActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.signout) {
                            if (Utils.isNetworkAvailable(MainActivity.this)) {
                                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE -> { //Yes button clicked
                                            dialog.dismiss();
                                            signOut();
                                        }
                                        case DialogInterface.BUTTON_NEGATIVE -> //No button clicked
                                                dialog.dismiss();
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Are you sure you want to logout?").setPositiveButton("Logout", dialogClickListener)
                                        .setNegativeButton("Cancel", dialogClickListener).show();
                            } else {
                                Utils.showToast(getString(R.string.no_internet_connection));
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
                        }
                        return true;

                    }

            );


            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.drawer, binding.toolbar, R.string.openDrawer, R.string.closeDrawer) {

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }
            };

            binding.drawer.setDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();

            setNavColor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getOfflineCategory(String fromData) {
        try {
            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(this);
            if (db != null) {
                List<Category> categoriesList = db.categoryDao().getAllCategory();
                if (categoriesList != null && !categoriesList.isEmpty()) {
                    categoriesDataList.clear();
                    for (int i = 0; i < categoriesList.size(); i++) {
                        CategoriesData.CategoriesBean categoriesBean = new CategoriesData.CategoriesBean();
                        Category category = categoriesList.get(i);
                        categoriesBean.setId(category.getId());
                        categoriesBean.setName(category.getName());
                        categoriesDataList.add(categoriesBean);
                    }
                    categoriesListAdapter.notifyDataSetChanged();
                    if (fromData.equalsIgnoreCase(Constants.POST)) {
                        binding.rvCategories.setVisibility(View.GONE);
                    } else {
                        binding.rvCategories.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertOfflineCategory() {
        try {
            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(this);
            if (db != null) {
                if (!categoriesDataList.isEmpty()) {
                    for (int i = 0; i < categoriesDataList.size(); i++) {
                        CategoriesData.CategoriesBean categoriesBean = categoriesDataList.get(i);
                        Category categoryDb = new Category();
                        categoryDb.setId(categoriesBean.getId());
                        categoryDb.setName(categoriesBean.getName());
                        db.categoryDao().insertCategory(categoryDb);
                        Log.i(TAG, "insertOfflineCategory: " + i);
                    }
                    Log.i(TAG, "insertOfflineCategory: Size " + db.categoryDao().getAllCategory().size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        this.menu = menu;
        if (sltPosition == 0 || sltPosition == 2 || sltPosition == 3) {
            MenuItem menuItem1 = menu.findItem(R.id.action_filter);
            MenuItem menuItem2 = menu.findItem(R.id.action_search);
            MenuItem menuItem3 = menu.findItem(R.id.action_select_lang);
            menuItem1.setVisible(false);
            menuItem2.setVisible(false);
            menuItem3.setVisible(false);
        }

        hideSubscriptionMenu();
        return super.onCreateOptionsMenu(menu);
    }

    private void hideSubscriptionMenu() {
        SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
        restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
        if (restored_Issubscribed) {
            if (menu != null) {
                MenuItem menuItem = menu.findItem(R.id.action_premium);
                if (menuItem != null) {
                    menuItem.setVisible(false);
                    invalidateOptionsMenu();
                }
            }
        }
    }

    void signOut() {
        try {
            Utils.showDialog(this);
            String authToken = "";

            if (Constants.getUserData(MainActivity.this) != null) {
                authToken = Constants.getUserData(MainActivity.this).getAuthentication_token();

                connectPost(MainActivity.this, null, ApiCallBack.signOut(authToken, Constants.getV6Value()), new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                        Utils.hideDialog();
                        Utils.setArticleLang(MainActivity.this, 1);
                        String uuid = sp.getString("UUID", "");
                        editor.clear();
                        editor.commit();
                        editor.putString("UUID", uuid);
                        editor.commit();

                        SharedPreferences prefs = getSharedPreferences("Purchase", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear().apply();

                        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                        assert intent != null;
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        Utils.hideDialog();
                    }

                    @Override
                    public void onConnectionFailure() {
                        Utils.hideDialog();
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        Utils.hideDialog();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            if (item.getItemId() == R.id.action_search) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.action_premium) {
                if (Utils.isNetworkAvailable(MainActivity.this)) {
                    boolean isAvailable = Utils.isIabServiceAvailable(getApplication());
                    if (isAvailable) {
                        Intent purchase = new Intent(MainActivity.this, RemoveAdActivity.class);
                        startActivity(purchase);
                    } else {
                        Toast.makeText(MainActivity.this, "In-App Subscription not supported", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Utils.showToast(getString(R.string.no_internet_connection));
                }
            } else if (item.getItemId() == R.id.action_filter) {
                if (Utils.isNetworkAvailable(this)) {
                    tab1.showFilterDialog();
                } else {
                    Utils.showToast("Please Check Your Internet Connection");
                }
            } else if (item.getItemId() == R.id.action_select_lang) {
                if (Utils.isNetworkAvailable(this)) {
                    displayLangSelectionDialog();
                } else {
                    Utils.showToast("Please Check Your Internet Connection");
                }
            }
            return super.onOptionsItemSelected(item);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        PersonalityDevelopmentApp.getInstance().setConnectivityListener(this);
       /* if (!subscribed) {
            initAppoDeal(R.id.adView, Appodeal.BANNER);
            Appodeal.setBannerCallbacks(new AppodealBannerCallbacks(this));
            Appodeal.show(MainActivity.this, Appodeal.BANNER_VIEW);
        } else {
            Appodeal.hide(MainActivity.this, Appodeal.BANNER_VIEW);
        }*/
        hideSubscriptionMenu();
        invalidateOptionsMenu();
        drawerAndNavigation();
        hideItem();
    }

    public void ShareApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "A guide for all those who seek improvements in their personality and willing to accept the change in them according to modern world. " +
                "This app will help you to enhance your personality with some unique tips along with expert advice   " +
                "Android app: http://bit.ly/pd_app, " +
                "IOS app: http://bit.ly/pd_ios_app";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Personality Development");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
        binding.drawer.closeDrawers();
    }

    public void rate_app() {
        Uri uri = Uri.parse("market://details?id=com.mayur.personalitydevelopment");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.mayur.personalitydevelopment")));
        }
        binding.drawer.closeDrawers();
    }

    private void initializeBillingInMainAct() {

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

//    private void checkUserSubscription() {
//        Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
//        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS, (billingResult1, purchasesList) -> {
//            if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK &&
//                    !Objects.requireNonNull(purchasesResult.getPurchasesList()).isEmpty()) {
//                String purchasedItem = purchasesList.get(0).getSkus().get(0);
//                inAppPurchaseToken = purchasesList.get(0).getPurchaseToken();
//                if (purchasedItem.equals("3_months")) {
//                    subscribed = true;
//                    subscriptionType = "3_months";
//                } else if (purchasedItem.equals("6_months")) {
//                    subscribed = true;
//                    subscriptionType = "6_months";
//                } else if (purchasedItem.equals("yearly")) {
//                    subscribed = true;
//                    subscriptionType = "yearly";
//                } else if (purchasedItem.equals("six_months_v2")) {
//                    subscribed = true;
//                    subscriptionType = "six_months_v2";
//                } else if (purchasedItem.equals("twelve_months_v2")) {
//                    subscribed = true;
//                    subscriptionType = "twelve_months_v2";
//                } else if (purchasedItem.equals("one_month_v2")) {
//                    subscribed = true;
//                    subscriptionType = "one_month_v2";
//                } else if (purchasedItem.equals("offer_twelve_months_v2")) {
//                    subscribed = true;
//                    subscriptionType = "offer_twelve_months_v2";
//                }
//
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                        if (subscribed) {
//                            SharedPreferences.Editor editor = getSharedPreferences("Purchase", MODE_PRIVATE).edit();
//                            editor.putBoolean("Issubscribed", subscribed);
//                            editor.apply();
//                            if (Utils.isNetworkAvailable(MainActivity.this)) {
//                                callSetSubscriptionAPI();
//                            } else {
//                                setUpFragment();
//                            }
//                        }
//                        hideSubscriptionMenu();
//                    }
//                });
//            }
//            else {
//                 MainActivity.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                            if (Utils.isNetworkAvailable(MainActivity.this)) {
//                                getSubscriptionAPI();
//                            } else {
//                                setUpFragment();
//                            }
//                        hideSubscriptionMenu();
//                    }
//                });
//            }
//        });
//    }

    private void checkUserSubscription() {
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                (billingResult, purchases) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !purchases.isEmpty()) {
                        String purchasedItem = purchases.get(0).getProducts().get(0);
                        Log.e("Purchase Item", purchasedItem);
                        inAppPurchaseToken = purchases.get(0).getPurchaseToken();
                        Log.e("inAppPurchaseToken Item", inAppPurchaseToken);
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
                        MainActivity.this.runOnUiThread(() -> {
                            if (subscribed) {
                                SharedPreferences.Editor editor = getSharedPreferences("Purchase", MODE_PRIVATE).edit();
                                editor.putBoolean("Issubscribed", subscribed);
                                editor.apply();
                                if (Utils.isNetworkAvailable(MainActivity.this)) {
                                    callSetSubscriptionAPI();
                                } else {
                                    setUpFragment();
                                }
                            }
                            hideSubscriptionMenu();
                        });
                    } else {
                        MainActivity.this.runOnUiThread(() -> {
                            if (Utils.isNetworkAvailable(MainActivity.this)) {
                                getSubscriptionAPI();
                            } else {
                                setUpFragment();
                            }
                            hideSubscriptionMenu();
                        });
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
                            setUpFragment();
                        }

                        @Override
                        public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                            Utils.hideDialog();
                            setUpFragment();
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
                            setUpFragment();
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
                                isLoading = false;
                                Utils.hideDialog();
                                Toast.makeText(getApplicationContext(), "EE Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                                Log.e("onException 2", e.getMessage() + "");
                            }
                            setUpFragment();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    private void getSubscriptionAPI() {
        Log.e("Get Subscription ", "API called");
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
                                setUpFragment();
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
                            isLoading = false;
                            Utils.hideDialog();
                            setUpFragment();
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
                            setUpFragment();
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
                            setUpFragment();
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
                            setUpFragment();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    void showAppRateDialog() {
        AppRate.with(this)
                .setInstallDays(1) // default 10, 0 means install day.
                .setLaunchTimes(3) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .setTitle(R.string.new_rate_dialog_title)
                .setTextLater(R.string.new_rate_dialog_later)
                .setTextNever(R.string.new_rate_dialog_never)
                .setTextRateNow(R.string.new_rate_dialog_ok)
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);
    }

    void setNavColor() {
        if (sp.getBoolean("light", false)) {
            binding.navigationView.setBackgroundColor(Color.parseColor("#363636"));
            binding.navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            binding.navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            binding.shadow.setBackgroundColor(Color.parseColor("#363636"));
        } else {
            binding.navigationView.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            binding.navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark2)));
            binding.shadow.setBackground(ContextCompat.getDrawable(this, R.drawable.shadow));
        }
    }

    public void showLoginDialog() {

        try {
            final Dialog dialog = new Dialog(MainActivity.this);

            Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_custom_login_2);

            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.rounded_rectangle_white_big_no_stroke));

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

            fb.setOnClickListener(view -> {
                dialog.dismiss();
                onFacebook();
            });

            google.setOnClickListener(view -> {
                dialog.dismiss();

                googleSignInClient.signOut();
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, Constants.LOGIN_TYPE.GOOGLE);

                //Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                //startActivityForResult(signInIntent, Constants.LOGIN_TYPE.GOOGLE);
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void init() {
        try {
            prepareGoogle();
            FacebookSdk.sdkInitialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void onFacebook() {
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

                                    onSignin(params);
                                }

                                LoginManager.getInstance().logOut();

                            } catch (JSONException e) {
                                e.printStackTrace();
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

    void onSignin(Map<String, Object> params) {
        try {
            Utils.showDialog(this);
            params.put("platform", "android");

            sp = PreferenceManager.getDefaultSharedPreferences(this);
            String token = sp.getString("FCM_TOKEN", "");
            if (token.length() > 0) {
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
                    Constants.setUserData(MainActivity.this, response);
                    initializeBilling();
                    updateToken();
                    displayMessage(getString(R.string.msg_logged_in));
                    Utils.hideDialog();
                    drawerAndNavigation();
                    hideItem();
                    selectMenu(1, bottomNavigationMenu);
                    fragmentTransaction = fragmentManager.beginTransaction();
                    tab1 = new Tab1();
                    fragmentTransaction.replace(R.id.llContainer, tab1);
                    fragmentTransaction.commit();
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
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (Utils.isNetworkAvailable(MainActivity.this)) {
                tab1.setColorData(sp.getBoolean("light", false));
                if (sltPosition == 0) {
                    post.changeReadingMode(null);
                }

                setNavColor();

                if (requestCode == Constants.LOGIN_TYPE.GOOGLE) {

                    try {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount acct = task.getResult(ApiException.class);

                        //GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                        //GoogleSignInAccount acct = result.getSignInAccount();

                        if (acct.getEmail() == null && acct.getEmail().trim().isEmpty()) {
                            Toast.makeText(MainActivity.this, "null", Toast.LENGTH_LONG).show();
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

                        onSignin(params);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.hideDialog();
                    }

                /*GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount acct = result.getSignInAccount();
                    if (acct.getEmail() == null && acct.getEmail().trim().isEmpty()) {
                        googleApiClient.clearDefaultAccountAndReconnect();
                        Toast.makeText(MainActivity.this, "null", Toast.LENGTH_LONG).show();
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

                    onSignin(params);
                }*/

                } else if (FacebookSdk.isFacebookRequestCode(requestCode)) {
                    callbackManager.onActivityResult(requestCode, resultCode, data);
                }
            } else {
                if (!restored_Issubscribed) {
                    noInterNetView();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            googleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getCategories(final String fromData) {
        String authToken = "";
        if (Constants.getUserData(MainActivity.this) != null) {
            authToken = Constants.getUserData(MainActivity.this).getAuthentication_token();
        }

        connectPost(MainActivity.this, null, ApiCallBack.getCategories(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value()), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                CategoriesData articlesData = new Gson().fromJson(response, CategoriesData.class);
                categoriesDataList.clear();
                categoriesDataList.addAll(articlesData.getCategories());
                categoriesListAdapter.notifyDataSetChanged();

//                binding.rvCategories.setVisibility(View.VISIBLE);

//                if (isFromSaverNotification){
//                    binding.rvCategories.setVisibility(View.GONE);
//                }

                if (fromData.equalsIgnoreCase(Constants.POST)) {
                    binding.rvCategories.setVisibility(View.GONE);
                } else {
                    if (isFromNotification) {
                        binding.rvCategories.setVisibility(View.GONE);
                    } else {
                        binding.rvCategories.setVisibility(View.VISIBLE);
                    }
                }
                if (restored_Issubscribed) {
                    insertOfflineCategory();
                    if (!isSynchRunning) {
                        syncData();
                    }
                }
            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                //Toast.makeText(MainActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Headers headers) {
                Toast.makeText(MainActivity.this, "category load Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionFailure() {
                //Toast.makeText(MainActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                Toast.makeText(MainActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
            }
        });

    }

    void setAdapter() {
        binding.rvCategories.setVisibility(View.VISIBLE);
        categoriesListAdapter = new CategoriesListAdapter(categoriesDataList, MainActivity.this);
        binding.rvCategories.setAdapter(categoriesListAdapter);
    }

    @Override
    public void onBackPressed() {
        if (Constants.IS_FROM_NOTIFICATION_ACT) {
            Constants.IS_FROM_NOTIFICATION_ACT = false;
            Intent i = new Intent(this, NotificationActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return;
        }
        try {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        try {
            if (isRunning) {
                SharedPreferences prefs = this.getSharedPreferences("Purchase", MODE_PRIVATE);
                restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
                Log.i(TAG, "isSubscribed : " + restored_Issubscribed);

                if (isConnected) {
                    Log.i(TAG, "onNetworkConnectionChanged: ON ");
                    if (restored_Issubscribed) {
                        if (!isSynchRunning) {
                            isSynchRunning = true;
                            syncData();
                            binding.bottomNavigation.setVisibility(View.VISIBLE);
                            binding.rvCategories.setVisibility(View.VISIBLE);
                        }
                    } else {
                        currentFragment();
                    }
                } else {
                    //TODO need to display no connections.
                    Log.i(TAG, "onNetworkConnectionChanged: OFF ");
                    if (!restored_Issubscribed) {
                        noInterNetView();
                    } else {
                        currentFragment();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void currentFragment() {
        try {
            if (sltPosition == 0) {
                selectMenu(0, bottomNavigationMenu);
                if (menu != null) {
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(false);
                }
                binding.rvCategories.setVisibility(View.GONE);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.llContainer, post);
                fragmentTransaction.commit();
                binding.bottomNavigation.setVisibility(View.VISIBLE);
            } else if (sltPosition == 1) {
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(true);
                menu.getItem(2).setVisible(true);
                binding.rvCategories.setVisibility(View.VISIBLE);
                getCategories(Constants.ARTICLE);
                sltPosition = 1;
                selectMenu(1, bottomNavigationMenu);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.llContainer, tab1);
                ft.commit();
                binding.bottomNavigation.setVisibility(View.VISIBLE);
                binding.rvCategories.setVisibility(View.VISIBLE);
            } else if (sltPosition == 2) {
                selectMenu(2, bottomNavigationMenu);
                if (menu != null) {
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(false);
                }
                binding.rvCategories.setVisibility(View.GONE);
                fragmentTransaction = fragmentManager.beginTransaction();
                QuotesFragment quotesFragment = new QuotesFragment();
                fragmentTransaction.replace(R.id.llContainer, quotesFragment);
                fragmentTransaction.commit();
                binding.bottomNavigation.setVisibility(View.VISIBLE);
            } else if (sltPosition == 3) {
                if (menu != null) {
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(false);
                }
                binding.rvCategories.setVisibility(View.VISIBLE);
                sltPosition = 3;
                selectMenu(3, bottomNavigationMenu);
                fragmentTransaction = fragmentManager.beginTransaction();
                CourseListingFragment courseListingFragment = new CourseListingFragment();
                fragmentTransaction.replace(R.id.llContainer, courseListingFragment);
                fragmentTransaction.commit();
                binding.bottomNavigation.setVisibility(View.VISIBLE);
                binding.rvCategories.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void syncData() {
        try {
            isSynchRunning = true;
            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(MainActivity.this);
            List<Article> list = db.articleDao().getAllArticlesSynch(true);
            ArrayList<String> articleIds = new ArrayList<>();
            ArrayList<Boolean> articleLikesIdsStatus = new ArrayList<>();
            ArrayList<Boolean> articleBookMarkIdsStatus = new ArrayList<>();
            if (list != null && !list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    articleIds.add(list.get(i).getId() + "");
                    articleLikesIdsStatus.add(list.get(i).isLike());
                    articleBookMarkIdsStatus.add(list.get(i).isBookMark());
                }
                String ids = android.text.TextUtils.join(",", articleIds);
                String likesStatus = android.text.TextUtils.join(",", articleLikesIdsStatus);
                String bookMarkStatus = android.text.TextUtils.join(",", articleBookMarkIdsStatus);
                Log.i(TAG, "onNetworkConnectionChanged: ids : " + ids);
                Log.i(TAG, "onNetworkConnectionChanged: ids Like status : " + likesStatus);
                Log.i(TAG, "onNetworkConnectionChanged: ids Bookmark status : " + bookMarkStatus);
                if (ids != null && ids.length() > 0) {
                    updateArticleLike(ids, likesStatus, bookMarkStatus, articleIds);
                } else {
                    currentFragment();
                }
            } else {
                Utils.hideDialog();
                currentFragment();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateArticleLike(final String articleIds, String articleLikeStatus, final String favoritesStatus, final ArrayList<String> articleSynchList) {

        String authToken = "";
        if (Constants.getUserData(MainActivity.this) != null) {
            authToken = Constants.getUserData(MainActivity.this).getAuthentication_token();
        }

        connectPost(MainActivity.this, null, ApiCallBack.multipleArticleLike(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), articleIds, articleLikeStatus), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                updateArticleFavorites(articleIds, favoritesStatus, articleSynchList);
            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                Utils.hideDialog();
                //Toast.makeText(MainActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Headers headers) {
                Utils.hideDialog();
                Log.i(TAG, "onFailure: Sync fail");
            }

            @Override
            public void onConnectionFailure() {
                Utils.hideDialog();
                Log.i(TAG, "onConnectionFailure: Sync fail");
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                Utils.hideDialog();
                Log.i(TAG, "onException: Sync fail");
            }
        });

    }

    public void updateArticleFavorites(String articleIds, String favoritsStatus, final ArrayList<String> articleSynchList) {

        String authToken = "";
        if (Constants.getUserData(MainActivity.this) != null) {
            authToken = Constants.getUserData(MainActivity.this).getAuthentication_token();
        }

        connectPost(MainActivity.this, null, ApiCallBack.multipleArticleFavorite(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), articleIds, Constants.getV6Value(), favoritsStatus), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                //syncPostLikes();
                ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(MainActivity.this);
                if (articleSynchList != null && articleSynchList.size() > 0) {
                    for (int i = 0; i < articleSynchList.size(); i++) {
                        db.articleDao().setSynch(false, Integer.parseInt(articleSynchList.get(i)));
                    }
                }
                currentFragment();
            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                Utils.hideDialog();
                //Toast.makeText(MainActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Headers headers) {
                Utils.hideDialog();
                Log.i(TAG, "onFailure: Sync fail");
            }

            @Override
            public void onConnectionFailure() {
                Utils.hideDialog();
                Log.i(TAG, "onConnectionFailure: Sync fail");
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                Log.i(TAG, "onException: Sync fail");
                Utils.hideDialog();
            }
        });

    }

    @Override
    public void onInterNetConnected() {
        Log.i(TAG, "onInterNetConnected: ");
        currentFragment();
    }

    private void displayLangSelectionDialog() {

        Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_lang_selection);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        RadioGroup radioGroup = dialog.findViewById(R.id.langRadioGroup);
        RadioButton englishRadioButton = dialog.findViewById(R.id.englishRadioBtn);
        RadioButton hindiRadioButton = dialog.findViewById(R.id.hindiRadioBtn);

        if (Utils.getArticleLang(this) == 1) {
            englishRadioButton.setChecked(true);
        } else if (Utils.getArticleLang(this) == 2) {
            hindiRadioButton.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = radioGroup.findViewById(checkedId);
                if (radioButton.getText().equals("English")) {
                    Utils.setArticleLang(MainActivity.this, 1);
                    setArticleLang(1);
                } else if (radioButton.getText().equals("Hindi")) {
                    Utils.setArticleLang(MainActivity.this, 2);
                    setArticleLang(2);
                }
                dialog.dismiss();
            }
        });

        if (Utils.isNetworkAvailable(this)) {
            dialog.show();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void setArticleLang(int lang) {
        allowLoader = true;
        if (!sp.getBoolean("guest_entry", false)) {
            if (Utils.isNetworkAvailable(this)) {
                callArticleLangApi(lang);
            }
        } else {
            if (Utils.isNetworkAvailable(this)) {
                callArticleLangApiGuest(lang);
            }
//            refreshArticleData();
        }
    }

    private void refreshArticleData() {
        if (Utils.isNetworkAvailable(this)) {
            tab1.onArticleRefresh();
        } else {
            tab1.totalPage = 0;
            tab1.current_page = 1;
            tab1.setOffLineData();
        }
    }

    public void callArticleLangApi(int lang) {
        try {
//            Utils.showDialog(this);
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, updateArticleLang(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), lang), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    refreshArticleData();
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    public void callArticleLangApiGuest(int lang) {
        try {
//            Utils.showDialog(this);
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, updateArticleLangGuest(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), lang, sp.getString(Constants.GUEST_ID, "")), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    refreshArticleData();
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Objects.equals(Objects.requireNonNull(intent.getExtras()).get("fromSaverNotification"), "savers")) {
            sltPosition = 3;
            if (menu != null) {
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(false);
                menu.getItem(2).setVisible(false);
            }
            binding.rvCategories.setVisibility(View.GONE);
            selectMenu(3, bottomNavigationMenu);
            fragmentTransaction = fragmentManager.beginTransaction();
            CourseListingFragment courseListingFragment = new CourseListingFragment();
            fragmentTransaction.replace(R.id.llContainer, courseListingFragment);
            fragmentTransaction.commit();
            bottomNavigationMenu = bottomNavigationView.getMenu();
            bottomNavigationView.setSelectedItemId(R.id.coursesList);
            bottomNavigationView.setItemIconTintList(null);
        }
    }
}

