package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.allCourses;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.ItemCourseListAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.Course;
import com.mayur.personalitydevelopment.models.InnerCourseList;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class AllCoursesActivity extends BaseActivity implements ItemCourseListAdapter.AdapterListerner {

    public SharedPreferences sp;
    private RecyclerView rvList;
    private ProgressBar progressBar;
    private ItemCourseListAdapter itemCourseListAdapter;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_courses);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Severs");
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        rvList = findViewById(R.id.rvList);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCourses();
    }

    private void getCourses() {

        try {
            progressBar.setVisibility(View.VISIBLE);

            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, allCourses(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), Utils.getCurrentDate()), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    InnerCourseList data = new Gson().fromJson(response, InnerCourseList.class);
                    Utils.hideDialog();
                    progressBar.setVisibility(View.GONE);

                    itemCourseListAdapter = new ItemCourseListAdapter(getApplicationContext(), data.getData(), AllCoursesActivity.this);
                    rvList.setLayoutManager(new LinearLayoutManager(AllCoursesActivity.this));
                    rvList.setAdapter(itemCourseListAdapter);
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
                    Toast.makeText(AllCoursesActivity.this, "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AllCoursesActivity.this, "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AllCoursesActivity.this, "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    @Override
    public void onClickEvent(Course item) {
        CoursesCategoriesListActivity.start(AllCoursesActivity.this, item.getId(), item.getCourseName(), false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//        }
//        return true;
//    }
}
