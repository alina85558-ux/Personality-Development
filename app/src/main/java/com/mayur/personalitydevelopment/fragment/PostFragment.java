package com.mayur.personalitydevelopment.fragment;

import static android.content.Context.MODE_PRIVATE;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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
import com.mayur.personalitydevelopment.activity.CreateArticleActivity;
import com.mayur.personalitydevelopment.activity.LikeUserListActivity;
import com.mayur.personalitydevelopment.activity.MainActivity;
import com.mayur.personalitydevelopment.activity.PostDetailActivity;
import com.mayur.personalitydevelopment.adapter.PostListAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.database.ArticleRoomDatabase;
import com.mayur.personalitydevelopment.database.Post;
import com.mayur.personalitydevelopment.databinding.ActivityMainBinding;
import com.mayur.personalitydevelopment.databinding.FragmentPostBinding;
import com.mayur.personalitydevelopment.models.PostData;
import com.mayur.personalitydevelopment.models.SubscriptionResponse;
import com.mayur.personalitydevelopment.viewholder.PostHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class PostFragment extends Fragment {

    private final static String TAG = PostFragment.class.getSimpleName();
    private final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg2RciyklkPme5MJ4IZUa0/XhQdZvThkJVnLWQib4AHWeBRN9UKU5PY4khMZLIsoEESShh09QY2LoxpfvC1N26N8/GKIFuL5fhZ47X7zMq+9HlrFE6Yv0eTr0Pr6UfZ0GJXosPddZp2Ed7ybCjERSmdzL0IL3CYTF2ZY6+zIlBPvpQd/1aeM61VrDjPf1n9ba0v/O38sLOmmYf3CFBLbMjvlX2Hg1LfArA0MFXbaPtXuE9MXMEyx3Vsbg+qP/dpE/JOa3OKR75hSMM4+qumTZ2nCkgVyrMyt49XR7FFFXfW6rf84AzfO+isGe/WtG5oBtX92UYG71IlI1gO67Fz8bjQIDAQAB";
    private final String MERCHANT_ID = null;
    private final List<PostData> postList = new ArrayList<>();
    public boolean isLifetimeActive = false;
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    public Boolean restored_Issubscribed;
    public boolean isFromLiked = false;
    public int tempPosition;
    public boolean tempStatus;
    public PostHolder.MyPostHolder tempHolder;
    CallbackManager callbackManager;
    private boolean subscribed = false;
    private String subscriptionType = "";
    private boolean isLoadingSubscription;
    private PostListAdapter postListAdapter;
    private FragmentPostBinding binding;
    private int totalPage = 0;
    private int current_page = 1;
    private boolean isLoading = false;
    private ActivityMainBinding activityMainBinding;
    private GoogleSignInClient googleSignInClient;
    private boolean isPostDetail = false;
    //private RecyclerViewSkeletonScreen skeletonScreen;
    private int postClickPosition = -1;
    private MenuItem premiumMenuItem;
    private BillingClient billingClient;
    private String inAppPurchaseToken = "";

    ActivityResultLauncher<Intent> startPostIntentActivity;

    ActivityResultLauncher<Intent> startEditIntentActivity;

    ActivityResultLauncher<Intent> startGoogleIntentActivity;

    ActivityResultLauncher<Intent> starPostDetailIntentActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false);
        SharedPreferences prefs = requireActivity().getSharedPreferences("Purchase", MODE_PRIVATE);
        restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
        sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        editor = sp.edit();

        setHasOptionsMenu(true);

        if (restored_Issubscribed) {
            postListAdapter = new PostListAdapter(postList, getActivity(), this);
            binding.rvPost.setAdapter(postListAdapter);
        } else {
            postListAdapter = new PostListAdapter(postList, getActivity(), PostFragment.this);
            binding.rvPost.setAdapter(postListAdapter);
        }

        binding.progress.setVisibility(View.GONE);



        binding.rvPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                try {
                    if (Utils.isNetworkAvailable(getActivity())) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.rvPost.getLayoutManager();

                        assert linearLayoutManager != null;
                        int lastvisibleitemposition = linearLayoutManager.findLastVisibleItemPosition();

                        if (lastvisibleitemposition == postListAdapter.getItemCount() - 1) {
                            if (!isLoading && current_page <= totalPage) {
                                current_page++;
                                getPostList();
                            }
                        }

                        if (dy > 0 && getActivity().findViewById(R.id.bottom_navigation).isShown()) {
                            //layoutParams.topMargin = 0;
                            //getActivity().findViewById(R.id.llContainer).setLayoutParams(layoutParams);
                            getActivity().findViewById(R.id.fabAddPost).setVisibility(View.GONE);
                        } else if (dy < 0) {
                            //layoutParams.topMargin = 100;
                            //getActivity().findViewById(R.id.llContainer).setLayoutParams(layoutParams);
                            getActivity().findViewById(R.id.fabAddPost).setVisibility(View.VISIBLE);
                        }
                    } else {
                        getOfflinePost();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.refreshLayout.setOnRefreshListener(() -> {
            if (Utils.isNetworkAvailable(getActivity())) {
                totalPage = 0;
                current_page = 1;
                postList.clear();
                binding.rvPost.setVisibility(View.GONE);
                postListAdapter.notifyDataSetChanged();
                syncPostLikes();
            } else {
                isLoading = false;
                binding.refreshLayout.setRefreshing(false);
                binding.progress.setVisibility(View.GONE);
                getOfflinePost();
            }

        });

        binding.fabAddPost.setOnClickListener(v -> {
            isFromLiked = false;
            if (Utils.isNetworkAvailable(getActivity())) {
                if (!sp.getBoolean("guest_entry", false)) {
                    Intent createPost = new Intent(getActivity(), CreateArticleActivity.class);
                    startPostIntentActivity.launch(createPost);
                    getActivity().overridePendingTransition(0, 0);
                } else {
                    showLoginDialog();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            }
        });


        prepareGoogle();

        return binding.getRoot();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        startPostIntentActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    ((MainActivity) requireActivity()).menu.getItem(0).setVisible(false);
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        totalPage = 0;
                        current_page = 1;
                        getPostList();
                    }

                });

        startEditIntentActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    ((MainActivity) requireActivity()).menu.getItem(0).setVisible(false);
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        totalPage = 0;
                        current_page = 1;
                        getPostList();
                    }
                });

        startGoogleIntentActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    ((MainActivity) requireActivity()).menu.getItem(0).setVisible(false);
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

         starPostDetailIntentActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    ((MainActivity) requireActivity()).menu.getItem(0).setVisible(false);
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        if ((Objects.requireNonNull(data.getStringExtra(Constants.ACTION_))).equalsIgnoreCase(Constants.POST_UPDATE)) {
                            int totalComments = data.getIntExtra("totalComments", 0);
                            PostData postData = new Gson().fromJson(data.getStringExtra("POST_DATA"), PostData.class);
                            postData.setTotalComments(totalComments);
                            postList.set(postClickPosition, postData);
                            postListAdapter.notifyItemChanged(postClickPosition);
                        } else {
                            postList.remove(postClickPosition);
                            postListAdapter.notifyItemRemoved(postClickPosition);
                            postListAdapter.notifyItemRangeChanged(postClickPosition, postList.size());
                        }
                        postClickPosition = -1;
                    }

                });
    }

    public void changeReadingMode(PostHolder.MyPostHolder myHolder) {
        try {
            ((MainActivity) requireActivity()).menu.getItem(0).setVisible(false);
            if (myHolder != null) {
                if (sp.getBoolean("light", false)) {
                    myHolder.cardViewPost.setCardBackgroundColor(Color.parseColor("#464646"));
                    myHolder.txtPostTime.setTextColor(Color.parseColor("#ffffff"));
                    myHolder.txtComments.setTextColor(Color.parseColor("#ffffff"));
                    myHolder.txtPostName.setTextColor(Color.parseColor("#ffffff"));
                    myHolder.txtPostDescription.setTextColor(Color.parseColor("#ffffff"));
                    myHolder.txtLikes.setTextColor(Color.parseColor("#ffffff"));
                    myHolder.ivOptions.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu_options_white));
                    binding.rvPost.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_grey));
                    myHolder.commentImageV.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_comment_white));
                } else {
                    binding.rvPost.setBackgroundColor(requireContext().getResources().getColor(R.color.white));
                    myHolder.cardViewPost.setCardBackgroundColor(Color.parseColor("#ffffff"));
                    myHolder.txtPostDescription.setTextColor(Color.parseColor("#000000"));
                    myHolder.txtPostTime.setTextColor(Color.parseColor("#838383"));
                    myHolder.txtComments.setTextColor(Color.parseColor("#838383"));
                    myHolder.txtPostName.setTextColor(Color.parseColor("#000000"));
                    myHolder.txtLikes.setTextColor(Color.parseColor("#838383"));
                    myHolder.ivOptions.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu_post_options));
                    myHolder.commentImageV.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_comment_black));
                }
            } else {
                postListAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void prepareGoogle() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isPostDetail) {
            if (Utils.isNetworkAvailable(requireActivity())) {
                totalPage = 0;
                current_page = 1;
                Utils.showDialog(getActivity());
                syncPostLikes();
            } else {
                totalPage = 0;
                current_page = 1;
                getOfflinePost();
            }
        }
        binding.progress.setVisibility(View.GONE);
    }

    private void getOfflinePost() {
        try {
            if (restored_Issubscribed) {
                binding.progress.setVisibility(View.GONE);
                try {
                    ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(getContext());
                    if (db != null) {
                        List<Post> postsList = db.postDao().getAllPost();
                        if (postsList != null && !postsList.isEmpty()) {
                            postList.clear();
                            for (int i = 0; i < postsList.size(); i++) {
                                PostData post = new PostData();
                                Post postDb = postsList.get(i);
                                post.setId(postDb.getId());
                                post.setCreatedAt(postDb.getCreatedDate());
                                post.setFirstName(postDb.getFirstName());
                                post.setLastName(postDb.getLastName());
                                post.setIsLike(postDb.isLike());
                                post.setTotalLikes(postDb.getTotalLike());
                                post.setTotalComments(postDb.getTotalComments());
                                post.setPostData(postDb.getPostData());
                                post.setProfilePhotoThumb(postDb.getProfileUrl());
                                post.setShowOptions(postDb.isShowOptions());
                                postList.add(post);
                            }
                            postListAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Utils.showToast(getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertOfflinePost() {
        try {
            if (restored_Issubscribed) {
                ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(getContext());
                if (db != null) {
                    if (!postList.isEmpty()) {
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
                            postDb.setTotalComments(post.getTotalComments());
                            postDb.setDelete(false);
                            db.postDao().insertPost(postDb);
                            Log.i(TAG, "insertOfflinePost: " + i);
                        }
                        Log.i(TAG, "insertOfflinePost: Size " + db.postDao().getAllPost().size());
                    }
                }
            }
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
                totalPage = 0;
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

            connectPost(getActivity(), null, ApiCallBack.getPostList(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), current_page + ""), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        isLoading = false;
                        binding.refreshLayout.setRefreshing(false);
                        binding.rvPost.setVisibility(View.VISIBLE);
                        binding.progress.setVisibility(View.GONE);

                        Utils.hideDialog();

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

                        if (posts.isEmpty()) {
                            binding.nodata.setVisibility(View.VISIBLE);
                            binding.rvPost.setVisibility(View.GONE);
                        } else {
                            binding.nodata.setVisibility(View.GONE);
                            binding.rvPost.setVisibility(View.VISIBLE);
                        }

                        postList.addAll(posts);
                        postListAdapter.notifyDataSetChanged();

                        insertOfflinePost();

                        Utils.hideDialog();

                        updateOptionMenuItemVisibility();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    binding.refreshLayout.setRefreshing(false);
                    isLoading = false;
                    binding.progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    try {
                        isLoading = false;
                        binding.refreshLayout.setRefreshing(false);
                        binding.progress.setVisibility(View.GONE);
                        Utils.hideDialog();
                        Toast.makeText(getContext(), "Failure", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionFailure() {
                    isLoading = false;
                    binding.refreshLayout.setRefreshing(false);
                    binding.progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    isLoading = false;
                    binding.refreshLayout.setRefreshing(false);
                    binding.progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "EE Failure", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openOptions(View viewFilter, PostData post, final int position) {
        try {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

            llEdit.setOnClickListener(v -> {
                popupWindow.dismiss();
                ((MainActivity) getActivity()).menu.getItem(0).setVisible(false);
                Intent createPost = new Intent(getActivity(), CreateArticleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("POST_DATA", postList.get(position));
                bundle.putInt("POSITION", position);
                createPost.putExtras(bundle);
                startEditIntentActivity.launch(createPost);
                getActivity().overridePendingTransition(0, 0);
            });

            llDelete.setOnClickListener(v -> {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE -> { //Yes button clicked
                            dialog.dismiss();
                            getPostDelete(position);
                        }
                        case DialogInterface.BUTTON_NEGATIVE -> //No button clicked
                                dialog.dismiss();
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.confirm_delete)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(getString(R.string.no), dialogClickListener).show();

                popupWindow.dismiss();
            });

            llReport.setOnClickListener(v -> {
                showPostReportDialog(position);
                popupWindow.dismiss();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void getPostDelete(final int position) {
        try {
            Utils.showDialog(getActivity());
            String authToken = "";
            if (Constants.getUserData(getActivity()) != null) {
                authToken = Constants.getUserData(getActivity()).getAuthentication_token();
            }
            connectPost(getActivity(), null, ApiCallBack.getPostDelete(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), postList.get(position).getId() + ""), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    try {
                        Toast.makeText(getActivity(), "Post successfully deleted", Toast.LENGTH_SHORT).show();
                        totalPage = 0;
                        current_page = 1;
                        try {
                            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(getContext());
                            db.postDao().deletePost(postList.get(position).getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getPostList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    isLoading = false;
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    isLoading = false;
                    Utils.hideDialog();
                    Toast.makeText(getContext(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    isLoading = false;
                    Utils.hideDialog();
                    Toast.makeText(getContext(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    isLoading = false;
                    Utils.hideDialog();
                    Toast.makeText(getContext(), "EE Failure", Toast.LENGTH_LONG).show();
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
                        case DialogInterface.BUTTON_POSITIVE -> { //Yes button clicked
                            dialog.dismiss();
                            getPostReport(position);
                        }
                        case DialogInterface.BUTTON_NEGATIVE -> //No button clicked
                                dialog.dismiss();
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.str_report_confirm)).setPositiveButton(getString(R.string.report), dialogClickListener)
                    .setNegativeButton(getString(R.string.cancel), dialogClickListener).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPostReport(int position) {
        try {

            String authToken = "";
            if (Constants.getUserData(getActivity()) != null) {
                authToken = Constants.getUserData(getActivity()).getAuthentication_token();
            }

            connectPost(getActivity(), null, ApiCallBack.getPostReport(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), postList.get(position).getId() + ""), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    try {
                        Toast.makeText(getActivity(), "We will verify that post shortly", Toast.LENGTH_SHORT).show();
                        Utils.hideDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    isLoading = false;
                    Utils.hideDialog();
                    //Toast.makeText(getActivity(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Headers headers) {
                    isLoading = false;
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    isLoading = false;
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    isLoading = false;
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "EE Failure", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateOptionMenuItemVisibility() {
        SharedPreferences prefs = getActivity().getSharedPreferences("Purchase", MODE_PRIVATE);
        restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
        if (restored_Issubscribed) {
            if (premiumMenuItem != null) {
                premiumMenuItem.setVisible(false);
                getActivity().invalidateOptionsMenu();
            }
        }
    }

    public void storeTempDataForLike(final int position, final boolean status, final PostHolder.MyPostHolder holder) {
        tempPosition = position;
        tempStatus = status;
        tempHolder = holder;
        Log.d("=>=>> ", "IN storeTempDataForLike " + tempPosition + " == " + tempStatus + " == " + tempHolder);
    }

    public void getPostLikes(final int position, final boolean status, final PostHolder.MyPostHolder holder) {

        Log.d("=>=>> ", "IN getPostLikes ");

        try {
            isFromLiked = false;
            String authToken = "";
            if (Constants.getUserData(getActivity()) != null) {
                authToken = Constants.getUserData(getActivity()).getAuthentication_token();
            }

            connectPost(getActivity(), null, ApiCallBack.getPostLike(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), postList.get(position).getId() + "", status), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    try {
                        Log.d("=>=>> ", "IN getPostLikes onResponseSuccess");
                        holder.linearLike.setClickable(true);
                        postList.get(position).setIsLike(status);
                        if (status) {
                            postList.get(position).setTotalLikes(postList.get(position).getTotalLikes() + 1);
                        } else {
                            postList.get(position).setTotalLikes(postList.get(position).getTotalLikes() - 1);
                        }
                        postListAdapter.notifyDataSetChanged();
                        updateOptionMenuItemVisibility();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Log.d("=>=>> ", "IN getPostLikes onResponseFailure");
                    isLoading = false;
                    holder.linearLike.setClickable(true);
                    holder.likeIcon.setChecked(postList.get(position).isIsLike());
                    holder.txtLikes.setClickable(true);
                    holder.txtLikes.setText(MessageFormat.format("{0}{1}", Utils.convertNumberToCount((postList.get(position).getTotalLikes())).trim(), getResources().getString(R.string.likes)));
                }

                @Override
                public void onFailure(Headers headers) {
                    Log.d("=>=>> ", "IN getPostLikes onFailure");
                    isLoading = false;
                    holder.linearLike.setClickable(true);
                    holder.likeIcon.setChecked(postList.get(position).isIsLike());
                    holder.txtLikes.setText(MessageFormat.format("{0}{1}", Utils.convertNumberToCount((postList.get(position).getTotalLikes())).trim(), getResources().getString(R.string.likes)));
                    holder.txtLikes.setClickable(true);
                }

                @Override
                public void onConnectionFailure() {
                    isLoading = false;
                    Log.d("=>=>> ", "IN getPostLikes onConnectionFailure");
                    holder.linearLike.setClickable(true);
                    holder.likeIcon.setChecked(postList.get(position).isIsLike());
                    holder.txtLikes.setText(MessageFormat.format("{0}{1}", Utils.convertNumberToCount((postList.get(position).getTotalLikes())).trim(), getResources().getString(R.string.likes)));
                    holder.txtLikes.setClickable(true);
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Log.d("=>=>> ", "IN getPostLikes onException");
                    isLoading = false;
                    holder.linearLike.setClickable(true);
                    holder.likeIcon.setChecked(postList.get(position).isIsLike());
                    holder.txtLikes.setText(MessageFormat.format("{0}{1}", Utils.convertNumberToCount((postList.get(position).getTotalLikes())).trim(), getResources().getString(R.string.likes)));
                    holder.txtLikes.setClickable(true);
                }
            });


        } catch (Exception e) {
            Log.d("=>=>> ", "IN getPostLikes catch => " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void syncPostLikes() {
        try {
            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(getContext());
            List<Post> postList = db.postDao().getAllPostSynch(true);
            if (postList != null && !postList.isEmpty()) {
                ArrayList<String> postIds = new ArrayList<>();
                ArrayList<Boolean> postLikeStatus = new ArrayList<>();

                for (int i = 0; i < postList.size(); i++) {
                    postIds.add(postList.get(i).getId() + "");
                    postLikeStatus.add(postList.get(i).isLike());
                }
                String ids = android.text.TextUtils.join(",", postIds);
                String status = android.text.TextUtils.join(",", postLikeStatus);
                updatePostLikes(ids, status, postIds);
            } else {
                getPostList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
            getPostList();
        }
    }

    public void updatePostLikes(final String postIds, String likesStatus, final ArrayList<String> postSynchList) {
        try {
            String authToken = "";
            if (Constants.getUserData(getContext()) != null) {
                authToken = Constants.getUserData(getContext()).getAuthentication_token();
            }

            connectPost(getContext(), null, ApiCallBack.multiplePostLike(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), postIds, Constants.getV6Value(), likesStatus), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Log.i(TAG, "onResponseSuccess: Post Like Update");
                    ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(getContext());
                    try {
                        if (postSynchList != null && postSynchList.size() > 0) {
                            for (int i = 0; postSynchList.size() > 0; i++) {
                                db.postDao().setSynch(false, Integer.parseInt(postSynchList.get(i)));
                            }
                        }

                        getPostList();

                    } catch (Exception e) {
                        e.printStackTrace();
                        getPostList();
                    }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onFacebook() {
        try {
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().logInWithReadPermissions(this, List.of("public_profile, email"));

            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                        try {
                            JSONObject fbResponse = new JSONObject(String.valueOf(response.getJSONObject()));
                            fbResponse.getString("email");
                            if (!fbResponse.getString("email").equals("")) {
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
            Utils.showDialog(getActivity());
            params.put("platform", "android");
            sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String token = sp.getString("FCM_TOKEN", "");
            if (token.length() > 0) {
                params.put("device_token", token);
            } else {
                params.put("device_token", "test");
            }
            //params.put("device_token", FirebaseInstanceId.getInstance().getToken());
            params.put("uuid", sp.getString("UUID", ""));

            connectPost(getActivity(), null, signIn(params), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    editor = sp.edit();
                    editor.putBoolean("guest_entry", false);
                    editor.commit();

                    Constants.setUserData(getActivity(), response);
                    initializeBilling();
                    updateToken();
                    Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_in), Toast.LENGTH_SHORT).show();
                    Log.d("=>=>> ", "onResponseSuccess: LOGGED IN  " + isFromLiked);
                    if (isFromLiked) {
                        Log.d("=>=>> ", "IN IF ");
                        getPostLikes(tempPosition, tempStatus, tempHolder);
                    }

                    updateOptionMenuItemVisibility();

                    ((MainActivity) getActivity()).drawerAndNavigation();
                    ((MainActivity) getActivity()).hideItem();

                    binding.refreshLayout.setRefreshing(false);
                    binding.progress.setVisibility(View.GONE);
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

    public void showLoginDialog() {
        try {
            final Dialog dialog = new Dialog(getActivity());

            Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_custom_login_2);

            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_rectangle_white_big_no_stroke));

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            ((MainActivity) requireActivity()).menu.getItem(0).setVisible(false);
            if (FacebookSdk.isFacebookRequestCode(requestCode)) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    public void onPostClick(int id, int position) {
        if (!sp.getBoolean("guest_entry", false)) {
            isPostDetail = true;
            postClickPosition = position;
        }
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra(Constants.POST_ID, String.valueOf(id));
        starPostDetailIntentActivity.launch(intent);
    }





    public void onPostLikeClick(int postId) {
        Intent intent = new Intent(getActivity(), LikeUserListActivity.class);
        intent.putExtra(Constants.POST_ID, String.valueOf(postId));
        startActivity(intent);
    }

    private void updateToken() {
        String authToken = "";
        if (Constants.getUserData(getActivity()) != null) {
            authToken = Constants.getUserData(getActivity()).getAuthentication_token();
        }
        connectPost(getActivity(), null, ApiCallBack.updateToken(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), Utils.getFcmToken(getActivity()), sp.getString("UUID", "")), new ApiConnection.ConnectListener() {
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        premiumMenuItem = menu.findItem(R.id.action_premium);
    }

    private void initializeBilling() {
        billingClient = BillingClient.newBuilder(requireActivity())
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

    /*private void checkUserSubscription() {
        Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS, (billingResult1, purchasesList) -> {
            if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK &&
                    !Objects.requireNonNull(purchasesResult.getPurchasesList()).isEmpty()) {
                String purchasedItem = purchasesList.get(0).getSkus().get(0);
                inAppPurchaseToken = purchasesList.get(0).getPurchaseToken();
                if (purchasedItem.equals("3_months")) {
                    subscribed = true;
                    subscriptionType = "3_months";
                } else if (purchasedItem.equals("6_months")) {
                    subscribed = true;
                    subscriptionType = "6_months";
                } else if (purchasedItem.equals("yearly")) {
                    subscribed = true;
                    subscriptionType = "yearly";
                } else if (purchasedItem.equals("six_months_v2")) {
                    subscribed = true;
                    subscriptionType = "six_months_v2";
                } else if (purchasedItem.equals("twelve_months_v2")) {
                    subscribed = true;
                    subscriptionType = "twelve_months_v2";
                } else if (purchasedItem.equals("one_month_v2")) {
                    subscribed = true;
                    subscriptionType = "one_month_v2";
                } else if (purchasedItem.equals("offer_twelve_months_v2")) {
                    subscribed = true;
                    subscriptionType = "offer_twelve_months_v2";
                }

                if (subscribed) {
                    SharedPreferences pref = getActivity().getSharedPreferences("Purchase", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("Issubscribed", subscribed);
                    editor.apply();
                    restored_Issubscribed = pref.getBoolean("Issubscribed", false);
                    callSetSubscriptionAPI();
                }
            } else {
                getSubscriptionAPI();
            }
        });
    }*/

    private void checkUserSubscription() {
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                (billingResult, purchases) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !purchases.isEmpty()) {
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

                    } else {
                        getSubscriptionAPI();
                    }
                }
        );
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
                                isLoadingSubscription = false;
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
                            isLoadingSubscription = false;
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
                                isLoadingSubscription = false;
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
                                isLoadingSubscription = false;
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
