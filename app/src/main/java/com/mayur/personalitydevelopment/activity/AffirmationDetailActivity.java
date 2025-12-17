package com.mayur.personalitydevelopment.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.base.BaseActivity;

import java.io.FileInputStream;
import java.io.IOException;

public class AffirmationDetailActivity extends BaseActivity {
    private ImageView imageView;
    private Toolbar maintoolbar;
    private View btnLogin;
    private TextView tvMessage;

    private int[] imagesList = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5};
    private int currentImage = 0, currentTextTitleIndex = 0;
    private MediaPlayer player;
    private String[] title;
    private Handler showDataHandler, showTitleHandler;
    private Runnable showDataRunnable;
    private FloatingActionButton repeatFloatingBtn;

    public static void start(Context context, String[] affirmations, int categoryId) {
        Intent starter = new Intent(context, AffirmationDetailActivity.class);
        starter.putExtra("title", affirmations);
        starter.putExtra("courseCategoryId", categoryId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affirmation_detail);
        title = getIntent().getStringArrayExtra("title");
        imageView = findViewById(R.id.imageView);
        maintoolbar = findViewById(R.id.maintoolbar);
        btnLogin = findViewById(R.id.btnLogin);
        tvMessage = findViewById(R.id.tvMessage);
        repeatFloatingBtn = findViewById(R.id.btnRepeat);

        setSupportActionBar(maintoolbar);
        maintoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        playMusic();
        showData();
        showTitle();
    }

    void showTitle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isDestroyed())
                return;
        }
        tvMessage.setText(title[currentTextTitleIndex++]);

        showTitleHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentTextTitleIndex < title.length) {
                    showTitle();
                } else {
                    showDataHandler.removeCallbacks(showDataRunnable);
                    stopMusic();
                    repeatFloatingBtn.setVisibility(View.VISIBLE);
                }
            }
        };
        showTitleHandler.postDelayed(runnable, 3000);
    }

    void showData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isDestroyed())
                return;
        }
        currentImage++;
        if (currentImage == imagesList.length)
            currentImage = 0;

        Glide.with(this).load(imagesList[currentImage]).transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        showDataHandler = new Handler();
        showDataRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentImage < imagesList.length) {
                    showData();
                }
            }
        };
        showDataHandler.postDelayed(showDataRunnable, 3000);
    }

    void playMusic() {
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        try {
            player = new MediaPlayer();
            FileInputStream fis = null;
            try {
                AssetFileDescriptor descriptor = getAssets().openFd("affirmation_music.mp3");
                player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(),
                        descriptor.getLength());

                descriptor.close();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.prepareAsync();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        finish();
                    }
                });
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException ignore) {
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopMusic() {
        try {
            if (player.isPlaying()) {
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        try {
            stopMusic();
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    public void repeatPlay(View view) {
        repeatFloatingBtn.setVisibility(View.GONE);
        currentImage = 0;
        currentTextTitleIndex = 0;
        playMusic();
        showData();
        showTitle();
    }

}
