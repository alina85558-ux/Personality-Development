package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.calenderScreen;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.CalenderData;
import com.riontech.calendar.Singleton;
import com.riontech.calendar.fragment.dao.Event;
import com.riontech.calendar.fragment.CalendarFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class CalenderEventsScreen extends BaseActivity implements CalendarFragment.onPrevNextFunction {
    private Toolbar maintoolbar;
    private CalendarFragment calendarFragment;

    public static void start(Context context) {
        Intent starter = new Intent(context, CalenderEventsScreen.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_events);
        maintoolbar = findViewById(R.id.maintoolbar);
        setSupportActionBar(maintoolbar);
        maintoolbar.setTitle("");
        setTitle("Progress Tracker");
        maintoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        int month = Calendar.getInstance().get(Calendar.MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        fetchData(month + 1, year);
        calendarFragment = CalendarFragment.newInstance(this, true, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.customCalendar, calendarFragment, "calendarFragment").commit();
    }

    void fetchData(int month, int year) {
        try {
            Utils.showDialog(this);
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, calenderScreen(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), month, year), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    Log.d("tag", "response:" + response);
                    CalenderData data = new Gson().fromJson(response, CalenderData.class);
                    if (data != null) {
                        Set keys = data.getTrackedDates().keySet();
                        Iterator<String> keyIterator = keys.iterator();
                        ArrayList<Event> eventList = new ArrayList<>();
                        while (keyIterator.hasNext()) {
                            String key = keyIterator.next();
                            Integer value = data.getTrackedDates().get(key);

                            String month = (Singleton.getInstance().getMonth().get(Calendar.MONTH) + 1) + "";

                            if ((Singleton.getInstance().getMonth().get(Calendar.MONTH) + 1) < 10) {
                                month = "0" + month;
                            }

                            String eventDate = Singleton.getInstance().getMonth().get(Calendar.YEAR) + "-" + month + "-" + key;

                            Event date = new Event();
                            date.setDate(eventDate);
                            date.setCount(String.valueOf(value));
                            date.setEventData(null);

                            eventList.add(date);
//                            customCalendar.addAnEvent("2019-11-" + key, value, null);
                        }
                        Singleton.getInstance().setEventManager(eventList);
                        calendarFragment.setTotalCompletedDays(data.getTotalCompletedDays());
                        calendarFragment.refreshDays();
                    }
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    Utils.hideDialog();
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
    public void onPrev() {
        GregorianCalendar calender = calendarFragment.setPreviousMonth();
        fetchData(calendarFragment.mCalendar.get(Calendar.MONTH) + 1, calendarFragment.mCalendar.get(Calendar.YEAR));
        calendarFragment.refreshCalendar();
    }

    @Override
    public void onNext() {
        GregorianCalendar calender = calendarFragment.setNextMonth();
        fetchData(calendarFragment.mCalendar.get(Calendar.MONTH) + 1, calendarFragment.mCalendar.get(Calendar.YEAR));
        calendarFragment.refreshCalendar();
    }
}
