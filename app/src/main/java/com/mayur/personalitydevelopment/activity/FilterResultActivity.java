package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.filterCategoryWise;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.signIn;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.unlockArticle;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.mayur.personalitydevelopment.databinding.ActivityFilterResultBinding;
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


public class FilterResultActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "FilterResultActivity";
    public static boolean isFromLogin;
    public ArrayList<Object> articlesBeen = new ArrayList<>();
    CallbackManager callbackManager;
    private ActivityFilterResultBinding binding;
    private int totalPage = 0;
    private int current_page = 1;
    private boolean isLoading = false;
    private ArticleListAdapter articleListAdapter;
    private SharedPreferences prefs;
    private int selectedArticleID = 0;
    private boolean isRefresh;
    private Articles currentSelected;
    private GoogleSignInClient googleSignInClient;
    private List<NativeAd> nativeAdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nativeAdList = new ArrayList<>();
        prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter_result);
        binding.lvMovies.setLayoutManager(new LinearLayoutManager(FilterResultActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setUpAdapter();
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

                if (Utils.isNetworkAvailable(FilterResultActivity.this)) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.lvMovies.getLayoutManager();

                    int lastvisibleitemposition = linearLayoutManager.findLastVisibleItemPosition();

                    if (lastvisibleitemposition == articleListAdapter.getItemCount() - 1) {

                        if (!isLoading && current_page <= totalPage) {
                            current_page++;
                            filterWiseSerch();
                        }
                    }
                }

            }
        });

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isNetworkAvailable(FilterResultActivity.this)) {
                    articlesBeen.clear();
                    totalPage = 0;
                    current_page = 1;
                    binding.lvMovies.setVisibility(View.GONE);
                    filterWiseSerch();
                } else {
                    binding.refreshLayout.setRefreshing(false);
                    binding.progress.setVisibility(View.GONE);
                    isLoading = false;
                }
            }
        });

        setColorData(sp.getBoolean("light", false));
        binding.tvTitle.setText(getIntent().getExtras().getString("category_name"));
        if (Utils.isNetworkAvailable(FilterResultActivity.this)) {
            Utils.showDialog(this);
            filterWiseSerch();
        } else {
            binding.refreshLayout.setRefreshing(false);
            binding.progress.setVisibility(View.GONE);
            isLoading = false;
            setOffLineData();
        }

    }

    private void setOffLineData() {
        if (restored_Issubscribed) {
            binding.progress.setVisibility(View.GONE);
            binding.refreshLayout.setRefreshing(false);
            isLoading = false;
            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(FilterResultActivity.this);
            if (db != null) {
                List<Article> offlineArticles = db.articleDao().getArticleByCategory(getIntent().getExtras().getInt("category_id"));
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
                        articlesBeen.add(articleDb);
                    }

                    articleListAdapter.notifyDataSetChanged();
                }
            }
        } else {
            binding.refreshLayout.setRefreshing(false);
            binding.progress.setVisibility(View.GONE);
            isLoading = false;
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
        final Typeface font = Typeface.createFromAsset(FilterResultActivity.this.getAssets(), "fonts/MRegular.ttf");
        binding.nodata.setTypeface(font);
        articleListAdapter = new ArticleListAdapter(articlesBeen, this, null, 2);
        binding.lvMovies.setAdapter(articleListAdapter);
    }

    public void articleClickView(Articles articles) {

        selectedArticleID = articles.getId();
        if (prefs.getBoolean("articleViewChoice", false)) {
            //initRewardVideo(true, articles);
        } else {
            showArticleOptionToWatchDialog(articles);
        }

    }

    private void showArticleOptionToWatchDialog(final Articles articles) {

        final Dialog dialog = new Dialog(FilterResultActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_rectangle_white_big_no_stroke));
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
                startActivity(new Intent(FilterResultActivity.this, RemoveAdActivity.class));
            }
        });

        btn_watch_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //initRewardVideo(true, articles);
            }
        });

        dialog.show();
    }

    /*private void initRewardVideo(boolean loadAndShow, final Articles articles) {
//        Appodeal.disableLocationPermissionCheck();
        Appodeal.setTesting(false);
        Appodeal.setAutoCache(Appodeal.REWARDED_VIDEO, true);
        Appodeal.initialize(FilterResultActivity.this, APP_KEY, Appodeal.REWARDED_VIDEO, list -> {
            //Appodeal initialization finished
            Appodeal.cache(FilterResultActivity.this, Appodeal.REWARDED_VIDEO);
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
                            updateWatchedVideoStatus(articles);
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
                    if (b) {
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateWatchedVideoStatus(articles);
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
                    Appodeal.show(FilterResultActivity.this, Appodeal.REWARDED_VIDEO);
                }
            }
        });
    }*/

    public void callArticleIntent(Articles currentSelectedArticle) {
        Log.e("intnent topic: ", currentSelectedArticle.getTopic() + " mehul");
        currentSelected = currentSelectedArticle;
        Intent intent = new Intent(FilterResultActivity.this, ArticleDetailActivity.class);
        intent.putExtra("Message", new Gson().toJson(currentSelectedArticle));
        intent.putExtra("IS_FROM", 2);
        startActivityForResult(intent, 102);
    }

    public void updateWatchedVideoStatus(final Articles articles) {

        String authToken = "";
        if (Constants.getUserData(FilterResultActivity.this) != null) {
            authToken = Constants.getUserData(FilterResultActivity.this).getAuthentication_token();
        }

        Log.e("AAAAauthToken: ", authToken + " token");

        connectPost(FilterResultActivity.this, null, unlockArticle(BaseActivity.getKYC(), authToken, false, Constants.getV6Value(), articles.getId(), true),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                        Log.e("MMMM article response: ", response);

                        callArticleIntent(articles);

                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //isRefresh = true;
                                currentSelected.setUser_article_is_locked(false);

                            }
                        }, 150);
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                        // Toast.makeText(FilterResultActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        Toast.makeText(FilterResultActivity.this, "Failure", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onConnectionFailure() {
                        Toast.makeText(FilterResultActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        Log.e("onException: ", headers.toString() + "");
                        Toast.makeText(FilterResultActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void showLoginDialog() {

        final Dialog dialog = new Dialog(FilterResultActivity.this);

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
                .enableAutoManage(FilterResultActivity.this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();*/

        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(FilterResultActivity.this, gso);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
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

               /* GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount acct = result.getSignInAccount();
                    if (acct.getEmail() == null && acct.getEmail().trim().isEmpty()) {
                        googleApiClient.clearDefaultAccountAndReconnect();
                        Toast.makeText(FilterResultActivity.this, "null", Toast.LENGTH_LONG).show();
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

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount acct = task.getResult(ApiException.class);

                //GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                //GoogleSignInAccount acct = result.getSignInAccount();

                if (acct.getEmail() == null && acct.getEmail().trim().isEmpty()) {
                    googleSignInClient.signOut();
                    Toast.makeText(FilterResultActivity.this, "null", Toast.LENGTH_LONG).show();
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

            } else if (FacebookSdk.isFacebookRequestCode(requestCode)) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.somehing_want_wrong), Toast.LENGTH_LONG).show();
        }
    }

    void onSignin(Map<String, Object> params) {
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
                    Constants.setUserData(FilterResultActivity.this, response);
                    initializeBilling();
                    updateToken();
                    displayMessage(getString(R.string.msg_logged_in));
                    binding.refreshLayout.setRefreshing(false);
                    Utils.showDialog(getApplicationContext());
                    binding.progress.setVisibility(View.GONE);
                    articlesBeen.clear();
                    current_page = 1;
                    articlesBeen.clear();
                    articleListAdapter.notifyDataSetChanged();
                    Utils.showDialog(FilterResultActivity.this);
                    filterWiseSerch();
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    // Toast.makeText(getApplicationContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
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
    protected void onResume() {
        super.onResume();
        try {
            if (isRefresh) {
                Log.e("In on Resume: ", "called");
                isLoading = false;
                binding.refreshLayout.setRefreshing(false);
                binding.progress.setVisibility(View.GONE);
                Utils.hideDialog();
                articlesBeen.clear();
                current_page = 1;
                articlesBeen.clear();
                articleListAdapter.notifyDataSetChanged();
                Utils.showDialog(FilterResultActivity.this);
                filterWiseSerch();
                isRefresh = false;
            } else if (isFromLogin) {
                isFromLogin = false;
                current_page = 1;
                totalPage = 0;
                Utils.showDialog(FilterResultActivity.this);
                filterWiseSerch();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filterWiseSerch() {
        Log.e("tag service call: ", " mehul");
        if (totalPage != 0 && current_page > totalPage) {
            return;
        }
        String authToken = "";
        if (Constants.getUserData(FilterResultActivity.this) != null) {
            authToken = Constants.getUserData(FilterResultActivity.this).getAuthentication_token();
        }

        if (current_page == 1) {
            if (!binding.refreshLayout.isRefreshing()) {
                binding.progress.setVisibility(View.GONE);
            }
        } else {
            binding.progress.setVisibility(View.VISIBLE);
        }

        connectPost(FilterResultActivity.this, null, filterCategoryWise(BaseActivity.getKYC(), authToken,
                sp.getBoolean("guest_entry", false), Constants.getV6Value(),
                current_page, getIntent().getExtras().getInt("category_id")),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                        ArticlesData articlesData = new Gson().fromJson(response, ArticlesData.class);
                        isLoading = false;
                        Utils.hideDialog();

                        binding.refreshLayout.setRefreshing(false);
                        if (totalPage == 0) {
                            //skeletonScreen.hide();
                            articlesBeen.clear();
                        }

                        binding.progress.setVisibility(View.GONE);
                        totalPage = articlesData.getTotal_pages();
                        articlesBeen.addAll(articlesData.getArticles());
                        articleListAdapter.notifyDataSetChanged();

                        if (!restored_Issubscribed){
                           createNativeAd();
                        }

                        if (articleListAdapter.getItemCount() == 0) {
                            binding.nodata.setVisibility(View.VISIBLE);
                            binding.lvMovies.setVisibility(View.GONE);
                        } else {
                            binding.nodata.setVisibility(View.GONE);
                            binding.lvMovies.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {

                        binding.refreshLayout.setRefreshing(false);
                        Utils.hideDialog();
                        binding.progress.setVisibility(View.GONE);
                        if (articleListAdapter.getItemCount() == 0) {
                            binding.nodata.setVisibility(View.VISIBLE);
                            binding.lvMovies.setVisibility(View.GONE);
                        } else {
                            binding.nodata.setVisibility(View.GONE);
                            binding.lvMovies.setVisibility(View.VISIBLE);
                        }
                        Utils.hideDialog();
                        //skeletonScreen.hide();
                        //Toast.makeText(FilterResultActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        binding.refreshLayout.setRefreshing(false);
                        Utils.hideDialog();
                        binding.progress.setVisibility(View.GONE);
                        if (articleListAdapter.getItemCount() == 0) {
                            binding.nodata.setVisibility(View.VISIBLE);
                            binding.lvMovies.setVisibility(View.GONE);
                        } else {
                            binding.nodata.setVisibility(View.GONE);
                            binding.lvMovies.setVisibility(View.VISIBLE);
                        }
                        Utils.hideDialog();
                        //skeletonScreen.hide();
                    }

                    @Override
                    public void onConnectionFailure() {
                        binding.refreshLayout.setRefreshing(false);
                        Utils.hideDialog();
                        binding.progress.setVisibility(View.GONE);
                        if (articleListAdapter.getItemCount() == 0) {
                            binding.nodata.setVisibility(View.VISIBLE);
                            binding.lvMovies.setVisibility(View.GONE);
                        } else {
                            binding.nodata.setVisibility(View.GONE);
                            binding.lvMovies.setVisibility(View.VISIBLE);
                        }
                        Utils.hideDialog();
                        //skeletonScreen.hide();
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        binding.refreshLayout.setRefreshing(false);
                        Utils.hideDialog();
                        binding.progress.setVisibility(View.GONE);
                        if (articleListAdapter.getItemCount() == 0) {
                            binding.nodata.setVisibility(View.VISIBLE);
                            binding.lvMovies.setVisibility(View.GONE);
                        } else {
                            binding.nodata.setVisibility(View.GONE);
                            binding.lvMovies.setVisibility(View.VISIBLE);
                        }
                        Utils.hideDialog();
                        //skeletonScreen.hide();
                    }
                });

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

    private void createNativeAd() {
        try {
            nativeAdList.clear();
            AdClass adClass = new AdClass();

            //---> initializing Google Ad SDK
            MobileAds.initialize(FilterResultActivity.this, initializationStatus -> {
                Log.d(TAG, "Google SDK Initialized");

                AdLoader adLoader = new AdLoader.Builder(FilterResultActivity.this, getString(R.string.native_adv_ids))

                        .forNativeAd(nativeAd -> {
                            Log.d(TAG, "Native Ad Loaded");
                            if (FilterResultActivity.this.isDestroyed()) {
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
}
