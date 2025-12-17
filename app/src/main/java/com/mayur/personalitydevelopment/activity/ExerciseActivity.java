package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.CountDownTimerCustom;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.Exercise;
import com.mayur.personalitydevelopment.models.YoutubeItem;

import java.util.concurrent.TimeUnit;

import me.tankery.lib.circularseekbar.CircularSeekBar;
import okhttp3.Headers;
import okhttp3.ResponseBody;

public class ExerciseActivity extends BaseActivity implements View.OnClickListener {

    private final int timeInterval = 10;
    private long duration = 30000;
    private YoutubeItem exerciseItem;
    private CircularSeekBar progressBar;
    private TextView timeTextView, exerciseNameTextView;
    private LottieAnimationView lottieAnimationView;
    private ImageView previousImageView, pauseImageView, nextImageView;
    private CountDownTimerCustom countDownTimer;
    private boolean isPaused;
    private Exercise exercise;
    private String currentExericeId;
    private String previousExericeId;
    private String nextExericeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initV();
        displayProgress();
        if (getIntent().getExtras() != null && getIntent().getSerializableExtra("selectedExercise") != null) {
            exerciseItem = (YoutubeItem) getIntent().getSerializableExtra("selectedExercise");
            exerciseNameTextView.setText(exerciseItem.getTitle());
            getSupportActionBar().setTitle(exerciseItem.getTitle());
            currentExericeId = exerciseItem.getId() + "";
            getExercise(currentExericeId);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initV() {
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        progressBar = findViewById(R.id.progressBar);
        timeTextView = findViewById(R.id.timeTextV);
        previousImageView = findViewById(R.id.previousImageV);
        pauseImageView = findViewById(R.id.pauseImageV);
        nextImageView = findViewById(R.id.nextImageV);
        exerciseNameTextView = findViewById(R.id.exerciseNameTextV);

        previousImageView.setOnClickListener(this);
        pauseImageView.setOnClickListener(this);
        nextImageView.setOnClickListener(this);
    }

    private void displayProgress() {
        progressBar.setMax(duration);
        countDownTimer = new CountDownTimerCustom(duration, timeInterval) {
            public void onTick(long millisUntilFinished) {
                timeTextView.setText(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + "");
                progressBar.setProgress(millisUntilFinished);
            }

            public void onFinish() {
                lottieAnimationView.cancelAnimation();
                progressBar.setProgress(100);
                resetTimerAndAnimation();
            }
        };
    }

    private void playLottieAnimation(String jsonUrl) {

        try {

            lottieAnimationView.setAnimationFromUrl(jsonUrl);
//        lottieAnimationView.setAnimationFromUrl("https://assets3.lottiefiles.com/packages/lf20_yXWIdm.json");
            lottieAnimationView.playAnimation();

        } catch (Exception e) {

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previousImageV:
                getExercise(previousExericeId);
                break;

            case R.id.pauseImageV:
                setPlayPauseImageView();
                break;

            case R.id.nextImageV:
                getExercise(nextExericeId);
                break;
        }
    }

    private void setPlayPauseImageView() {
        if (isPaused) {
            pauseImageView.setImageResource(R.drawable.ic_pause);
            isPaused = false;
            countDownTimer.resume();
            lottieAnimationView.resumeAnimation();
        } else {
            pauseImageView.setImageResource(R.drawable.ic_play);
            isPaused = true;
            countDownTimer.pause();
            lottieAnimationView.pauseAnimation();
        }
    }


    private void getExercise(String exerciseId) {
        Utils.showDialog(this);
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.getExercise(
                BaseActivity.getKYC(),
                authToken,
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value(),
                exerciseId,
                "Exercise"),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        Utils.hideDialog();

                        Gson gson = new GsonBuilder().setLenient().create();

                        exercise = gson.fromJson(response, Exercise.class);

                        getSupportActionBar().setTitle(exercise.getTitle());
                        exerciseNameTextView.setText(exercise.getTitle());
                        currentExericeId = exercise.getId();
                        previousExericeId = exercise.getPrev();
                        nextExericeId = exercise.getNext();
                        timeTextView.setText(exercise.getTime_duration());
                        resetTimerAndAnimation();

                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onConnectionFailure() {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(getBaseContext(), "CC Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(getBaseContext(), "EE Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }
                });
    }

    private void resetTimerAndAnimation() {
        duration = TimeUnit.SECONDS.toMillis(Long.parseLong(exercise.getTime_duration()));
        countDownTimer.cancel();
        displayProgress();
        isPaused = false;
        progressBar.setProgress(0);
        countDownTimer.start();
        countDownTimer.pause();
        playLottieAnimation(exercise.getAnimated_img());
        lottieAnimationView.pauseAnimation();
        setPlayPauseImageView();
    }

}