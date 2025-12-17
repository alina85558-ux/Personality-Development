package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.searchArticles;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.signIn;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.unlockArticle;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
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
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.ArticleListAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.database.Article;
import com.mayur.personalitydevelopment.database.ArticleRoomDatabase;
import com.mayur.personalitydevelopment.databinding.ActivitySearchBinding;
import com.mayur.personalitydevelopment.models.AdClass;
import com.mayur.personalitydevelopment.models.Articles;
import com.mayur.personalitydevelopment.models.ArticlesData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.ResponseBody;


public class SearchActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "SearchActivity";
    public static boolean isFromLogin;
    public ArrayList<Object> articlesBeen = new ArrayList<>();
    public String topic = "";
    int selectedArticleID = 0;
    boolean isRefresh;
    CallbackManager callbackManager;
    private ActivitySearchBinding binding;
    private SharedPreferences prefs;
    private ArticleListAdapter articleListAdapter;
    private int totalPage = 0;
    private int current_page = 1;
    private boolean isLoading = false;
    private GoogleSignInClient googleSignInClient;
    private AdView mAdView;
    private List<NativeAd> nativeAdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        nativeAdList = new ArrayList<>();
        prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.lvMovies.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.back.setOnClickListener(view -> onBackPressed());
        setUpAdapter();
        mAdView = findViewById(R.id.adView);
        if (sp.getBoolean("guest_entry", false)) {
            init();
        }

        binding.lvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Utils.isNetworkAvailable(SearchActivity.this)) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.lvMovies.getLayoutManager();
                    int lastvisibleitemposition = linearLayoutManager.findLastVisibleItemPosition();
                    if (lastvisibleitemposition == articleListAdapter.getItemCount() - 1) {
                        if (!isLoading && current_page <= totalPage) {
                            current_page++;
                            searchFor();
                        }
                    }
                }

            }
        });

        binding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_SEARCH && !binding.etSearch.getText().toString().trim().equals("")) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.etSearch.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    articlesBeen.clear();
                    current_page = 1;
                    topic = binding.etSearch.getText().toString().trim();
                    if (Utils.isNetworkAvailable(SearchActivity.this)) {
                        Utils.showDialog(SearchActivity.this);
                        searchFor();
                    } else {
                        if (restored_Issubscribed) {
                            setOffLineData(topic);
                        } else {
                            Utils.showToast(getString(R.string.no_internet_connection));
                        }
                    }

                    return true;

                }
                return false;
            }
        });

        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etSearch.setText("");
            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (binding.etSearch.getText().toString().length() == 0) {
                    binding.clear.setVisibility(View.GONE);
                } else {
                    binding.clear.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isNetworkAvailable(SearchActivity.this)) {
                    articlesBeen.clear();
                    current_page = 1;
                    topic = binding.etSearch.getText().toString().trim();
                    articlesBeen.clear();
                    binding.lvMovies.setVisibility(View.GONE);
                    articleListAdapter.notifyDataSetChanged();
                    searchFor();
                } else {
                    binding.progress.setVisibility(View.GONE);
                    isLoading = false;
                    binding.refreshLayout.setRefreshing(false);
                }
            }
        });

        setColorData(sp.getBoolean("light", false));


    }

    private void setOffLineData(String searchKey) {
        if (restored_Issubscribed) {
            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(this);
            if (db != null) {
                List<Article> offlineArticles = db.articleDao().getArticleSearchKey(searchKey);

                if (offlineArticles != null && offlineArticles.size() == 0){
                    offlineArticles = db.articleDao().getArticleSearchKeyOne(searchKey);
                }

                if (offlineArticles != null) {
                    articlesBeen.clear();
                    totalPage = 0;
                    for (int i = 0; i < offlineArticles.size(); i++) {
                        Article article = offlineArticles.get(i);
                        Articles articleDb = new Articles();
                        articleDb.setId(article.getId());
                        articleDb.setIs_like(article.isLike());
                        articleDb.setIs_favourite(article.isBookMark());
                        articleDb.setArticle_is_locked(article.isArticleLocked());
                        articleDb.setDescription(article.getDescriptions());
                        articleDb.setTotal_likes(article.getNoOfLikes());
                        articleDb.setCreated_at(article.getTimeStamp());
                        articleDb.setTopic(article.getTopic());
                        articleDb.setPhoto(article.getArticle_photo());
                        if (article.getLanguage_type() == Utils.getArticleLang(this)) {
                            articlesBeen.add(articleDb);
                        }
                    }
                    articleListAdapter.notifyDataSetChanged();
                }
            }
        } else {
            Utils.showToast(getString(R.string.no_internet_connection));
        }
    }

    public void setColorData(boolean light) {

        if (light) {
            binding.nodata.setTextColor(Color.parseColor("#ffffff"));
            binding.lvMovies.setBackgroundColor(Color.parseColor("#363636"));
            binding.rel.setBackgroundColor(Color.parseColor("#363636"));
        } else {
            binding.nodata.setTextColor(Color.parseColor("#000000"));
            binding.lvMovies.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.rel.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        articleListAdapter.notifyDataSetChanged();
    }

    void setUpAdapter() {
        if (restored_Issubscribed) {
            articleListAdapter = new ArticleListAdapter(articlesBeen, this, null, 5);
            binding.lvMovies.setAdapter(articleListAdapter);
        } else {
            final Typeface font = Typeface.createFromAsset(getResources().getAssets(), "fonts/MRegular.ttf");
            binding.nodata.setTypeface(font);
            articleListAdapter = new ArticleListAdapter(articlesBeen, SearchActivity.this, null, 5);
            binding.lvMovies.setAdapter(articleListAdapter);
        }
    }

    private void articleClickView(int articleID) {
        selectedArticleID = articleID;
        if (prefs.getBoolean("articleViewChoice", false)) {
            //initRewardVideo(true, articleID);
        } else {
            showArticleOptionToWatchDialog(articleID);
        }
    }

    private void showArticleOptionToWatchDialog(final int articleID) {

        final Dialog dialog = new Dialog(SearchActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_rectangle_white_big_no_stroke));
        dialog.setContentView(R.layout.dialog_premium_dialog);
//        dialog.setCanceledOnTouchOutside(false);

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

        ck_select_default_choice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("articleViewChoice", b);
                editor.commit();
            }
        });

        btn_subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                startActivity(new Intent(SearchActivity.this, RemoveAdActivity.class));
            }
        });

        btn_watch_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //initRewardVideo(true, articleID);
            }
        });

        dialog.show();

    }

    /*private void initRewardVideo(boolean loadAndShow, final int articleID) {

//        Appodeal.disableLocationPermissionCheck();
        Appodeal.setTesting(false);
        Appodeal.setAutoCache(Appodeal.REWARDED_VIDEO, true);

        Appodeal.initialize(SearchActivity.this, APP_KEY, Appodeal.REWARDED_VIDEO);
        Appodeal.cache(SearchActivity.this, Appodeal.REWARDED_VIDEO);

        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {

            @Override
            public void onRewardedVideoLoaded(boolean b) {

            }

            @Override
            public void onRewardedVideoFailedToLoad() {
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateWatchedVideoStatus(articleID);
                    }
                }, 250);
            }

            @Override
            public void onRewardedVideoShown() {
                Log.e("onRewardedVideoShown ", "");
            }

            @Override
            public void onRewardedVideoShowFailed() {

            }

            @Override
            public void onRewardedVideoFinished(double v, String s) {

            }

            @Override
            public void onRewardedVideoClosed(boolean b) {
                Log.e("onRewardedVideoClosed ", "" + b);
                if (b) {
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateWatchedVideoStatus(articleID);
                        }
                    }, 250);
                }
            }

            @Override
            public void onRewardedVideoExpired() {

            }

            @Override
            public void onRewardedVideoClicked() {

            }
        });

        if (loadAndShow) {
            if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
                Appodeal.show(SearchActivity.this, Appodeal.REWARDED_VIDEO);
            }
        }
    }*/

    public void callArticleIntent(Articles currentSelectedArticle) {
        Intent intent = new Intent(SearchActivity.this, ArticleDetailActivity.class);
        intent.putExtra("Message", new Gson().toJson(currentSelectedArticle));
        intent.putExtra("IS_FROM", 3);
        startActivityForResult(intent, 102);
    }

    public void updateWatchedVideoStatus(int articleID) {

        String authToken = "";
        if (Constants.getUserData(SearchActivity.this) != null) {
            authToken = Constants.getUserData(SearchActivity.this).getAuthentication_token();
        }

        Log.e("authToken: ", authToken + " token");

        connectPost(SearchActivity.this, null, unlockArticle(BaseActivity.getKYC(), authToken, false, Constants.getV6Value(), articleID, true),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isRefresh = true;
                            }
                        }, 150);
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                        //Toast.makeText(SearchActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        Toast.makeText(SearchActivity.this, "Failure", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onConnectionFailure() {
                        Toast.makeText(SearchActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        Log.e("onException: ", headers.toString() + "");
                        Toast.makeText(SearchActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void showLoginDialog() {

        final Dialog dialog = new Dialog(SearchActivity.this);

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
                onFacebook();

            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

                googleSignInClient.signOut();
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, Constants.LOGIN_TYPE.GOOGLE);

                //Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                //startActivityForResult(signInIntent, Constants.LOGIN_TYPE.GOOGLE);
            }
        });

        dialog.show();
    }

    void onFacebook() {

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

                            } else {

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

    }

    void init() {

        prepareGoogle();

        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    void prepareGoogle() {
       /* GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(SearchActivity.this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();*/

        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(SearchActivity.this, gso);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 102) {

            Articles bean = new Gson().fromJson(data.getStringExtra("data"), Articles.class);

            for (int i = 0; i < articlesBeen.size(); i++) {
                Articles articles = (Articles) articlesBeen.get(i);
                if (bean.getId() == articles.getId()) {
                    articlesBeen.set(i, bean);
                    articleListAdapter.notifyDataSetChanged();
                    break;
                }

            }

        } else if (requestCode == Constants.LOGIN_TYPE.GOOGLE) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount acct = task.getResult(ApiException.class);

                if (acct.getEmail() == null && acct.getEmail().trim().isEmpty()) {
                    googleSignInClient.signOut();
                    Toast.makeText(SearchActivity.this, "null", Toast.LENGTH_LONG).show();
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
            }

        } else if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    void onSignin(Map<String, Object> params) {
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
                Constants.setUserData(SearchActivity.this, response);
                initializeBilling();
                updateToken();
                displayMessage(getString(R.string.msg_logged_in));
                binding.refreshLayout.setRefreshing(false);
                Utils.hideDialog();
                binding.progress.setVisibility(View.GONE);
                articlesBeen.clear();
                current_page = 1;
                topic = binding.etSearch.getText().toString().trim();
                articlesBeen.clear();
                articleListAdapter.notifyDataSetChanged();
                searchFor();
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

    }

    @Override
    protected void onResume() {
        for (Object item : articlesBeen) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.resume();
            }
        }
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

        if (isRefresh) {
            Log.e("In on Resume: ", "called");
            isLoading = false;
            binding.refreshLayout.setRefreshing(false);
            binding.progress.setVisibility(View.GONE);
            Utils.hideDialog();
            articlesBeen.clear();
            current_page = 1;
            topic = binding.etSearch.getText().toString().trim();
            articlesBeen.clear();
            articleListAdapter.notifyDataSetChanged();
            Utils.showDialog(this);
            searchFor();
            isRefresh = false;
        } else if (isFromLogin) {
            current_page = 1;
            isFromLogin = false;
            totalPage = 0;
            articlesBeen.clear();
            searchFor();
        }
    }

    public void searchFor() {
        if (totalPage != 0 && current_page > totalPage) {
            return;
        }
        String authToken = "";
        if (Constants.getUserData(SearchActivity.this) != null) {
            authToken = Constants.getUserData(SearchActivity.this).getAuthentication_token();
        }

        if (current_page == 1) {
            if (!binding.refreshLayout.isRefreshing()) {
                binding.progress.setVisibility(View.GONE);
            }
        } else {
            binding.progress.setVisibility(View.VISIBLE);
        }

        connectPost(SearchActivity.this, null,
                searchArticles(BaseActivity.getKYC(), authToken,
                        sp.getBoolean("guest_entry", false),
                        Constants.getV6Value(), topic,
                        String.valueOf(current_page),
                        Utils.getArticleLang(this)),
                new ApiConnection.ConnectListener() {

                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                        ArticlesData articlesData = new Gson().fromJson(response, ArticlesData.class);
                        isLoading = false;
                        binding.refreshLayout.setRefreshing(false);
                        Utils.hideDialog();
                        binding.progress.setVisibility(View.GONE);
                        totalPage = articlesData.getTotal_pages();
                        articlesBeen.addAll(articlesData.getArticles());
                        articleListAdapter.notifyDataSetChanged();

                        if (articleListAdapter.getItemCount() == 0) {
                            binding.nodata.setVisibility(View.VISIBLE);
                            binding.lvMovies.setVisibility(View.GONE);
                        } else {
                            binding.nodata.setVisibility(View.GONE);
                            binding.lvMovies.setVisibility(View.VISIBLE);
                        }

                        Log.i(TAG, "onResponseSuccess: "+articlesData.getArticles().size());
                        if (!restored_Issubscribed){
                            createNativeAd();
                        }

                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {

                        binding.refreshLayout.setRefreshing(false);

                        binding.progress.setVisibility(View.GONE);
                        Utils.hideDialog();
                        if (articleListAdapter.getItemCount() == 0) {
                            binding.nodata.setVisibility(View.VISIBLE);
                        } else {
                            binding.nodata.setVisibility(View.GONE);
                        }
                        //Toast.makeText(SearchActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        binding.refreshLayout.setRefreshing(false);

                        Utils.hideDialog();
                        binding.progress.setVisibility(View.GONE);
                        if (articleListAdapter.getItemCount() == 0) {
                            binding.nodata.setVisibility(View.VISIBLE);
                        } else {
                            binding.nodata.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onConnectionFailure() {

                        binding.refreshLayout.setRefreshing(false);

                        Utils.hideDialog();
                        binding.progress.setVisibility(View.GONE);
                        if (articleListAdapter.getItemCount() == 0) {
                            binding.nodata.setVisibility(View.VISIBLE);
                        } else {
                            binding.nodata.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        binding.refreshLayout.setRefreshing(false);
                        Utils.hideDialog();
                        binding.progress.setVisibility(View.GONE);
                        if (articleListAdapter.getItemCount() == 0) {
                            binding.nodata.setVisibility(View.VISIBLE);
                        } else {
                            binding.nodata.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void createNativeAd() {
        try {
            nativeAdList.clear();
            AdClass adClass = new AdClass();

            //---> initializing Google Ad SDK
            MobileAds.initialize(SearchActivity.this, initializationStatus -> {
                Log.d(TAG, "Google SDK Initialized");

                AdLoader adLoader = new AdLoader.Builder(SearchActivity.this, getString(R.string.native_adv_ids))

                        .forNativeAd(nativeAd -> {
                            Log.d(TAG, "Native Ad Loaded");
                            if (SearchActivity.this.isDestroyed()) {
                                nativeAd.destroy();
                                Log.d(TAG, "Native Ad Destroyed");
                                return;
                            }
                            nativeAdList.add(nativeAd);
                            int advPosition = 0;
                            if(articlesBeen.size() >= 10){
                                advPosition = articlesBeen.size() - 10;
                            }
                            if(!adClass.getAdLoader().isLoading()) {
                                for (int i=0;i<nativeAdList.size();i++) {
                                    for(int j=articlesBeen.size()-10;j<articlesBeen.size();j++) {
                                        if (j == advPosition) {
                                            advPosition += 5;
                                            this.articlesBeen.add(j,nativeAdList.get(i));
                                        }
                                    }
                                }
                                articleListAdapter.setObject(articlesBeen);
                            }
                        })

                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError adError) {
                                // Handle the failure by logging, altering the UI, and so on.
                                Log.d(TAG, "Native Ad Failed To Load");

                                new CountDownTimer(10000, 1000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        Log.d(TAG, "Timer : " + millisUntilFinished / 1000);
                                    }

                                    @Override
                                    public void onFinish() {
                                        Log.d(TAG, "Reloading NativeAd");

                                        createNativeAd();
                                    }
                                }.start();

                            }
                        })
                        .withNativeAdOptions(new NativeAdOptions.Builder()
                                .build())
                        .build();

                adLoader.loadAds(new AdRequest.Builder().build(), 3);
                adClass.setAdLoader(adLoader);
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
