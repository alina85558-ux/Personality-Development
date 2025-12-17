package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.guestEntry;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.signIn;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.connection.ConnectionDetector;
import com.mayur.personalitydevelopment.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class LoginActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 101;

    // private GoogleApiClient googleApiClient;

    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;
    private ActivityLoginBinding binding;
    private boolean showP = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.activity_login);
        if (!sp.getBoolean("guest_entry", false)) {
            if (Constants.getUserData(LoginActivity.this) != null) {
                newActivity(MainActivity.class, null, true);
            }
        } else {
            newActivity(MainActivity.class, null, true);
        }

        init();

        cd = new ConnectionDetector(LoginActivity.this);

        binding.ivFacebook.setOnClickListener(view -> onFacebook());

        binding.ivGmail.setOnClickListener(view -> {
            googleSignInClient.signOut();
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
            /*Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, Constants.LOGIN_TYPE.GOOGLE);*/
        });

        binding.skip.setOnClickListener(view -> {
            if (Utils.isNetworkAvailable(LoginActivity.this)) {
                onGuestEntry();
            } else {
                Utils.showToast(getString(R.string.no_internet_connection));
            }
        });

        binding.signUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        binding.closeKeyBoard.setOnClickListener(view1 -> {
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        binding.btnLogin.setOnClickListener(view -> {
            if (binding.edtEmail.getText().toString().equals("")) {
                binding.edtEmail.setError(getString(R.string.cannot_be_empty));
                return;
            }

            if (binding.edtPassword.getText().toString().equals("")) {
                binding.edtPassword.setError(getString(R.string.cannot_be_empty));
                return;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("email", binding.edtEmail.getText().toString().replace(" ", ""));
            params.put("password", binding.edtPassword.getText().toString());
            params.put("login_type", Constants.LOGIN_TYPE.NORMAL);

            if (Utils.isNetworkAvailable(LoginActivity.this)) {
                onSignin(params);
            } else {
                Utils.showToast(getString(R.string.no_internet_connection));
            }
        });

        binding.forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        isInternetPresent = ConnectionDetector.isConnectingToInternet();

        binding.switchPass.setOnClickListener(view -> {
            showP = !showP;
            showPass(showP);
        });

    }

    void showPass(boolean flag) {
        try {
            if (flag) {
                binding.switchPass.setImageResource(R.drawable.ic_enhanced_encryption_black_24dp);
                binding.edtPassword.setTransformationMethod(null);
            } else {
                binding.switchPass.setImageResource(R.drawable.ic_no_encryption_black_24dp);
                binding.edtPassword.setTransformationMethod(new PasswordTransformationMethod());
            }

            binding.edtPassword.setSelection(binding.edtPassword.getText().length());
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
            LoginManager.getInstance().logInWithReadPermissions(this, List.of("public_profile, email"));

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
                    e.printStackTrace();
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

            try {
                sp = PreferenceManager.getDefaultSharedPreferences(this);
                String token = String.valueOf(FirebaseMessaging.getInstance().getToken());
                if (token.length() > 0) {
                    params.put("device_token", token);
                } else {
                    params.put("device_token", "test");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            params.put("uuid", sp.getString("UUID", ""));

            connectPost(this, null, signIn(params), new ApiConnection.ConnectListener() {

                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    editor.putBoolean("guest_entry", false);
                    editor.commit();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Utils.setArticleLang(LoginActivity.this, jsonObject.getInt("language_type"));
                        //  String profilePic = jsonObject.getString("profile_photo_original");
                        //  String profileThumb = jsonObject.getString("profile_photo_thumb");
                        Constants.setUserData(LoginActivity.this, response);
                        initializeBilling();
                        updateToken();
//                        displayMessage(getString(R.string.msg_logged_in));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    newActivity(MainActivity.class, null, true);
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData.string());
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    Toast.makeText(getApplicationContext(), R.string.somehing_want_wrong, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    Toast.makeText(getApplicationContext(), R.string.somehing_want_wrong, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(getApplicationContext(), R.string.somehing_want_wrong, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void onGuestEntry() {
        try {
            Utils.showDialog(this);
            Map<String, Object> params = new HashMap<>();
            params.put("platform", "android");

            try {
                //JSONObject object = new JSONObject(FirebaseInstanceId.getInstance().getToken());
                sp = PreferenceManager.getDefaultSharedPreferences(this);
                String token = sp.getString("FCM_TOKEN", "");
                if (token.length() > 0) {
                    params.put("device_token", token);
                } else {
                    params.put("device_token", "test");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            params.put("uuid", sp.getString("UUID", ""));
            //params.put(BuildConfig.AUTH_PARAM,Constants.getV6Value());

            connectPost(this, null, guestEntry(BaseActivity.getKYC(), Constants.getV6Value(), params), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String guestId = jsonObject.getString("guest_id");
                        editor.putBoolean("guest_entry", true);
                        editor.putString(Constants.GUEST_ID, guestId);
                        editor.commit();
                        newActivity(MainActivity.class, null, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(getApplicationContext(), "FF", Toast.LENGTH_LONG).show();
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
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RC_SIGN_IN) {

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    Map<String, Object> params = new HashMap<>();
                    params.put("email", account.getEmail().trim());
                    params.put("first_name", account.getDisplayName().split("\\s+")[0].trim());
                    params.put("last_name", account.getDisplayName().split("\\s+")[1].trim());

                    if (account.getPhotoUrl() != null && String.valueOf(account.getPhotoUrl()).trim().length() > 0) {
                        params.put("user_profile_photo", String.valueOf(account.getPhotoUrl()).trim());
                    } else {
                        params.put("user_profile_photo", "");
                    }

                    params.put("social_id", account.getId());
                    params.put("login_type", Constants.LOGIN_TYPE.GOOGLE);

                    onSignin(params);

                } catch (ApiException e) {
                    e.printStackTrace();
                }

             /*   GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if (result.isSuccess()) {
                    GoogleSignInAccount acct = result.getSignInAccount();
                    if (acct.getEmail() == null && acct.getEmail().trim().isEmpty()) {
                        googleSignInClient.clearDefaultAccountAndReconnect();
                        return;
                    }

                    Map<String, Object> params = new HashMap<>();
                    params.put("email", acct.getEmail().trim());
                    params.put("first_name", acct.getDisplayName().split("\\s+")[0].trim());
                    params.put("last_name", acct.getDisplayName().split("\\s+")[1].trim());
                    params.put("user_profile_photo", String.valueOf(acct.getPhotoUrl()).trim());
                    params.put("social_id", acct.getId());
                    params.put("login_type", Constants.LOGIN_TYPE.GOOGLE);
                    googleSignInClient.clearDefaultAccountAndReconnect();

                    onSignin(params);
                }*/
            } else { // if (FacebookSdk.isFacebookRequestCode(requestCode))
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void prepareGoogle() {
        try {
 /*           GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            googleApiClient.connect();*/


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void terms_n_codition(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bestifyme.com/terms"));
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
