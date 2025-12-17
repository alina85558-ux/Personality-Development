package com.mayur.personalitydevelopment.activity;

import static android.content.Intent.getIntent;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.signIn;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.CommentDataSource;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.CommentAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.database.ArticleRoomDatabase;
import com.mayur.personalitydevelopment.databinding.ActivityPostDetailBinding;
import com.mayur.personalitydevelopment.fragment.OptionsBottomSheetFragment;
import com.mayur.personalitydevelopment.listener.BottomSheetListener;
import com.mayur.personalitydevelopment.listener.BottomSheetMenuClickListener;
import com.mayur.personalitydevelopment.listener.BottomSheetSubMenuClickListener;
import com.mayur.personalitydevelopment.listener.LikeBtnClickListener;
import com.mayur.personalitydevelopment.listener.LikeUnlikeClickListener;
import com.mayur.personalitydevelopment.models.Comment;
import com.mayur.personalitydevelopment.models.PostData;
import com.mayur.personalitydevelopment.models.Reply;
import com.mayur.personalitydevelopment.models.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class PostDetailActivity extends BaseActivity implements View.OnClickListener, LikeUnlikeClickListener, LikeBtnClickListener, BottomSheetMenuClickListener, BottomSheetListener, BottomSheetSubMenuClickListener {
    private final int EDIT_POST = 101;
    CallbackManager callbackManager;
    ArrayList<Comment> commentArrayList;
    UserData userData;
    private ActivityPostDetailBinding binding;
    private PostData postData;
    private RelativeLayout remove_ad_rl;
    private MediaPlayer mMediaPlayer;
    private String postId = "";
    private GoogleSignInClient googleSignInClient;
    private CommentAdapter adapter;
    private Comment currentCommentForReply;
    private Reply currentInnerReply;
    private int currentCommentPos;
    private boolean isDarkTheme = false;
    private Comment mDeleteComment;
    private Reply mDeleteReply;
    private int mIndexSubDelete;
    private int fromMain = 0;
    private boolean mIsCommentWriter = false;
    private String selectedCommentId = "";
    private int totalComments = 0;
    private LinearLayoutManager linearLayoutManager;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(PostDetailActivity.this, R.layout.activity_post_detail);
        remove_ad_rl = findViewById(R.id.remove_ad);
        userData = Constants.getUserData(PostDetailActivity.this);
        mAdView = findViewById(R.id.adView);
        init();

        binding.linearLike.setOnClickListener(this);
        binding.imgOption.setOnClickListener(this);
        binding.txtLikes.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        changeReadingMode();
        initViews();

        final RequestOptions placeholder_error = new RequestOptions().error(R.drawable.ic_user).placeholder(R.drawable.ic_user).diskCacheStrategy(DiskCacheStrategy.ALL);
        if (userData != null && userData.getProfileThumb() != null && userData.getProfileThumb() != "") {
            Glide.with(this).load(userData.getProfileThumb()).apply(placeholder_error).into(binding.profileImage);
        }

        swipeRefreshLayoutListn();
    }

    private void swipeRefreshLayoutListn() {
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isNetworkAvailable(PostDetailActivity.this)) {
                    binding.refreshLayout.setRefreshing(false);
                    getPostDetail(postId);
                } else {
                    Utils.showToast(getString(R.string.no_internet_connection));
                    binding.refreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void init() {
        if (sp.getBoolean("guest_entry", false)) {
            try {
                prepareGoogle();
                FacebookSdk.sdkInitialize(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.e("Received Intent Target", getIntent().getComponent() + "");

        if (getIntent().hasExtra(Constants.POST_ID)) {
            postId = getIntent().getStringExtra(Constants.POST_ID);
            Log.e("Post Detial Screen", postId.toString());
            getPostDetail(postId);
        }
    }

    void prepareGoogle() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(PostDetailActivity.this, gso);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPostDetail(String postID) {
        try {
            Utils.showDialog(this);
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, ApiCallBack.getPostDetailAPI(BaseActivity.getKYC(),
                    authToken,
                    sp.getBoolean("guest_entry", false),
                    Constants.getV6Value(),
                    1,
                    postID), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    try {
                        postData = new Gson().fromJson(response, PostData.class);
                        setPostDetail(postData);
                        totalComments = postData.getTotalComments();
                        getComments();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    binding.refreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Headers headers) {
                    binding.refreshLayout.setRefreshing(false);
                    Utils.hideDialog();
                    try {
                        Toast.makeText(PostDetailActivity.this, "Failure", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    binding.refreshLayout.setRefreshing(false);
                    Toast.makeText(PostDetailActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    binding.refreshLayout.setRefreshing(false);
                    Toast.makeText(PostDetailActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Utils.hideDialog();
            binding.refreshLayout.setRefreshing(false);
            e.printStackTrace();
        }
    }

    private void setPostDetail(PostData postDetail) {
        binding.postName.setText(String.format("%s %s", postDetail.getFirstName(), postDetail.getLastName()));
        binding.postDate.setReferenceTime(postDetail.getCreatedAt());
        binding.postDetails.setText(Html.fromHtml(postDetail.getPostData()));

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MRegular.ttf");
        binding.postName.setTypeface(font);
        binding.postDetails.setTypeface(font);
        binding.txtLikes.setTypeface(font);

        binding.likeIcon.setChecked(postDetail.isIsLike());

        if (Utils.convertNumberToCount(postDetail.getTotalLikes()).equalsIgnoreCase("0")) {
            binding.txtLikes.setText(getResources().getString(R.string.likes));
            binding.txtLikes.setVisibility(View.GONE);
        } else {
            binding.txtLikes.setText(String.format("%s%s", Utils.convertNumberToCount(postDetail.getTotalLikes()), getResources().getString(R.string.likes)));
            binding.txtLikes.setVisibility(View.VISIBLE);
        }

        RequestOptions options = new RequestOptions();
        final RequestOptions placeholder_error = options.error(R.drawable.ic_user).placeholder(R.drawable.ic_user).diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(this)
                .load(postDetail.getProfilePhotoThumb())
                .apply(placeholder_error).into(binding.imgProfilePic);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLike:
                onLikeClick();
                break;
            case R.id.img_option:
                if (Utils.isNetworkAvailable(this)) {
                    openOptions(binding.imgOption);
                } else {
                    Utils.showToast(getString(R.string.no_internet_connection));
                }
                break;
            case R.id.txtLikes:
                Intent intent = new Intent(this, LikeUserListActivity.class);
                intent.putExtra(Constants.POST_ID, postId);
                startActivity(intent);
                break;
        }
    }

    public boolean checkUserLogin() {
        if (sp.getBoolean("guest_entry", false)) {
            showLoginDialog();
            return false;
        } else {
            return true;
        }
    }

    private void onLikeClick() {
        if (!sp.getBoolean("guest_entry", false)) {
            int totalCount;
            if (!postData.isIsLike()) {
                play(R.raw.like_click_sound);
                totalCount = (postData.getTotalLikes() + 1);
            } else {
                totalCount = (postData.getTotalLikes() - 1);
            }
            binding.likeIcon.setChecked(!postData.isIsLike());

            SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
            restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
            if (restored_Issubscribed && !Utils.isNetworkAvailable(this)) {
                ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(this);
                db.postDao().setLikes(totalCount, !postData.isIsLike(), postData.getId());
                db.postDao().setSynch(true, postData.getId());
                if (!postData.isIsLike()) {
                    postData.setIsLike(!postData.isIsLike());
                    postData.setTotalLikes(postData.getTotalLikes() + 1);
                } else {
                    postData.setIsLike(!postData.isIsLike());
                    postData.setTotalLikes(postData.getTotalLikes() - 1);
                }
            } else {
                getPostLikes(!postData.isIsLike());
            }
        } else {
            binding.linearLike.setClickable(true);
            showLoginDialog();
        }
    }

    public void openOptions(View viewFilter) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.dialog_filter, null, false);
            final PopupWindow popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);

            LinearLayout llEdit = layout.findViewById(R.id.llEdit);
            LinearLayout llDelete = layout.findViewById(R.id.llDelete);
            LinearLayout llReport = layout.findViewById(R.id.llReport);

            if (postData.isShowOptions()) {
                llEdit.setVisibility(View.VISIBLE);
                llDelete.setVisibility(View.VISIBLE);
                llReport.setVisibility(View.GONE);
            } else {
                llEdit.setVisibility(View.GONE);
                llDelete.setVisibility(View.GONE);
                llReport.setVisibility(View.VISIBLE);
            }

            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            final View view1 = popupWindow.getContentView();
            view1.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            popupWindow.showAsDropDown(viewFilter, 0, -10);

            llEdit.setOnClickListener(v -> {
                popupWindow.dismiss();
                Intent createPost = new Intent(PostDetailActivity.this, CreateArticleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("POST_DATA", postData);
                createPost.putExtras(bundle);
                startActivityForResult(createPost, EDIT_POST);
                overridePendingTransition(0, 0);
            });

            llDelete.setOnClickListener(v -> {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE: //Yes button clicked
                            dialog.dismiss();
                            getPostDelete();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE: //No button clicked
                            dialog.dismiss();
                            break;
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
                builder.setMessage(getString(R.string.confirm_delete)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(getString(R.string.no), dialogClickListener).show();

                popupWindow.dismiss();
            });

            llReport.setOnClickListener(v -> {
                showPostReportDialog();
                popupWindow.dismiss();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPostDelete() {

        if (!Utils.isNetworkAvailable(this)) {
            Utils.showToast(getString(R.string.no_internet_connection));
            return;
        }

        try {
            Utils.showDialog(PostDetailActivity.this);
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }
            connectPost(this, null, ApiCallBack.getPostDelete(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), postData.getId() + ""), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    try {
                        Toast.makeText(PostDetailActivity.this, "Post successfully deleted", Toast.LENGTH_SHORT).show();
                        try {
                            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(PostDetailActivity.this);
                            db.postDao().deletePost(postData.getId());
                            Intent intent = getIntent();
                            intent.putExtra(Constants.ACTION_, Constants.POST_DELETE);
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                    Utils.hideDialog();
                    Toast.makeText(PostDetailActivity.this, "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    Toast.makeText(PostDetailActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(PostDetailActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPostReportDialog() {
        try {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: //Yes button clicked
                        dialog.dismiss();
                        getPostReport();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE: //No button clicked
                        dialog.dismiss();
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.str_report_confirm)).setPositiveButton(getString(R.string.report), dialogClickListener)
                    .setNegativeButton(getString(R.string.cancel), dialogClickListener).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPostReport() {

        if (!Utils.isNetworkAvailable(this)) {
            Utils.showToast(getString(R.string.no_internet_connection));
            return;
        }

        try {
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }
            connectPost(PostDetailActivity.this, null, ApiCallBack.getPostReport(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), postData.getId() + ""), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    try {
                        Toast.makeText(PostDetailActivity.this, "We will verify that post shortly", Toast.LENGTH_SHORT).show();
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
                    Utils.hideDialog();
                    Toast.makeText(PostDetailActivity.this, "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    Toast.makeText(PostDetailActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(PostDetailActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play(int rid) {
        stop();
        mMediaPlayer = MediaPlayer.create(this, rid);
        mMediaPlayer.setOnCompletionListener(mediaPlayer -> stop());
        mMediaPlayer.start();
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void getPostLikes(final boolean status) {

        if (!Utils.isNetworkAvailable(this)) {
            Utils.showToast(getString(R.string.no_internet_connection));
            return;
        }

        try {
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(PostDetailActivity.this, null, ApiCallBack.getPostLike(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), postData.getId() + "", status), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    try {
                        binding.linearLike.setClickable(true);
                        postData.setIsLike(status);
                        if (status) {
                            postData.setTotalLikes(postData.getTotalLikes() + 1);
                        } else {
                            postData.setTotalLikes(postData.getTotalLikes() - 1);
                        }

                        if (Utils.convertNumberToCount(postData.getTotalLikes()).equalsIgnoreCase("0")) {
                            binding.txtLikes.setText(getResources().getString(R.string.likes));
                            binding.txtLikes.setVisibility(View.GONE);
                        } else {
                            binding.txtLikes.setText(String.format("%s%s", Utils.convertNumberToCount(postData.getTotalLikes()), getResources().getString(R.string.likes)));
                            binding.txtLikes.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    binding.linearLike.setClickable(true);
                    binding.likeIcon.setChecked(postData.isIsLike());
                    binding.txtLikes.setClickable(true);
                    binding.txtLikes.setText(String.format("%s%s", Utils.convertNumberToCount((postData.getTotalLikes())), getResources().getString(R.string.likes)));
                }

                @Override
                public void onFailure(Headers headers) {
                    binding.linearLike.setClickable(true);
                    binding.likeIcon.setChecked(postData.isIsLike());
                    binding.txtLikes.setText(String.format("%s%s", Utils.convertNumberToCount((postData.getTotalLikes())), getResources().getString(R.string.likes)));
                    binding.txtLikes.setClickable(true);
                }

                @Override
                public void onConnectionFailure() {
                    binding.linearLike.setClickable(true);
                    binding.likeIcon.setChecked(postData.isIsLike());
                    binding.txtLikes.setText(String.format("%s%s", Utils.convertNumberToCount((postData.getTotalLikes())), getResources().getString(R.string.likes)));
                    binding.txtLikes.setClickable(true);
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    binding.linearLike.setClickable(true);
                    binding.likeIcon.setChecked(postData.isIsLike());
                    binding.txtLikes.setText(String.format("%s%s", Utils.convertNumberToCount((postData.getTotalLikes())), getResources().getString(R.string.likes)));
                    binding.txtLikes.setClickable(true);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            if (!restored_Issubscribed) {
                MobileAds.initialize(this, initializationStatus -> {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    if (mAdView != null) {
//                        mAdView.setAdSize(AdSize.BANNER);
                        mAdView.loadAd(adRequest);
                        mAdView.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                if (mAdView != null) {
                    mAdView.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_POST) {
            if (resultCode == Activity.RESULT_OK) {
                String postDesc = data.getStringExtra("POST_DESC");
                binding.postDetails.setText(Html.fromHtml(postDesc));
                postData.setPostData(postDesc);
            }
        } else if (requestCode == Constants.LOGIN_TYPE.GOOGLE) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount acct = task.getResult(ApiException.class);
                assert acct != null;
                if (acct.getEmail() == null && acct.getEmail().trim().isEmpty()) {
                    Toast.makeText(PostDetailActivity.this, "null", Toast.LENGTH_LONG).show();
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
        } else if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showLoginDialog() {
        try {
            final Dialog dialog = new Dialog(this);
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

            fb.setOnClickListener(view -> {
                dialog.dismiss();
                onFacebook();
            });

            google.setOnClickListener(view -> {
                dialog.dismiss();
                googleSignInClient.signOut();
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, Constants.LOGIN_TYPE.GOOGLE);
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onFacebook() {
        try {
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile, email"));

            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
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


//  CommentCode

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

            connectPost(PostDetailActivity.this, null, signIn(params), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    editor = sp.edit();
                    editor.putBoolean("guest_entry", false);
                    editor.commit();

                    Constants.setUserData(PostDetailActivity.this, response);
                    initializeBilling();
                    updateToken();
                    displayMessage(getString(R.string.msg_logged_in));
                    userData = Constants.getUserData(PostDetailActivity.this);
                    getPostDetail(postId);
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    Toast.makeText(PostDetailActivity.this, "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    Toast.makeText(PostDetailActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(PostDetailActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        if (Constants.IS_FROM_NOTIFICATION_ACT) {
            Constants.IS_FROM_NOTIFICATION_ACT = false;
            Intent i = new Intent(getApplicationContext(), NotificationActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return;
        }
        if (getIntent().hasExtra(Constants.POST_ID)) {
            Log.e("On BackPressed", "Post_id");
            if (getIntent().hasExtra(Constants.FROM) && Objects.requireNonNull(getIntent().getStringExtra(Constants.FROM)).equalsIgnoreCase(Constants.FROM_NOTIFICATION)) {
                Log.e("hasExtra", Constants.FROM+"");

                Intent intent = new Intent(PostDetailActivity.this, MainActivity.class);
                intent.putExtra(Constants.FROM, Constants.FROM_NOTIFICATION_POST);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Get the component name of the nested intent.
                ComponentName name = intent.resolveActivity(getPackageManager());
                // Check that the package name and class name contain the expected values.

                Log.e("getPackageName()",  name.getPackageName());
                Log.e("getClassName()",  name.getClassName());

                if (name.getPackageName().equals("com.mayur.personalitydevelopment"))
//                        && name.getClassName().equals("com.mayur.personalitydevelopment.activity.PostDetailActivity"))
                {
                    // Redirect the nested intent.
                    startActivity(intent);
                    finish();
                }
            } else {
                Log.e("No HasExtra", "Called");
                Intent intent = getIntent();
                intent.putExtra(Constants.ACTION_, Constants.POST_UPDATE);
                intent.putExtra("POST_DATA", new Gson().toJson(postData));
                intent.putExtra("totalComments", totalComments);
                // Get the component name of the nested intent.
                ComponentName name = intent.resolveActivity(getPackageManager());
                // Check that the package name and class name contain the expected values.
                if (name.getPackageName().equals("com.mayur.personalitydevelopment") &&
                        name.getClassName().equals("com.mayur.personalitydevelopment.activity.PostDetailActivity")) {
                    // Redirect the nested intent.
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }
    }

    public void changeReadingMode() {
        if (sp.getBoolean("light", false)) {
            isDarkTheme = true;
            binding.cardViewPost.setCardBackgroundColor(Color.parseColor("#464646"));
            binding.postDate.setTextColor(Color.parseColor("#ffffff"));
            binding.postName.setTextColor(Color.parseColor("#ffffff"));
            binding.postDetails.setTextColor(Color.parseColor("#ffffff"));
            binding.txtLikes.setTextColor(Color.parseColor("#ffffff"));
            binding.imgOption.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_options_white));
            binding.rlMain.setBackgroundColor(getResources().getColor(R.color.dark_grey));
            binding.editComment.setTextColor(Color.parseColor("#ffffff"));
            binding.llAddCommentView.setBackgroundColor(ContextCompat.getColor(PostDetailActivity.this, R.color.dark_grey));
//            binding.adsRem.setTextColor(getResources().getColor(R.color.white));
//            binding.adsRem.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_white));
        } else {
            isDarkTheme = false;
            binding.cardViewPost.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.postDetails.setTextColor(Color.parseColor("#000000"));
            binding.postDate.setTextColor(Color.parseColor("#838383"));
            binding.postName.setTextColor(Color.parseColor("#000000"));
            binding.txtLikes.setTextColor(Color.parseColor("#464646"));
            binding.imgOption.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_post_options));
            binding.rlMain.setBackgroundColor(getResources().getColor(R.color.white));
            binding.editComment.setTextColor(Color.parseColor("#000000"));
            binding.llAddCommentView.setBackgroundColor(ContextCompat.getColor(PostDetailActivity.this, R.color.white));
//            binding.adsRem.setTextColor(getResources().getColor(R.color.colorPrimary));
//            binding.adsRem.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
        }
    }

    public void Premium_fun(View v) {
        try {
            boolean isAvailable = Utils.isIabServiceAvailable(this);
            if (isAvailable) {
                Intent purchase = new Intent(PostDetailActivity.this, RemoveAdActivity.class);
                startActivity(purchase);
            } else {
                Toast.makeText(PostDetailActivity.this, "In-App Subscription not supported", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapterInterface() {
        adapter.setMakeCommentInterface((commentPos, comment) -> {
            currentCommentPos = commentPos;
            currentCommentForReply = comment;
            StringBuilder firstName = new StringBuilder(comment.getFirstName());
            firstName.setCharAt(0, Character.toUpperCase(firstName.charAt(0)));
            StringBuilder lastName = new StringBuilder();
            if (comment.getLastLame() != null && !comment.getLastLame().equals("") && comment.getLastLame().length() > 0) {
                lastName = new StringBuilder(comment.getLastLame());
                lastName.setCharAt(0, Character.toUpperCase(lastName.charAt(0)));
            }
            String userName = firstName.toString() + lastName.toString();
            showReplyOverLay(userName);
            openKeyboard();
        });

        adapter.setMakeInnerReplyInterface((commentPos, reply) -> {
            currentCommentPos = commentPos;
            currentInnerReply = reply;
            StringBuilder firstName = new StringBuilder(reply.getFirstName());
            firstName.setCharAt(0, Character.toUpperCase(firstName.charAt(0)));
            StringBuilder lastName = new StringBuilder();
            if (reply.getLastLame() != null && !reply.getLastLame().equals("") && reply.getLastLame().length() > 0) {
                lastName = new StringBuilder(reply.getLastLame());
                lastName.setCharAt(0, Character.toUpperCase(lastName.charAt(0)));
            }
            String userName = firstName.toString() + lastName.toString();
            showReplyOverLay(userName);
        });

        adapter.setCommentDelete(comment -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            alertDialog.setMessage("Delete Comment?");
            alertDialog.setCancelable(false);

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    deleteComment(comment);
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        });

        adapter.setMakeInnerDeleteInterface((commentPos, reply) -> {
            currentCommentPos = commentPos;
            currentInnerReply = reply;
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            alertDialog.setMessage("Delete Comment?");
            alertDialog.setCancelable(false);

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    deleteReply(reply);
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        });

    }

    private void deleteCommentConfDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        alertDialog.setMessage("Delete Comment?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (fromMain == 1) {
                    deleteComment(mDeleteComment);
                } else if (fromMain == 2) {
                    deleteReply(mDeleteReply);
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    private void deleteComment(Comment comment) {
        ArrayList<Comment> newCommentList = new ArrayList<>(adapter.getCommentArrayList());
        newCommentList.remove(comment);
        adapter.makeComment(newCommentList, false);
        int tempCount = 0;
        if (comment.getmReplies() != null && comment.getmReplies().size() > 0) {
            tempCount = comment.getmReplies().size() + 1;
        } else {
            tempCount = 1;
        }
        deleteComment(comment.getmId() + "", tempCount);
    }

    private void deleteReply(Reply reply) {
        ArrayList<Comment> newCommentList = new ArrayList<>(adapter.getCommentArrayList());
        Comment prevComment = newCommentList.get(currentCommentPos);
        ArrayList<Reply> newReplyList = new ArrayList<>(prevComment.getmReplies());
        newReplyList.remove(reply);
        Comment newComment = new Comment.Builder()
                .withCommentText(prevComment.getmCommentText())
                .withCommentUserId(prevComment.getmCommentUserId())
                .withFirstName(prevComment.getFirstName())
                .withProfilePhotoThumb(prevComment.getProfilePhotoThumb())
                .withId(prevComment.getmId())
                .withLikedByMe(prevComment.ismLikedByMe())
                .withTotalLikes(prevComment.getTotalLikes())
                .withReplies(newReplyList)
                .withTime(prevComment.getmCreatedAt())
                .withTimestamp(prevComment.getmTimestamp())
                .build();
        newCommentList.set(currentCommentPos, newComment);
        adapter.makeComment(newCommentList, true);
        deleteComment(reply.getmId() + "", 1);
    }

    private void initViews() {
        togglePostBtn(false);
        setupEditTextWatcher();
        setupPostBtn();
        setupCloseBtnReplyLayout();
    }

    private void togglePostBtn(boolean enable) {
        binding.postBtn.setEnabled(enable);
        binding.postBtn.setAlpha(enable ? 1f : 0.5f);
    }

    private void setupEditTextWatcher() {
        binding.editComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                togglePostBtn(!charSequence.toString().trim().isEmpty());
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void setupPostBtn() {
        binding.postBtn.setOnClickListener(view -> {
            if (!checkUserLogin()) {
                return;
            }
            if (binding.replyParent.getVisibility() == View.GONE) {
                addComment();
                binding.recyclerView.scrollToPosition(0);
            } else {
                Comment comment;
                if (currentInnerReply != null) {
                    // Get Comment From Position For The Inner Reply
                    comment = adapter.getCommentArrayList().get(currentCommentPos);
                } else {
                    comment = currentCommentForReply;
                }

                addReply(comment, currentInnerReply != null ? currentInnerReply.getFirstName() : "");
                // Close reply overlay
                binding.replyParent.setVisibility(View.GONE);
            }
            binding.editComment.setText("");
        });
    }

    @SuppressLint("SetTextI18n")
    private void showReplyOverLay(String username) {
        binding.replyParent.setVisibility(View.VISIBLE);
        binding.editComment.requestFocus();
        binding.replyTo.setText("Replying to @" + username);
    }

    private void setupCloseBtnReplyLayout() {
        binding.closeReply.setOnClickListener(view -> {
            binding.replyParent.setVisibility(View.GONE);
            binding.editComment.clearFocus();
            binding.editComment.setText("");
        });
    }

    private void addComment() {
        String text = Objects.requireNonNull(binding.editComment.getText()).toString().trim();
        int myUserId = userData.getUser_id();
        String myUsername = userData.getFirst_name() + " " + userData.getLast_name();
        int commentId = (int) new Date().getTime();
        int likeCount = 0;
        ArrayList<Reply> replies = new ArrayList<>();
        String time = new Date().getTime() + "";
        String timeStamp = Utils.getCurrentTimeStamp();
        Comment comment = CommentDataSource.createComment(text, myUserId, myUsername, commentId, false, likeCount, replies, time, userData.getProfileThumb(), timeStamp);
        ArrayList<Comment> newCommentList = new ArrayList<>();
        newCommentList.add(comment);
        newCommentList.addAll(adapter.getCommentArrayList());
        adapter.makeComment(newCommentList, false);
        adapter.parentComment = true;
        addCommentToApi(text, "");
    }

    private void addReply(Comment currentCommentForReply, String innerReplyUserName) {
        try {
            int parentCommentId = currentCommentForReply.getmId();
            String commentText;
            if (innerReplyUserName != null) {
                commentText = ("@" + innerReplyUserName + " " + binding.editComment.getText().toString().trim());
            } else {
                commentText = Objects.requireNonNull(binding.editComment.getText()).toString().trim();
            }
            int myUserId = userData.getUser_id();
            String myUsername = userData.getFirst_name() + " " + userData.getLast_name();
            int commentId = (int) new Date().getTime();
            int likeCount = 0;
            String time = new Date().getTime() + "";
            String timeStamp = Utils.getCurrentTimeStamp();

            //Construct a reply
            Reply reply = CommentDataSource.createReply(parentCommentId, commentText,
                    myUserId, myUsername, commentId, false, likeCount, time, userData.getProfileThumb(), timeStamp);
            //Make a copy of current comments
            ArrayList<Comment> newCommentList = adapter.getCommentArrayList();
            //currentComment position
            int commentPos = newCommentList.indexOf(currentCommentForReply);
            // get same comment from new List
            Comment prevComment = newCommentList.get(commentPos);
            // New Reply List
            ArrayList<Reply> newReplyList = new ArrayList<>();
            // add New Reply to newReplyList
            newReplyList.add(reply);
            newReplyList.addAll(prevComment.getmReplies());
            // Make new comment object with new replyList
            Comment newComment =
                    new Comment.Builder()
                            .withCommentText(prevComment.getmCommentText())
                            .withCommentUserId(prevComment.getmCommentUserId())
                            .withFirstName(prevComment.getFirstName())
                            .withProfilePhotoThumb(prevComment.getProfilePhotoThumb())
                            .withId(prevComment.getmId())
                            .withLikedByMe(prevComment.ismLikedByMe())
                            .withTotalLikes(prevComment.getTotalLikes())
                            .withTime(prevComment.getmCreatedAt())
                            .withTimestamp(prevComment.getmTimestamp())
                            .withReplies(newReplyList).build();

            // update old comment with newComment
            newCommentList.set(commentPos, newComment);
            adapter.makeComment(newCommentList, false);
            addCommentToApi(binding.editComment.getText().toString().trim(), parentCommentId + "");
        } catch (Exception e) {

        }
    }

    @Override
    public void onLikeUnlikeButtonClick(String commentId) {
        likeUnlikeComment(commentId);
    }

    @Override
    public void onLikeButtonClick(String commentId) {
        likeUserInfoComments(commentId);
    }

    @Override
    public void onBottomSheetMenuClick(Comment comment, boolean isCommentWriter) {
        mIsCommentWriter = isCommentWriter;
        fromMain = 1;
        mDeleteComment = comment;
        selectedCommentId = comment.getmId() + "";
        showBottomSheet();
    }

    @Override
    public void onBottomSheetSubMenuClick(int pos, Reply reply, boolean isCommentWriter) {
        mIsCommentWriter = isCommentWriter;
        fromMain = 2;
        mIndexSubDelete = pos;
        mDeleteReply = reply;
        currentCommentPos = pos;
        currentInnerReply = reply;
        selectedCommentId = reply.getmId() + "";
        showBottomSheet();
    }

    private void showBottomSheet() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isCommentWriter", mIsCommentWriter);
        OptionsBottomSheetFragment tempObj = OptionsBottomSheetFragment.newInstance(bundle);
        tempObj.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onBottomSheetItemClick(int item) {
        if (item == 1) {
            showCommentReportDialog();
        } else if (item == 2) {
            deleteCommentConfDialog();
        }
    }

    private void showCommentReportDialog() {
        try {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: //Yes button clicked
                        dialog.dismiss();
                        reportComment(selectedCommentId);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE: //No button clicked
                        dialog.dismiss();
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.str_report_comment_confirm)).setPositiveButton(getString(R.string.report), dialogClickListener)
                    .setNegativeButton(getString(R.string.cancel), dialogClickListener).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getComments() {
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showToast(getString(R.string.no_internet_connection));
            return;
        }
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.getComments(
                BaseActivity.getKYC(),
                authToken,
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value(), postId),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        try {
                            commentArrayList = new Gson().fromJson(new JSONObject(response).getString("comments"), new TypeToken<List<Comment>>() {
                            }.getType());
                            linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this, LinearLayoutManager.VERTICAL, false);
                            binding.recyclerView.setLayoutManager(linearLayoutManager);
                            if (adapter == null) {
                                adapter = new CommentAdapter(PostDetailActivity.this, PostDetailActivity.this, userData, PostDetailActivity.this, isDarkTheme, PostDetailActivity.this, PostDetailActivity.this);
                                setAdapterInterface();
                                binding.recyclerView.setNestedScrollingEnabled(false);
                                binding.recyclerView.setAdapter(adapter);
                                adapter.makeComment(commentArrayList, false);
                            } else {
                                adapter.mUserData = userData;
                                adapter.notifyDataSetChanged();
                                adapter.makeComment(commentArrayList, false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        binding.refreshLayout.setRefreshing(false);

                        new CountDownTimer(350, 500) {
                            public void onFinish() {
                                Utils.hideDialog();
                            }

                            public void onTick(long millisUntilFinished) {
                            }
                        }.start();
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                        binding.refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        Utils.hideDialog();
                        try {
                            Toast.makeText(PostDetailActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                        binding.refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onConnectionFailure() {
                        Utils.hideDialog();
                        try {
                            Toast.makeText(PostDetailActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                        binding.refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        Utils.hideDialog();
                        try {
                            Toast.makeText(PostDetailActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        binding.refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void addCommentToApi(String commentMsg, String parentId) {
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showToast(getString(R.string.no_internet_connection));
            return;
        }
        Utils.showDialog(this);
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.addComments(
                BaseActivity.getKYC(),
                authToken,
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value(), postId, commentMsg, parentId),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        totalComments++;
                        adapter.isNewCommentAdded = true;
                        adapter.isSubCommentVisible = true;
                        adapter.isMainCommentNeedToShow = false;
                        getComments();
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(PostDetailActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onConnectionFailure() {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(PostDetailActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(PostDetailActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }
                });
    }

    private void likeUnlikeComment(String commentId) {
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showToast(getString(R.string.no_internet_connection));
            return;
        }
//        Utils.showDialog(this);
        String authToken = "";
        if (Constants.getUserData(PostDetailActivity.this) != null) {
            authToken = Constants.getUserData(PostDetailActivity.this).getAuthentication_token();
        }

        connectPost(PostDetailActivity.this, null, ApiCallBack.likeUnlikeComments(
                BaseActivity.getKYC(),
                authToken,
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value(), commentId),
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
                            Toast.makeText(PostDetailActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onConnectionFailure() {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(PostDetailActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(PostDetailActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }
                });

    }

    private void likeUserInfoComments(String commentId) {
        Intent intent = new Intent(this, LikeUserListActivity.class);
        intent.putExtra("commentId", commentId);
        startActivity(intent);
    }

    private void deleteComment(String commentId, int updateCommentCount) {
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showToast(getString(R.string.no_internet_connection));
            return;
        }
        Utils.showDialog(PostDetailActivity.this);

        String authToken = "";
        if (Constants.getUserData(PostDetailActivity.this) != null) {
            authToken = Constants.getUserData(PostDetailActivity.this).getAuthentication_token();
        }

        connectPost(PostDetailActivity.this, null, ApiCallBack.deleteComments(
                BaseActivity.getKYC(),
                authToken,
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value(), commentId),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        if (totalComments > 0) {
                            totalComments = totalComments - updateCommentCount;
                        }
                        getComments();
//                        Utils.hideDialog();
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(PostDetailActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onConnectionFailure() {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(PostDetailActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(PostDetailActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }
                });

    }

    private void reportComment(String commentId) {
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showToast(getString(R.string.no_internet_connection));
            return;
        }

        Utils.showDialog(PostDetailActivity.this);
        String authToken = "";
        if (Constants.getUserData(PostDetailActivity.this) != null) {
            authToken = Constants.getUserData(PostDetailActivity.this).getAuthentication_token();
        }

        connectPost(PostDetailActivity.this, null, ApiCallBack.reportComments(
                BaseActivity.getKYC(),
                authToken,
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value(), commentId),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        Toast.makeText(PostDetailActivity.this, "We will verify that comment shortly", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(PostDetailActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onConnectionFailure() {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(PostDetailActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(PostDetailActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }
                });
    }

    private void openKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1);
    }

}
