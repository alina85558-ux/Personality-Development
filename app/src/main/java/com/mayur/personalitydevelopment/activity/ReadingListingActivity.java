package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.allReading;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.ReadingListAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.Articles;
import com.mayur.personalitydevelopment.models.ReadingListingResponse;

import org.json.JSONObject;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class ReadingListingActivity extends BaseActivity implements ReadingListAdapter.AdapterListener {

    private RecyclerView rvList;
    private ProgressBar progressBar;
    private Toolbar maintoolbar;
    private ReadingListAdapter courseCategoriesAdapter;
    private String course;
    private int categoryId;

    public static void start(Context context, int courseCategoryId, int categoryId, String course) {
        Intent starter = new Intent(context, ReadingListingActivity.class);
        starter.putExtra("categoryId", categoryId);
        starter.putExtra("course", course);
        starter.putExtra("courseCategoryId", courseCategoryId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_categories);
        categoryId = getIntent().getIntExtra("categoryId", 0);
        course = getIntent().getStringExtra("course");
        maintoolbar = findViewById(R.id.maintoolbar);
        rvList = findViewById(R.id.rvList);
        rvList.setPadding(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.recyclerview_done_button_padding));
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        findViewById(R.id.btnDone).setVisibility(View.VISIBLE);

        setSupportActionBar(maintoolbar);
        maintoolbar.setTitle(course);
        setTitle(course);
        maintoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getCourses();
        setColorData();
    }


    public void getCourses() {
        try {
            Utils.showDialog(this);

            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, allReading(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value()), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    ReadingListingResponse data = new Gson().fromJson(response, ReadingListingResponse.class);

                    courseCategoriesAdapter = new ReadingListAdapter(getBaseContext(), data.getData(), ReadingListingActivity.this);
                    rvList.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    rvList.addItemDecoration(new DividerItemDecoration(getBaseContext(), DividerItemDecoration.HORIZONTAL));
                    rvList.setAdapter(courseCategoriesAdapter);
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
                    Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    @Override
    public void onClickEvent(Articles musicItem) {
        Intent intent = new Intent(this, ArticleDetailActivity.class);
        intent.putExtra("Message", new Gson().toJson(musicItem));
        intent.putExtra("IS_FROM", 1);
        startActivityForResult(intent, 102);
    }

    public void setColorData() {
        boolean light = sp.getBoolean("light", false);
        try {
            if (light) {
                findViewById(R.id.content).setBackgroundColor(Color.parseColor("#363636"));
            } else {
                findViewById(R.id.content).setBackgroundColor(Color.parseColor("#ffffff"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }
}
