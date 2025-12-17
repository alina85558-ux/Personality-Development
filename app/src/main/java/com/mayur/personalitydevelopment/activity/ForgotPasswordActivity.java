package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.forgotPass;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.databinding.ActivityForgotPasswordBinding;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class ForgotPasswordActivity extends BaseActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(ForgotPasswordActivity.this, R.layout.activity_forgot_password);
        binding.btnFPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.getText().toString().trim()).matches()) {
                    binding.edtEmail.setError(getResources().getString(R.string.invalid_email));
                    return;
                }

                binding.btnFPass.setClickable(false);
                Map<String, Object> map = new HashMap<>();
                map.put("email", binding.edtEmail.getText().toString().trim());
                forgotPassword(map);
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void forgotPassword(Map<String, Object> map) {
        Utils.showDialog(this);
        connectPost(this,  null, forgotPass(map), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                Utils.hideDialog();
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                binding.btnFPass.setClickable(true);
                Utils.hideDialog();
                //Toast.makeText(getApplicationContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Headers headers) {
                Utils.hideDialog();
                binding.btnFPass.setClickable(true);        Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionFailure() {
                Utils.hideDialog();
                binding.btnFPass.setClickable(true);   Toast.makeText(getApplicationContext(), "CC Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                Utils.hideDialog();
                binding.btnFPass.setClickable(true);     Toast.makeText(getApplicationContext(), "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
            }
        });

    }
}
