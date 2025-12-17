package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.likedArticleList;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.unlockArticle;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.ArticleListAdapter;
import com.mayur.personalitydevelopment.app.PersonalityDevelopmentApp;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.connection.ConnectivityReceiver;
import com.mayur.personalitydevelopment.database.Article;
import com.mayur.personalitydevelopment.database.ArticleCategory;
import com.mayur.personalitydevelopment.database.ArticleRoomDatabase;
import com.mayur.personalitydevelopment.databinding.ActivityLikesBinding;
import com.mayur.personalitydevelopment.fragment.NoInternetConnectionFragment;
import com.mayur.personalitydevelopment.models.AdClass;
import com.mayur.personalitydevelopment.models.Articles;
import com.mayur.personalitydevelopment.models.ArticlesData;
import com.mayur.personalitydevelopment.models.PostData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class LikesActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    public ArrayList<Object> articlesBeen = new ArrayList<>();
    int totalPage = 0;
    int current_page = 1;
    boolean isLoading = false;
    private ActivityLikesBinding binding;
    private SharedPreferences prefs;
    private boolean beforeViewSelectedArticleLikeStatus;
    private int selectedArticleID = 0;
    private boolean isRefresh;
    private Articles currentSelectedArticle;
    private ArticleListAdapter articleListAdapter;
    private String TAG = LikesActivity.class.getSimpleName();
    private boolean isSynchRunning = false;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private List<NativeAd> nativeAdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nativeAdList = new ArrayList<>();
        prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_likes);
        LinearLayoutManager manager = new LinearLayoutManager(LikesActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.lvMovies.setLayoutManager(manager);

        setUpAdapter();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (Utils.isNetworkAvailable(LikesActivity.this)) {
            Utils.showDialog(this);
            if (restored_Issubscribed) {
                if (!isSynchRunning) {
                    isSynchRunning = true;
                    syncData(true);
                }
            } else {
                getLikes();
            }
        } else {
            binding.progress.setVisibility(View.GONE);
            binding.refreshLayout.setRefreshing(false);
            isLoading = false;
            setOffLineData();
        }

        setColorData(sp.getBoolean("light", false));

        binding.lvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Utils.isNetworkAvailable(LikesActivity.this)) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.lvMovies.getLayoutManager();
                    int lastvisibleitemposition = linearLayoutManager.findLastVisibleItemPosition();
                    if (lastvisibleitemposition == articleListAdapter.getItemCount() - 1) {
                        if (!isLoading && current_page <= totalPage) {
                            current_page++;
                            getLikes();
                        }
                    }
                }
            }
        });

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isNetworkAvailable(LikesActivity.this)) {
                    totalPage = 0;
                    current_page = 1;
                    isLoading = false;
                    articlesBeen.clear();
                    binding.lvMovies.setVisibility(View.GONE);
                    articleListAdapter.notifyDataSetChanged();
                    if (restored_Issubscribed) {
                        if (!isSynchRunning) {
                            isSynchRunning = true;
                            syncData(true);
                        }
                    } else {
                        getLikes();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    binding.refreshLayout.setRefreshing(false);
                    isLoading = false;
                }
            }
        });


    }

    private void setOffLineData() {
        if (restored_Issubscribed) {
            binding.progress.setVisibility(View.GONE);
            binding.refreshLayout.setRefreshing(false);
            isLoading = false;
            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(LikesActivity.this);
            if (db != null) {
                List<Article> offlineArticles = db.articleDao().getLikesArticle(true);
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

    @Override
    protected void onResume() {
        super.onResume();
        PersonalityDevelopmentApp.getInstance().setConnectivityListener(this);
        if (isRefresh) {
            Log.e("In on Resume: ", "called");
            isLoading = false;
            binding.refreshLayout.setRefreshing(false);
            binding.progress.setVisibility(View.GONE);
            Utils.hideDialog();
            totalPage = 0;
            current_page = 1;
            isLoading = false;
            articlesBeen.clear();
            articleListAdapter.notifyDataSetChanged();
            Utils.showDialog(this);
            getLikes();
            isRefresh = false;
        }
    }

    public void onBackPressed() {
        this.finish();
    }

    void setUpAdapter() {
        if (restored_Issubscribed) {
            articleListAdapter = new ArticleListAdapter(articlesBeen, this, null, 3);
            binding.lvMovies.setAdapter(articleListAdapter);
        } else {
            final Typeface font = Typeface.createFromAsset(getResources().getAssets(), "fonts/MRegular.ttf");
            binding.nodata.setTypeface(font);
            articleListAdapter = new ArticleListAdapter(articlesBeen, LikesActivity.this, null, 3);
            binding.lvMovies.setAdapter(articleListAdapter);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        SharedPreferences prefs = this.getSharedPreferences("Purchase", MODE_PRIVATE);
        restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
        Log.i(TAG, "isSubscribed : " + restored_Issubscribed);

        if (isConnected) {
            Log.i(TAG, "onNetworkConnectionChanged: ON ");
            if (restored_Issubscribed) {
                if (!isSynchRunning) {
                    isSynchRunning = true;
                    syncData(false);
                }
            } else {
                Utils.hideDialog();
            }
        } else {
            //TODO need to display no connections.
            Log.i(TAG, "onNetworkConnectionChanged: OFF ");
            if (!restored_Issubscribed) {
                noInterNetView();
            } else {
                Utils.hideDialog();
            }
        }
    }

    private void syncData(boolean isRefresh) {
        isSynchRunning = true;
        ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(LikesActivity.this);
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
            Log.i(TAG, "onNetworkConnectionChanged: ids Bookmark status : " + bookMarkStatus);
            if (ids != null && ids.length() > 0) {
                updateArticleLike(ids, likesStatus, isRefresh, articleIds);
            } else {
                isSynchRunning = false;
                if (isRefresh) {
                    getLikes();
                }
            }
        } else {
            Utils.hideDialog();
            isSynchRunning = false;
            if (isRefresh) {
                getLikes();
            }
        }
    }

    private void noInterNetView() {
        try {
            fragmentTransaction = fragmentManager.beginTransaction();
            NoInternetConnectionFragment noInterNetFragment = new NoInternetConnectionFragment();
            fragmentTransaction.replace(R.id.rel, noInterNetFragment);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateArticleLike(final String articleIds, String articleLikeStatus, final boolean isRefresh, final ArrayList<String> articleSynchList) {

        String authToken = "";
        if (Constants.getUserData(LikesActivity.this) != null) {
            authToken = Constants.getUserData(LikesActivity.this).getAuthentication_token();
        }

        connectPost(LikesActivity.this, null, ApiCallBack.multipleArticleLike(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), articleIds, articleLikeStatus), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                Log.i(TAG, "onResponseSuccess: Article Like Update Successfully.");

                ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(LikesActivity.this);
                if (articleSynchList != null && articleSynchList.size() > 0) {
                    for (int i = 0; i < articleSynchList.size(); i++) {
                        db.articleDao().setSynch(false, Integer.parseInt(articleSynchList.get(i)));
                    }
                }

                isSynchRunning = false;
                if (isRefresh) {
                    getLikes();
                }
            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                //Toast.makeText(MainActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Headers headers) {
                Log.i(TAG, "onFailure: Sync fail");
            }

            @Override
            public void onConnectionFailure() {
                Log.i(TAG, "onConnectionFailure: Sync fail");
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                Log.i(TAG, "onException: Sync fail");
            }
        });

    }

    public void articleClickView(int articleID) {
        selectedArticleID = articleID;
        if (prefs.getBoolean("articleViewChoice", false)) {
            //initRewardVideo(true, articleID);
        } else {
            showArticleOptionToWatchDialog(articleID);
        }
    }

    private void showArticleOptionToWatchDialog(final int articleID) {

        final Dialog dialog = new Dialog(LikesActivity.this);
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
                startActivity(new Intent(LikesActivity.this, RemoveAdActivity.class));
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

        Appodeal.initialize(LikesActivity.this, APP_KEY, Appodeal.REWARDED_VIDEO, list -> {
            //Appodeal initialization finished
            Appodeal.cache(LikesActivity.this, Appodeal.REWARDED_VIDEO);

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
                    Appodeal.show(LikesActivity.this, Appodeal.REWARDED_VIDEO);
                }
            }
        });

    }*/

    public void callArticleIntent(Articles currentSelectedArticle) {
        Intent intent = new Intent(LikesActivity.this, ArticleDetailActivity.class);
        intent.putExtra("Message", new Gson().toJson(currentSelectedArticle));
//        startActivityForResult(intent, 102);
        startArticleDetailIntentActivity.launch(intent);
    }

    ActivityResultLauncher<Intent> startArticleDetailIntentActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    Articles bean = new Gson().fromJson(data.getStringExtra("data"), Articles.class);
                    for (int i = 0; i < articlesBeen.size(); i++) {
                        Articles articles = (Articles) articlesBeen.get(i);
                        if (bean.getId() == articles.getId()) {
                            if (!bean.isIs_like()) {
                                articlesBeen.remove(i);
                                articleListAdapter.notifyDataSetChanged();
                                //customAdapter.notifyDataSetChanged();
                                break;
                            } else {
                                if (bean.getId() == articles.getId()) {
                                    articlesBeen.set(i, bean);
                                    articleListAdapter.notifyDataSetChanged();
                                    //customAdapter.notifyDataSetChanged();

                                    break;
                                }
                            }
                        }
                    }
                }

                if (articleListAdapter.getItemCount() == 0) {
                    binding.nodata.setVisibility(View.VISIBLE);
                } else {
                    binding.nodata.setVisibility(View.GONE);
                }

            });

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            if(data != null && data.getStringExtra("data") != null){
                Articles bean = new Gson().fromJson(data.getStringExtra("data"), Articles.class);
                for (int i = 0; i < articlesBeen.size(); i++) {
                    Articles articles = (Articles) articlesBeen.get(i);
                    if (bean.getId() == articles.getId()) {
                        if (!bean.isIs_like()) {
                            articlesBeen.remove(i);
                            articleListAdapter.notifyDataSetChanged();
                            //customAdapter.notifyDataSetChanged();
                            break;
                        } else {
                            if (bean.getId() == articles.getId()) {
                                articlesBeen.set(i, bean);
                                articleListAdapter.notifyDataSetChanged();
                                //customAdapter.notifyDataSetChanged();

                                break;
                            }
                        }
                    }
                }
            }
        }

        if (articleListAdapter.getItemCount() == 0) {
            binding.nodata.setVisibility(View.VISIBLE);
        } else {
            binding.nodata.setVisibility(View.GONE);
        }
    }*/

    public void updateWatchedVideoStatus(int articleID) {

        String authToken = "";
        if (Constants.getUserData(LikesActivity.this) != null) {
            authToken = Constants.getUserData(LikesActivity.this).getAuthentication_token();
        }

        Log.e("authToken: ", authToken + " token");

        connectPost(LikesActivity.this, null, unlockArticle(BaseActivity.getKYC(), authToken, false, Constants.getV6Value(), articleID, true),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                        //callArticleIntent();

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
                        //Toast.makeText(LikesActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        Toast.makeText(LikesActivity.this, "Failure", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onConnectionFailure() {
                        Toast.makeText(LikesActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        Log.e("onException: ", headers.toString() + "");
                        Toast.makeText(LikesActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                    }
                });
    }




    public void getLikes() {

        if (totalPage != 0 && current_page > totalPage) {
            return;
        }

        String authToken = "";
        if (Constants.getUserData(LikesActivity.this) != null) {
            authToken = Constants.getUserData(LikesActivity.this).getAuthentication_token();
        }

        if (current_page == 1) {
            if (!binding.refreshLayout.isRefreshing()) {
                binding.progress.setVisibility(View.GONE);
            }
        } else {
            binding.progress.setVisibility(View.VISIBLE);
        }

        connectPost(LikesActivity.this, null, likedArticleList(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), "" + current_page), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                ArticlesData articlesData = new Gson().fromJson(response, ArticlesData.class);
                isLoading = false;
                binding.refreshLayout.setRefreshing(false);
                binding.progress.setVisibility(View.GONE);
                totalPage = articlesData.getTotal_pages();
                articlesBeen.addAll(articlesData.getArticles());

                Utils.hideDialog();
                if (articleListAdapter.getItemCount() == 0) {
                    binding.nodata.setVisibility(View.VISIBLE);
                    binding.lvMovies.setVisibility(View.GONE);
                } else {
                    binding.nodata.setVisibility(View.GONE);
                    binding.lvMovies.setVisibility(View.VISIBLE);
                }
                articleListAdapter.notifyDataSetChanged();

                if (restored_Issubscribed) {
                    insertArticle(articlesData.getArticles());
                } else {
                    Log.i(TAG, "onResponseSuccess: "+articlesData.getArticles().size());
                    if(articlesBeen.size() >= 5){
                        createNativeAd();
                    }

                }
            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                binding.nodata.setVisibility(View.VISIBLE);
                binding.refreshLayout.setRefreshing(false);
                binding.progress.setVisibility(View.GONE);
                Utils.hideDialog();
                //Toast.makeText(LikesActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Headers headers) {
                binding.progress.setVisibility(View.GONE);
                binding.refreshLayout.setRefreshing(false);
                Utils.hideDialog();
                Toast.makeText(LikesActivity.this, "Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionFailure() {
                binding.refreshLayout.setRefreshing(false);
                binding.progress.setVisibility(View.GONE);
                Utils.hideDialog();
                Toast.makeText(LikesActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                binding.refreshLayout.setRefreshing(false);
                binding.progress.setVisibility(View.GONE);
                Utils.hideDialog();
                Toast.makeText(LikesActivity.this, "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void insertArticle(List<Articles> articles) {
        if (articles != null && !articles.isEmpty()) {
            ArticleRoomDatabase articleRoomDatabase = ArticleRoomDatabase.getDatabase(LikesActivity.this);
            for (int i = 0; i < articles.size(); i++) {
                Articles article = articles.get(i);
                Article articleDb = new Article();
                articleDb.setId(article.getId());
                articleDb.setLike(article.isIs_like());
                articleDb.setLikeTimeStamp(System.currentTimeMillis());
                articleDb.setBookMark(article.isIs_favourite());
                articleDb.setBookMarkTimeStamp(System.currentTimeMillis());
                articleDb.setArticleLocked(article.isArticle_is_locked());
                articleDb.setDescriptions(article.getDescription());
                articleDb.setLocked(article.isUser_article_is_locked());
                articleDb.setNoOfLikes(article.getTotal_likes());
                articleDb.setTimeStamp(article.getCreated_at());
                articleDb.setTopic(article.getTopic());
                articleDb.setArticle_photo(article.getPhoto());
                insertOfflineArticleCategory(article);
                articleRoomDatabase.articleDao().insertArticle(articleDb);
                Log.i(TAG, "insertArticle: IS BOOKMARK " + article.isIs_favourite());
                Log.i(TAG, "DATABASE ROOM doInBackground: " + i);
            }
        }
    }

    private void insertOfflineArticleCategory(Articles articles) {
        ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(LikesActivity.this);
        if (db != null) {
            if (articles != null) {
                ArticleCategory articleCategory = new ArticleCategory();
                articleCategory.setCategory_article_id(articles.getId());
                if (articles.getCategory_ids() != null && articles.getCategory_ids().size() > 0) {
                    for (int j = 0; j < articles.getCategory_ids().size(); j++) {
                        articleCategory.setArticle_category_id(articles.getCategory_ids().get(j));
                        db.articleCategoryDao().insertArticleCategory(articleCategory);
                        Log.i(TAG, "insertArticleCategory: ARTICLE CATEGORY " + articles.getTopic());
                    }
                } else {
                    Log.i(TAG, "insertArticleCategory: No CATEGORY " + articles.getTopic());
                }

            }
            Log.i(TAG, "insertArticleCategory: Size " + db.categoryDao().getAllCategory().size());
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

        // customAdapter.notifyDataSetChanged();
        articleListAdapter.notifyDataSetChanged();
    }

    private void createNativeAd() {
        try {
            nativeAdList.clear();
            AdClass adClass = new AdClass();

            //---> initializing Google Ad SDK
            MobileAds.initialize(LikesActivity.this, initializationStatus -> {
                Log.d(TAG, "Google SDK Initialized");
                AdLoader adLoader = new AdLoader.Builder(LikesActivity.this, getString(R.string.native_adv_ids))
                        .forNativeAd(nativeAd -> {
                            Log.d(TAG, "Native Ad Loaded");
                            if (LikesActivity.this.isDestroyed()) {
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
                                for (int i=0;i < nativeAdList.size();i++) {
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
