package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.allCategoryOfCourse;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.signIn;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.startTrialDays;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.SpacesItemDecoration;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.CourseCategoriesAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.CourseCategory;
import com.mayur.personalitydevelopment.models.InnerCategoryList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class CoursesCategoriesListActivity extends BaseActivity implements CourseCategoriesAdapter.AdapterListener
{

    private RecyclerView rvList;
    private ProgressBar progressBar;
    private CourseCategoriesAdapter courseCategoriesAdapter;
    private GoogleSignInClient googleSignInClient;
    private int categoryId;
    private CallbackManager callbackManager;
    private InnerCategoryList apiData;
    private boolean isFromNotification = false;

    public static void start(Context context, int categoryId, String course, boolean isFromNotification) {
        Intent starter = new Intent(context, CoursesCategoriesListActivity.class);
        starter.putExtra("categoryId", categoryId);
        starter.putExtra("course", course);
        starter.putExtra("isFromNotification", isFromNotification);
        context.startActivity(starter);
    }

    public static void popUntil(Context context) {
        Intent starter = new Intent(context, CoursesCategoriesListActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_categories);
        categoryId = getIntent().getIntExtra("categoryId", 0);
        String course = getIntent().getStringExtra("course");
        isFromNotification = getIntent().getBooleanExtra("isFromNotification", false);
        Toolbar maintoolbar = findViewById(R.id.maintoolbar);
        rvList = findViewById(R.id.rvList);
        progressBar = findViewById(R.id.progressBar);
        setSupportActionBar(maintoolbar);
        maintoolbar.setTitle(course);
        setTitle(course);
        maintoolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (sp.getBoolean("guest_entry", false)) {
            init();
        }

        rvList.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        rvList.addItemDecoration(new SpacesItemDecoration(spacingInPixels, true));

    }

    void init() {
        try {
            prepareGoogle();
            FacebookSdk.sdkInitialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void prepareGoogle() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(CoursesCategoriesListActivity.this, gso);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCourses() {
        try {
            progressBar.setVisibility(View.VISIBLE);

            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, allCategoryOfCourse(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), categoryId, Utils.getCurrentDate()), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    apiData = new Gson().fromJson(response, InnerCategoryList.class);
                    Utils.hideDialog();
                    progressBar.setVisibility(View.GONE);

                    courseCategoriesAdapter = new CourseCategoriesAdapter(getBaseContext(), apiData.getData(), CoursesCategoriesListActivity.this);
//                    rvList.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
//                    int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
//                    rvList.addItemDecoration(new SpacesItemDecoration(spacingInPixels, true));
                    rvList.setAdapter(courseCategoriesAdapter);
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    progressBar.setVisibility(View.GONE);
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    public void onClickEvent(CourseCategory courseCategory) {
        if (sp.getBoolean("guest_entry", false)) {
            showLoginDialog();
        } else {
            SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
            restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
            if (restored_Issubscribed) {
                gotoActivity(courseCategory);
            } else {
                if (apiData != null && apiData.getTrialInfo().getAccessFlag() == 0) {
                    showTrialStartDialog(courseCategory);
                } else if (apiData != null && apiData.getTrialInfo().getAccessFlag() == 1) {
                    gotoActivity(courseCategory);
                } else if (apiData != null && apiData.getTrialInfo().getAccessFlag() == 2) {
                    showExpiriedTrialDialog();
                }
            }
        }
    }

    private void gotoActivity(CourseCategory courseCategory) {
        if (courseCategory.getCategoryName().contains("Visualization") || courseCategory.getCategoryName().contains("Silence")) {
            MusicCategoryActivity.start(this, courseCategory.getId(), 1, courseCategory.getCategoryName());
        } else if (courseCategory.getCategoryName().contains("Exercise")) {
            YoutubeCategoryActivity.start(this, courseCategory.getId(), 1, courseCategory.getCategoryName());
        } else if (courseCategory.getCategoryName().contains("Affirmation")) {
            AffirmationCategoryActivity.start(this, courseCategory.getId(), 1, courseCategory.getCategoryName());
        } else if (courseCategory.getCategoryName().contains("Reading")) {
            ReadingListingActivity.start(this, courseCategory.getId(), 1, courseCategory.getCategoryName());
        } else if (courseCategory.getCategoryName().toLowerCase().contains("scribing")) {
//            ScribingActivity.start(this, courseCategory.getId(), 1, courseCategory.getCategoryName());
            TodoListActivity.start(this, courseCategory.getId(), 1, courseCategory.getCategoryName());
        }
    }


    private void showTrialStartDialog(CourseCategory courseCategory) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        alertDialog.setMessage(apiData.getTrialInfo().getAccessMessage());
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                callApistartTrialDays(courseCategory);
            }
        });
        alertDialog.show();
    }

    private void showExpiriedTrialDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        alertDialog.setMessage(apiData.getTrialInfo().getAccessMessage());
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                startActivity(new Intent(CoursesCategoriesListActivity.this, RemoveAdActivity.class));
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alertDialog.show();
    }

    public void callApistartTrialDays(CourseCategory courseCategory) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, startTrialDays(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), Utils.getCurrentDate(), categoryId + ""), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    progressBar.setVisibility(View.GONE);
                    gotoActivity(courseCategory);
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    progressBar.setVisibility(View.GONE);
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.calenderMenu) {
            CalenderEventsScreen.start(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void showLoginDialog() {

        try {
            final Dialog dialog = new Dialog(this);

            Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_custom_login_2);

            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.rounded_rectangle_white_big_no_stroke));

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

            google.setOnClickListener(view -> {
                dialog.dismiss();
                if (googleSignInClient != null) {
                    googleSignInClient.signOut();
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startGoogleActivity.launch(signInIntent);
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ActivityResultLauncher<Intent> startGoogleActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (Utils.isNetworkAvailable(CoursesCategoriesListActivity.this)) {
                    try {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();

                            try {
                                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                                GoogleSignInAccount acct = task.getResult(ApiException.class);

                                if (acct.getEmail() == null && acct.getEmail().trim().isEmpty()) {
                                    Toast.makeText(CoursesCategoriesListActivity.this, "null", Toast.LENGTH_LONG).show();
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
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Utils.hideDialog();
                    }
                }
            });

    void onFacebook() {
        try {
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().logInWithReadPermissions(this, List.of("public_profile, email"));

            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<>() {
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
                    Constants.setUserData(CoursesCategoriesListActivity.this, response);
                    initializeBilling();
                    updateToken();
                    displayMessage(getString(R.string.msg_logged_in));
                    Utils.hideDialog();
                    getCourses();
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
            if (Utils.isNetworkAvailable(CoursesCategoriesListActivity.this)) {
              if (FacebookSdk.isFacebookRequestCode(requestCode)) {
                    callbackManager.onActivityResult(requestCode, resultCode, data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCourses();
    }

    @Override
    public void onBackPressed() {
        if (isFromNotification) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("isFromNotification", true);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
        finish();
    }
}
