package com.mayur.personalitydevelopment.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.activity.ProfileActivity;
import com.mayur.personalitydevelopment.database.ArticleRoomDatabase;
import com.mayur.personalitydevelopment.models.PostData;
import com.mayur.personalitydevelopment.viewholder.ProfilePostHolder;

import java.util.ArrayList;
import java.util.List;

public class ProfilePostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public SharedPreferences.Editor editor;
    public Boolean restored_Issubscribed;
    private final ProfilePostHolder profilePostViewHolder = new ProfilePostHolder();
    private List<PostData> postDataList = new ArrayList<>();
    private final Activity context;
    private final SharedPreferences sp;
    private SharedPreferences prefs;
    private MediaPlayer mMediaPlayer;
    private ProfileActivity profileActivity;

    public ProfilePostListAdapter(List<PostData> postDataList, Activity context, ProfileActivity profileActivity) {
        this.postDataList = postDataList;
        this.context = context;
        this.profileActivity = profileActivity;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(int rid) {
        stop();
        mMediaPlayer = MediaPlayer.create(context, rid);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });
        mMediaPlayer.start();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        profilePostViewHolder.setItemBinding(context, parent);
        return profilePostViewHolder.getHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ProfilePostHolder.MyPostHolder myHolder = profilePostViewHolder.castHolder(holder);

        PostData postData = postDataList.get(position);

        myHolder.txtPostName.setText(postDataList.get(position).getFirstName() + " " + postDataList.get(position).getLastName());
        myHolder.txtPostTime.setReferenceTime(postDataList.get(position).getCreatedAt());
        myHolder.txtPostDescription.setText(Html.fromHtml(postDataList.get(position).getPostData()));
        if (postDataList.get(position).getTotalComments() > 0) {
            myHolder.txtComments.setText(String.valueOf((postDataList.get(position).getTotalComments())));
        } else {
            myHolder.txtComments.setText("");
        }
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/MRegular.ttf");
        myHolder.txtPostName.setTypeface(font);
        myHolder.txtPostDescription.setTypeface(font);
        myHolder.txtLikes.setTypeface(font);

        if (!postData.isIsLike()) {
            myHolder.txtLikes.setText(Utils.convertNumberToCount(postDataList.get(position).getTotalLikes() + 1) + context.getResources().getString(R.string.likes));
        } else {
            myHolder.txtLikes.setText(Utils.convertNumberToCount(postDataList.get(position).getTotalLikes() - 1) + context.getResources().getString(R.string.likes));
        }
        myHolder.linearLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int totalCount = 0;
                if (!sp.getBoolean("guest_entry", false)) {
                    profileActivity.isFromLiked = false;
                    if (!postDataList.get(position).isIsLike()) {
                        play(R.raw.like_click_sound);
                        totalCount = (postDataList.get(position).getTotalLikes() + 1);
                        myHolder.txtLikes.setText(Utils.convertNumberToCount(totalCount) + context.getResources().getString(R.string.likes));
                    } else {
                        totalCount = (postDataList.get(position).getTotalLikes() - 1);
                        myHolder.txtLikes.setText(Utils.convertNumberToCount(totalCount) + context.getResources().getString(R.string.likes));
                    }

                    myHolder.likeIcon.setChecked(!postDataList.get(position).isIsLike());
                    myHolder.linearLike.setClickable(false);
                    if (profileActivity != null) {
                        SharedPreferences prefs = context.getSharedPreferences("Purchase", MODE_PRIVATE);
                        restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
                        if (restored_Issubscribed && !Utils.isNetworkAvailable(context)) {
                            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(context);
                            db.postDao().setLikes(totalCount, !postDataList.get(position).isIsLike(), postDataList.get(position).getId());
                            db.postDao().setSynch(true, postDataList.get(position).getId());
                            if (!postDataList.get(position).isIsLike()) {
                                postDataList.get(position).setIsLike(!postDataList.get(position).isIsLike());
                                postDataList.get(position).setTotalLikes(postDataList.get(position).getTotalLikes() + 1);
                            } else {
                                postDataList.get(position).setIsLike(!postDataList.get(position).isIsLike());
                                postDataList.get(position).setTotalLikes(postDataList.get(position).getTotalLikes() - 1);
                            }
                            notifyDataSetChanged();
                        } else {
                            profileActivity.getPostLikes(position, !postDataList.get(position).isIsLike(), myHolder);
                        }
                    } else {
                        SharedPreferences prefs = context.getSharedPreferences("Purchase", MODE_PRIVATE);
                        restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
                        if (restored_Issubscribed && !Utils.isNetworkAvailable(context)) {
                            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(context);
                            db.postDao().setLikes(totalCount, !postDataList.get(position).isIsLike(), postDataList.get(position).getId());
                            db.postDao().setSynch(true, postDataList.get(position).getId());
                            if (!postDataList.get(position).isIsLike()) {
                                postDataList.get(position).setIsLike(!postDataList.get(position).isIsLike());
                                postDataList.get(position).setTotalLikes(postDataList.get(position).getTotalLikes() + 1);
                            } else {
                                postDataList.get(position).setIsLike(!postDataList.get(position).isIsLike());
                                postDataList.get(position).setTotalLikes(postDataList.get(position).getTotalLikes() - 1);
                            }
                            notifyDataSetChanged();
                        } else {
                            ((ProfileActivity) context).getPostLikes(position, !postDataList.get(position).isIsLike(), myHolder);
                        }
                    }

                } else {
                    myHolder.linearLike.setClickable(true);
                    if (profileActivity != null) {
                        profileActivity.isFromLiked = true;
                        profileActivity.storeTempDataForLike(position, !postDataList.get(position).isIsLike(), myHolder);
                        profileActivity.showLoginDialog();
                    }
                }
            }
        });

        myHolder.likeIcon.setChecked(postDataList.get(position).isIsLike());
        if (Utils.convertNumberToCount(postDataList.get(position).getTotalLikes()).equalsIgnoreCase("0")) {
            myHolder.txtLikes.setText(context.getResources().getString(R.string.likes));
            myHolder.txtLikes.setVisibility(View.GONE);
        } else {
            myHolder.txtLikes.setText(Utils.convertNumberToCount(postDataList.get(position).getTotalLikes()) + context.getResources().getString(R.string.likes));
            myHolder.txtLikes.setVisibility(View.VISIBLE);
        }

        if (sp.getInt("textSize", 16) != 16) {
            myHolder.txtPostDescription.setTextSize(sp.getInt("textSize", 16) - 2);
        }

        RequestOptions options = new RequestOptions();
        final RequestOptions placeholder_error = options.error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(postDataList.get(position).getProfilePhotoThumb())
                .apply(placeholder_error).into(myHolder.ivUserPic);

        if (postDataList.get(position).isShowOptions()) {
            myHolder.ivOptions.setVisibility(View.VISIBLE);
        } else {
            myHolder.ivOptions.setVisibility(View.VISIBLE);
        }

        myHolder.ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkAvailable(context)) {
                    if (profileActivity != null) {
                        profileActivity.openOptions(myHolder.ivOptions, postDataList.get(position), position);
                    } else {
                        ((ProfileActivity) context).openOptions(myHolder.ivOptions, postDataList.get(position), position);
                    }
                } else {
                    Utils.showToast(context.getString(R.string.no_internet_connection));
                }

            }
        });

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profileActivity != null) {
                    ((profileActivity)).onPostClick(postDataList.get(position).getId(), position);
                }
            }
        });

        myHolder.txtLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileActivity != null) {
                    ((profileActivity)).onPostLikeClick(postDataList.get(position).getId());
                } else {
                    ((ProfileActivity) context).onPostLikeClick(postDataList.get(position).getId());
                }

            }
        });

        try {
            if (profileActivity != null) {
                profileActivity.changeReadingMode(myHolder);
                Log.e("PostFragment", "Change Mode Called");
            } else {
                ((ProfileActivity) context).changeReadingMode(myHolder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return postDataList != null ? postDataList.size() : 0;
    }


}