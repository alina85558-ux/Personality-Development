package com.mayur.personalitydevelopment.notification;

import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.activity.ArticleDetailActivity;
import com.mayur.personalitydevelopment.activity.MainActivity;
import com.mayur.personalitydevelopment.activity.PostDetailActivity;
import com.mayur.personalitydevelopment.activity.RemoveAdActivity;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static int NOTIFICATION_ID = 1;
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;

    String CHANNEL_ID = "betstifyme_channel_01";
    int importance = NotificationManager.IMPORTANCE_HIGH;
    private NotificationChannel mChannel;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        editor.putString("FCM_TOKEN", s);
        Utils.saveFcmToken(this, s);
        Log.e("Token ", s.toString());
        updateToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("NotificationDebug", "FCM Message Received");

        // Now check contents
        if (remoteMessage.getNotification() != null) {
            Log.e("NotificationDebug", "Notification Body: " + remoteMessage.getNotification().getBody());
        }

        Log.e("NotificationDebug", "remoteMessage : " + remoteMessage.toString());

        if (remoteMessage.getData().size() > 0) {
            Log.e("NotificationDebug", "Data Payload: " + remoteMessage.getData().toString());
        }


        Log.d(TAG, remoteMessage.getData().toString());
        Log.e("Notification From", "From: " + remoteMessage.getFrom());
        Log.e("Notification Data", "From Data: " + remoteMessage.getData().toString());
        Map<String, String> map = remoteMessage.getData();
        Log.e("in FCM msg", ">>" + map.toString());

        CharSequence name = getString(R.string.channel_name);
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }*/
        //message will contain the Push Message
        String detail = remoteMessage.getData().get("detail");
        String title = remoteMessage.getData().get("title");
        //imageUri will contain URL of the image to be displayed with Notification
        String imageUri = remoteMessage.getData().get("image");
        Bitmap bitmap = null;
        if (imageUri != null && !imageUri.equalsIgnoreCase("")) {
            bitmap = getBitmapfromUrl(imageUri);
        }
        sendNotification(detail, bitmap, title, remoteMessage.getData());
    }

    private void sendNotification(String messageBody, Bitmap image, String title, Map<String, String> remoteData) {

//        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = null;
        String redirectTo = "";
        if (remoteData != null) {
            redirectTo = remoteData.get("redirect_to");
        }

        if (redirectTo != null && !redirectTo.isEmpty()) {
            if (redirectTo.equalsIgnoreCase("article_detail")) {
                String passArticleId = remoteData.get("article_id");
                if (passArticleId != null && !passArticleId.equalsIgnoreCase("")) {
                    intent = new Intent(this, ArticleDetailActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, passArticleId);
                    intent.putExtra(Constants.FROM, Constants.FROM_NOTIFICATION);
                } else {
                    intent = new Intent(this, MainActivity.class);
                }
            } else if (redirectTo.equalsIgnoreCase("premium_page")) {
                intent = new Intent(this, RemoveAdActivity.class);
            } else if (redirectTo.equalsIgnoreCase("url")) {
                String redirectUrl = remoteData.get("redirect_url");
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(redirectUrl));
            } else if (redirectTo.equalsIgnoreCase("post")) {
                String passPostId = remoteData.get("post_id");
                if (passPostId != null && !passPostId.equalsIgnoreCase("")) {
                    Log.e("Notification Post Id", passPostId);
                    intent = new Intent(this, PostDetailActivity.class);
                    intent.putExtra(Constants.POST_ID, passPostId);
                    intent.putExtra(Constants.FROM, Constants.FROM_NOTIFICATION);
                } else {
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra(Constants.FROM, Constants.FROM_NOTIFICATION_POST);
                }
            }
            else if (redirectTo.equalsIgnoreCase("savers")){
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("fromSaverNotification", "savers");
            }
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        assert intent != null;
       /* intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = createPendingIntent(this, Utils.getCurrentTimeMillis(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);*/
        Log.e("PendingIntent Target", intent.getComponent() + "");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, Utils.getCurrentTimeMillis(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);



        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    name,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("BestifyMe Channel Description");
            notificationManager.createNotificationChannel(channel);
        }

//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
//        notificationBuilder.setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.notification_icon)).getBitmap());/*Notification icon image*/
//        notificationBuilder.setSmallIcon(R.drawable.notification_ic);
//        notificationBuilder.setContentTitle(title);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.notification_icon);
        Bitmap largeIcon = drawable instanceof BitmapDrawable ? ((BitmapDrawable) drawable).getBitmap() : null;
        notificationBuilder
                .setSmallIcon(R.drawable.notification_ic)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);



        if (image == null) {
            if (messageBody != null && !messageBody.equalsIgnoreCase("")) {
                notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(messageBody)));
            }
        } else {
            if (messageBody != null && !messageBody.equalsIgnoreCase("")) {
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image).setSummaryText(Html.fromHtml((messageBody))));
            } else {
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));
            }
        }

        if (image != null) {
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                    .bigPicture(image);

            if (messageBody != null && !messageBody.trim().isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    bigPictureStyle.setSummaryText(Html.fromHtml(messageBody, Html.FROM_HTML_MODE_LEGACY));
                }else  {
                    bigPictureStyle.setSummaryText(Html.fromHtml(messageBody));
                }
            }

            notificationBuilder.setStyle(bigPictureStyle);
        } else if (messageBody != null && !messageBody.trim().isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(Html.fromHtml(messageBody, Html.FROM_HTML_MODE_LEGACY)));
            }else  {
                notificationBuilder.setStyle(
                        new NotificationCompat.BigTextStyle()
                                .bigText(Html.fromHtml(messageBody)) // Legacy version for API < 24
                );
            }
        }

//        notificationBuilder.setAutoCancel(true);
//        notificationBuilder.setSound(defaultSoundUri);
//        notificationBuilder.setContentIntent(pendingIntent);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationBuilder.setChannelId(CHANNEL_ID);
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationManager.createNotificationChannel(mChannel);
//        }

//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
//        NOTIFICATION_ID++;
        notificationManager.notify(NOTIFICATION_ID++, notificationBuilder.build());
    }

    /*
     *To get a Bitmap image from the URL received
     * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public void updateToken(String token) {
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.updateToken(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), token, sp.getString("UUID", "")), new ApiConnection.ConnectListener() {
            @Override
            public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                try {
                    Log.i(TAG, "onResponseSuccess: UPDATE TOKEN SUCCESSFULLY");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                Log.i(TAG, "onResponseFailure: ");
            }

            @Override
            public void onFailure(Headers headers) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onConnectionFailure() {
                Log.i(TAG, "onConnectionFailure: ");
            }

            @Override
            public void onException(Headers headers, int StatusCode) {
                Log.i(TAG, "onException: ");
            }
        });

    }

    public static String getAKCalculation() {
        return "TXSRWPO";
    }

    public static PendingIntent createPendingIntent(Context context, int id, Intent intent, int flag) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            return PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_IMMUTABLE | flag);
        } else {
            return PendingIntent.getActivity(context, id, intent, flag);
        }
    }
}