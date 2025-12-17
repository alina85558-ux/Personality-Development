package com.mayur.personalitydevelopment.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.activity.ProfileActivity;
import com.mayur.personalitydevelopment.database.ArticleRoomDatabase;
import com.mayur.personalitydevelopment.fragment.PostFragment;
import com.mayur.personalitydevelopment.models.PostData;
import com.mayur.personalitydevelopment.viewholder.PostHolder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final PostHolder postViewHolder = new PostHolder();
    private final Activity context;
    private final SharedPreferences sp;
    private final PostFragment postFragment;
    public SharedPreferences.Editor editor;
    public Boolean restored_Issubscribed;
    private List<PostData> postDataList = new ArrayList<>();
    private MediaPlayer mMediaPlayer;

    public PostListAdapter(List<PostData> postDataList, Activity context, PostFragment postFragment) {
        this.postDataList = postDataList;
        this.context = context;
        this.postFragment = postFragment;
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        postViewHolder.setItemBinding(context, parent);
        return postViewHolder.getHolder();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final PostHolder.MyPostHolder myHolder = postViewHolder.castHolder(holder);

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
            myHolder.txtLikes.setText(MessageFormat.format("{0}{1}", Utils.convertNumberToCount(postDataList.get(position).getTotalLikes() + 1).trim(), context.getResources().getString(R.string.likes)));
        } else {
            myHolder.txtLikes.setText(MessageFormat.format("{0}{1}", Utils.convertNumberToCount(postDataList.get(position).getTotalLikes() - 1).trim(), context.getResources().getString(R.string.likes)));
        }
        myHolder.linearLike.setOnClickListener(view -> {

            int totalCount = 0;
            if (!sp.getBoolean("guest_entry", false)) {
                postFragment.isFromLiked = false;
                if (!postDataList.get(position).isIsLike()) {
                    play(R.raw.like_click_sound);
                    totalCount = (postDataList.get(position).getTotalLikes() + 1);
                    myHolder.txtLikes.setText(MessageFormat.format("{0}{1}", Utils.convertNumberToCount(totalCount).trim(), context.getResources().getString(R.string.likes)));
                } else {
                    totalCount = (postDataList.get(position).getTotalLikes() - 1);
                    myHolder.txtLikes.setText(MessageFormat.format("{0}{1}", Utils.convertNumberToCount(totalCount).trim(), context.getResources().getString(R.string.likes)));
                }

                myHolder.likeIcon.setChecked(!postDataList.get(position).isIsLike());
                myHolder.linearLike.setClickable(false);
                {
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
                        postFragment.getPostLikes(position, !postDataList.get(position).isIsLike(), myHolder);
                    }
                }

            } else {
                myHolder.linearLike.setClickable(true);
                if (postFragment != null) {
                    postFragment.isFromLiked = true;
                    postFragment.storeTempDataForLike(position, !postDataList.get(position).isIsLike(), myHolder);
                    postFragment.showLoginDialog();
                }
            }
        });

        myHolder.likeIcon.setChecked(postDataList.get(position).isIsLike());
        if (Utils.convertNumberToCount(postDataList.get(position).getTotalLikes()).equalsIgnoreCase("0")) {
            myHolder.txtLikes.setText(context.getResources().getString(R.string.likes));
            myHolder.txtLikes.setVisibility(View.GONE);
        } else {
            myHolder.txtLikes.setText(MessageFormat.format("{0}{1}", Utils.convertNumberToCount(postDataList.get(position).getTotalLikes()).trim(), context.getResources().getString(R.string.likes)));
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

        myHolder.ivOptions.setOnClickListener(v -> {
            if (Utils.isNetworkAvailable(context)) {
                if (postFragment != null) {
                    postFragment.openOptions(myHolder.ivOptions, postDataList.get(position), position);
                } else {
                    ((ProfileActivity) context).openOptions(myHolder.ivOptions, postDataList.get(position), position);
                }
            } else {
                Utils.showToast(context.getString(R.string.no_internet_connection));
            }

        });

        myHolder.itemView.setOnClickListener(view -> {
            if (postFragment != null) {
                ((postFragment)).onPostClick(postDataList.get(position).getId(), position);
            }
        });

        myHolder.txtLikes.setOnClickListener(v -> {
            if (postFragment != null) {
                ((postFragment)).onPostLikeClick(postDataList.get(position).getId());
            } else {
                ((ProfileActivity) context).onPostLikeClick(postDataList.get(position).getId());
            }

        });

        try {
            if (postFragment != null) {
                postFragment.changeReadingMode(myHolder);
                Log.e("PostFragment", "Change Mode Called");
            } else {
//                ((ProfileActivity) context).changeReadingMode(myHolder);
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