package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.setNotificationTime;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class WakeUpTimeSettingActivity extends BaseActivity {

    private TextView selectedTimeTextiew;
    private String selectedTime = "";
    private String wakeUpTime = "";
    private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("hh:mm aa", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up_time_setting);
        initV();

        if (getIntent().getStringExtra("wakeUpTime") != null && !getIntent().getStringExtra("wakeUpTime").equals("")) {
            wakeUpTime = getIntent().getStringExtra("wakeUpTime");
            selectedTimeTextiew.setText(Utils.changeHourFormat(wakeUpTime));
        }
    }

    private void initV() {
        selectedTimeTextiew = findViewById(R.id.tvSelectedTime);
    }

    public void on6AmClick(View view) {
        setTime(06, 00);
    }

    public void on7AmClick(View view) {
        setTime(07, 00);
    }

    public void on8AmClick(View view) {
        setTime(8, 00);
    }

    public void onCustomWakeUpClockClick(View view) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        if (wakeUpTime != null && !wakeUpTime.equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            Date date = null;
            try {
                date = sdf.parse(wakeUpTime);
            } catch (ParseException e) {
            }
            mcurrentTime.setTime(date);

            hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            minute = mcurrentTime.get(Calendar.MINUTE);
        }

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                eReminderTime.setText( selectedHour + ":" + selectedMinute);
                setTime(selectedHour, selectedMinute);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    void setTime(int hour, int minute) {
        DecimalFormat formatter = new DecimalFormat("00");
        selectedTime = (formatter.format(hour) + ":" + formatter.format(minute) + ":00");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        selectedTimeTextiew.setText(dateTimeFormatter.format(calendar.getTime()));
//        ((FloatingActionButton) findViewById(R.id.btnDone)).set(formatter.format(hour) + ":" + formatter.format(minute) + "  Done ");
    }

    private String getUtcTime() {
        String stringDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
//        String dateWithTime = stringDate + " "+ Utils.changeHourFormat(selectedTime);

        String dateWithTime = stringDate + " " + selectedTime;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            Date date = format.parse(dateWithTime);
            DateFormat df = DateFormat.getTimeInstance();
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            String utcTime = df.format(date);
            SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm:ss a");
            SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm:ss");
            return date24Format.format(date12Format.parse(utcTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onDoneClick(View view) {
        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Select time first", Toast.LENGTH_LONG).show();
            return;
        }

        String utcTime = getUtcTime();

        try {
            Utils.showDialog(this);
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
            Date currentLocalTime = calendar.getTime();
            DateFormat date = new SimpleDateFormat("z", Locale.getDefault());
            String timeZone = date.format(currentLocalTime);


            connectPost(this, null, setNotificationTime(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), selectedTime, timeZone, utcTime),
                    new ApiConnection.ConnectListener() {
                        @Override
                        public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                            Utils.hideDialog();
                            Log.d("tag", "response:" + response);
                            finish();
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

    public void onBackPress(View view) {
        onBackPressed();
    }
}
