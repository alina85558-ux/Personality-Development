package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.allAffirmation;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.SpacesItemDecoration;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.AffirmationCategoriesAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.Affirmation;
import com.mayur.personalitydevelopment.models.InnerAffirmationCategoryResponse;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class AffirmationCategoryActivity extends BaseActivity implements AffirmationCategoriesAdapter.AdapterListener {

    private RecyclerView rvList;
    private ProgressBar progressBar;
    private Toolbar maintoolbar;
    private AffirmationCategoriesAdapter courseCategoriesAdapter;
    private String course;
    private int categoryId;

    public static void start(Context context, int courseCategoryId, int categoryId, String course) {
        Intent starter = new Intent(context, AffirmationCategoryActivity.class);
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
        progressBar = findViewById(R.id.progressBar);
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
    }


    public void getCourses() {
        try {
            progressBar.setVisibility(View.VISIBLE);

            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, allAffirmation(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value()), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    InnerAffirmationCategoryResponse data = new Gson().fromJson(response, InnerAffirmationCategoryResponse.class);
                    Utils.hideDialog();
                    progressBar.setVisibility(View.GONE);

                    courseCategoriesAdapter = new AffirmationCategoriesAdapter(getBaseContext(), data.getMusics(), AffirmationCategoryActivity.this);
                    int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
                    rvList.addItemDecoration(new SpacesItemDecoration(spacingInPixels, true));
                    rvList.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
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


    public void onClickEvent(Affirmation musicItem) {
        AffirmationListingCategoryActivity.start(this, courseCategoryId, musicItem.getId(), musicItem.getCategoryName(), musicItem.getDetail_image_url());
    }
}
