package com.mayur.personalitydevelopment.fragment;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.allCourses;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.activity.CoursesCategoriesListActivity;
import com.mayur.personalitydevelopment.activity.WakeUpTimeSettingActivity;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.Course;
import com.mayur.personalitydevelopment.models.InnerCourseList;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class CourseListingFragment extends Fragment implements View.OnClickListener {

    private TextView remainingSectionMsgTextView;
    private ImageView timerImageView;
    private Button watchVideoButton, exploreSaversButton;
    private String courseName;
    private int courseId;
    private String wakeUpTime;
    private SharedPreferences sp;
    private String youtubeVideoUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_listing, null);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initV(view);
        return view;
    }

    private void initV(View view) {
        remainingSectionMsgTextView = view.findViewById(R.id.remainingTaskMsgTextV);
        timerImageView = view.findViewById(R.id.wakeupImageView);
        watchVideoButton = view.findViewById(R.id.watchVideoBtn);
        exploreSaversButton = view.findViewById(R.id.exploreSaversBtn);

        timerImageView.setOnClickListener(this);
        watchVideoButton.setOnClickListener(this);
        exploreSaversButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.isNetworkAvailable(getActivity())) {
            getCourses();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.wakeupImageView:
                if (Utils.isNetworkAvailable(getActivity())) {
                    getAllSettings();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.watchVideoBtn:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeVideoUrl)));
                break;

            case R.id.exploreSaversBtn:
//                startActivity(new Intent(getActivity(), AllCoursesActivity.class));
                CoursesCategoriesListActivity.start(getActivity(), courseId, courseName, false);
                break;
        }

    }

    private void getAllSettings() {
        Utils.showDialog(getActivity());
        try {
            String authToken = "";
            if (Constants.getUserData(getActivity()) != null) {
                authToken = Constants.getUserData(getActivity()).getAuthentication_token();
            }

            connectPost(getActivity(), null, ApiCallBack.listAllSettings(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value()), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    try {
                        JSONObject object = new JSONObject(response);

                        if (object.getString("wakeup_time").equals("")) {
                            wakeUpTime = "";
                        } else {
                            wakeUpTime = object.getString("wakeup_time");
                        }

                        gotoWakeUpTimeSettingActivity();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    // Toast.makeText(getActivity(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "EE Failure", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Utils.hideDialog();
            e.printStackTrace();
        }
    }

    private void gotoWakeUpTimeSettingActivity() {
        Intent intent = new Intent(getActivity(), WakeUpTimeSettingActivity.class);
        intent.putExtra("wakeUpTime", wakeUpTime);
        startActivity(intent);
    }

    private void getCourses() {
        Utils.showDialog(getActivity());
        try {
            String authToken = "";
            if (Constants.getUserData(getActivity()) != null) {
                authToken = Constants.getUserData(getActivity()).getAuthentication_token();
            }

            connectPost(getActivity(), null, allCourses(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), Utils.getCurrentDate()), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    InnerCourseList data = new Gson().fromJson(response, InnerCourseList.class);
                    ArrayList<Course> courseList = new ArrayList<>();
                    courseList = data.getData();
                    Course course = courseList.get(0);
                    courseName = course.getCourseName();
                    courseId = course.getId();
                    youtubeVideoUrl = data.getYoutubeVideoUrl();

                    if (course.getAccessmessage().equals("")) {
                        remainingSectionMsgTextView.setVisibility(View.INVISIBLE);
                    } else {
                        remainingSectionMsgTextView.setVisibility(View.VISIBLE);
                        if (Utils.isSubscribed(getActivity())) {
                            remainingSectionMsgTextView.setText(course.getRemainTaskMsg());
                        } else {
                            remainingSectionMsgTextView.setText(course.getAccessmessage());
                        }
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Toast.makeText(getActivity(), "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }



}
