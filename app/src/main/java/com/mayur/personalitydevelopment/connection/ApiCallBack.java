package com.mayur.personalitydevelopment.connection;

import static com.mayur.personalitydevelopment.connection.ApiConnection.getClient;

import com.mayur.personalitydevelopment.models.AddNoteListModel;
import com.mayur.personalitydevelopment.models.Card;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by Admin on 5/16/2017.
 */

public class ApiCallBack {

    static API getRestInterface() {
        Retrofit retrofit = getClient();
        API restInterface = retrofit.create(API.class);
        return restInterface;
    }

    public static Call<ResponseBody> signUp(Map<String, Object> parameters) {
        try {
            Call<ResponseBody> callBack = getRestInterface().SignUp(parameters);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> signIn(Map<String, Object> parameters) {
        try {
            Call<ResponseBody> callBack = getRestInterface().SignIn(parameters);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> guestEntry(int header, int auth_key, Map<String, Object> parameters) {
        try {
            Call<ResponseBody> callBack = getRestInterface().GuestEntry(header, auth_key, parameters);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> forgotPass(Map<String, Object> parameters) {
        try {
            Call<ResponseBody> callBack = getRestInterface().ForgotPass(parameters);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> articles(int sAuthenticationToken, String authenticationToken, boolean isGuest,
                                              int auth_key_v6,
                                              String filter_opt,
                                              String page,
                                              int lang) {
        try {
            Call<ResponseBody> callBack = getRestInterface().Articles(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, filter_opt, page, lang);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> importFavArticles(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String article_ids) {
        try {
            Call<ResponseBody> callBack = getRestInterface().importFavArticles(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, article_ids);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> allArticles(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String page) {
        try {
            Call<ResponseBody> callBack = getRestInterface().InvalidArticles(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, page);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> favArticleList(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String page) {
        try {
            Call<ResponseBody> callBack = getRestInterface().favArticles(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, page);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> likedArticleList(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String page) {
        try {
            Call<ResponseBody> callBack = getRestInterface().likedArticles(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, page);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> favArticle(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, int article_id, boolean status) {
        try {
            Call<ResponseBody> callBack = getRestInterface().favArticle(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, article_id, status);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> likeArticle(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, int article_id, boolean status) {
        try {
            Call<ResponseBody> callBack = getRestInterface().likeArticle(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, article_id, status);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> searchArticles(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String topic, String page, int lang) {
        try {
            Call<ResponseBody> callBack = getRestInterface().SearchArticles(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, topic, page, lang);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> allQuotes(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String page) {
        try {
            Call<ResponseBody> callBack = getRestInterface().AllQuotes(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, page);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> createFeedBack(int sAuthenticationToken, boolean isGuest, int auth_key_v6, Map<String, Object> stringMap) {
        try {
            Call<ResponseBody> callBack = getRestInterface().CreateFeedBack(sAuthenticationToken, isGuest, auth_key_v6, stringMap);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> createRequest(int sAuthenticationToken, boolean isGuest, int auth_key_v6, Map<String, Object> stringMap) {
        try {
            Call<ResponseBody> callBack = getRestInterface().CreateRequest(sAuthenticationToken, isGuest, auth_key_v6, stringMap);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> signOut(String authenticationToken, int auth_key_v6) {
        try {
            Call<ResponseBody> callBack = getRestInterface().SignOut(authenticationToken, auth_key_v6);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getCategories(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getCategories(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getPostList(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String page) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getPostList(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, page);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getUserPostList(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String page) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getUserPostList(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, page);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getUserProfile(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getUserProfile(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> deleteProfilePic(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6) {
        try {
            Call<ResponseBody> callBack = getRestInterface().deleteUserProfilePic(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> verifyUserEmail(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6) {
        try {
            Call<ResponseBody> callBack = getRestInterface().verifyUserEmail(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> editUserProfile(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String firstName, String lastName) {
        try {
            Call<ResponseBody> callBack = getRestInterface().editUserProfile(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, firstName, lastName);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> updateToken(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String deviceToken, String uuid) {
        try {
            Call<ResponseBody> callBack = getRestInterface().updateDeviceToken(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, deviceToken, uuid);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> updateProfilePic(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, MultipartBody.Part photo) {
        try {
            Call<ResponseBody> callBack = getRestInterface().updateProfile(sAuthenticationToken, authenticationToken, isGuest, photo, auth_key_v6);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getAddPost(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String post_data) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getAddPost(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, post_data);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getPostDelete(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String postId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getDeletePost(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, postId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getPostReport(int sAuthenticationToken,
                                                   String authenticationToken,
                                                   boolean isGuest,
                                                   int auth_key_v6,
                                                   String postId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getReportPost(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, postId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getPostLike(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String postId, boolean status) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getLikePost(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, postId, status);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getPostEdit(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String post_data, String postId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getEditPost(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, post_data, postId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> filterCategoryWise(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, int page, int category_id) {
        try {
            Call<ResponseBody> callBack = getRestInterface().filerCategoryWise(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, page, category_id);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> listAllSettings(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6) {
        try {
            Call<ResponseBody> callBack = getRestInterface().listAllSetings(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> setNotifiactions(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, boolean status) {
        try {
            Call<ResponseBody> callBack = getRestInterface().setNotifications(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, status);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> setEmailNotifiactions(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, boolean status) {
        try {
            Call<ResponseBody> callBack = getRestInterface().setEmailNotifications(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, status);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> unlockArticle(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, int article_id, boolean status) {
        try {
            Call<ResponseBody> callBack = getRestInterface().unlockArticle(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, article_id, status);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> multipleArticleLike(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String article_id, String status) {
        try {
            Call<ResponseBody> callBack = getRestInterface().multipleArticleLikes(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, article_id, status);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> multipleArticleFavorite(int sAuthenticationToken, String authenticationToken, boolean isGuest, String article_id, int auth_key_v6, String status) {
        try {
            Call<ResponseBody> callBack = getRestInterface().multipleArticleFavorite(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, article_id, status);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> multiplePostLike(int sAuthenticationToken, String authenticationToken, boolean isGuest, String post_id, int auth_key_v6, String status) {
        try {
            Call<ResponseBody> callBack = getRestInterface().multiplePostLikes(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, post_id, status);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getRelatedArticle(int sAuthenticationToken, String authenticationToken, boolean isGuest, String article_id, int auth_key_v6, int lang) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getRelatedArticles(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, article_id, lang);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> multipleArticleReward(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String article_id, String status) {
        try {
            Call<ResponseBody> callBack = getRestInterface().multipleArticleReward(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, article_id, status);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Call<ResponseBody> visibleSettings(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, int article_id, boolean status) {
        Call<ResponseBody> callBack = getRestInterface().visibleSettings(sAuthenticationToken, auth_key_v6);
        return callBack;
    }

    //public static Call<ResponseBody> callRewardVideoWatch()
/*
    public static Call<ResponseBody> signUpNormal(Map<String, Object> parameters) {
        Call<ResponseBody> callBack = getRestInterface().SignUpNormal(parameters);
        return callBack;
    }

    public static Call<ResponseBody> signIn(Map<String, Object> parameters) {
        Call<ResponseBody> callBack = getRestInterface().SignInSocial(parameters);
        return callBack;
    }
*/

    public static Call<ResponseBody> articleDetail(int sAuthenticationToken,
                                                   String authenticationToken,
                                                   boolean isGuest,
                                                   int auth_key_v6,
                                                   String article_id,
                                                   int page) {
        try {
            Call<ResponseBody> callBack = getRestInterface().articleDetail(sAuthenticationToken,
                    authenticationToken, isGuest, auth_key_v6,
                    article_id,
                    page);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static Call<ResponseBody> getSubscriptionDetail(int sAuthenticationToken,
//                                                   String authenticationToken,
//                                                   boolean isGuest,
//                                                   int auth_key_v6) {
//        try {
//            Call<ResponseBody> callBack = getRestInterface().getSubscriptionDetail(sAuthenticationToken,
//                    authenticationToken,
//                    isGuest,
//                    auth_key_v6);
//            return callBack;
//        }catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static Call<ResponseBody> getSubscriptionDetail(int sAuthenticationToken,
                                                           String authenticationToken,
                                                           boolean isGuest,
                                                           int auth_key_v6) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getSubscriptionDetail(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> setSubscriptionDetail(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, boolean isSubscriptionActive,
                                                           String subscriptionType, String inAppPurchaseToken) {
        try {
            Call<ResponseBody> callBack = getRestInterface().setSubscriptionDetail(sAuthenticationToken,
                    authenticationToken,
                    isGuest,
                    auth_key_v6,
                    isSubscriptionActive,
                    subscriptionType,
                    inAppPurchaseToken);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getOfferFlagAPI(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getOfferFlag(sAuthenticationToken,
                    authenticationToken, isGuest, auth_key_v6);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getPostDetailAPI(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6,
                                                      int page, String postId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getPostDetail(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6,
                    page, postId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getLikeList(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String postId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getLikeList(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6,
                    postId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> allCourses(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String currentDate) {
        try {
            Call<ResponseBody> callBack = getRestInterface().allCources(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, currentDate);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> allCategoryOfCourse(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, int categoryId, String currentDate) {
        try {
            Call<ResponseBody> callBack = getRestInterface().courcesCategories(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, categoryId, currentDate);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> calenderScreen(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, int month, int year) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getCalenderData(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, month, year);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> allMusic(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, int categoryId, String category) {
        try {
            Call<ResponseBody> callBack = getRestInterface().musicCourseData(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, categoryId, category);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> doneCourse(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, int courseId, String currentDateTime) {
        try {
            Call<ResponseBody> callBack = getRestInterface().trackCourseFormData(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, courseId, currentDateTime);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> allYoutube(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, int categoryId, String category) {
        try {
            Call<ResponseBody> callBack = getRestInterface().youtubeData(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, categoryId, category);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> affirmationListing(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, int categoryId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().affirmationListing(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, categoryId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> allAffirmation(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6) {
        try {
            Call<ResponseBody> callBack = getRestInterface().affirmationCategories(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> allReading(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6) {
        try {
            Call<ResponseBody> callBack = getRestInterface().articlesData(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getScribingCards(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6) {
        try {
            Call<ResponseBody> callBack = getRestInterface().scribingCards(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> deleteScribingCards(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, Card card) {
        try {
            int id = 0;
            if (card.getCourse_category_id() != null)
                id = card.getCourse_category_id();
            else
                id = card.getId();
            Call<ResponseBody> callBack = getRestInterface().deleteScribing(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, "" + id);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> createScribingCards(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, Card card, boolean isUpdating) {
        try {
            if (!isUpdating) {
                Call<ResponseBody> callBack = getRestInterface().createScribing(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, card);
                return callBack;
            } else {
                Call<ResponseBody> callBack = getRestInterface().updateScribing(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, card, card.getId().toString());
                return callBack;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getTodoList(int sAuthenticationToken,
                                                 String authenticationToken,
                                                 boolean isGuest,
                                                 int auth_key_v6) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getTodoList(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> updateNoteItemStatus(int sAuthenticationToken,
                                                          String authenticationToken,
                                                          boolean isGuest,
                                                          int auth_key_v6,
                                                          String id,
                                                          boolean is_checked) {
        try {
            Call<ResponseBody> callBack = getRestInterface().updateNoteItemStatus(sAuthenticationToken,
                    authenticationToken,
                    isGuest,
                    auth_key_v6,
                    id,
                    is_checked);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> deleteTodo(int sAuthenticationToken,
                                                String authenticationToken,
                                                boolean isGuest,
                                                int auth_key_v6,
                                                String id) {
        try {
            Call<ResponseBody> callBack = getRestInterface().deleteTodo(sAuthenticationToken,
                    authenticationToken,
                    isGuest,
                    auth_key_v6,
                    id);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> createNote(int sAuthenticationToken,
                                                String authenticationToken,
                                                boolean isGuest,
                                                int auth_key_v6,
                                                AddNoteListModel addNoteListModel) {
        try {
            Call<ResponseBody> callBack = getRestInterface().createNote(sAuthenticationToken,
                    authenticationToken,
                    isGuest,
                    auth_key_v6,
                    addNoteListModel);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> updateNoteData(int sAuthenticationToken,
                                                    String authenticationToken,
                                                    boolean isGuest,
                                                    int auth_key_v6,
                                                    String id,
                                                    AddNoteListModel addNoteListModel) {
        try {
            Call<ResponseBody> callBack = getRestInterface().updateNoteData(sAuthenticationToken,
                    authenticationToken,
                    isGuest,
                    auth_key_v6,
                    id,
                    addNoteListModel);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> setNotificationTime(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String time, String timezone, String utcTime) {
        try {
            Call<ResponseBody> callBack = getRestInterface().setNotificationTime(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, time, timezone, utcTime);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> startTrialDays(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, String trialStartDate, String courseId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().startTrialDays(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, trialStartDate, courseId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getExercise(int sAuthenticationToken,
                                                 String authenticationToken,
                                                 boolean isGuest,
                                                 int auth_key_v6,
                                                 String exerciseId,
                                                 String category) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getExercise(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6,
                    exerciseId, category);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> updateArticleLang(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, int lang) {
        try {
            Call<ResponseBody> callBack = getRestInterface().updateArticleLang(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, lang);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> updateArticleLangGuest(int sAuthenticationToken, String authenticationToken, boolean isGuest, int auth_key_v6, int lang, String guestId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().updateArticleLangGuest(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, lang, guestId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getComments(int sAuthenticationToken,
                                                 String authenticationToken,
                                                 boolean isGuest,
                                                 int auth_key_v6,
                                                 String postId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getComments(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, postId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getNotification(int sAuthenticationToken,
                                                     String authenticationToken,
                                                     boolean isGuest,
                                                     int auth_key_v6,
                                                     int page) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getNotification(sAuthenticationToken,
                    authenticationToken,
                    isGuest,
                    auth_key_v6,
                    page + "");
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> getNotificationGuest(int sAuthenticationToken,
                                                          boolean isGuest,
                                                          int auth_key_v6,
                                                          int page,
                                                          String guestId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().getNotificationGuest(sAuthenticationToken,
                    isGuest,
                    auth_key_v6,
                    page + "",
                    guestId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> addComments(int sAuthenticationToken,
                                                 String authenticationToken,
                                                 boolean isGuest,
                                                 int auth_key_v6,
                                                 String postId,
                                                 String commentMessage,
                                                 String parentId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().addComments(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, postId, commentMessage, parentId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> likeUnlikeComments(int sAuthenticationToken,
                                                        String authenticationToken,
                                                        boolean isGuest,
                                                        int auth_key_v6,
                                                        String commentId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().likeUnlikeComments(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, commentId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> deleteComments(int sAuthenticationToken,
                                                    String authenticationToken,
                                                    boolean isGuest,
                                                    int auth_key_v6,
                                                    String commentId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().deleteComments(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, commentId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> likeUserInfoComments(int sAuthenticationToken,
                                                          String authenticationToken,
                                                          boolean isGuest,
                                                          int auth_key_v6,
                                                          String commentId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().likeUserInfoComments(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, commentId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Call<ResponseBody> reportComments(int sAuthenticationToken,
                                                    String authenticationToken,
                                                    boolean isGuest,
                                                    int auth_key_v6,
                                                    String commentId) {
        try {
            Call<ResponseBody> callBack = getRestInterface().reportComments(sAuthenticationToken, authenticationToken, isGuest, auth_key_v6, commentId);
            return callBack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

