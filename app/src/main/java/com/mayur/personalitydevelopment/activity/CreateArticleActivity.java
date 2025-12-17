package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.ColorsAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.databinding.ActivityCreatePostBinding;
import com.mayur.personalitydevelopment.models.PostData;

import java.util.ArrayList;

import customview.richeditor.RichEditor;
import okhttp3.Headers;
import okhttp3.ResponseBody;

public class CreateArticleActivity extends BaseActivity implements View.OnClickListener{

    private final int EDIT_POST = 101;
    private String TAG = CreateArticleActivity.class.getSimpleName();
    private RichEditor edtPost;
    private TextView txtSubmit,txt_ch_count;
    private PostData post;
    private int position;
    private RecyclerView recyclerView;
    private ColorsAdapter colorsAdapter;

    private ActivityCreatePostBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create_post);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_post);

        edtPost = findViewById(R.id.edtPost);
        edtPost.setPlaceholder(getString(R.string.post_hint));
        txtSubmit = findViewById(R.id.txtSubmit);
        txt_ch_count = findViewById(R.id.txt_ch_count);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        colorsAdapter = new ColorsAdapter(this, getColors());
        recyclerView.setAdapter(colorsAdapter);
        txtSubmit.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (getIntent().getExtras() != null){
            post = (PostData) getIntent().getSerializableExtra("POST_DATA");
            position = getIntent().getExtras().getInt("POSITION");
            edtPost.setHtml(post.getPostData());
            toolbar.setTitle(getString(R.string.edit_post));
        }else{
            toolbar.setTitle(getString(R.string.create_post));
        }

        edtPost.setInputEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            if (post != null){
                setResult(EDIT_POST);
                finish();
            }else{
                onBackPressed();
            }
        });

        edtPost.setOnTouchListener((v, event) -> {
            edtPost.setInputEnabled(true);
            return false;
        });

        txtSubmit.setClickable(true);
        edtPost.setOnTextChangeListener(text -> {
            Log.i(TAG, "Change Text " + text);

            try {
                if (Html.fromHtml(text).toString().length() > 0 && Html.fromHtml(text).toString().length() <= 201){
                    txt_ch_count.setText(Html.fromHtml(text).toString().length() +"/ 200");
                    txt_ch_count.setTextColor(getBaseContext().getResources().getColor(R.color.black));
                }else{
                    txt_ch_count.setTextColor(getBaseContext().getResources().getColor(R.color.red1));
                    edtPost.setInputEnabled(false);
                }

                if (!TextUtils.isEmpty(text)) {
                    if (text.length() > 10) {
                        txtSubmit.setBackground(getResources().getDrawable(R.drawable.rounded_corner_app_purple_rect));
                        txtSubmit.setClickable(true);
                    } else {
                        txtSubmit.setBackground(getResources().getDrawable(R.drawable.rounded_corner_app_dark_grey_rect));
                        txtSubmit.setClickable(false);
                    }
                } else {
                    txtSubmit.setBackground(getResources().getDrawable(R.drawable.rounded_corner_app_dark_grey_rect));
                    txtSubmit.setClickable(false);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

    }

    /* Call From ColorsAdapter.java
     *  Get Color which selected in bottom colors list.
     *  @param position get selected position index
     *  @param selectedColor get selected color
     * */
    public void onColorsClick(int position, String selectedColor) {
        if (edtPost != null) {
            Log.i(TAG, "Selected Colors  " + selectedColor);
            if (edtPost.toString().length() == 1){
                edtPost.setEditorFontColor(Color.parseColor(selectedColor));
            }else {
                edtPost.setTextColor(Color.parseColor(selectedColor));
            }

        }
    }

    private ArrayList<String> getColors() {
        ArrayList<String> colorsList = new ArrayList<>();
        colorsList.add("#000000");
        colorsList.add("#3fa4b4");


    /*    colorsList.add("#ff7800");
        colorsList.add("#fcff00");
        colorsList.add("#3abf0b");
        colorsList.add("#10e1e9");
        colorsList.add("#123eac");
        colorsList.add("#6000ff");
        colorsList.add("#9016aa");*/
        return colorsList;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtSubmit:
                if(post != null){
                    if (edtPost.getHtml() != null && Html.fromHtml(edtPost.getHtml()).toString().trim().length() > 3 ){
                        if (Html.fromHtml(edtPost.getHtml()).toString().trim().length() <= 200){
                            getPostEdit();
                        }else{
                            Toast.makeText(CreateArticleActivity.this, "You can submit maximum 200 characters", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(CreateArticleActivity.this, R.string.valid_post, Toast.LENGTH_LONG).show();
                    }
                }else{
                    if (edtPost.getHtml() != null && Html.fromHtml(edtPost.getHtml()).toString().trim().length() > 5){
                        if (Html.fromHtml(edtPost.getHtml()).toString().trim().length() <= 200){
                            getCreatePost();
                        }else{
                            Toast.makeText(CreateArticleActivity.this, "You can submit maximum 200 characters", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(CreateArticleActivity.this, R.string.valid_post, Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public void getPostEdit() {
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.getPostEdit(BaseActivity.getKYC(),
                authToken, sp.getBoolean("guest_entry", false),
                Constants.getV6Value(), edtPost.getHtml(), post.getId() + ""), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                try {
                    Utils.hideDialog();
                    Log.e("Post Description", edtPost.getHtml());
                    Intent intent = getIntent();
                    intent.putExtra("POST_DESC", edtPost.getHtml());
                    setResult(RESULT_OK, intent);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                Utils.hideDialog();
                //Toast.makeText(CreateArticleActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Headers headers) {
                Utils.hideDialog();
                Toast.makeText(CreateArticleActivity.this, "Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionFailure() {
                Utils.hideDialog();
                Toast.makeText(CreateArticleActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                Utils.hideDialog();
                Toast.makeText(CreateArticleActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getCreatePost() {

        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.getAddPost(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false),Constants.getV6Value(),   edtPost.getHtml()), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                try {
                    Toast.makeText(CreateArticleActivity.this, "Post successfully submitted", Toast.LENGTH_LONG).show();
                    Utils.hideDialog();
                    setResult(RESULT_OK);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                Utils.hideDialog();
                //Toast.makeText(CreateArticleActivity.this, responseData.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Headers headers) {
                Utils.hideDialog();
                Toast.makeText(CreateArticleActivity.this, "Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionFailure() {
                Utils.hideDialog();
                Toast.makeText(CreateArticleActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                Utils.hideDialog();
                Toast.makeText(CreateArticleActivity.this, "EE Failure", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
