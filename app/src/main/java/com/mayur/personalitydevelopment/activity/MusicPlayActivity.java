package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.doneCourse;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utilities;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.app.PersonalityDevelopmentApp;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.MusicItem;
import com.mayur.personalitydevelopment.service.NotificationService;

import me.tankery.lib.circularseekbar.CircularSeekBar;
import okhttp3.Headers;
import okhttp3.ResponseBody;

public class MusicPlayActivity extends BaseActivity implements CircularSeekBar.OnCircularSeekBarChangeListener, View.OnClickListener {

    // Handler to update UI timer, progress bar etc,.
    private MusicItem musicItem;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = true;
    private TextView songTotalDurationLabel, songCurrentDurationLabel, songTitleLabel;
    private ImageView ivPlay;
    private String title;
    private int categoryId;
    private Toolbar maintoolbar;
    private CircularSeekBar seekBar;
    private Utilities utils;
    private int courseCategoryIdTemp;
    private boolean isPlaying = false;
    private boolean isSeekbarInteractionTurnedOn = false;
    private Handler mHandler = new Handler();
    Runnable runnable;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("onReceive", "called");
            if (intent.hasExtra("songFinished")) {
                ivPlay.setImageResource(R.drawable.ic_play_circle_filled);
                isPlaying = false;
            } else if (intent.hasExtra("songTime")) {
// Displaying Total Duration time
                isPlaying = true;
                if (!isSeekbarInteractionTurnedOn) {
                    long totalDuration = intent.getLongExtra("songTime", 0);
                    long currentDuration = intent.getLongExtra("playedTime", 0);
                    songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
                    // Displaying time completed playing
                    songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

                    // Updating progress bar
                    int progress = utils.getProgressPercentage(currentDuration, totalDuration);
                    //Log.d("Progress", ""+progress);
                    setProgressForLayout(progress);
                }
                ivPlay.setImageResource(R.drawable.ic_pause_circle_filled);
            } else if (intent.hasExtra("songPaused")) {
                ivPlay.setImageResource(R.drawable.ic_play_circle_filled);
                isPlaying = false;
            } else if (intent.hasExtra("isLoading")) {
                boolean isLoading = intent.getBooleanExtra("isLoading", false);
                if (isLoading) {
                    findViewById(R.id.pbProgress).setVisibility(View.VISIBLE);
                    findViewById(R.id.ivPlay).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.pbProgress).setVisibility(View.GONE);
                    findViewById(R.id.ivPlay).setVisibility(View.VISIBLE);
                }
            }
        }
    };
    private FloatingActionButton doneFloatingActionButton;
    private boolean isFromNotification;

    public static void start(Context context, String title, int courseCategoryId, int categoryId, MusicItem musicItem) {
        Intent starter = new Intent(context, MusicPlayActivity.class);
        starter.putExtra("musicItem", musicItem);
        starter.putExtra("title", title);
        starter.putExtra("courseCategoryId", courseCategoryId);
        starter.putExtra("categoryId", categoryId);
        context.startActivity(starter);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        utils = new Utilities();
        Log.e("onCreate", "called");
        registerReceiver(broadcastReceiver, new IntentFilter("songPlayService"), Context.RECEIVER_NOT_EXPORTED );

        if (getIntent().hasExtra("fromNotification")) {
            isFromNotification = getIntent().getExtras().getBoolean("fromNotification");
        }

        title = getIntent().getStringExtra("title");
        musicItem = (MusicItem) getIntent().getSerializableExtra("musicItem");
        categoryId = getIntent().getIntExtra("categoryId", 0);
        courseCategoryIdTemp = getIntent().getIntExtra("courseCategoryId", 0);


        doneFloatingActionButton = findViewById(R.id.doneBtn);
        doneFloatingActionButton.setOnClickListener(this);
        maintoolbar = findViewById(R.id.maintoolbar);
        songTitleLabel = findViewById(R.id.songTitleLabel);
        songCurrentDurationLabel = findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = findViewById(R.id.songTotalDurationLabel);
        setSupportActionBar(maintoolbar);
        maintoolbar.setTitle(title);
        setTitle(title);
        maintoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Listeners
//        songProgressBar.setOnSeekBarChangeListener(this); // Important
        // Important

        ivPlay = findViewById(R.id.ivPlay);
        ivPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setMusicData();
                // check for already playing

                if (!isPlaying) {
                    Log.e("ivPlay", "called");
                    Intent serviceIntent = new Intent(MusicPlayActivity.this, NotificationService.class);
                    serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                    serviceIntent.putExtra(Constants.ACTION.NOTIFICATION_TITLE, title);
                    serviceIntent.putExtra(Constants.ACTION.NOTIFICATION_SOUND_URL, musicItem.getUrl());
                    serviceIntent.putExtra(Constants.ACTION.NOTIFICATION_CATEGORY_ID, categoryId);
                    ivPlay.setImageResource(R.drawable.ic_pause_circle_filled);
                    isPlaying = true;
                    if (seekBar.getProgress() < 99)
                        serviceIntent.putExtra("progress", seekBar.getProgress());
                    else
                        serviceIntent.putExtra("progress", 0);
                    startService(serviceIntent);
                } else {
                    if (Utils.isNetworkAvailable(MusicPlayActivity.this)) {
                        isPlaying = false;
                        Intent playIntent = new Intent(MusicPlayActivity.this, NotificationService.class);
                        playIntent.setAction(Constants.ACTION.PAUSEFOREGROUND_ACTION);
                        playIntent.putExtra("progress", seekBar.getProgress());
                        startService(playIntent);
                        ivPlay.setImageResource(R.drawable.ic_play_circle_filled);
                    } else {
                        Toast.makeText(MusicPlayActivity.this, "Internet not available. Try after some time.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        ImageView imageView = findViewById(R.id.imageView);

        RequestOptions options = new RequestOptions();
        final RequestOptions placeholder_error = options.error(R.drawable.temo)
                .placeholder(R.drawable.temo).diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(this).load(musicItem.getImage_url()).apply(placeholder_error)
//                .apply(RequestOptions.circleCropTransform())
                .into(imageView);

        seekBar = findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void setMusicData() {
        Constants.music_title = title;
        Constants.music_url = musicItem.getUrl();
        Constants.music_image_url = musicItem.getImage_url();

        Constants.music_category_id = categoryId;
        Constants.music_course_category_id = courseCategoryIdTemp;

    }

    private void setProgressForLayout(int percentage) {
        seekBar.setProgress(percentage);
    }


    /**
     *
     */
    @Override
    public void onProgressChanged(CircularSeekBar seekBar, float progress, boolean fromTouch) {
//        mp.seekTo();
        int prog = (int) progress;
//        getMediaPlayer().seekTo(prog);
        Log.e("Progress", "changes");
    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(CircularSeekBar seekBar) {
        // remove message Handler from updating progress bar
        isSeekbarInteractionTurnedOn = true;
    }

    /**
     * When user stops moving the progress hanlder
     */
    @Override
    public void onStopTrackingTouch(CircularSeekBar seekBar) {
        isSeekbarInteractionTurnedOn = false;
        Intent intent = new Intent("songPlayService");
        intent.putExtra("seekTo", seekBar.getProgress());
        sendBroadcast(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        registerReceiver(broadcastReceiver, new IntentFilter("songPlayService"));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (isPlaying) {
            Intent playIntent = new Intent(MusicPlayActivity.this, NotificationService.class);
            playIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            playIntent.putExtra("progress", seekBar.getProgress());
            startService(playIntent);
        }
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        try {
            new NotificationService().stopNotificationService(this);
            if (isFromNotification) {
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
                goBack();
            }
            finish();
        } catch (Exception e) {
            Log.e("MusicPlayActivity", "onBackPressed: " + e + "");
        }
    }

    @Override
    public void onClick(View view) {
        new NotificationService().stopNotificationService(this);
        if (isFromNotification) {
            onDoneBtnClick();
        } else {
            onDoneClick(view);
        }
    }

    private void goBack() {
        CoursesCategoriesListActivity.start(this, 1, "SAVERS", true);
        finish();
    }

    private void onDoneBtnClick() {
        try {
//            Utils.showDialog(this);
            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, doneCourse(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), courseCategoryIdTemp, Utils.getCurrentDateWithTime()), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
//                    Utils.hideDialog();
                    Log.d("tag", "response:" + response);
                    goBack();
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
//                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Headers headers) {
//                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
//                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
//                    Utils.hideDialog();
                    Toast.makeText(getBaseContext(), "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    public MediaPlayer getMediaPlayer() {
        return PersonalityDevelopmentApp.getInstance().getMediaPlayer();
    }




}
