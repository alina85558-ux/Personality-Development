package com.mayur.personalitydevelopment.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.mayur.personalitydevelopment.BuildConfig;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.models.UserData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 10/31/2017.
 */

public class Constants {

    public static boolean IS_NEW_ACT = false;
    public static boolean IS_FROM_NOTIFICATION_ACT = false;
    public static boolean IS_RELATED_ARTICLE_CLICK = false;
    public static int RELATED_ARTICLE_ACTIVITY_INSTANCE_COUNT = 0;
    public static String GUEST_ID = "GUEST_ID";
    public static String ARTICLE_ID = "ARTICLE_ID";
    public static String FROM = "FROM";
    public static String FROM_NOTIFICATION = "FROM_NOTIFICATION";
    public static String POST_ID = "POST_ID";
    public static String POST_DELETE = "POST_DELETE";
    public static String POST_UPDATE = "POST_UPDATE";
    public static String ACTION_ = "ACTION";
    public static String FROM_NOTIFICATION_POST = "FROM_NOTIFICATION_POST";
    public static String POST = "POST";
    public static String ARTICLE = "ARTICLE";
    public static String music_title = "";
    public static String music_url = "";
    public static String music_image_url = "";
    public static int music_course_category_id;
    public static int music_category_id;

    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String DATE_FORMAT = "dd/MM/yyyy";

