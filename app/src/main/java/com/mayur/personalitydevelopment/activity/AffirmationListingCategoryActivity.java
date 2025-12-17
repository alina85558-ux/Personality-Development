package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.affirmationListing;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.AffirmationListingCategoriesAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.AffirmationListing;
import com.mayur.personalitydevelopment.models.InnerAffirmationListingCategoryResponse;

import java.util.ArrayList;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class AffirmationListingCategoryActivity extends BaseActivity implements AffirmationListingCategoriesAdapter.AdapterListener, View.OnClickListener {

    private RecyclerView rvList;
    private ProgressBar progressBar;
    private Toolbar maintoolbar;
    private AffirmationListingCategoriesAdapter courseCategoriesAdapter;
    private String course;
    private String categoryImage;
    private FloatingActionButton playFloatingActionButton;
    private int categoryId;

    public static void start(Context context, int courseCategoryId, int categoryId, String course, String categoryImage) {
        Intent starter = new Intent(context, AffirmationListingCategoryActivity.class);
        starter.putExtra("categoryId", categoryId);
        starter.putExtra("course", course);
        starter.putExtra("courseCategoryId", courseCategoryId);
        starter.putExtra("categoryImage", categoryImage);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affirmation_listing);
        categoryId = getIntent().getIntExtra("categoryId", 0);
        course = getIntent().getStringExtra("course");
        categoryImage = getIntent().getStringExtra("categoryImage");
        maintoolbar = findViewById(R.id.maintoolbar);
        rvList = findViewById(R.id.rvList);
        progressBar = findViewById(R.id.progressBar);
        setSupportActionBar(maintoolbar);
        maintoolbar.setTitle(course);
        setTitle(course);
        playFloatingActionButton = findViewById(R.id.playFloatingBtn);
        playFloatingActionButton.setOnClickListener(this);
        maintoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getCourses();

        ImageView backToolbar = findViewById(R.id.backToolbar);

        RequestOptions options = new RequestOptions();
        final RequestOptions placeholder_error = options.error(R.drawable.temo)
                .placeholder(R.drawable.temo).diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(this).load(categoryImage).apply(placeholder_error).into(backToolbar);

    }


    public void getCourses() {
        try {
            progressBar.setVisibility(View.VISIBLE);

            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, affirmationListing(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), categoryId), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    InnerAffirmationListingCategoryResponse data = new Gson().fromJson(response, InnerAffirmationListingCategoryResponse.class);
                    Utils.hideDialog();
                    progressBar.setVisibility(View.GONE);

                    courseCategoriesAdapter = new AffirmationListingCategoriesAdapter(getBaseContext(), data.getMusics(), AffirmationListingCategoryActivity.this);
                    rvList.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    rvList.addItemDecoration(new DividerItemDecoration(getBaseContext(), DividerItemDecoration.HORIZONTAL));
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

    public void onClickEvent(AffirmationListing musicItem) {
        musicItem.setSelected(!musicItem.isSelected());
        courseCategoriesAdapter.notifyDataSetChanged();
    }


    public void goToAffirmationDetail() {
        if (courseCategoriesAdapter == null || courseCategoriesAdapter.getItemCount() == 0)
            return;
        ArrayList<AffirmationListing> affirmationListings = courseCategoriesAdapter.getItems();
        ArrayList<String> selectedAffirmations = new ArrayList<>();
        for (int index = 0; index < affirmationListings.size(); index++) {
            if (affirmationListings.get(index).isSelected())
                selectedAffirmations.add(affirmationListings.get(index).getText());
        }

        if (selectedAffirmations.size() == 0) {
            Toast.makeText(this, "Please select at least one affirmation", Toast.LENGTH_LONG).show();
            return;
        }

        String[] str = new String[selectedAffirmations.size()];
        str = selectedAffirmations.toArray(str);
        AffirmationDetailActivity.start(this, str, courseCategoryId);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_done, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menuDone:
//                goToAffirmationDetail();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onClick(View v) {
        goToAffirmationDetail();
    }
}
