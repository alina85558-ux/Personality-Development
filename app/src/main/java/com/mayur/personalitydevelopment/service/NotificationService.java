package com.mayur.personalitydevelopment.service;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;
import static com.mayur.personalitydevelopment.Utils.Constants.ACTION.NOTIFICATION_CATEGORY_ID;
import static com.mayur.personalitydevelopment.Utils.Constants.ACTION.NOTIFICATION_SOUND_URL;
import static com.mayur.personalitydevelopment.Utils.Constants.ACTION.NOTIFICATION_TITLE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utilities;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.activity.MainActivity;
import com.mayur.personalitydevelopment.app.PersonalityDevelopmentApp;

import java.io.IOException;

public class NotificationService extends Service implements MediaPlayer.OnCompletionListener {

    final String LOG_TAG = "notification_service";
    final String idChannel = "music";
    Notification status;
    String title, soundUrl;
    float progress;
    int categoryId;
    private Handler mHandler = new Handler();
    private Utilities utils;
    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (getMediaPlayer().isPlaying()) {
                long totalDuration = getMediaPlayer().getDuration();
                long currentDuration = getMediaPlayer().getCurrentPosition();
                Intent intent = new Intent("songPlayService");
                intent.putExtra("songTime", totalDuration);
                intent.putExtra("playedTime", currentDuration);
                sendBroadcast(intent);
                mHandler.postDelayed(this, 100);
            }
        }
    };
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Notification ", "onReceive Called");
            if (intent.hasExtra("seekTo")) {
                int totalDuration = getMediaPlayer().getDuration();
                int currentPosition = utils.progressToTimer(Math.round(intent.getFloatExtra("seekTo", 0)), totalDuration);

                // forward or backward to certain seconds
                getMediaPlayer().seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
            }
        }
    };

    public MediaPlayer getMediaPlayer() {
        return PersonalityDevelopmentApp.getInstance().getMediaPlayer();
    }

    public void releasePlayer() {
        PersonalityDevelopmentApp.getInstance().releaseMediaPlayer();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(broadcastReceiver, new IntentFilter("songPlayService"), Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getAction()) {
                case Constants.ACTION.STARTFOREGROUND_ACTION:
                    title = intent.getStringExtra(NOTIFICATION_TITLE);
                    soundUrl = intent.getStringExtra(NOTIFICATION_SOUND_URL);
                    categoryId = intent.getIntExtra(NOTIFICATION_CATEGORY_ID, 0);
                    progress = intent.getFloatExtra("progress", 0);


                    showNotification(title, soundUrl, categoryId, false);
//                Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
                    initMediaPlayer();
                    break;
                case Constants.ACTION.PREV_ACTION:
//                Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
                    Log.i(LOG_TAG, "Clicked Previous");
                    break;
                case Constants.ACTION.PLAY_ACTION:
//                Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
                    Log.i(LOG_TAG, "Clicked Play");
                    if (intent.hasExtra("progress"))
                        progress = intent.getFloatExtra("progress", 0);
                    if (getMediaPlayer().isPlaying()) {
                        getMediaPlayer().pause();
                        Intent stopPlayingIntent = new Intent("songPlayService");
                        stopPlayingIntent.putExtra("songPaused", true);
                        sendBroadcast(stopPlayingIntent);
                        this.stopForeground(false);
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.cancelAll();
                        stopServiceForcefully();
                    } else {
                        // Resume song
                        if (getMediaPlayer() != null) {
                            playSong();
                        }
                    }
                    break;
                case Constants.ACTION.NEXT_ACTION:
//                Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
                    Log.i(LOG_TAG, "Clicked Next");
                    break;
                case Constants.ACTION.STOPFOREGROUND_ACTION:
//                stopServiceForcefully();
                    break;
                case Constants.ACTION.PAUSEFOREGROUND_ACTION:
                    stopServiceForcefully();
                    break;
            }
        }
        return START_STICKY;
    }

    private void stopServiceForcefully() {
        Log.i(LOG_TAG, "Received Stop Foreground Intent");
//                Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
        if (getMediaPlayer() != null && getMediaPlayer().isPlaying()) {
            releasePlayer();
            mHandler.removeCallbacks(mUpdateTimeTask);
        }
        Intent stopPlayingIntent = new Intent("songPlayService");
        stopPlayingIntent.putExtra("songPaused", true);
        sendBroadcast(stopPlayingIntent);
        stopForeground(true);
        stopSelf();
    }

    public void stopNotificationService(Context context) {
        if (getMediaPlayer() != null && getMediaPlayer().isPlaying()) {
            releasePlayer();
            mHandler.removeCallbacks(mUpdateTimeTask);
        }
        stopSelf();
        context.stopService(new Intent(context, NotificationService.class));
    }

    private void showNotification(String title, String soundUrl, int categoryId, boolean isPLaying) {
// Using RemoteViews to bind custom layouts into Notification
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        RemoteViews bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);

// showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                Constants.getDefaultAlbumArt(this));

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("fromMusicService", true);
//        notificationIntent.setAction("fromMusicService");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, Utils.getCurrentTimeMillis(), notificationIntent, FLAG_IMMUTABLE);

        Intent previousIntent = new Intent(this, NotificationService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, Utils.getCurrentTimeMillis(), previousIntent, FLAG_IMMUTABLE);

        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, Utils.getCurrentTimeMillis(), playIntent, FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(this, NotificationService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, Utils.getCurrentTimeMillis(), nextIntent, FLAG_IMMUTABLE);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, Utils.getCurrentTimeMillis(), closeIntent, FLAG_IMMUTABLE);

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        if (!isPLaying) {
            views.setImageViewResource(R.id.status_bar_play, R.drawable.ic_play_circle_filled);
        } else {
            views.setImageViewResource(R.id.status_bar_play, R.drawable.ic_pause_circle_filled);
        }
        bigViews.setImageViewResource(R.id.status_bar_play, R.drawable.ic_pause_circle_filled);

        views.setTextViewText(R.id.status_bar_track_name, getString(R.string.app_name));
        bigViews.setTextViewText(R.id.status_bar_track_name, getString(R.string.app_name));

        views.setTextViewText(R.id.status_bar_artist_name, title);
        bigViews.setTextViewText(R.id.status_bar_artist_name, title);

        bigViews.setTextViewText(R.id.status_bar_album_name, "Album Name");


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(idChannel, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            mChannel.setDescription("Music meditation");
            mNotificationManager.createNotificationChannel(mChannel);

            status = new Notification.Builder(this, idChannel)
                    .setCustomContentView(views)
//                    .setCustomBigContentView(bigViews)
                    .setChannelId(idChannel)
                    .setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(Constants.getDefaultAlbumArt(this))
                    .build();
            status.flags = Notification.FLAG_ONGOING_EVENT;
            status.contentIntent = pendingIntent;

            mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
        } else {
            status = new Notification.Builder(this).build();
            status.contentView = views;
            status.bigContentView = bigViews;
            status.flags = Notification.FLAG_ONGOING_EVENT;
            status.icon = R.mipmap.ic_launcher;
            status.contentIntent = pendingIntent;
            mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        } else {
            this.startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
        }
    }

    void initMediaPlayer() {
        utils = new Utilities();
        getMediaPlayer().setOnCompletionListener(this);
        playSong();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Intent intent = new Intent("songPlayService");
        intent.putExtra("songFinished", true);
        sendBroadcast(intent);
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * Function to play a song
     */
    public void playSong() {
        // Play song
        try {
            if (getMediaPlayer().isPlaying()) {
                getMediaPlayer().stop();
            }
            getMediaPlayer().reset();
            getMediaPlayer().setDataSource(this, Uri.parse(soundUrl));
            getMediaPlayer().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    updateProgressBar();
                    int totalDuration = mp.getDuration();
                    int currentPosition = utils.progressToTimer((int) progress, totalDuration);
                    if (totalDuration == currentPosition) {
                        currentPosition = 0;
                    }
                    // forward or backward to certain seconds
                    mp.seekTo(currentPosition);
                    mp.start();
                    sendPreparingBroadcast(false);
                    showNotification(title, soundUrl, categoryId, true);
                }
            });
            getMediaPlayer().setOnInfoListener(new MediaPlayer.OnInfoListener() {

                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            sendPreparingBroadcast(true);
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            sendPreparingBroadcast(false);
                            break;
                    }
                    return false;
                }
            });
            getMediaPlayer().prepareAsync();
            sendPreparingBroadcast(true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            sendPreparingBroadcast(false);
            e.printStackTrace();
        }
    }

    public void sendPreparingBroadcast(boolean value) {
        Intent intent = new Intent("songPlayService");
        intent.putExtra("isLoading", value);
        sendBroadcast(intent);
    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        stopServiceForcefully();
        super.onTaskRemoved(rootIntent);
    }

}