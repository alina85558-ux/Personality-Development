package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.signIn;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.ProfilePostListAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.PostData;
import com.mayur.personalitydevelopment.models.UserData;
import com.mayur.personalitydevelopment.viewholder.ProfilePostHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class ProfileActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private static final int PROFILE_PIC_FULL_SCREEN = 10012;
    private final int CREATE_POST = 100;
    private final int EDIT_POST = 101;
    private final boolean mIsTheToolsVisible = false;
    private final List<PostData> postList = new ArrayList<>();
    private final String userProfileUrl = "";
    private final int POST_DETAIL = 1050;
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    public Boolean restored_Issubscribed;
    public boolean isFromLiked = false;
    public int tempPosition;
    public boolean tempStatus;
    public ProfilePostHolder.MyPostHolder tempHolder;
    CallbackManager callbackManager;
    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private RelativeLayout rlProfile;
    private ImageView ivProfile, ivEditProfile, ivEditProfilea;
    private TextView tvUserName;
    private TextView tvUserEmail;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView rvPost;
    private FloatingActionButton fabAddPost;
    private TextView nodata;
    private ProgressBar progress;
    private ProfilePostListAdapter postListAdapter;
    private int totalPage = 0;
    private int current_page = 1;
    private boolean isLoading = false;
    private String userEmail = "";
    private String userFirstName = "";
    private String userLastName = "";
    private ImageView ivLikes, ivFavorite, ivSetting, ivVerify;
    private boolean isVerify = false;
    private String userOriginalProfile = "";
    private String userThombProfile = "";
    private NestedScrollView nested_scroll;
    private LinearLayout llTop;
    private ImageView iv_back;
    private int visibleItemCount;
    private GoogleSignInClient googleSignInClient;
    private boolean isPostDetail = false;
    private int postClickPosition = -1;

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        try {
            AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                    ? new AlphaAnimation(0f, 1f)
                    : new AlphaAnimation(1f, 0f);

            alphaAnimation.setDuration(duration);
            alphaAnimation.setFillAfter(true);
            v.startAnimation(alphaAnimation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_next);

        bindActivity();

        mAppBarLayout.addOnOffsetChangedListener(this);

        llTop = findViewById(R.id.llTop);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
        ivEditProfile = findViewById(R.id.ivEditProfile);
        ivEditProfilea = findViewById(R.id.ivEditProfilea);
        ivProfile = findViewById(R.id.ivProfile);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        rvPost = findViewById(R.id.rvPost);
        nodata = findViewById(R.id.nodata);
        progress = findViewById(R.id.progress);
        ivLikes = findViewById(R.id.ivLikes);
        ivFavorite = findViewById(R.id.ivFavorite);
        ivSetting = findViewById(R.id.ivSetting);
        ivVerify = findViewById(R.id.ivVerify);
        nested_scroll = findViewById(R.id.nested_scroll);


        SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
        restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        postListAdapter = new ProfilePostListAdapter(postList, ProfileActivity.this, this);
        rvPost.setAdapter(postListAdapter);
        progress.setVisibility(View.GONE);
        rvPost.setNestedScrollingEnabled(false);

        rvPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rvPost.getLayoutManager();
                int lastvisibleitemposition = linearLayoutManager.findLastVisibleItemPosition();
                if (lastvisibleitemposition == postListAdapter.getItemCount() - 1) {
                    if (!isLoading && current_page <= totalPage) {
                        current_page++;
                        getPostList();
                    }
                }
            }
        });


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("EMAIL", userEmail);
                intent.putExtra("FIRST_NAME", userFirstName);
                intent.putExtra("LAST_NAME", userLastName);
                intent.putExtra("PROFILE_URL", userThombProfile);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        ivEditProfilea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivEditProfile.performClick();
            }
        });

        ivLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, LikesActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FavouriteActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        ivVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: //Yes button clicked
                                dialog.dismiss();
                                verifyUserEmail();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE: //No button clicked
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setMessage("Verify Email address?").setPositiveButton("Yes, sure", dialogClickListener)
                        .setNegativeButton("Not now", dialogClickListener).show();
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileFullImageActivity.class);
                intent.putExtra("PROFILE_URL", userOriginalProfile);
                startActivityForResult(intent, PROFILE_PIC_FULL_SCREEN);
                overridePendingTransition(0, 0);
            }
        });

    }

    public void changeReadingMode(ProfilePostHolder.MyPostHolder myHolder) {
        if (myHolder != null) {
            if (sp.getBoolean("light", false)) {
                myHolder.cardViewPost.setCardBackgroundColor(Color.parseColor("#464646"));
                myHolder.txtPostTime.setTextColor(Color.parseColor("#ffffff"));
                myHolder.txtPostName.setTextColor(Color.parseColor("#ffffff"));
                myHolder.txtPostDescription.setTextColor(Color.parseColor("#ffffff"));
                myHolder.txtLikes.setTextColor(Color.parseColor("#ffffff"));
                myHolder.ivOptions.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_options_white));
                rvPost.setBackgroundColor(getResources().getColor(R.color.dark_grey));
                nested_scroll.setBackgroundColor(getResources().getColor(R.color.dark_grey));
                myHolder.commentImageV.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_white));
            } else {
                rvPost.setBackgroundColor(getResources().getColor(R.color.white));
                nested_scroll.setBackgroundColor(getResources().getColor(R.color.white));
                myHolder.cardViewPost.setCardBackgroundColor(Color.parseColor("#ffffff"));
                myHolder.txtPostDescription.setTextColor(Color.parseColor("#000000"));
                myHolder.txtPostTime.setTextColor(Color.parseColor("#000000"));
                myHolder.txtPostName.setTextColor(Color.parseColor("#000000"));
                myHolder.txtLikes.setTextColor(Color.parseColor("#464646"));
                myHolder.ivOptions.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_post_options));
                myHolder.commentImageV.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_black));
            }
        } else {
            postListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        progress.setVisibility(View.GONE);
        getUserProfile();
    }

    private void bindActivity() {
        mToolbar = findViewById(R.id.main_toolbar);
        mTitle = findViewById(R.id.main_textview_title);
        mTitleContainer = findViewById(R.id.main_linearlayout_title);
        mAppBarLayout = findViewById(R.id.main_appbar);
        iv_back = findViewById(R.id.iv_back);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);

    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                iv_back.setVisibility(View.VISIBLE);
                ivEditProfile.setVisibility(View.VISIBLE);
                mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mIsTheTitleVisible = true;
            }
        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mToolbar.setBackgroundColor(getResources().getColor(R.color.transaction));
                ivEditProfile.setVisibility(View.GONE);
                iv_back.setVisibility(View.GONE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        try {
            if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
                if (mIsTheTitleContainerVisible) {
                    startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                    mIsTheTitleContainerVisible = false;
                }
            } else {
                if (!mIsTheTitleContainerVisible) {
                    startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                    mIsTheTitleContainerVisible = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CREATE_POST) {
                if (resultCode == Activity.RESULT_OK) {
                    totalPage = 0;
                    current_page = 1;
                    getPostList();
                }
            } else if (requestCode == EDIT_POST) {
                if (resultCode == Activity.RESULT_OK) {
                    totalPage = 0;
                    current_page = 1;
                    getPostList();
                }
            } else if (requestCode == PROFILE_PIC_FULL_SCREEN) {
//                totalPage = 0;
//                current_page = 1;
//                getPostList();
            } else if (requestCode == POST_DETAIL) {
                if (resultCode == Activity.RESULT_OK) {
                    if ((data.getStringExtra(Constants.ACTION_)).equalsIgnoreCase(Constants.POST_UPDATE)) {
                        int totalComments = data.getIntExtra("totalComments", 0);
                        PostData postData = new Gson().fromJson(data.getStringExtra("POST_DATA"), PostData.class);
                        postData.setTotalComments(totalComments);
                        postList.set(postClickPosition, postData);
                        postListAdapter.notifyItemChanged(postClickPosition);
                        postClickPosition = -1;
                    } else {
                        postList.remove(postClickPosition);
                        postListAdapter.notifyItemRemoved(postClickPosition);
                        postListAdapter.notifyItemRangeChanged(postClickPosition, postList.size());
                        postClickPosition = -1;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openOptions(View viewFilter, PostData post, final int position) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.dialog_filter, null, false);
            final PopupWindow popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);

            LinearLayout llEdit = layout.findViewById(R.id.llEdit);
            LinearLayout llDelete = layout.findViewById(R.id.llDelete);
            LinearLayout llReport = layout.findViewById(R.id.llReport);

            if (post.isShowOptions()) {
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

            llEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    Intent createPost = new Intent(ProfileActivity.this, CreateArticleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("POST_DATA", postList.get(position));
                    bundle.putInt("POSITION", position);
                    createPost.putExtras(bundle);
                    startActivityForResult(createPost, EDIT_POST);
                    overridePendingTransition(0, 0);
                }
            });

            llDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE: //Yes button clicked
                                    dialog.dismiss();
                                    getPostDelete(position);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE: //No button clicked
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setMessage(getString(R.string.confirm_delete)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                            .setNegativeButton(getString(R.string.no), dialogClickListener).show();

                    popupWindow.dismiss();
                }
            });

            llReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPostReportDialog(position);
                    popupWindow.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPostReportDialog(final int position) {
        try {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE: //Yes button clicked
                            dialog.dismiss();
                            getPostReport(position);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE: //No button clicked
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.str_report_confirm)).setPositiveButton(getString(R.string.report), dialogClickListener)
                    .setNegativeButton(getString(R.string.cancel), dialogClickListener).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getPostReport(int position) {
        try {
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, ApiCallBack.getPostReport(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), postList.get(position).getId() + ""), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    try {
                        Toast.makeText(ProfileActivity.this, "We will verify it shortly", Toast.LENGTH_SHORT).show();
                        Utils.hideDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    isLoading = false;
                    Utils.hideDialog();
                    //Toast.makeText(ProfileActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Headers headers) {
                    isLoading = false;
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    isLoading = false;
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    isLoading = false;
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPostDelete(int position) {

        try {
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, ApiCallBack.getPostDelete(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), postList.get(position).getId() + ""), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    try {
                        Toast.makeText(ProfileActivity.this, "Post deleted", Toast.LENGTH_SHORT).show();
                        totalPage = 0;
                        current_page = 1;
                        getPostList();
                        Utils.hideDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    isLoading = false;
                    Utils.hideDialog();
                    //Toast.makeText(ProfileActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Headers headers) {
                    isLoading = false;
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    isLoading = false;
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    isLoading = false;
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPostList() {
        try {
            if (totalPage != 0 && current_page > totalPage) {
                return;
            }
            isLoading = true;

            if (current_page == 1) {
            /*if (!refreshLayout.isRefreshing()) {
                Utils.showDialog(ProfileActivity.this);
                progress.setVisibility(View.GONE);
            }*/
            } else {
                progress.setVisibility(View.VISIBLE);
            }

            String authToken = "";
            if (Constants.getUserData(ProfileActivity.this) != null) {
                authToken = Constants.getUserData(ProfileActivity.this).getAuthentication_token();
            }

            connectPost(this, null, ApiCallBack.getUserPostList(BaseActivity.getKYC(),
                    authToken, sp.getBoolean("guest_entry", false),
                    Constants.getV6Value(), current_page + ""),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                isLoading = false;
                                // refreshLayout.setRefreshing(false);
                                progress.setVisibility(View.GONE);
                                if (totalPage == 0) {
                                    postList.clear();
                                }

                                totalPage = (int) jsonObject.getDouble("total_pages");

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
                                    post.setTotalComments(jsonObjectData.getInt("total_comments"));
                                    post.setUpdatedAt(jsonObjectData.getLong("updated_at"));
                                    posts.add(post);
                                }

                                if (posts != null && posts.isEmpty()) {
                                    nodata.setVisibility(View.VISIBLE);
                                    rvPost.setVisibility(View.GONE);
                                } else {
                                    nodata.setVisibility(View.GONE);
                                    rvPost.setVisibility(View.VISIBLE);
                                }

                                postList.addAll(posts);
                                postListAdapter.notifyDataSetChanged();
                                Utils.hideDialog();
                            } catch (Exception e) {
                                Utils.hideDialog();
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                            // refreshLayout.setRefreshing(false);
                            isLoading = false;
                            progress.setVisibility(View.GONE);
                            Utils.hideDialog();
                            //Toast.makeText(ProfileActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Headers headers) {
                            isLoading = false;
                            refreshLayout.setRefreshing(false);
                            progress.setVisibility(View.GONE);
                            Utils.hideDialog();
                            Toast.makeText(ProfileActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onConnectionFailure() {
                            isLoading = false;
                            // refreshLayout.setRefreshing(false);
                            progress.setVisibility(View.GONE);
                            Utils.hideDialog();
                            Toast.makeText(ProfileActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onException(Headers headers, int StatusCode) {
                            isLoading = false;
                            // refreshLayout.setRefreshing(false);
                            progress.setVisibility(View.GONE);
                            Utils.hideDialog();
                            Toast.makeText(ProfileActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPostLikes(final int position, final boolean status, final ProfilePostHolder.MyPostHolder holder) {

        try {
            isFromLiked = false;
            String authToken = "";
            if (Constants.getUserData(ProfileActivity.this) != null) {
                authToken = Constants.getUserData(ProfileActivity.this).getAuthentication_token();
            }

            connectPost(ProfileActivity.this, null, ApiCallBack.getPostLike(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), postList.get(position).getId() + "", status), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    try {
                        holder.linearLike.setClickable(true);
                        postList.get(position).setIsLike(status);
                        if (status) {
                            postList.get(position).setTotalLikes(postList.get(position).getTotalLikes() + 1);
                        } else {
                            postList.get(position).setTotalLikes(postList.get(position).getTotalLikes() - 1);
                        }
                        postListAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    isLoading = false;
                    holder.linearLike.setClickable(true);
                    holder.likeIcon.setChecked(postList.get(position).isIsLike());
                    holder.txtLikes.setClickable(true);
                    holder.txtLikes.setText(Utils.convertNumberToCount((postList.get(position).getTotalLikes())) + getResources().getString(R.string.likes));
                }

                @Override
                public void onFailure(Headers headers) {
                    isLoading = false;
                    holder.linearLike.setClickable(true);
                    holder.likeIcon.setChecked(postList.get(position).isIsLike());
                    holder.txtLikes.setText(Utils.convertNumberToCount((postList.get(position).getTotalLikes())) + getResources().getString(R.string.likes));
                    holder.txtLikes.setClickable(true);
                }

                @Override
                public void onConnectionFailure() {
                    isLoading = false;
                    holder.linearLike.setClickable(true);
                    holder.likeIcon.setChecked(postList.get(position).isIsLike());
                    holder.txtLikes.setText(Utils.convertNumberToCount((postList.get(position).getTotalLikes())) + getResources().getString(R.string.likes));
                    holder.txtLikes.setClickable(true);
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    isLoading = false;
                    holder.linearLike.setClickable(true);
                    holder.likeIcon.setChecked(postList.get(position).isIsLike());
                    holder.txtLikes.setText(Utils.convertNumberToCount((postList.get(position).getTotalLikes())) + getResources().getString(R.string.likes));
                    holder.txtLikes.setClickable(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verifyUserEmail() {

        try {
            Utils.showDialog(ProfileActivity.this);
            isLoading = true;
            String authToken = "";
            if (Constants.getUserData(ProfileActivity.this) != null) {
                authToken = Constants.getUserData(ProfileActivity.this).getAuthentication_token();
            }

            connectPost(this, null, ApiCallBack.verifyUserEmail(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value()), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                    try {
                        //JSONObject jsonObject = new JSONObject(response);
                        isLoading = false;

                        Toast.makeText(ProfileActivity.this, "Please check your email to verify", Toast.LENGTH_LONG).show();

                        Utils.hideDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    isLoading = false;
                    progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                    //Toast.makeText(ProfileActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Headers headers) {
                    isLoading = false;
                    progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    isLoading = false;
                    progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    isLoading = false;
                    progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserProfile() {
        try {
            Utils.showDialog(this);
            isLoading = true;
            String authToken = "";
            if (Constants.getUserData(ProfileActivity.this) != null) {
                authToken = Constants.getUserData(ProfileActivity.this).getAuthentication_token();
            }

            connectPost(this, null, ApiCallBack.getUserProfile(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value()), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        isLoading = false;

                        userFirstName = jsonObject.getString("first_name");
                        if (!jsonObject.isNull("last_name")) {
                            userLastName = jsonObject.getString("last_name");
                        }
                        userEmail = jsonObject.getString("email");
                        isVerify = jsonObject.getBoolean("is_verified");
                        userOriginalProfile = jsonObject.getString("profile_photo_original");
                        userThombProfile = jsonObject.getString("profile_photo_thumb");
                        tvUserEmail.setText(userEmail);

                        if (userFirstName != null && userFirstName.length() > 0 && userLastName != null && userLastName.length() > 0) {
                            tvUserName.setText(userFirstName + " " + userLastName);
                            mTitle.setText(userFirstName + " " + userLastName);
                        } else if (userFirstName != null && userFirstName.length() > 0) {
                            tvUserName.setText(userFirstName);
                            mTitle.setText(userFirstName);
                        } else if (userLastName != null && userLastName.length() > 0) {
                            tvUserName.setText(userLastName);
                            mTitle.setText(userLastName);
                        }

                        UserData userData = Constants.getUserData(ProfileActivity.this);
                        userData.setFirst_name(userFirstName);
                        userData.setLast_name(userLastName);
                        userData.setUser_email(userEmail);
                        userData.setProfileThumb(userThombProfile);
                        userData.setProfilePic(userOriginalProfile);
                        Constants.setUserData(ProfileActivity.this, new Gson().toJson(userData));

                        if (isVerify) {
                            ivVerify.setVisibility(View.GONE);
                        } else {
                            ivVerify.setVisibility(View.VISIBLE);
                        }

                        RequestOptions options = new RequestOptions();
                        final RequestOptions placeholder_error = options.error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                                .diskCacheStrategy(DiskCacheStrategy.ALL);
                        Glide.with(ProfileActivity.this)
                                .load(userThombProfile)
                                .apply(placeholder_error).into(ivProfile);

                        Utils.hideDialog();
                        getPostList();
                    } catch (Exception e) {
                        Utils.hideDialog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    isLoading = false;
                    progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    isLoading = false;
                    progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    isLoading = false;
                    progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    isLoading = false;
                    progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPostLikeClick(int postId) {
        Intent intent = new Intent(this, LikeUserListActivity.class);
        intent.putExtra(Constants.POST_ID, String.valueOf(postId));
        startActivity(intent);
    }

    public void storeTempDataForLike(final int position, final boolean status, final ProfilePostHolder.MyPostHolder holder) {
        tempPosition = position;
        tempStatus = status;
        tempHolder = holder;
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
                }
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
                                Utils.hideDialog();
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
                    Utils.hideDialog();
                }

                @Override
                public void onError(FacebookException e) {
                    Utils.hideDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
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
                    Utils.hideDialog();
                    editor = sp.edit();
                    editor.putBoolean("guest_entry", false);
                    editor.commit();

                    Constants.setUserData(ProfileActivity.this, response);
                    initializeBilling();
                    updateToken();
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.msg_logged_in), Toast.LENGTH_SHORT).show();
                    Log.d("=>=>> ", "onResponseSuccess: LOGGED IN  " + isFromLiked);
                    if (isFromLiked) {
                        Log.d("=>=>> ", "IN IF ");
                        getPostLikes(tempPosition, tempStatus, tempHolder);
                    }
                    updateOptionMenuItemVisibility();
                    totalPage = 0;
                    current_page = 1;
                    isLoading = false;
                    postList.clear();
                    postListAdapter.notifyDataSetChanged();
                    getPostList();
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    //Toast.makeText(getActivity(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(ProfileActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    private void updateOptionMenuItemVisibility() {
        SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
        restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
        if (restored_Issubscribed) {
        }
    }

    public void onPostClick(int id, int position) {
        if (!sp.getBoolean("guest_entry", false)) {
            isPostDetail = true;
            postClickPosition = position;
        }
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra(Constants.POST_ID, String.valueOf(id));
        startActivityForResult(intent, POST_DETAIL);
    }

}
