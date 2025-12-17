package com.mayur.personalitydevelopment.fragment;

import static android.content.Context.MODE_PRIVATE;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.allQuotes;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.articles;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.signIn;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.unlockArticle;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.QueryPurchasesParams;
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
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.activity.ArticleDetailActivity;
import com.mayur.personalitydevelopment.activity.MainActivity;
import com.mayur.personalitydevelopment.activity.RemoveAdActivity;
import com.mayur.personalitydevelopment.adapter.ArticleListAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.database.Article;
import com.mayur.personalitydevelopment.database.ArticleCategory;
import com.mayur.personalitydevelopment.database.ArticleRoomDatabase;
import com.mayur.personalitydevelopment.database.Post;
import com.mayur.personalitydevelopment.database.Quote;
import com.mayur.personalitydevelopment.databinding.FragmentTab1Binding;
import com.mayur.personalitydevelopment.models.AdClass;
import com.mayur.personalitydevelopment.models.Articles;
import com.mayur.personalitydevelopment.models.ArticlesData;
import com.mayur.personalitydevelopment.models.PostData;
import com.mayur.personalitydevelopment.models.Quotes;
import com.mayur.personalitydevelopment.models.SubscriptionResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class Tab1 extends Fragment {

    private static final String TAG = Tab1.class.getSimpleName();
    public static boolean isFromLogin;
    private final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg2RciyklkPme5MJ4IZUa0/XhQdZvThkJVnLWQib4AHWeBRN9UKU5PY4khMZLIsoEESShh09QY2LoxpfvC1N26N8/GKIFuL5fhZ47X7zMq+9HlrFE6Yv0eTr0Pr6UfZ0GJXosPddZp2Ed7ybCjERSmdzL0IL3CYTF2ZY6+zIlBPvpQd/1aeM61VrDjPf1n9ba0v/O38sLOmmYf3CFBLbMjvlX2Hg1LfArA0MFXbaPtXuE9MXMEyx3Vsbg+qP/dpE/JOa3OKR75hSMM4+qumTZ2nCkgVyrMyt49XR7FFFXfW6rf84AzfO+isGe/WtG5oBtX92UYG71IlI1gO67Fz8bjQIDAQAB";
    private final String MERCHANT_ID = null;
    public boolean isLifetimeActive = false;
    public Boolean restored_Issubscribed = false;
    public ArticleListAdapter articleListAdapter;
    public SharedPreferences.Editor editor;
    public ArrayList<Object> articlesBeen = new ArrayList<>();
    public int totalPage = 0;
    public int current_page = 1;
    CallbackManager callbackManager;
    boolean isRefresh;
    private boolean subscribed = false;
    private String subscriptionType = "";
    private FragmentTab1Binding binding;
    private SharedPreferences sp, prefs;
    private boolean isLoading = false;
    private GoogleSignInClient googleSignInClient;
    private String filterOption = Utils.FILTER_NEW_TO_OLD;
    private MainActivity mainActivity;
    private BillingClient billingClient;
    private String inAppPurchaseToken = "";
    private RewardedAd rewardedAd;
    private List<NativeAd> nativeAdList;

    ActivityResultLauncher<Intent> startArticleActivity;

    public Tab1() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab1, container, false);
        nativeAdList = new ArrayList<>();
        sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.lvMovies.setLayoutManager(manager);

        prefs = this.requireActivity().getSharedPreferences("Purchase", MODE_PRIVATE);
        restored_Issubscribed = prefs.getBoolean("Issubscribed", false);

        final Typeface font = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/MRegular.ttf");
        binding.nodata.setTypeface(font);
        articleListAdapter = new ArticleListAdapter(articlesBeen, getActivity(), this, 1);
        binding.lvMovies.setAdapter(articleListAdapter);

        setColorData(sp.getBoolean("light", false));

        if (!Utils.isNetworkAvailable(requireActivity())) {
            setOffLineData();
        } else {
            current_page = 1;
            totalPage = 0;
            Utils.showDialog(getActivity());
            getTOKnowAPI();
        }

        binding.lvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Utils.isNetworkAvailable(requireActivity())) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.lvMovies.getLayoutManager();

                    assert linearLayoutManager != null;
                    int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                    if (lastVisibleItemPosition == articleListAdapter.getItemCount() - 1) {

                        if (!isLoading && current_page <= totalPage) {
                            current_page++;
                            getTOKnowAPI();
                        }
                    }
                } else {
                    totalPage = 0;
                    current_page = 1;
                    setOffLineData();
                    binding.progress.setVisibility(View.GONE);
                }
            }
        });

        binding.refreshLayout.setOnRefreshListener(this::onArticleRefresh);
        prepareGoogle();
        return binding.getRoot();
    }



    private void createNativeAd() {
        try {
            nativeAdList.clear();
            AdClass adClass = new AdClass();

            //---> initializing Google Ad SDK
            MobileAds.initialize(mainActivity, initializationStatus -> {
                AdLoader adLoader = new AdLoader.Builder(requireContext(), getString(R.string.native_adv_ids))
                        .forNativeAd(nativeAd -> {
                            if(getActivity() != null){
                                if (getActivity().isDestroyed()) {
                                    nativeAd.destroy();
                                    return;
                                }
                            }
                            nativeAdList.add(nativeAd);
                            int advPosition = 0;
                            if(articlesBeen.size() >= 10){
                                advPosition = articlesBeen.size() - 10;
                            }
                            if(!adClass.getAdLoader().isLoading()) {
                                for (int i=0;i<nativeAdList.size();i++) {
                                    for(int j=advPosition;j<articlesBeen.size();j++) {
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
                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                // Handle the failure by logging, altering the UI, and so on.

                                new CountDownTimer(10000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    @Override
                                    public void onFinish() {
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

    public void onArticleRefresh() {
        if (Utils.isNetworkAvailable(requireActivity())) {
            totalPage = 0;
            current_page = 1;
            isLoading = true;
            articlesBeen.clear();
            binding.lvMovies.setVisibility(View.GONE);
            articleListAdapter.notifyDataSetChanged();
            getTOKnowAPI();
        } else {
            totalPage = 0;
            current_page = 1;
            setOffLineData();
            binding.refreshLayout.setRefreshing(false);
            binding.progress.setVisibility(View.GONE);
        }
    }

    public void setOffLineData() {
        if (restored_Issubscribed) {
            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(getContext());
            if (db != null) {
                List<Article> offlineArticles = db.articleDao().getAllArticles();
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
                        if (article.getLanguage_type() == Utils.getArticleLang(getActivity())) {
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

    @Override
    public void onResume() {
        super.onResume();
        if (isRefresh) {
            isLoading = false;
            binding.refreshLayout.setRefreshing(false);
            binding.progress.setVisibility(View.GONE);
            if (articleListAdapter != null) {
                articleListAdapter.notifyDataSetChanged();
            }
        } else if (isFromLogin) {
            isFromLogin = false;
            current_page = 1;
            totalPage = 0;
            Utils.showDialog(getActivity());
            getTOKnowAPI();
        } else {
            if (articleListAdapter != null) {
                articleListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = ((MainActivity) context);

        startArticleActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Articles bean = new Gson().fromJson(data.getStringExtra("data"), Articles.class);
                        for (int i = 0; i < articlesBeen.size(); i++) {
                            Articles articles = (Articles) articlesBeen.get(i);
                            if (bean.getId() == articles.getId()) {
                                articlesBeen.set(i, bean);
                                articleListAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                });
    }

    public void getTOKnowAPI() {
        if (MainActivity.allowLoader) {
            Utils.showDialog(getActivity());
        }

        try {
            if (totalPage != 0 && current_page > totalPage) {
                return;
            }
            isLoading = true;

            if (current_page == 1) {
                if (!binding.refreshLayout.isRefreshing()) {
                    binding.progress.setVisibility(View.GONE);
                }
            } else {
                binding.progress.setVisibility(View.VISIBLE);
            }

            String authToken = "";
            if (Constants.getUserData(getActivity()) != null) {
                authToken = Constants.getUserData(getActivity()).getAuthentication_token();
            }

            connectPost(getActivity(), null, articles(BaseActivity.getKYC(),
                            authToken,
                            sp.getBoolean("guest_entry", false),
                            Constants.getV6Value(),
                            filterOption,
                            current_page + "", Utils.getArticleLang(requireActivity())),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                            MainActivity.allowLoader = false;
                            ArticlesData articlesData = new Gson().fromJson(response, ArticlesData.class);
                            isLoading = false;
                            binding.refreshLayout.setRefreshing(false);
                            binding.progress.setVisibility(View.GONE);
                            binding.lvMovies.setVisibility(View.VISIBLE);

                            Utils.hideDialog();

                            if (totalPage == 0) {
                                articlesBeen.clear();
                            }
                            totalPage = articlesData.getTotal_pages();
                            articlesBeen.addAll(articlesData.getArticles());
                            articleListAdapter.notifyDataSetChanged();
                            if (restored_Issubscribed) {
                                insertArticle(articlesData.getArticles());
                            } else {
                                createNativeAd();
                            }
                        }

                        @Override
                        public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                            MainActivity.allowLoader = false;
                            binding.refreshLayout.setRefreshing(false);
                            isLoading = false;
                            binding.progress.setVisibility(View.GONE);
                            Utils.hideDialog();
                            //skeletonScreen.hide();
                        }

                        @Override
                        public void onFailure(Headers headers) {
                            MainActivity.allowLoader = false;
                            try {
                                isLoading = false;
                                binding.refreshLayout.setRefreshing(false);
                                binding.progress.setVisibility(View.GONE);
                                Utils.hideDialog();
                                //skeletonScreen.hide();
                                Toast.makeText(getContext(), "Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                            }
                        }

                        @Override
                        public void onConnectionFailure() {
                            MainActivity.allowLoader = false;
                            try {
                                isLoading = false;
                                binding.refreshLayout.setRefreshing(false);
                                binding.progress.setVisibility(View.GONE);
                                Utils.hideDialog();
                                Toast.makeText(getContext(), "CC Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                            }
                        }

                        @Override
                        public void onException(Headers headers, int StatusCode) {
                            MainActivity.allowLoader = false;
                            try {
                                isLoading = false;
                                binding.refreshLayout.setRefreshing(false);
                                binding.progress.setVisibility(View.GONE);
                                Utils.hideDialog();
                                //skeletonScreen.hide();
                                Toast.makeText(getContext(), "EE Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                            }
                        }
                    });
        } catch (Exception e) {
            MainActivity.allowLoader = false;
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    private void insertArticle(List<Articles> articles) {
        if (articles != null && !articles.isEmpty()) {
            ArticleRoomDatabase articleRoomDatabase = ArticleRoomDatabase.getDatabase(getContext());
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
                articleDb.setLanguage_type(article.getLanguage_type());
                articleDb.setSearch_txt(article.getSearch_txt());
                articleDb.setLocked(article.isUser_article_is_locked());
                articleDb.setNoOfLikes(article.getTotal_likes());
                articleDb.setTimeStamp(article.getCreated_at());
                articleDb.setTopic(article.getTopic());
                articleDb.setArticle_photo(article.getPhoto());
                articleRoomDatabase.articleDao().insertArticle(articleDb);
                insertOfflineArticleCategory(article);
            }
        }

        if (restored_Issubscribed) {
            ArticleRoomDatabase articleRoomDatabase = ArticleRoomDatabase.getDatabase(getContext());
            if (articleRoomDatabase.postDao().getAllPost().isEmpty()) {
                getPostList();
            }

            if (articleRoomDatabase.quotesDao().getAllQuotes().isEmpty()) {
                getQuotes();
            }
        }

    }

    private void insertOfflineArticleCategory(Articles articles) {
        ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(getActivity());
        if (db != null) {
            if (articles != null) {
                ArticleCategory articleCategory = new ArticleCategory();
                articleCategory.setCategory_article_id(articles.getId());
                if (articles.getCategory_ids() != null && !articles.getCategory_ids().isEmpty()) {
                    for (int j = 0; j < articles.getCategory_ids().size(); j++) {
                        articleCategory.setArticle_category_id(articles.getCategory_ids().get(j));
                        db.articleCategoryDao().insertArticleCategory(articleCategory);
                    }
                }
            }
        }
    }

    public void getPostList() {
        try {
            String authToken = "";
            if (Constants.getUserData(getActivity()) != null) {
                authToken = Constants.getUserData(getActivity()).getAuthentication_token();
            }

            connectPost(getActivity(), null, ApiCallBack.getPostList(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), 1 + ""), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Utils.hideDialog();

                        ArrayList<PostData> posts = new ArrayList<PostData>();
                        JSONArray jsonObjects = jsonObject.getJSONArray("posts");

                        for (int i = 0; i < jsonObjects.length(); i++) {
                            PostData post = new PostData();
                            JSONObject jsonObjectData = jsonObjects.getJSONObject(i);
                            post.setId(jsonObjectData.getInt("id"));
                            post.setCreatedAt(jsonObjectData.getLong("created_at"));
                            post.setFirstName(jsonObjectData.getString("first_name"));
                            post.setIsLike(jsonObjectData.getBoolean("is_like"));
                            post.setLastName(jsonObjectData.getString("last_name"));
                            post.setPostData(jsonObjectData.getString("post_data"));
                            post.setProfilePhotoThumb(jsonObjectData.getString("profile_photo_thumb"));
                            post.setShowOptions(jsonObjectData.getBoolean("show_options"));
                            post.setTotalLikes(jsonObjectData.getInt("total_likes"));
                            post.setUpdatedAt(jsonObjectData.getLong("updated_at"));
                            posts.add(post);
                        }

                        List<PostData> postList = new ArrayList<>();
                        postList.addAll(posts);
                        insertOfflinePost(postList);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.hideDialog();
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.hideDialog();
                    }
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

        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    private void insertOfflinePost(List<PostData> postList) {
        if (restored_Issubscribed) {
            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(getContext());
            if (db != null) {
                if (postList != null && !postList.isEmpty()) {
                    for (int i = 0; i < postList.size(); i++) {
                        PostData post = postList.get(i);
                        Post postDb = new Post();
                        postDb.setId(post.getId());
                        postDb.setCreatedDate(post.getCreatedAt());
                        postDb.setFirstName(post.getFirstName());
                        postDb.setLastName(post.getLastName());
                        postDb.setLike(post.isIsLike());
                        postDb.setPostData(post.getPostData());
                        postDb.setProfileUrl(post.getProfilePhotoThumb());
                        postDb.setShowOptions(post.isShowOptions());
                        postDb.setTotalLike(post.getTotalLikes());
                        postDb.setDelete(false);
                        db.postDao().insertPost(postDb);
                        Log.i(TAG, "insertOfflinePost: " + i);
                    }
                    Log.i(TAG, "insertOfflinePost: Size " + db.postDao().getAllPost().size());
                }
            }

        }
    }

    public void getQuotes() {

        try {

            String authToken = "";
            if (Constants.getUserData(getActivity()) != null) {
                authToken = Constants.getUserData(getActivity()).getAuthentication_token();
            }

            connectPost(getActivity(), null, allQuotes(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), "" + 1), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Quotes articlesData = new Gson().fromJson(response, Quotes.class);
                    Utils.hideDialog();
                    isLoading = false;
                    if (current_page == 1) {

                    }
                    binding.progress.setVisibility(View.GONE);
                    totalPage = articlesData.getTotal_pages();
                    if (!articlesData.getQuotes().isEmpty()) {
                        insertOfflineQuotes(articlesData.getQuotes());
                    }
                    Utils.hideDialog();
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    binding.progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    binding.progress.setVisibility(View.GONE);
                }

                @Override
                public void onConnectionFailure() {
                    binding.progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    private void insertOfflineQuotes(List<Quotes.QuotesBean> quotes) {
        if (restored_Issubscribed) {
            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(getContext());
            if (db != null) {
                if (quotes != null && !quotes.isEmpty()) {
                    for (int i = 0; i < quotes.size(); i++) {
                        Quotes.QuotesBean quotesBean = quotes.get(i);
                        Quote quote = new Quote();
                        quote.setId(quotesBean.getId());
                        quote.setImageUrl(quotesBean.getImage_url());
                        db.quotesDao().insertQuotes(quote);
                        Log.i(TAG, "insertOfflineQuotes: " + i);
                    }
                    Log.i(TAG, "insertOfflineQuotes: Size " + db.quotesDao().getAllQuotes().size());
                }
            }
        }
    }

    public void showLoginDialog() {

        final Dialog dialog = new Dialog(requireActivity());

        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_login_2);

        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.rounded_rectangle_white_big_no_stroke));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth() * 85 / 100;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MRegular.ttf");
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
            startGoogleIntentActivity.launch(signInIntent);
        });

        dialog.show();
    }

    ActivityResultLauncher<Intent> startGoogleIntentActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    try {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount acct = task.getResult(ApiException.class);
                        if (acct.getEmail() == null && acct.getEmail().trim().isEmpty()) {
                            Toast.makeText(getActivity(), "null", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Map<String, Object> params = new HashMap<>();
                        params.put("email", acct.getEmail().trim());
                        params.put("first_name", Objects.requireNonNull(acct.getDisplayName()).split("\\s+")[0].trim());
                        params.put("last_name", acct.getDisplayName().split("\\s+")[1].trim());
                        if (acct.getPhotoUrl() != null && !String.valueOf(acct.getPhotoUrl()).trim().isEmpty()) {
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
                }
            });

    void prepareGoogle() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    public void onFacebook() {
        try {
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().logInWithReadPermissions(this, List.of("public_profile, email"));

            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {

                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            try {

                                JSONObject fbResponse = new JSONObject(String.valueOf(response.getJSONObject()));

                                fbResponse.getString("email");
                                if (!fbResponse.getString("email").isEmpty()) {

                                    Map<String, Object> params = new HashMap<>();
                                    params.put("email", fbResponse.getString("email").trim());
                                    params.put("first_name", fbResponse.getString("name").split("\\s+")[0].trim());
                                    params.put("last_name", fbResponse.getString("name").split("\\s+")[1].trim());
                                    params.put("user_profile_photo", "https://graph.facebook.com/" + fbResponse.getString("id") + "/picture?type=large".trim());
                                    params.put("social_id", fbResponse.getString("id"));
                                    params.put("login_type", Constants.LOGIN_TYPE.FACEBOOK);
                                    Utils.showDialog(getActivity());
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
            Utils.hideDialog();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
             if (FacebookSdk.isFacebookRequestCode(requestCode)) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    void onSignin(Map<String, Object> params) {
        try {
            params.put("platform", "android");
            sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String token = sp.getString("FCM_TOKEN", "");
            if (!token.isEmpty()) {
                params.put("device_token", token);
            } else {
                params.put("device_token", "test");
            }
            //params.put("device_token", FirebaseInstanceId.getInstance().getToken());
            params.put("uuid", sp.getString("UUID", ""));

            connectPost(getActivity(), null, signIn(params), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                    editor = sp.edit();
                    editor.putBoolean("guest_entry", false);
                    editor.commit();

                    Constants.setUserData(getActivity(), response);
                    initializeBilling();
                    updateToken();
                    Toast.makeText(mainActivity, getResources().getString(R.string.msg_logged_in), Toast.LENGTH_SHORT).show();

                    ((MainActivity) requireActivity()).drawerAndNavigation();
                    ((MainActivity) requireActivity()).hideItem();

                    binding.refreshLayout.setRefreshing(false);
                    Utils.hideDialog();
                    binding.progress.setVisibility(View.GONE);

                    totalPage = 0;
                    current_page = 1;
                    isLoading = false;
                    articlesBeen.clear();
                    articleListAdapter.notifyDataSetChanged();
                    Utils.showDialog(getActivity());
                    getTOKnowAPI();

                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "EE Failure", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }
    private void showArticleOptionToWatchDialog(final Articles articles) {

        final Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_premium_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth() * 85 / 100;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        CheckBox ck_select_default_choice = dialog.findViewById(R.id.ck_select_default_choice);

        TextView btn_subscribe = dialog.findViewById(R.id.btn_subscribe);
        TextView btn_watch_ad = dialog.findViewById(R.id.btn_watch_ad);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MRegular.ttf");
        TextView txt_titile = dialog.findViewById(R.id.txt_titile);
        TextView txt_content = dialog.findViewById(R.id.txt_content);
        TextView txt_ck_content = dialog.findViewById(R.id.txt_ck_content);

        txt_titile.setTypeface(font);
        txt_content.setTypeface(font);
        txt_ck_content.setTypeface(font);

        ck_select_default_choice.setOnCheckedChangeListener((compoundButton, b) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("articleViewChoice", b);
            editor.apply();
        });

        btn_subscribe.setOnClickListener(view -> {
            dialog.dismiss();
            startActivity(new Intent(getActivity(), RemoveAdActivity.class));
        });

        btn_watch_ad.setOnClickListener(view -> {
            dialog.dismiss();
            initRewardVideo( articles);
        });

        dialog.show();

    }

    private void initRewardVideo(Articles articles) {
        try {
            Utils.showDialog(getActivity());
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(mainActivity, getString(R.string.reward_adv_ids),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d(TAG, loadAdError.toString());
                            rewardedAd = null;
                            Utils.hideDialog();
                            Handler handler = new Handler();
                            handler.postDelayed(() -> updateWatchedVideoStatus(articles), 250);
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd ad) {
                            rewardedAd = ad;
                            Log.d(TAG, "Ad was loaded.");
                            Utils.hideDialog();
                            if (rewardedAd != null) {
                                rewardedAd.show(getActivity(), rewardItem -> {
                                    // Handle the reward.
                                    Log.d(TAG, "The user earned the reward.");
                                });
                            } else {
                                Log.d(TAG, "The rewarded ad wasn't ready yet.");
                            }

                            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdClicked() {
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when ad is dismissed.
                                    // Set the ad reference to null so you don't show the ad a second time.
                                    Handler handler = new Handler();
                                    handler.postDelayed(() -> updateWatchedVideoStatus(articles), 250);
                                    rewardedAd = null;
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    // Called when ad fails to show.
                                    rewardedAd = null;
                                }

                                @Override
                                public void onAdImpression() {
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                }
                            });
                        }
                    });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public void callArticleIntent(Articles currentSelectedArticle) {


        Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
        intent.putExtra("Message", new Gson().toJson(currentSelectedArticle));
        intent.putExtra("IS_FROM", 1);
        startArticleActivity.launch(intent);
    }

    public void updateWatchedVideoStatus(final Articles articles) {
        String authToken = "";
        if (Constants.getUserData(getActivity()) != null) {
            authToken = Constants.getUserData(getActivity()).getAuthentication_token();
        }

        connectPost(getActivity(), null, unlockArticle(BaseActivity.getKYC(), authToken, false, Constants.getV6Value(), articles.getId(), true),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        isLoading = false;

                        binding.refreshLayout.setRefreshing(false);
                        binding.progress.setVisibility(View.GONE);

                        callArticleIntent(articles);

                        Handler h = new Handler();
                        h.postDelayed(() -> isRefresh = true, 150);
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                        //Toast.makeText(getActivity(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        Toast.makeText(getActivity(), "Failure", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onConnectionFailure() {
                        Toast.makeText(getActivity(), "CC Failure", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        Log.e("onException: ", headers.toString() + "");
                        Toast.makeText(getActivity(), "EE Failure", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void setColorData(boolean light) {
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    public void showFilterDialog() {

        final LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.popup_filter, null);

        RadioButton rbNewToOld = promptsView.findViewById(R.id.rbNewToOld);
        RadioButton rbOldToNew = promptsView.findViewById(R.id.rbOldToNew);
        RadioButton rbLiked = promptsView.findViewById(R.id.rbLiked);
        RadioButton rbPremium = promptsView.findViewById(R.id.rbPremium);

        Typeface font = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/MRegular.ttf");

        rbNewToOld.setTypeface(font);
        rbOldToNew.setTypeface(font);
        rbLiked.setTypeface(font);
        rbPremium.setTypeface(font);

        if (filterOption.equalsIgnoreCase(Utils.FILTER_NEW_TO_OLD)) {
            rbNewToOld.setChecked(true);
        } else if (filterOption.equalsIgnoreCase(Utils.FILTER_OLD_TO_NEW)) {
            rbOldToNew.setChecked(true);
        } else if (filterOption.equalsIgnoreCase(Utils.FILTER_MOST_LIKED)) {
            rbLiked.setChecked(true);
        } else if (filterOption.equalsIgnoreCase(Utils.FILTER_PREMIUM)) {
            rbPremium.setChecked(true);
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptsView);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        rbNewToOld.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && isChecked) {
                current_page = 1;
                totalPage = 0;
                filterOption = Utils.FILTER_NEW_TO_OLD;
                Utils.showDialog(getActivity());
                getTOKnowAPI();

                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }

            }
        });

        rbOldToNew.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && isChecked) {
                current_page = 1;
                totalPage = 0;
                filterOption = Utils.FILTER_OLD_TO_NEW;
                Utils.showDialog(getActivity());
                getTOKnowAPI();

                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });

        rbLiked.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && isChecked) {
                current_page = 1;
                totalPage = 0;
                filterOption = Utils.FILTER_MOST_LIKED;
                Utils.showDialog(getActivity());
                getTOKnowAPI();
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });

        rbPremium.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && isChecked) {
                current_page = 1;
                totalPage = 0;
                filterOption = Utils.FILTER_PREMIUM;
                Utils.showDialog(getActivity());
                getTOKnowAPI();
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    private void updateToken() {
        String authToken = "";
        if (Constants.getUserData(getActivity()) != null) {
            authToken = Constants.getUserData(getActivity()).getAuthentication_token();
        }
        connectPost(getActivity(), null, ApiCallBack.updateToken(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), Utils.getFcmToken(getActivity()), sp.getString("UUID", "")), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {

            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
            }

            @Override
            public void onFailure(Headers headers) {
            }

            @Override
            public void onConnectionFailure() {
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
            }
        });
    }

    private void initializeBilling() {
         billingClient = BillingClient.newBuilder(requireActivity())
                .setListener((billingResult, purchases) -> Log.d(TAG, "onPurchasesUpdated: "))
                .enablePendingPurchases(PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts()   // enable pending for one-time products
                        .build())
                .enableAutoServiceReconnection() // recommended for automatic reconnection
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
        QueryPurchasesParams subsParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build();

        billingClient.queryPurchasesAsync(subsParams,
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
                        if (subscribed) {
                            SharedPreferences pref = getActivity().getSharedPreferences("Purchase", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("Issubscribed", subscribed);
                            editor.apply();
                            restored_Issubscribed = pref.getBoolean("Issubscribed", false);
                            callSetSubscriptionAPI();
                        }
                    }else {
                        getSubscriptionAPI();
                    }
                });
    }

    private void callSetSubscriptionAPI() {
        try {
            String authToken = "";
            if (Constants.getUserData(getActivity()) != null) {
                authToken = Constants.getUserData(getActivity()).getAuthentication_token();
            }

            Utils.showDialog(getActivity());
            connectPost(getActivity(), null, ApiCallBack.setSubscriptionDetail(BaseActivity.getKYC(),
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
                                Toast.makeText(getActivity(), "Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                            }
                        }

                        @Override
                        public void onConnectionFailure() {
                            try {
                                Utils.hideDialog();
                                Toast.makeText(getActivity(), "CC Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                            }
                        }

                        @Override
                        public void onException(Headers headers, int StatusCode) {
                            try {
                                Utils.hideDialog();
                                Toast.makeText(getActivity(), "EE Failure", Toast.LENGTH_LONG).show();
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
            if (Constants.getUserData(getActivity()) != null) {
                authToken = Constants.getUserData(getActivity()).getAuthentication_token();
            }

            Utils.showDialog(getActivity());
            connectPost(getActivity(), null, ApiCallBack.getSubscriptionDetail(BaseActivity.getKYC(),
                            authToken,
                            sp.getBoolean("guest_entry", false),
                            Constants.getV6Value()),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                            Utils.hideDialog();
                            SubscriptionResponse.SubscriptionData subscriptionData = new Gson().fromJson(response, SubscriptionResponse.SubscriptionData.class);
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("Purchase", MODE_PRIVATE).edit();
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
                            Utils.hideDialog();
                        }

                        @Override
                        public void onFailure(Headers headers) {
                            try {
                                Utils.hideDialog();
                                Toast.makeText(getActivity(), "Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                            }
                        }

                        @Override
                        public void onConnectionFailure() {
                            try {
                                Utils.hideDialog();
                                Toast.makeText(getActivity(), "CC Failure", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.hideDialog();
                            }
                        }

                        @Override
                        public void onException(Headers headers, int StatusCode) {
                            try {
                                Utils.hideDialog();
                                Toast.makeText(getActivity(), "EE Failure", Toast.LENGTH_LONG).show();
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
