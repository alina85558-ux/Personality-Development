package com.mayur.personalitydevelopment.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.ReplyDiffUtil;
import com.mayur.personalitydevelopment.activity.PostDetailActivity;
import com.mayur.personalitydevelopment.databinding.ReplyLayoutItemBinding;
import com.mayur.personalitydevelopment.listener.BottomSheetSubMenuClickListener;
import com.mayur.personalitydevelopment.listener.LikeBtnSubClickListener;
import com.mayur.personalitydevelopment.listener.LikeUnlikeSubClickListener;
import com.mayur.personalitydevelopment.listener.MakeChildAdapterDeleteInterface;
import com.mayur.personalitydevelopment.listener.MakeChildAdapterReplyInterface;
import com.mayur.personalitydevelopment.models.Reply;
import com.mayur.personalitydevelopment.models.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.MyAdapter> {
    private final ArrayList<Reply> replyArrayList = new ArrayList<>();
    private final Context mContext;
    private final LikeUnlikeSubClickListener likeUnlikeSubClickListener;
    private final LikeBtnSubClickListener likeBtnSubClickListener;
    private final UserData userData;
    private final BottomSheetSubMenuClickListener bottomSheetSubMenuClickListener;
    private final boolean isDarkTheme;
    private final int mIndex;
    private MakeChildAdapterReplyInterface makeChildAdapterReplyInterface;
    private MakeChildAdapterDeleteInterface makeChildAdapterDeleteInterface;
    private MediaPlayer mMediaPlayer;

    ReplyAdapter(Context mContext, LikeUnlikeSubClickListener likeUnlikeSubClickListener, UserData userData, LikeBtnSubClickListener likeBtnSubClickListener, boolean isDarkTheme, BottomSheetSubMenuClickListener bottomSheetSubMenuClickListener, int mIndex) {
        this.mContext = mContext;
        this.likeUnlikeSubClickListener = likeUnlikeSubClickListener;
        this.likeBtnSubClickListener = likeBtnSubClickListener;
        this.bottomSheetSubMenuClickListener = bottomSheetSubMenuClickListener;
        this.userData = userData;
        this.isDarkTheme = isDarkTheme;
        this.mIndex = mIndex;
    }

    public void setMakeChildAdapterDeleteInterface(MakeChildAdapterDeleteInterface makeChildAdapterDeleteInterface) {
        this.makeChildAdapterDeleteInterface = makeChildAdapterDeleteInterface;
    }

    public void setMakeChildAdapterReplyInterface(MakeChildAdapterReplyInterface makeChildAdapterReplyInterface) {
        this.makeChildAdapterReplyInterface = makeChildAdapterReplyInterface;
    }

    public void makeReply(ArrayList<Reply> newReplyList) {
        ReplyDiffUtil diffCallback = new ReplyDiffUtil(this.replyArrayList,
                newReplyList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        replyArrayList.clear();
        replyArrayList.addAll(newReplyList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public MyAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyAdapter(ReplyLayoutItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter holder, int position) {
        Reply reply = replyArrayList.get(position);
        holder.bind(reply);
    }

    @Override
    public int getItemCount() {
        return replyArrayList.size();
    }

    class MyAdapter extends RecyclerView.ViewHolder {
        ReplyLayoutItemBinding view;

        public MyAdapter(@NonNull ReplyLayoutItemBinding itemBinding) {
            super(itemBinding.getRoot());
            view = itemBinding;
        }

        @SuppressLint("SetTextI18n")
        void bind(Reply reply) {
            changeReadingMode();
            setSubCommentMenu(reply);
//            longPressDeleteComment(reply);
            setImageView(reply);
            prepareComment(reply);
            prepareLikeCount(reply);
            prepareTime(reply);
            prepareLikeButton(reply);
            prepareReplyBtn(reply);
        }

        private void setImageView(Reply reply) {
            final RequestOptions placeholder_error = new RequestOptions().error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(mContext).load(reply.getProfilePhotoThumb()).apply(placeholder_error).into(view.profileImage);
        }

        private void prepareComment(Reply reply) {
            StringBuilder firstName = new StringBuilder(reply.getFirstName());
            firstName.setCharAt(0, Character.toUpperCase(firstName.charAt(0)));

            StringBuilder lastName = new StringBuilder();
            if (reply.getLastLame() != null && !reply.getLastLame().equals("") && reply.getLastLame().length() > 0) {
                lastName = new StringBuilder(reply.getLastLame());
                lastName.setCharAt(0, Character.toUpperCase(lastName.charAt(0)));
            }

            String userName = firstName.toString() + " " + lastName.toString();
            view.userNameTextView.setText(userName);
            view.userNameTextView.setHighlightColor(Color.TRANSPARENT);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/MRegular.ttf");
            view.commentTextView.setTypeface(font);
            view.commentTextView.setText(reply.getmCommentText());
        }

        private void prepareTime(Reply reply) {
            view.time.setReferenceTime(reply.getmCreatedAt());
        }

        @SuppressLint("SetTextI18n")
        private void prepareLikeCount(Reply reply) {
            int count = Integer.parseInt(reply.getTotalLikes());
            if (count > 0) {
                view.likeCount.setVisibility(View.VISIBLE);
                view.likeCount.setText(reply.getTotalLikes() + " " + "Likes");
                view.likeCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeBtnSubClickListener.onLikeButtonSubClick(reply.getmId() + "");
                    }
                });
            } else {
                view.likeCount.setVisibility(View.GONE);
            }
        }

        private void prepareLikeButton(Reply reply) {
            // Two Drawables
            Drawable whiteLike = ContextCompat.getDrawable(view.getRoot().getContext(),
                    R.drawable.ic_like_white);
            Drawable redLike = ContextCompat.getDrawable(view.getRoot().getContext(),
                    R.drawable.ic_like_red);

            // Set Specific Image
            if (reply.ismLikedByMe()) {
                view.like.setBackgroundDrawable(redLike);
            } else {
                view.like.setBackgroundDrawable(whiteLike);
            }

            // Set Specific Image on click
            view.like.setOnClickListener(view -> {
                if (mContext instanceof PostDetailActivity) {
                    if (!((PostDetailActivity) mContext).checkUserLogin()) {
                        return;
                    }
                }
                AppCompatImageButton imageButton = (AppCompatImageButton) view;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    likeUnlikeSubClickListener.onLikeUnlikeSubButtonClick(reply.getmId() + "");
                    int totalCount = Integer.parseInt(reply.getTotalLikes());
                    if (Objects.equals(Objects.requireNonNull(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_like_white)).getConstantState(), imageButton.getBackground().getConstantState())) {
                        reply.setmLikedByMe(true);
                        int count = totalCount + 1;
                        reply.setTotalLikes(count + "");
                        imageButton.setBackground(redLike);
                        play(R.raw.like_click_sound);
                    } else {
                        reply.setmLikedByMe(false);
                        int count = totalCount - 1;
                        reply.setTotalLikes(count + "");
                        imageButton.setBackground(whiteLike);
                    }
                    notifyDataSetChanged();
                }
            });
        }

        private void prepareReplyBtn(Reply reply) {
            view.reply.setOnClickListener(view -> makeChildAdapterReplyInterface.makeReply(reply));
        }

        private void longPressDeleteComment(Reply reply) {
            view.getRoot().setOnLongClickListener(view -> {
                if (reply.getmCommentUserId() == userData.getUser_id()) {
                    makeChildAdapterDeleteInterface.deleteReply(reply);
                    return true;
                } else {
                    return false;
                }
            });
        }

        private void setSubCommentMenu(Reply reply) {
            view.commentMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof PostDetailActivity) {
                        if (!((PostDetailActivity) mContext).checkUserLogin()) {
                            return;
                        }
                    }
                    bottomSheetSubMenuClickListener.onBottomSheetSubMenuClick(mIndex, reply, reply.getmCommentUserId() == userData.getUser_id());
                }
            });
        }


        public void play(int rid) {
            stop();
            mMediaPlayer = MediaPlayer.create(view.getRoot().getContext(), rid);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                }
            });
            mMediaPlayer.start();
        }

        public void stop() {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }

        public void changeReadingMode() {
            if (isDarkTheme) {
                view.replyCommentCardV.setCardBackgroundColor(Color.parseColor("#464646"));
                view.commentTextView.setTextColor(Color.parseColor("#ffffff"));
                view.userNameTextView.setTextColor(Color.parseColor("#ffffff"));
                view.time.setTextColor(Color.parseColor("#ffffff"));
                view.likeCount.setTextColor(Color.parseColor("#ffffff"));
                view.reply.setTextColor(Color.parseColor("#ffffff"));
                view.commentMenu.setImageDrawable(view.getRoot().getContext().getResources().getDrawable(R.drawable.ic_menu_options_white));
            } else {
                view.replyCommentCardV.setCardBackgroundColor(Color.parseColor("#ffffff"));
                view.commentTextView.setTextColor(Color.parseColor("#000000"));
                view.userNameTextView.setTextColor(Color.parseColor("#000000"));
                view.time.setTextColor(Color.parseColor("#838383"));
                view.likeCount.setTextColor(Color.parseColor("#000000"));
                view.reply.setTextColor(Color.parseColor("#000000"));
                view.commentMenu.setImageDrawable(view.getRoot().getContext().getResources().getDrawable(R.drawable.ic_menu_post_options));
            }
        }
    }
}
