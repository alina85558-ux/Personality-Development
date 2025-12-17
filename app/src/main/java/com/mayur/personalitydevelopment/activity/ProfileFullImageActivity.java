package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.UserData;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class ProfileFullImageActivity extends BaseActivity {

    private ImageView ivProfile;
    private String profileUrl = "";
    private Button btnRemoveProfile;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullimage_profile);

        ivProfile = findViewById(R.id.ivProfile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnRemoveProfile = findViewById(R.id.btnRemoveProfile);

        btnRemoveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: //Yes button clicked
                                dialog.dismiss();
                                deleteProfilePicture();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE: //No button clicked
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileFullImageActivity.this);
                builder.setMessage("Are you sure you want to remove your profile?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            profileUrl = bundle.getString("PROFILE_URL");

            RequestOptions options = new RequestOptions();
            final RequestOptions placeholder_error = options.error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            if (profileUrl != null && profileUrl.length() > 0){
                Glide.with(ProfileFullImageActivity.this)
                        .load(profileUrl).apply(placeholder_error).into(ivProfile);
            }

        }

    }

    public void deleteProfilePicture() {
        Utils.showDialog(this);
        isLoading = true;
        String authToken = "";
        if (Constants.getUserData(ProfileFullImageActivity.this) != null) {
            authToken = Constants.getUserData(ProfileFullImageActivity.this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.deleteProfilePic(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false),Constants.getV6Value()), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                try {
                    //JSONObject jsonObject = new JSONObject(response);
                    isLoading = false;

                    UserData userData = Constants.getUserData(ProfileFullImageActivity.this);
                    userData.setProfilePic("");
                    userData.setProfileThumb("");
                    Constants.setUserData(ProfileFullImageActivity.this,new Gson().toJson(userData));

                    Toast.makeText(ProfileFullImageActivity.this, "Photo deleted successfully", Toast.LENGTH_LONG).show();
                    finish();

                    Utils.hideDialog();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                isLoading = false;
                Utils.hideDialog();
                //Toast.makeText(ProfileFullImageActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Headers headers) {
                isLoading = false;
                Utils.hideDialog();
                Toast.makeText(ProfileFullImageActivity.this, "Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionFailure() {
                isLoading = false;
                Utils.hideDialog();
                Toast.makeText(ProfileFullImageActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                isLoading = false;
                Utils.hideDialog();
                Toast.makeText(ProfileFullImageActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
