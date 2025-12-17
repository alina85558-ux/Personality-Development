package com.mayur.personalitydevelopment.connection;

import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.models.AddNoteListModel;
import com.mayur.personalitydevelopment.models.Card;
import com.mayur.personalitydevelopment.models.GetToKnow;
import com.mayur.personalitydevelopment.models.Quotes;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {
    @FormUrlEncoded
    @POST("/RetrofitExample/insert.php")
    void insertUser(
            @Field("contact_info") String contact_info,
            @Field("message") String message,
            //  @Field("password") String password,
            //  @Field("email") String email,
            Callback<Response> callback);

    @GET("/pd_all_test.php")
    void getToKnow(Callback<GetToKnow> callback);

    @GET("/pd_quote.php")
    void getQuotes(Callback<Quotes> callback);

//    @FormUrlEncoded
//    @POST("/RetrofitExample/insert.php")
//    public void insertUserFeedback(
//
//            //  @Field("password") String password,
//            //  @Field("email") String email,
//            Callback<Response> callback);
//

    @FormUrlEncoded
    @POST(Constants.API_URL.SIGN_UP_NORMAL)
    Call<ResponseBody> SignUp(@FieldMap Map<String, Object> stringMap);

    @FormUrlEncoded
    @POST(Constants.API_URL.SIGN_IN_NORMAL)
    Call<ResponseBody> SignIn(@FieldMap Map<String, Object> stringMap);

    @FormUrlEncoded
    @POST(Constants.API_URL.GUEST_ENTRY)
    Call<ResponseBody> GuestEntry(@Header("s-authentication-token") int header, @Header("TXSRWPO") int authkey, @FieldMap Map<String, Object> stringMap);

    @FormUrlEncoded
    @POST(Constants.API_URL.FORGOT_PASSWORD)
    Call<ResponseBody> ForgotPass(@FieldMap Map<String, Object> stringMap);

    @FormUrlEncoded
    @POST(Constants.API_URL.ARTICLES)
    Call<ResponseBody> Articles(@Header("s-authentication-token") int sAuthenticationToken,
                                @Header("authentication-token") String authenticationToken,
                                @Header("is-guest") boolean isGuest,
                                @Header("TXSRWPO") int authkey,
                                @Field("filter_opt") String filter_opt,
                                @Field("page") String page,
                                @Field("language_type") int lang);

    @FormUrlEncoded
    @POST(Constants.API_URL.ARTICLES)
    Call<ResponseBody> InvalidArticles(@Header("s-authentication-token") int sAuthenticationToken,
                                       @Header("authentication-token") String authenticationToken,
                                       @Header("is-guest") boolean isGuest,
                                       @Header("TXSRWPO") int authkey,
                                       @Field("page") String page);

    @FormUrlEncoded
    @POST(Constants.API_URL.IMPORT_FAV)
    Call<ResponseBody> importFavArticles(@Header("s-authentication-token") int sAuthenticationToken,
                                         @Header("authentication-token") String authenticationToken,
                                         @Header("is-guest") boolean isGuest,
                                         @Header("TXSRWPO") int authkey,
                                         @Field("article_ids") String page);

    @FormUrlEncoded
    @POST(Constants.API_URL.FAV_ARTICLE_LIST)
    Call<ResponseBody> favArticles(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("page") String page);

    @FormUrlEncoded
    @POST(Constants.API_URL.LIKED_ARTICLE_LIST)
    Call<ResponseBody> likedArticles(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("page") String page);

    @FormUrlEncoded
    @POST(Constants.API_URL.FAVOURITE_ARTICLE)
    Call<ResponseBody> favArticle(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("article_id") int article_id, @Field("status") boolean status);

    @FormUrlEncoded
    @POST(Constants.API_URL.LIKE_ARTICLE)
    Call<ResponseBody> likeArticle(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("article_id") int article_id, @Field("status") boolean status);

    @FormUrlEncoded
    @POST(Constants.API_URL.SEARCH_ARTICLES)
    Call<ResponseBody> SearchArticles(@Header("s-authentication-token") int sAuthenticationToken,
                                      @Header("authentication-token") String authenticationToken,
                                      @Header("is-guest") boolean isGuest,
                                      @Header("TXSRWPO") int authkey,
                                      @Field("topic") String topic,
                                      @Field("page") String page,
                                      @Field("language_type") int lang);

    @FormUrlEncoded
    @POST(Constants.API_URL.QUOTES)
    Call<ResponseBody> AllQuotes(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("page") String topic);

    @FormUrlEncoded
    @POST(Constants.API_URL.CREATE_FEEDBACK)
    Call<ResponseBody> CreateFeedBack(@Header("s-authentication-token") int sAuthenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @FieldMap Map<String, Object> stringMap);

    @FormUrlEncoded
    @POST(Constants.API_URL.CREATE_REQUEST)
    Call<ResponseBody> CreateRequest(@Header("s-authentication-token") int sAuthenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @FieldMap Map<String, Object> stringMap);

    @POST(Constants.API_URL.SIGN_OUT)
    Call<ResponseBody> SignOut(@Header("authentication-token") String authenticationToken, @Header("TXSRWPO") int authkey);

    @POST(Constants.API_URL.CATEGORIES)
    Call<ResponseBody> getCategories(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey);

    @FormUrlEncoded
    @POST(Constants.API_URL.POST)
    Call<ResponseBody> getPostList(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("page") String page);

    @FormUrlEncoded
    @POST(Constants.API_URL.USER_POST)
    Call<ResponseBody> getUserPostList(@Header("s-authentication-token") int sAuthenticationToken,
                                       @Header("authentication-token") String authenticationToken,
                                       @Header("is-guest") boolean isGuest,
                                       @Header("TXSRWPO") int authkey,
                                       @Field("page") String page);

    @POST(Constants.API_URL.USER_PROFILE)
    Call<ResponseBody> getUserProfile(@Header("s-authentication-token") int sAuthenticationToken,
                                      @Header("authentication-token") String authenticationToken,
                                      @Header("is-guest") boolean isGuest,
                                      @Header("TXSRWPO") int authkey);

    @POST(Constants.API_URL.DELETE_PROFILE_PIC)
    Call<ResponseBody> deleteUserProfilePic(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey);

    @POST(Constants.API_URL.RESEND_CONFIRMATION_MAIL)
    Call<ResponseBody> verifyUserEmail(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey);

    @FormUrlEncoded
    @POST(Constants.API_URL.EDIT_USER_PROFILE)
    Call<ResponseBody> editUserProfile(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("first_name") String first_name, @Field("last_name") String last_name);

    @FormUrlEncoded
    @POST(Constants.API_URL.UPDATE_TOKEN)
    Call<ResponseBody> updateDeviceToken(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token")
    String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("device_token") String device_token, @Field("uuid") String uuid);

    @Multipart
    @POST(Constants.API_URL.UPDATE_PROFILE_PIC)
    Call<ResponseBody> updateProfile(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Part MultipartBody.Part profile_image, @Header("TXSRWPO") int authkey);

    @FormUrlEncoded
    @POST(Constants.API_URL.ADD_POST)
    Call<ResponseBody> getAddPost(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("post_data") String post_data);

    @FormUrlEncoded
    @POST(Constants.API_URL.EDIT_POST)
    Call<ResponseBody> getEditPost(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("post_data") String post_data, @Field("post_id") String post_id);

    @FormUrlEncoded
    @POST(Constants.API_URL.DELETE_POST)
    Call<ResponseBody> getDeletePost(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("post_id") String post_id);

    @FormUrlEncoded
    @POST(Constants.API_URL.REPORT_POST)
    Call<ResponseBody> getReportPost(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("post_id") String post_id);

    @FormUrlEncoded
    @POST(Constants.API_URL.LIKE_POST)
    Call<ResponseBody> getLikePost(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("post_id") String post_id, @Field("status") boolean status);

    @FormUrlEncoded
    @POST(Constants.API_URL.CATEGORIESWISE_FILTER)
    Call<ResponseBody> filerCategoryWise(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("page") int page, @Field("category_id") int category_id);

    @POST(Constants.API_URL.LIST_ALL_SETTINGS)
    Call<ResponseBody> listAllSetings(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey);

    @FormUrlEncoded
    @POST(Constants.API_URL.SET_NOTIFICATIONS)
    Call<ResponseBody> setNotifications(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("status") boolean status);

    @FormUrlEncoded
    @POST(Constants.API_URL.SET_EMAIL_NOTIFICATIONS)
    Call<ResponseBody> setEmailNotifications(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("status") boolean status);

    @FormUrlEncoded
    @POST(Constants.API_URL.WATCH_REWARD_VIDEOS)
    Call<ResponseBody> unlockArticle(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("article_id") int article_id, @Field("status") boolean status);

    @POST(Constants.API_URL.VISIBLE_SETTINGS)
    Call<ResponseBody> visibleSettings(@Header("s-authentication-token") int sAuthenticationToken, @Header("TXSRWPO") int authkey);

    @FormUrlEncoded
    @POST(Constants.API_URL.MULTIPLE_ARTICLE_LIKES)
    Call<ResponseBody> multipleArticleLikes(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("article_id") String article_id, @Field("status") String status);

    @FormUrlEncoded
    @POST(Constants.API_URL.MULTIPLE_ARTICLE_FAVOURITE)
    Call<ResponseBody> multipleArticleFavorite(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("article_id") String article_id, @Field("status") String status);

    @FormUrlEncoded
    @POST(Constants.API_URL.MULTIPLE_POST_LIKES)
    Call<ResponseBody> multiplePostLikes(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("post_id") String post_id, @Field("status") String status);

    @FormUrlEncoded
    @POST(Constants.API_URL.MULTIPLE_ARTICLE_REWARDS)
    Call<ResponseBody> multipleArticleReward(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Field("article_id") String article_id, @Field("status") String status);

    @FormUrlEncoded
    @POST(Constants.API_URL.RELATED_ARTICLES)
    Call<ResponseBody> getRelatedArticles(@Header("s-authentication-token") int sAuthenticationToken,
                                          @Header("authentication-token") String authenticationToken,
                                          @Header("is-guest") boolean isGuest,
                                          @Header("TXSRWPO") int authkey,
                                          @Field("article_id") String article_id,
                                          @Field("language_type") int lang);

    @FormUrlEncoded
    @POST(Constants.API_URL.ARTICLE_DETAIL)
    Call<ResponseBody> articleDetail(@Header("s-authentication-token") int sAuthenticationToken,
                                     @Header("authentication-token") String authenticationToken,
                                     @Header("is-guest") boolean isGuest,
                                     @Header("TXSRWPO") int authkey,
                                     @Field("article_id") String article_id,
                                     @Field("page") int page);

    @GET(Constants.API_URL.GET_OFFER_FLAG)
    Call<ResponseBody> getOfferFlag(@Header("s-authentication-token") int sAuthenticationToken,
                                    @Header("authentication-token") String authenticationToken,
                                    @Header("is-guest") boolean isGuest,
                                    @Header("TXSRWPO") int authkey);

    @POST(Constants.API_URL.GET_SUBSCRIPTION_DETAIL)
    Call<ResponseBody> getSubscriptionDetail(@Header("s-authentication-token") int sAuthenticationToken,
                                             @Header("authentication-token") String authenticationToken,
                                             @Header("is-guest") boolean isGuest,
                                             @Header("TXSRWPO") int authkey);

    @FormUrlEncoded
    @POST(Constants.API_URL.SET_SUBSCRIPTION_DETAIL)
    Call<ResponseBody> setSubscriptionDetail(@Header("s-authentication-token") int sAuthenticationToken,
                                             @Header("authentication-token") String authenticationToken,
                                             @Header("is-guest") boolean isGuest,
                                             @Header("TXSRWPO") int authkey,
                                             @Field("is_subscription_active") boolean is_subscription_active,
                                             @Field("subscription_type") String subscription_type,
                                             @Field("purchase_token") String inAppPurchaseToken);

    @FormUrlEncoded
    @POST(Constants.API_URL.GET_POST_DETAIL)
    Call<ResponseBody> getPostDetail(@Header("s-authentication-token") int sAuthenticationToken,
                                     @Header("authentication-token") String authenticationToken,
                                     @Header("is-guest") boolean isGuest,
                                     @Header("TXSRWPO") int authkey,
                                     @Field("page") int page,
                                     @Field("post_id") String post_id);

    @FormUrlEncoded
    @POST(Constants.API_URL.GET_LIKE_LIST)
    Call<ResponseBody> getLikeList(@Header("s-authentication-token") int sAuthenticationToken,
                                   @Header("authentication-token") String authenticationToken,
                                   @Header("is-guest") boolean isGuest,
                                   @Header("TXSRWPO") int authkey,
                                   @Field("post_id") String post_id);

    @GET(Constants.API_URL.COURSES)
    Call<ResponseBody> allCources(@Header("s-authentication-token") int sAuthenticationToken,
                                  @Header("authentication-token") String authenticationToken,
                                  @Header("is-guest") boolean isGuest,
                                  @Header("TXSRWPO") int authkey,
                                  @Query("today_date") String currentDate);

    @GET(Constants.API_URL.COURSE_CATEGORIES)
    Call<ResponseBody> courcesCategories(@Header("s-authentication-token") int sAuthenticationToken,
                                         @Header("authentication-token") String authenticationToken,
                                         @Header("is-guest") boolean isGuest,
                                         @Header("TXSRWPO") int authkey,
                                         @Query("course_id") int course_id,
                                         @Query("today_date") String currentDate);

    @GET(Constants.API_URL.CALENDER)
    Call<ResponseBody> getCalenderData(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Query("month") int month, @Query("year") int year);

    @GET(Constants.API_URL.COURSE_MUSIC)
    Call<ResponseBody> musicCourseData(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Query("course_id") int course_id, @Query("category") String category);

    @FormUrlEncoded
    @POST(Constants.API_URL.TRACK_COURSE)
    Call<ResponseBody> trackCourseFormData(@Header("s-authentication-token") int sAuthenticationToken,
                                           @Header("authentication-token") String authenticationToken,
                                           @Header("is-guest") boolean isGuest,
                                           @Header("TXSRWPO") int authkey,
                                           @Field("course_category_id") int course_category_id,
                                           @Field("track_date") String currentDateTime);

    @GET(Constants.API_URL.COURSE_EXTERNAL_LINKS)
    Call<ResponseBody> youtubeData(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Query("course_id") int course_id, @Query("category") String category);

    @GET(Constants.API_URL.AFFIRMATION_CATEGORY_WITH_ID)
    Call<ResponseBody> affirmationListing(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Query("affirmation_category_id") int course_id);

    @GET(Constants.API_URL.AFFIRMATION_CATEGORIES)
    Call<ResponseBody> affirmationCategories(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey);

    @GET(Constants.API_URL.READING_ARTICLES)
    Call<ResponseBody> articlesData(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey);

    @GET(Constants.API_URL.SCRIBING_CARDS)
    Call<ResponseBody> scribingCards(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey);

    @DELETE(Constants.API_URL.SCRIBING_CARDS + "/{id}")
    Call<ResponseBody> deleteScribing(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Path("id") String id);

    @POST(Constants.API_URL.SCRIBING_CARDS)
    Call<ResponseBody> createScribing(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Body Card card);

    @PUT(Constants.API_URL.SCRIBING_CARDS + "/{id}")
    Call<ResponseBody> updateScribing(@Header("s-authentication-token") int sAuthenticationToken, @Header("authentication-token") String authenticationToken, @Header("is-guest") boolean isGuest, @Header("TXSRWPO") int authkey, @Body Card card, @Path("id") String id);

    @GET(Constants.API_URL.CARDS)
    Call<ResponseBody> getTodoList(@Header("s-authentication-token") int sAuthenticationToken,
                                   @Header("authentication-token") String authenticationToken,
                                   @Header("is-guest") boolean isGuest,
                                   @Header("TXSRWPO") int authkey);

    @FormUrlEncoded
    @PUT(Constants.API_URL.NOTES + "/{id}/")
    Call<ResponseBody> updateNoteItemStatus(
            @Header("s-authentication-token") int sAuthenticationToken,
            @Header("authentication-token") String authenticationToken,
            @Header("is-guest") boolean isGuest,
            @Header("TXSRWPO") int authkey,
            @Path("id") String id,
            @Field("is_checked") boolean is_checked);

    @DELETE(Constants.API_URL.CARDS + "/{id}/")
    Call<ResponseBody> deleteTodo(
            @Header("s-authentication-token") int sAuthenticationToken,
            @Header("authentication-token") String authenticationToken,
            @Header("is-guest") boolean isGuest,
            @Header("TXSRWPO") int authkey,
            @Path("id") String id);

    @POST(Constants.API_URL.CARDS)
    Call<ResponseBody> createNote(@Header("s-authentication-token") int sAuthenticationToken,
                                  @Header("authentication-token") String authenticationToken,
                                  @Header("is-guest") boolean isGuest,
                                  @Header("TXSRWPO") int authkey,
                                  @Body AddNoteListModel addNoteListModel);

    @PUT(Constants.API_URL.CARDS + "/{id}/")
    Call<ResponseBody> updateNoteData(
            @Header("s-authentication-token") int sAuthenticationToken,
            @Header("authentication-token") String authenticationToken,
            @Header("is-guest") boolean isGuest,
            @Header("TXSRWPO") int authkey,
            @Path("id") String id,
            @Body AddNoteListModel addNoteListModel);

    @FormUrlEncoded
    @POST(Constants.API_URL.NOTIFICATION_TIME)
    Call<ResponseBody> setNotificationTime(
            @Header("s-authentication-token") int sAuthenticationToken,
            @Header("authentication-token") String authenticationToken,
            @Header("is-guest") boolean isGuest,
            @Header("TXSRWPO") int authkey,
            @Field("notification_time") String notification_time,
            @Field("user_timezone") String user_timezone,
            @Field("notification_utc_time") String utcTime);

    @FormUrlEncoded
    @POST(Constants.API_URL.SEVEN_DAYS_TRIAL)
    Call<ResponseBody> startTrialDays(@Header("s-authentication-token") int sAuthenticationToken,
                                      @Header("authentication-token") String authenticationToken,
                                      @Header("is-guest") boolean isGuest,
                                      @Header("TXSRWPO") int authkey,
                                      @Field("trial_start_date") String trialStartDate,
                                      @Field("course_id") String courseId);

    @GET(Constants.API_URL.COURSE_EXTERNAL_LINK + "{exerciseId}")
    Call<ResponseBody> getExercise(@Header("s-authentication-token") int sAuthenticationToken,
                                   @Header("authentication-token") String authenticationToken,
                                   @Header("is-guest") boolean isGuest,
                                   @Header("TXSRWPO") int authkey,
                                   @Path("exerciseId") String exerciseId,
                                   @Query("category") String category);

    @FormUrlEncoded
    @POST(Constants.API_URL.UPDATE_ARTICLE_LANGUAGE)
    Call<ResponseBody> updateArticleLang(@Header("s-authentication-token") int sAuthenticationToken,
                                         @Header("authentication-token") String authenticationToken,
                                         @Header("is-guest") boolean isGuest,
                                         @Header("TXSRWPO") int authkey,
                                         @Field("language_type") int lang);

    @FormUrlEncoded
    @POST(Constants.API_URL.UPDATE_ARTICLE_LANGUAGE)
    Call<ResponseBody> updateArticleLangGuest(@Header("s-authentication-token") int sAuthenticationToken,
                                         @Header("authentication-token") String authenticationToken,
                                         @Header("is-guest") boolean isGuest,
                                         @Header("TXSRWPO") int authkey,
                                         @Field("language_type") int lang,
                                         @Field("guest_id") String guestId);

    @FormUrlEncoded
    @POST(Constants.API_URL.GET_COMMENTS)
    Call<ResponseBody> getComments(@Header("s-authentication-token") int sAuthenticationToken,
                                   @Header("authentication-token") String authenticationToken,
                                   @Header("is-guest") boolean isGuest,
                                   @Header("TXSRWPO") int authkey,
                                   @Field("post_id") String postId);

    @FormUrlEncoded
    @POST(Constants.API_URL.GET_NOTIFICATION)
    Call<ResponseBody> getNotification(@Header("s-authentication-token") int sAuthenticationToken,
                                       @Header("authentication-token") String authenticationToken,
                                       @Header("is-guest") boolean isGuest,
                                       @Header("TXSRWPO") int authkey,
                                       @Field("page") String page);

    @FormUrlEncoded
    @POST(Constants.API_URL.GET_NOTIFICATION)
    Call<ResponseBody> getNotificationGuest(@Header("s-authentication-token") int sAuthenticationToken,
                                       @Header("is-guest") boolean isGuest,
                                       @Header("TXSRWPO") int authkey,
                                       @Field("page") String page,
                                       @Field("guest_id") String guestId);

    @FormUrlEncoded
    @POST(Constants.API_URL.CREATE_COMMENTS)
    Call<ResponseBody> addComments(@Header("s-authentication-token") int sAuthenticationToken,
                                   @Header("authentication-token") String authenticationToken,
                                   @Header("is-guest") boolean isGuest,
                                   @Header("TXSRWPO") int authkey,
                                   @Field("post_id") String postId,
                                   @Field("comment_message") String commentMessage,
                                   @Field("parent_id") String parentId);

    @FormUrlEncoded
    @POST(Constants.API_URL.LIKE_UNLIKE_COMMENTS)
    Call<ResponseBody> likeUnlikeComments(@Header("s-authentication-token") int sAuthenticationToken,
                                          @Header("authentication-token") String authenticationToken,
                                          @Header("is-guest") boolean isGuest,
                                          @Header("TXSRWPO") int authkey,
                                          @Field("comment_id") String commentId);

    @FormUrlEncoded
    @POST(Constants.API_URL.DELETE_COMMENTS)
    Call<ResponseBody> deleteComments(@Header("s-authentication-token") int sAuthenticationToken,
                                      @Header("authentication-token") String authenticationToken,
                                      @Header("is-guest") boolean isGuest,
                                      @Header("TXSRWPO") int authkey,
                                      @Field("comment_id") String commentId);

    @FormUrlEncoded
    @POST(Constants.API_URL.LIKE_USER_INFO_COMMENTS)
    Call<ResponseBody> likeUserInfoComments(@Header("s-authentication-token") int sAuthenticationToken,
                                            @Header("authentication-token") String authenticationToken,
                                            @Header("is-guest") boolean isGuest,
                                            @Header("TXSRWPO") int authkey,
                                            @Field("comment_id") String commentId);

    @FormUrlEncoded
    @POST(Constants.API_URL.REPORT_COMMENTS)
    Call<ResponseBody> reportComments(@Header("s-authentication-token") int sAuthenticationToken,
                                      @Header("authentication-token") String authenticationToken,
                                      @Header("is-guest") boolean isGuest,
                                      @Header("TXSRWPO") int authkey,
                                      @Field("comment_id") String commentId);

}



