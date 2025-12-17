package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.createFeedBack;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.connection.ConnectionDetector;
import com.mayur.personalitydevelopment.databinding.ActivityFeedbackBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    private String output = "";
    private Button buttonRegister;
    private ActivityFeedbackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback);

        cd = new ConnectionDetector(FeedbackActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.buttonRegister.setOnClickListener(this);

        setColorData();

        if (!sp.getBoolean("guest_entry", false)) {
            binding.editTextName.setText(Constants.getUserData(FeedbackActivity.this).getUser_email());
        }

    }

    void setColorData() {
        if (sp.getBoolean("light", false)) {
            binding.near.setCardBackgroundColor(Color.parseColor("#464646"));
            binding.editTextName.setTextColor(Color.parseColor("#ffffff"));
            binding.editTextUsername.setTextColor(Color.parseColor("#ffffff"));
            binding.editTextName.setHintTextColor(Color.parseColor("#ffffff"));
            binding.editTextUsername.setHintTextColor(Color.parseColor("#ffffff"));
            binding.main.setBackgroundColor(Color.parseColor("#363636"));
        } else {
            binding.near.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.editTextName.setTextColor(Color.parseColor("#000000"));
            binding.editTextName.setHintTextColor(Color.parseColor("#000000"));
            binding.editTextUsername.setHintTextColor(Color.parseColor("#000000"));
            binding.editTextUsername.setTextColor(Color.parseColor("#000000"));
            binding.main.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    private void insertUser(Map<String, Object> stringMap) {
        connectPost(FeedbackActivity.this,  null, createFeedBack(BaseActivity.getKYC(), sp.getBoolean("guest_entry", false),Constants.getV6Value(), stringMap), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                binding.buttonRegister.setEnabled(true);
                if (sp.getBoolean("guest_entry", false)) {
                    binding.editTextName.setText("");
                }
                binding.editTextUsername.setText("");
                Toast.makeText(FeedbackActivity.this, "Feedback Submitted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                binding.buttonRegister.setEnabled(true);
                //Toast.makeText(FeedbackActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Headers headers) {
                binding.buttonRegister.setEnabled(true);
                Toast.makeText(FeedbackActivity.this, "Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionFailure() {
                binding.buttonRegister.setEnabled(true);
                Toast.makeText(FeedbackActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                binding.buttonRegister.setEnabled(true);
                Toast.makeText(FeedbackActivity.this, "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            if (!emailValidator(binding.editTextName.getText().toString())) {
                Toast.makeText(FeedbackActivity.this, "Please Enter Valid E-mail address", Toast.LENGTH_SHORT).show();
            } else if (emailValidator(binding.editTextName.getText().toString()) &&
                    !binding.editTextUsername.getText().toString().trim().equals("")) {
                Map<String, Object> stringMap = new HashMap<String, Object>();
                stringMap.put("email", binding.editTextName.getText().toString().trim());
                stringMap.put("description", binding.editTextUsername.getText().toString().trim());
                stringMap.put("os_type", "Android");
                binding.buttonRegister.setEnabled(false);
                insertUser(stringMap);
            } else {
                Toast.makeText(FeedbackActivity.this, "Please Enter Required Information", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(FeedbackActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