    public Constants(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getV6Value() {
        return Utils.getKv(BuildConfig.APPLICATION_ID);
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.play, options);
        } catch (Error | Exception ee) {
        }
        return bm;
    }

    public static String getBaseUrl() {
        return "http://bestifyme.com/api/v5/";
    }

    public static void setUserData(Context context, String userData) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userData", userData);
        editor.apply();
    }

    public static UserData getUserData(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return new Gson().fromJson(sp.getString("userData", ""), UserData.class);
    }

    public int getDays() {
        return preferences.getInt("showAfter", 2);
    }

    public long getLastShown() {
        return preferences.getLong("lastShownAt", 0);
    }

    public void setInstallDate() {
        editor.putLong("installedAt", Calendar.getInstance().getTimeInMillis());
        editor.commit();
    }

    public long getInstalledAt() {
        return preferences.getLong("installedAt", -1);
    }

    public boolean showP() {

        int dateDifference = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), getDate(getInstalledAt(), DATE_FORMAT),
                getDate(Calendar.getInstance().getTimeInMillis(), DATE_FORMAT));
        System.out.println("dateDifference: " + dateDifference);

        return dateDifference == 2 || dateDifference == 7 || dateDifference == 10;
    }

    //    {image=http://bestifyme.com/system/articles/photos/000/000/044/original/motivation.jpg, title=How to Stay Motivated Towards Your Goal, article_id=44, redirect_to=article_detail}


    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

    public interface STATUS_CODE {
        int OK = 200;
        int FAILURE = 400;
    }

    public interface ACTION {
        String MAIN_ACTION = "com.marothiatechs.customnotification.action.main";
        String INIT_ACTION = "com.marothiatechs.customnotification.action.init";
        String PREV_ACTION = "com.marothiatechs.customnotification.action.prev";
        String PLAY_ACTION = "com.marothiatechs.customnotification.action.play";
        String NEXT_ACTION = "com.marothiatechs.customnotification.action.next";
        String STARTFOREGROUND_ACTION = "com.marothiatechs.customnotification.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.marothiatechs.customnotification.action.stopforeground";
        String PAUSEFOREGROUND_ACTION = "com.marothiatechs.customnotification.action.pauseforeground";
        String NOTIFICATION_TITLE = "title";
        String NOTIFICATION_SOUND_URL = "music_url";
        String NOTIFICATION_CATEGORY_ID = "music_category_id";
    }

    public interface API_URL {
        String SIGN_UP_NORMAL = "signup";
        String SIGN_IN_NORMAL = "signin";
        String GUEST_ENTRY = "guest_entry";
        String SIGN_OUT = "signout";
        String FORGOT_PASSWORD = "forgot_password";
        String ARTICLES = "articles";
        String FAVOURITE_ARTICLE = "favourite_article";
        String LIKE_ARTICLE = "like_article";
        String SEARCH_ARTICLES = "search_articles";
        String QUOTES = "all_quotes";
        String CREATE_FEEDBACK = "create_feedback";
        String CREATE_REQUEST = "create_request";
        String FAV_ARTICLE_LIST = "favourite_articles_list";
        String LIKED_ARTICLE_LIST = "liked_articles_list";
        String IMPORT_FAV = "add_old_favourite_article";
        String CATEGORIES = "categories";
        String POST = "posts";
        String USER_POST = "users_list_posts";
        String USER_PROFILE = "list_profile_details";
        String RESEND_CONFIRMATION_MAIL = "resend_confirmation_mail";
        String DELETE_PROFILE_PIC = "delete_profile_photo";
        String EDIT_USER_PROFILE = "edit_profile_details";
        String UPDATE_TOKEN = "update_fcm_token";
        String UPDATE_PROFILE_PIC = "edit_profile_photo";
        String DELETE_POST = "delete_post";
        String REPORT_POST = "report_post";
        String EDIT_POST = "update_post";
        String ADD_POST = "create_post";
        String LIKE_POST = "like_post";
        String CATEGORIESWISE_FILTER = "categorywise_articles";
        String LIST_ALL_SETTINGS = "list_all_settings";
        String SET_NOTIFICATIONS = "set_notification";
        String SET_EMAIL_NOTIFICATIONS = "set_email_subscription";
        String WATCH_REWARD_VIDEOS = "watch_reward_videos";
        String VISIBLE_SETTINGS = "visible_settings";
        String MULTIPLE_ARTICLE_LIKES = "like_article_multiple";
        String MULTIPLE_ARTICLE_FAVOURITE = "favourite_article_multiple";
        String MULTIPLE_POST_LIKES = "like_post_multiple";
        String MULTIPLE_ARTICLE_REWARDS = "watch_reward_videos_multiple";
        String ARTICLE_DETAIL = "articles_detail";
        String RELATED_ARTICLES = "related_articles";
        String GET_OFFER_FLAG = "get_offer_flag";
        String GET_SUBSCRIPTION_DETAIL = "get_subscription_details";
        String SET_SUBSCRIPTION_DETAIL = "set_subscription_details";
        String GET_POST_DETAIL = "post_details";
        String GET_LIKE_LIST = "get_like_info";
        String COURSES = "courses";
        String COURSE_CATEGORIES = "course_categories";
        String CALENDER = "courses/dashboard";
        String COURSE_MUSIC = "course_musics";
        String TRACK_COURSE = "course_categories/track_course";
        String COURSE_EXTERNAL_LINKS = "course_external_links";
        String AFFIRMATION_CATEGORY_WITH_ID = "affirmations";
        String AFFIRMATION_CATEGORIES = "affirmation_categories";
        String READING_ARTICLES = "reading_articles";
        String SCRIBING_CARDS = "cards";
        String CARDS = "cards";
        String NOTES = "notes";
        String NOTIFICATION_TIME = "courses/set_notification_time";
        String SEVEN_DAYS_TRIAL = "start_trial";
        String COURSE_EXTERNAL_LINK = "course_external_links/";
        String UPDATE_ARTICLE_LANGUAGE = "update_language";
        String GET_COMMENTS = "get_comments";
        String GET_NOTIFICATION = "get_notifications";
        String CREATE_COMMENTS = "create_comment";
        String DELETE_COMMENTS = "delete_comment";
        String LIKE_UNLIKE_COMMENTS = "like_unlike_comment";
        String LIKE_USER_INFO_COMMENTS = "get_like_user_info";
        String REPORT_COMMENTS = "report_comment";

    }

    public interface LOGIN_TYPE {
        int FACEBOOK = 1;
        int GOOGLE = 2;
        int NORMAL = 0;
    }


}