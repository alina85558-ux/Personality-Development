package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.ImageGallaryConst;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.UserData;

import org.json.JSONObject;

import java.io.File;

import customview.imagepicker.PickerBuilder;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class EditProfileActivity extends BaseActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 1212;
    private boolean isLoading = false;
    private ImageView ivEditProfile, ivEditProfilePic, ivProfile;
    private EditText edtFirstName, edtLastName, edtEmailAddress;
    private String profileUrl = "";

    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        SharedPreferences prefs = getSharedPreferences("Purchase", MODE_PRIVATE);
        restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        ivEditProfile = findViewById(R.id.ivEditProfile);
        ivEditProfilePic = findViewById(R.id.ivEditProfilePic);
        ivProfile = findViewById(R.id.ivProfile);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmailAddress = findViewById(R.id.edtEmailAddress);

        //Save Edited profile button on top
        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtFirstName.getText().toString().trim().length() == 0) {
                    Toast.makeText(EditProfileActivity.this, "Please Enter First Name", Toast.LENGTH_SHORT).show();
                }/*else if (edtLastName.getText().toString().trim().length() == 0){
                    Toast.makeText(EditProfileActivity.this,"Please enter last name",Toast.LENGTH_SHORT).show();
                }*/ else {
                    editUserProfile(edtFirstName.getText().toString().trim(), edtLastName.getText().toString().trim());
                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            edtEmailAddress.setText(bundle.getString("EMAIL"));
            edtFirstName.setText(bundle.getString("FIRST_NAME"));
            edtFirstName.setSelection(bundle.getString("FIRST_NAME").length());
            edtLastName.setText(bundle.getString("LAST_NAME"));
            edtLastName.setSelection(bundle.getString("LAST_NAME").length());
            profileUrl = bundle.getString("PROFILE_URL");

            RequestOptions options = new RequestOptions();
            final RequestOptions placeholder_error = options.error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            if (profileUrl != null && profileUrl.length() > 0) {
                Glide.with(EditProfileActivity.this)
                        .load(profileUrl).apply(placeholder_error).into(ivProfile);
            }

        }

       /* ivProfile.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST);
            } else {
                pickImg();
            }
        });*/

        ivProfile.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                // Permission is not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            } else {
                pickImg();
            }
        });

        pickMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        // Handle selected image
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    pickImg();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Please grant permissions to update profile picture.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void pickImg() {

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_capture, null);
        dialog.setContentView(view);
        dialog.show();
        LinearLayout ll_photo = dialog.findViewById(R.id.ll_photo);
        LinearLayout ll_document = dialog.findViewById(R.id.ll_video);

        assert ll_photo != null;
        ll_photo.setOnClickListener(v -> {
            //dialog.dismiss();
            new PickerBuilder(this, PickerBuilder.SELECT_FROM_CAMERA)
                    .setOnImageReceivedListener(imageUri -> {
                        try {
                            profileUrl = Utils.compressImage(imageUri.getPath(), EditProfileActivity.this);
                            ivProfile.setImageBitmap(new BitmapFactory().decodeFile(profileUrl));
                            if (!profileUrl.isEmpty()) {
                                updateProfilePicture();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .setImageName(System.currentTimeMillis() + "")
                    .setImageFolderName(ImageGallaryConst.CACHESFILES_STORAGE)
                    .withTimeStamp(false)
                    .setCropScreenColor(Color.CYAN)
                    .start();

            dialog.dismiss();
        });

        assert ll_document != null;
        ll_document.setOnClickListener(v -> {
            dialog.dismiss();
            try {
                new PickerBuilder(EditProfileActivity.this, PickerBuilder.SELECT_FROM_GALLERY)
                        .setOnImageReceivedListener(imageUri -> {
                            try {
                                profileUrl = Utils.compressImage(imageUri.getPath(), EditProfileActivity.this);
                                ivProfile.setImageBitmap(new BitmapFactory().decodeFile(profileUrl));
                                if (!profileUrl.isEmpty()) {
                                    updateProfilePicture();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        })
                        .setImageName(System.currentTimeMillis() + "")
                        .setImageFolderName(ImageGallaryConst.CACHESFILES_STORAGE)
                        .setCropScreenColor(Color.CYAN)
                        .setOnPermissionRefusedListener(new PickerBuilder.onPermissionRefusedListener() {
                            @Override
                            public void onPermissionRefused() {

                            }
                        })
                        .start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void editUserProfile(final String firstName, String lastName) {
        isLoading = true;
        String authToken = "";
        if (Constants.getUserData(EditProfileActivity.this) != null) {
            authToken = Constants.getUserData(EditProfileActivity.this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.editUserProfile(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), firstName, lastName), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    isLoading = false;

                    String firstName = jsonObject.getString("first_name");
                    String lastName = jsonObject.getString("last_name");
                    String email = jsonObject.getString("email");
                    String profilePic = jsonObject.getString("profile_photo_original");
                    String profileThumb = jsonObject.getString("profile_photo_thumb");

                    UserData userData = Constants.getUserData(EditProfileActivity.this);
                    userData.setFirst_name(firstName);
                    userData.setLast_name(lastName);
                    userData.setUser_email(email);
                    userData.setProfilePic(profilePic);
                    userData.setProfileThumb(profileThumb);
                    Constants.setUserData(EditProfileActivity.this, new Gson().toJson(userData));

                    Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_LONG).show();
                    finish();

                    Utils.hideDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                isLoading = false;
                Utils.hideDialog();
                //Toast.makeText(EditProfileActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Headers headers) {
                isLoading = false;
                Utils.hideDialog();
                Toast.makeText(EditProfileActivity.this, "Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionFailure() {
                isLoading = false;
                Utils.hideDialog();
                Toast.makeText(EditProfileActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                isLoading = false;
                Utils.hideDialog();
                Toast.makeText(EditProfileActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void updateProfilePicture() {
        isLoading = true;
        String authToken = "";
        if (Constants.getUserData(EditProfileActivity.this) != null) {
            authToken = Constants.getUserData(EditProfileActivity.this).getAuthentication_token();
        }

        File file = new File(profileUrl);
        RequestBody profilePic = Utils.imageToBody(file.getAbsolutePath());
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo", file.getName(), profilePic);

        connectPost(this, null, ApiCallBack.updateProfilePic(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), body), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    isLoading = false;
                    String profileOriginal = jsonObject.getString("profile_photo_original");
                    String profileThumb = jsonObject.getString("profile_photo_thumb");

                    UserData userData = Constants.getUserData(EditProfileActivity.this);
                    userData.setProfilePic(profileOriginal);
                    userData.setProfileThumb(profileThumb);
                    Constants.setUserData(EditProfileActivity.this, new Gson().toJson(userData));

                    Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_LONG).show();

                    Utils.hideDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                isLoading = false;
                Utils.hideDialog();
                //Toast.makeText(EditProfileActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Headers headers) {
                isLoading = false;
                Utils.hideDialog();
                Toast.makeText(EditProfileActivity.this, "Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionFailure() {
                isLoading = false;
                Utils.hideDialog();
                Toast.makeText(EditProfileActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                isLoading = false;
                Utils.hideDialog();
                Toast.makeText(EditProfileActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
            }
        });

    }

}
