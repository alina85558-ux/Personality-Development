package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.signUp;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.databinding.ActivitySignUpBinding;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class SignUpActivity extends BaseActivity {

    private ActivitySignUpBinding binding;
    private boolean showP = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(SignUpActivity.this, R.layout.activity_sign_up);

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateSignUpForm()) {

                    Map<String, Object> map = new HashMap<>();

                    String name = binding.edtFirstname.getText().toString();

                    if (name.trim().contains(" ")) {
                        String[] splitName = name.split(" ");
                        map.put("first_name", splitName[0]);
                        map.put("last_name", splitName[1]);
                    } else {
                        map.put("first_name", name);
                    }

                    map.put("email", binding.edtEmail.getText().toString().trim().replace(" ", ""));
                    map.put("password", /*Constantss.SHA1(*/binding.edtPassword.getText().toString().trim()/*)*/);
                    signUpUser(map);
                }
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.switchPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showP = !showP;
                showPass(showP);
            }
        });
    }

    void showPass(boolean flag) {
        if (flag) {
            binding.switchPass.setImageResource(R.drawable.ic_enhanced_encryption_black_24dp);
            binding.edtPassword.setTransformationMethod(null);
        } else {
            binding.switchPass.setImageResource(R.drawable.ic_no_encryption_black_24dp);
            binding.edtPassword.setTransformationMethod(new PasswordTransformationMethod());
        }
        binding.edtPassword.setSelection(binding.edtPassword.getText().length());
    }

    void signUpUser(Map<String, Object> map) {

        Utils.showDialog(this);
        map.put("platform", "android");
        map.put("login_type", "0");
        try{
            sp = PreferenceManager.getDefaultSharedPreferences(this);
//            String token = sp.getString("FCM_TOKEN","");
            String token = String.valueOf(FirebaseMessaging.getInstance().getToken());
            if (token != null && token.length() > 0){
                map.put("device_token",token);
            }else{
                map.put("device_token","test");
            }
            //JSONObject object = new JSONObject(FirebaseInstanceId.getInstance().getToken());
            //map.put("device_token", object.getString("token"));
        }catch (Exception e){
            e.printStackTrace();
            //map.put("device_token", FirebaseInstanceId.getInstance().getToken());
        }
        map.put("uuid", sp.getString("UUID", ""));

        connectPost(this,  null, signUp(map), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                Utils.hideDialog();
                editor.putBoolean("guest_entry", false);
                editor.commit();
                Constants.setUserData(SignUpActivity.this, response);
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                Utils.hideDialog();
                // Toast.makeText(getApplicationContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
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

    }

    boolean validateSignUpForm() {

        if (binding.edtFirstname.getText().toString().equals("")) {
            binding.edtFirstname.setError(getResources().getString(R.string.cannot_be_empty));
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.getText().toString().trim()).matches()) {
            binding.edtEmail.setError(getResources().getString(R.string.invalid_email));
            return false;
        }

        if (binding.edtPassword.getText().toString().equals("")) {
            binding.edtPassword.setError(getResources().getString(R.string.cannot_be_empty));
            return false;
        }

        if (binding.edtPassword.getText().toString().length() < 6) {
            binding.edtPassword.setError(getResources().getString(R.string.must_be_atleast));
            return false;
        }

        return true;
    }

}
