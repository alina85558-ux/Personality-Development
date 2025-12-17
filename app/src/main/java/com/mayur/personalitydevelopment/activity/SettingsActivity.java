package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.signIn;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.databinding.ActivitySettingsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class SettingsActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private ActivitySettingsBinding binding;
    private String[] items = {"Huge", "Large", "Medium", "Small", "Tiny"};
    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;
    private String wakeUpTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            binding = DataBindingUtil.setContentView(SettingsActivity.this, R.layout.activity_settings);

            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            binding.switchTheme.setOnClickListener(view -> {
                editor.putBoolean("light", binding.switchTheme.isChecked());
                editor.commit();
                setData();
            });

            binding.fontsize.setOnClickListener(view -> {
                if (Utils.isNetworkAvailable(SettingsActivity.this)) {
                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Font Size")
                            .setSingleChoiceItems(items, getSize(), null)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                                    setSize(selectedPosition);

                                    binding.currentfontsize.setText(items[getSize()]);
                                }
                            }).show();
                } else {
                    Utils.showToast(getString(R.string.no_internet_connection));
                }
            });

            binding.switchTheme.setChecked(sp.getBoolean("light", false));

            setData();

            binding.currentfontsize.setText(items[getSize()]);

            binding.switchNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sp.getBoolean("guest_entry", false)) {
                        showLoginDialog();
                    } else {
                        if (Utils.isNetworkAvailable(SettingsActivity.this)) {
                            setNotif();
                        } else {
                            Utils.showToast(getString(R.string.no_internet_connection));
                        }
                    }
                }
            });

            if (sp.getBoolean("guest_entry", false)) {
                binding.switchEmail.setVisibility(View.GONE);
                binding.sepLineEmail.setVisibility(View.GONE);
            } else {
                binding.sepLineEmail.setVisibility(View.VISIBLE);
                binding.switchEmail.setVisibility(View.VISIBLE);

                binding.switchEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Utils.isNetworkAvailable(SettingsActivity.this)) {
                            setEmailNotif();
                        } else {
                            Utils.showToast(getString(R.string.no_internet_connection));
                        }
                    }
                });
            }

            if (sp.getBoolean("guest_entry", false)) {
                init();
            }

            String my_date = "16/06/2018";

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date strDate = null;
            try {
                strDate = sdf.parse(my_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (new Date().after(strDate)) {

            } else {
                binding.switchNotification.setVisibility(View.GONE);
                binding.sep0.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSize() {
        try {
            switch (sp.getInt("textSize", 15)) {
                case 26:
                    return 0;
                case 22:
                    return 1;
                case 18:
                    return 2;
                case 14:
                    return 3;
                case 10:
                    return 4;
                default:
                    return 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 3;
        }
    }

    public void setSize(int position) {
        try {
            switch (position) {
                case 0:
                    changePref(26);
                    break;
                case 1:
                    changePref(22);
                    break;
                case 2:
                    changePref(18);
                    break;
                case 3:
                    changePref(14);
                    break;
                case 4:
                    changePref(10);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void changePref(int size) {
        editor.putInt("textSize", size);
        editor.commit();
    }

    public void getAllSettings(final boolean b) {
        try {
            String authToken = "";
            if (Constants.getUserData(SettingsActivity.this) != null) {
                authToken = Constants.getUserData(SettingsActivity.this).getAuthentication_token();
            }

            connectPost(SettingsActivity.this, null, ApiCallBack.listAllSettings(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value()), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                    try {
                        JSONObject object = new JSONObject(response);
                        binding.switchNotification.setChecked(object.getBoolean("notification"));
                        binding.switchEmail.setChecked(object.getBoolean("email_subscription_active"));
                        if (b) {
                            setNotif();
                        }

                        if (object.getString("wakeup_time").equals("")) {
                            wakeUpTime = "";
                            binding.tvWakeUpTimeValue.setText("");
                            binding.tvWakeUpTimeValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning, 0, 0, 0);
                        } else {
                            wakeUpTime = object.getString("wakeup_time");
                            binding.tvWakeUpTimeValue.setText(Utils.changeHourFormat(wakeUpTime));
                            binding.tvWakeUpTimeValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    // Toast.makeText(SettingsActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Headers headers) {
                    Toast.makeText(SettingsActivity.this, "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Toast.makeText(SettingsActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Toast.makeText(SettingsActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEmailNotif() {

        try {
            String authToken = "";
            if (Constants.getUserData(SettingsActivity.this) != null) {
                authToken = Constants.getUserData(SettingsActivity.this).getAuthentication_token();
            }

            connectPost(SettingsActivity.this, null, ApiCallBack.setEmailNotifiactions(BaseActivity.getKYC(), authToken,
                    sp.getBoolean("guest_entry", false), Constants.getV6Value(),
                    binding.switchEmail.isChecked()),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                            getAllSettings(false);
                            if (binding.switchEmail.isChecked()) {
                                Toast.makeText(SettingsActivity.this, "You Will Receive Email Notifications", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, "You Will Not Receive Email Notifications", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                            binding.switchNotification.setChecked(!binding.switchNotification.isChecked());
                            //Toast.makeText(SettingsActivity.this, responseData.string(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Headers headers) {
                            binding.switchNotification.setChecked(!binding.switchNotification.isChecked());
                        }

                        @Override
                        public void onConnectionFailure() {
                            binding.switchNotification.setChecked(!binding.switchNotification.isChecked());
                        }

                        @Override
                        public void onException(Headers headers, int StatusCode) {
                            binding.switchNotification.setChecked(!binding.switchNotification.isChecked());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setNotif() {

        try {
            String authToken = "";
            if (Constants.getUserData(SettingsActivity.this) != null) {
                authToken = Constants.getUserData(SettingsActivity.this).getAuthentication_token();
            }

            connectPost(SettingsActivity.this, null, ApiCallBack.setNotifiactions(BaseActivity.getKYC(), authToken,
                    sp.getBoolean("guest_entry", false), Constants.getV6Value(),
                    binding.switchNotification.isChecked()),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                            getAllSettings(false);
                            if (binding.switchNotification.isChecked()) {
                                Toast.makeText(SettingsActivity.this, "You Will Receive Notifications", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, "You Will Not Receive Notifications", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                            binding.switchNotification.setChecked(!binding.switchNotification.isChecked());
                            //Toast.makeText(SettingsActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Headers headers) {
                            binding.switchNotification.setChecked(!binding.switchNotification.isChecked());
                        }

                        @Override
                        public void onConnectionFailure() {
                            binding.switchNotification.setChecked(!binding.switchNotification.isChecked());
                        }

                        @Override
                        public void onException(Headers headers, int StatusCode) {
                            binding.switchNotification.setChecked(!binding.switchNotification.isChecked());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void setData() {
        try {
            if (sp.getBoolean("light", false)) {
                binding.rel.setBackgroundColor(Color.parseColor("#464646"));
                binding.switchNotification.setTextColor(Color.parseColor("#ffffff"));
                binding.switchTheme.setTextColor(Color.parseColor("#ffffff"));
                binding.switchEmail.setTextColor(Color.parseColor("#ffffff"));
                binding.sep1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.sep.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.sepLineEmail.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.sep0.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fontsize.setTextColor(Color.parseColor("#ffffff"));
                binding.currentfontsize.setTextColor(Color.parseColor("#ffffff"));
                binding.sep1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.vWakeUptime.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvWakeUpTime.setTextColor(Color.parseColor("#ffffff"));
                binding.tvWakeUpTimeValue.setTextColor(Color.parseColor("#ffffff"));
            } else {
                binding.rel.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.switchNotification.setTextColor(Color.parseColor("#000000"));
                binding.switchTheme.setTextColor(Color.parseColor("#000000"));
                binding.switchEmail.setTextColor(Color.parseColor("#000000"));
                binding.sep1.setBackgroundColor(Color.parseColor("#000000"));
                binding.sep.setBackgroundColor(Color.parseColor("#000000"));
                binding.sep0.setBackgroundColor(Color.parseColor("#000000"));
                binding.sepLineEmail.setBackgroundColor(Color.parseColor("#000000"));
                binding.fontsize.setTextColor(Color.parseColor("#000000"));
                binding.currentfontsize.setTextColor(Color.parseColor("#000000"));
                binding.sep1.setBackgroundColor(Color.parseColor("#000000"));
                binding.vWakeUptime.setBackgroundColor(Color.parseColor("#000000"));
                binding.tvWakeUpTime.setTextColor(Color.parseColor("#000000"));
                binding.tvWakeUpTimeValue.setTextColor(Color.parseColor("#000000"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoginDialog() {

        try {
            final Dialog dialog = new Dialog(SettingsActivity.this);

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
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    startActivityForResult(signInIntent, Constants.LOGIN_TYPE.GOOGLE);
                }
            });

            DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    binding.switchNotification.setChecked(true);
                }
            };

            dialog.setOnCancelListener(onCancelListener);
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

    @Override
    protected void onResume() {
        super.onResume();

        if (!sp.getBoolean("guest_entry", false)) {
            if (Utils.isNetworkAvailable(SettingsActivity.this)) {
                getAllSettings(false);
            } else {
                // Utils.showToast(getString(R.string.no_internet_connection));
            }
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
                    Constants.setUserData(SettingsActivity.this, response);
                    initializeBilling();
                    updateToken();
                    displayMessage(getString(R.string.msg_logged_in));
                    if (!sp.getBoolean("guest_entry", false)) {
                        getAllSettings(true);
                    }

                    Utils.hideDialog();
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    binding.switchNotification.setChecked(true);
                    // Toast.makeText(getApplicationContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    binding.switchNotification.setChecked(true);
                    Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    binding.switchNotification.setChecked(true);
                    // Toast.makeText(getApplicationContext(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    binding.switchNotification.setChecked(true);
                    //Toast.makeText(getApplicationContext(), "EE Failure", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.LOGIN_TYPE.GOOGLE) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                if (acct.getEmail() == null && acct.getEmail().trim().isEmpty()) {
                    googleApiClient.clearDefaultAccountAndReconnect();
                    Toast.makeText(SettingsActivity.this, "null", Toast.LENGTH_LONG).show();
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
            }
        } else if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    void prepareGoogle() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            googleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSetupWakeUpTimeClick(View view) {
        if (sp.getBoolean("guest_entry", false)) {
            showLoginDialog();
        } else {
            Intent intent = new Intent(this, WakeUpTimeSettingActivity.class);
            intent.putExtra("wakeUpTime", wakeUpTime);
            startActivity(intent);
        }
    }

}
