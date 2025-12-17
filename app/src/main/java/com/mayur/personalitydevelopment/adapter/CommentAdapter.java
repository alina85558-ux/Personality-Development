package com.mayur.personalitydevelopment.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.CommentDiffUtil;
import com.mayur.personalitydevelopment.activity.PostDetailActivity;
import com.mayur.personalitydevelopment.databinding.CommentLayoutItemBinding;
import com.mayur.personalitydevelopment.listener.BottomSheetMenuClickListener;
import com.mayur.personalitydevelopment.listener.BottomSheetSubMenuClickListener;
import com.mayur.personalitydevelopment.listener.CommentDelete;
import com.mayur.personalitydevelopment.listener.LikeBtnClickListener;
import com.mayur.personalitydevelopment.listener.LikeBtnSubClickListener;
import com.mayur.personalitydevelopment.listener.LikeUnlikeClickListener;
import com.mayur.personalitydevelopment.listener.LikeUnlikeSubClickListener;
import com.mayur.personalitydevelopment.listener.MakeCommentInterface;
import com.mayur.personalitydevelopment.listener.MakeInnerDeleteInterface;
import com.mayur.personalitydevelopment.listener.MakeInnerReplyInterface;
import com.mayur.personalitydevelopment.models.Comment;
import com.mayur.personalitydevelopment.models.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyAdapter> implements LikeUnlikeSubClickListener, LikeBtnSubClickListener {
    private final RecyclerView.RecycledViewPool viewPool =
            new RecyclerView.RecycledViewPool();

    private final ArrayList<Comment> commentArrayList = new ArrayList<>();
    private final LikeUnlikeClickListener likeUnlikeClickListener;
    private final LikeBtnClickListener likeBtnClickListener;
    private final boolean isDarkTheme;
    private final BottomSheetMenuClickListener bottomSheetMenuClickListener;
    private final BottomSheetSubMenuClickListener bottomSheetSubMenuClickListener;
    private final Context mContext;
    public UserData mUserData;
    public int clickedLabelItemPos = -1;
    public boolean isNewCommentAdded = false;
    public boolean isMainCommentNeedToShow = false;
    public boolean isSubCommentVisible = false;
    public boolean parentComment = false;
    public boolean childComment = false;
    private MakeCommentInterface makeCommentInterface;
    private MakeInnerReplyInterface makeInnerReplyInterface;
    private MakeInnerDeleteInterface makeInnerDeleteInterface;
    private CommentDelete commentDelete;
    private MediaPlayer mMediaPlayer;
    private int mainCommentNeedToShowPos = 0;

    public CommentAdapter(Context mContext, LikeUnlikeClickListener likeUnlikeClickListener, UserData mUserData, LikeBtnClickListener likeBtnClickListener, boolean isDarkTheme, BottomSheetMenuClickListener bottomSheetMenuClickListener, BottomSheetSubMenuClickListener bottomSheetSubMenuClickListener) {
        this.mContext = mContext;
        this.likeUnlikeClickListener = likeUnlikeClickListener;
        this.likeBtnClickListener = likeBtnClickListener;
        this.bottomSheetMenuClickListener = bottomSheetMenuClickListener;
        this.bottomSheetSubMenuClickListener = bottomSheetSubMenuClickListener;
        this.mUserData = mUserData;
        this.isDarkTheme = isDarkTheme;
    }

    public void setCommentDelete(CommentDelete commentDelete) {
        this.commentDelete = commentDelete;
    }

    public void setMakeInnerDeleteInterface(MakeInnerDeleteInterface makeInnerDeleteInterface) {
        this.makeInnerDeleteInterface = makeInnerDeleteInterface;
    }

    public void setMakeInnerReplyInterface(MakeInnerReplyInterface makeInnerReplyInterface) {
        this.makeInnerReplyInterface = makeInnerReplyInterface;
    }

    public ArrayList<Comment> getCommentArrayList() {
        return commentArrayList;
    }

    public void setMakeCommentInterface(MakeCommentInterface makeCommentInterface) {
        this.makeCommentInterface = makeCommentInterface;
    }

    public void makeComment(ArrayList<Comment> newCommentList, boolean setReplayVisible) {
        CommentDiffUtil diffCallback = new CommentDiffUtil(this.commentArrayList, newCommentList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        commentArrayList.clear();
        commentArrayList.addAll(newCommentList);
        diffResult.dispatchUpdatesTo(this);
    }


    @NonNull
    @Override
    public MyAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentLayoutItemBinding itemBinding =
                CommentLayoutItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                        false);
        return new MyAdapter(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter holder, int position) {
        Comment comment = commentArrayList.get(position);
        holder.bind(comment, position);
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    @Override
    public void onLikeUnlikeSubButtonClick(String commentId) {
        likeUnlikeClickListener.onLikeUnlikeButtonClick(commentId);
    }

    @Override
    public void onLikeButtonSubClick(String commentId) {
        likeBtnClickListener.onLikeButtonClick(commentId);
    }

    class MyAdapter extends RecyclerView.ViewHolder {
        CommentLayoutItemBinding view;

        public MyAdapter(@NonNull CommentLayoutItemBinding itemBinding) {
            super(itemBinding.getRoot());
            view = itemBinding;
        }

        @SuppressLint("SetTextI18n")
        void bind(Comment comment, int pos) {
            setCommentMenu(comment);
//            longPressDeleteComment(comment);
            changeReadingMode();
            setImageView(comment);
            prepareComment(comment);
            prepareLikeCount(comment);
            prepareTime(comment);
            prepareLikeButton(comment, pos);
            prepareReply(comment, pos);
            prepareReplyBtn(comment, pos);
            setChildRv(comment, getAdapterPosition());
        }

        private void setImageView(Comment comment) {
            final RequestOptions placeholder_error = new RequestOptions().error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(view.getRoot().getContext()).load(comment.getProfilePhotoThumb()).apply(placeholder_error).into(view.profileImage);
        }

        private void prepareComment(Comment comment) {
            //Get Username
            StringBuilder firstName = new StringBuilder(comment.getFirstName());
            firstName.setCharAt(0, Character.toUpperCase(firstName.charAt(0)));

            StringBuilder lastName = new StringBuilder();
            if (comment.getLastLame() != null && !comment.getLastLame().equals("") && comment.getLastLame().length() > 0) {
                lastName = new StringBuilder(comment.getLastLame());
                lastName.setCharAt(0, Character.toUpperCase(lastName.charAt(0)));
            }
            String userName = firstName.toString() + " " + lastName.toString();
            view.userNameTextView.setText(userName);
            view.userNameTextView.setHighlightColor(Color.TRANSPARENT);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/MRegular.ttf");
            view.commentTextView.setTypeface(font);
            view.commentTextView.setText(comment.getmCommentText());
        }


        private void prepareTime(Comment comment) {
            view.time.setReferenceTime(comment.getmCreatedAt());
        }


        @SuppressLint("SetTextI18n")
        private void prepareLikeCount(Comment comment) {
            int count = Integer.parseInt(comment.getTotalLikes());
            if (count > 0) {
                view.likeCount.setVisibility(View.VISIBLE);
                view.likeCount.setText(comment.getTotalLikes() + " " + "Likes");
                view.likeCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeBtnClickListener.onLikeButtonClick(comment.getmId() + "");
                    }
                });
            } else {
                view.likeCount.setVisibility(View.GONE);
            }
        }

        private void prepareLikeButton(Comment comment, int pos) {
            // Two Drawables
            Drawable whiteLike = ContextCompat.getDrawable(view.getRoot().getContext(), R.drawable.ic_like_white);
            Drawable redLike = ContextCompat.getDrawable(view.getRoot().getContext(), R.drawable.ic_like_red);


            // Set Specific Image
            if (comment.ismLikedByMe()) {
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
                isMainCommentNeedToShow = true;
                mainCommentNeedToShowPos = commentArrayList.indexOf(comment);

                AppCompatImageButton imageButton = (AppCompatImageButton) view;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    likeUnlikeClickListener.onLikeUnlikeButtonClick(comment.getmId() + "");
                    int totalCount = Integer.parseInt(comment.getTotalLikes());
//                    if (Objects.equals(Objects.requireNonNull(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_like_white)).getConstantState(), imageButton.getBackground().getConstantState())) {
                      if (!comment.ismLikedByMe()) {
                        comment.setmLikedByMe(true);
                        int count = totalCount + 1;
                        comment.setTotalLikes(count + "");
                        imageButton.setBackground(redLike);
                        play(R.raw.like_click_sound);
                    } else {
                        comment.setmLikedByMe(false);
                        int count = totalCount - 1;
                        comment.setTotalLikes(count + "");
                        imageButton.setBackground(whiteLike);
                    }
                    notifyDataSetChanged();
//                }
            });
        }

        @SuppressLint("SetTextI18n")
        private void prepareReply(Comment comment, int pos) {
            boolean isReplyAvailable = !comment.getmReplies().isEmpty();
            if (isReplyAvailable) {
                view.viewRepliesParent.setVisibility(View.VISIBLE);
                view.viewReplies.setText("View " + comment.getmReplies().size() + " Reply");
                view.viewRepliesParent.setOnClickListener(v -> {
                    if (view.replyRV.getVisibility() == View.VISIBLE) {
                        isSubCommentVisible = false;
                        view.replyRV.setVisibility(View.GONE);
                        view.viewReplies.setText("View " + comment.getmReplies().size() + " Reply");
                        return;
                    } else {
                        isSubCommentVisible = true;
                    }

                    view.replyRV.setVisibility(View.VISIBLE);
                    view.viewReplies.setText("Hide Reply");
                });
            } else {
                view.viewRepliesParent.setVisibility(View.GONE);
            }
        }

        private void prepareReplyBtn(Comment comment, int pos) {
            view.reply.setOnClickListener(view -> {
                        if (mContext instanceof PostDetailActivity) {
                            if (!((PostDetailActivity) mContext).checkUserLogin()) {
                                return;
                            }
                        }
                        clickedLabelItemPos = commentArrayList.indexOf(comment);
                        makeCommentInterface.makeComment(clickedLabelItemPos, comment);
                    }
            );
        }

        private void setChildRv(Comment comment, int commentPos) {
            boolean subCommentAvail = false;
            subCommentAvail = comment.getmReplies() != null && comment.getmReplies().size() > 0;
            if (isMainCommentNeedToShow) {
                handleReplyRVVisibility(isSubCommentVisible && isMainCommentNeedToShow && mainCommentNeedToShowPos == commentPos);
            } else {
                if (!parentComment) {
                    handleReplyRVVisibility(isNewCommentAdded && clickedLabelItemPos == commentPos && subCommentAvail);
                } else {
                    handleReplyRVVisibility(false);
                }
            }
            view.replyRV.setFocusable(false);
            view.replyRV.setHasFixedSize(true);
            view.replyRV.setItemAnimator(null);
            view.replyRV.setRecycledViewPool(viewPool);
            LinearLayoutManager childLayoutManager =
                    new LinearLayoutManager(view.replyRV.getContext()) {
                        @Override
                        public boolean canScrollVertically() {
                            return false;
                        }
                    };
            view.replyRV.setLayoutManager(childLayoutManager);
            ReplyAdapter adapter = new ReplyAdapter(mContext, CommentAdapter.this, mUserData, CommentAdapter.this, isDarkTheme, bottomSheetSubMenuClickListener, commentPos);
            adapter.setMakeChildAdapterReplyInterface(reply -> makeInnerReplyInterface.makeInnerReply(commentPos, reply));
            adapter.setMakeChildAdapterDeleteInterface(reply -> makeInnerDeleteInterface.makeInnerDeleteReply(commentPos, reply));
            adapter.makeReply(comment.getmReplies());
            view.replyRV.setAdapter(adapter);

            if (commentArrayList.get(commentArrayList.size() - 1) == comment) {
                parentComment = false;
            }

        }

        private void handleReplyRVVisibility(boolean isVisible) {
            if (isVisible) {
                view.replyRV.setVisibility(View.VISIBLE);
                view.viewReplies.setText("Hide Reply");
            } else {
                view.replyRV.setVisibility(View.GONE);
            }
        }

        private void longPressDeleteComment(Comment comment) {
            view.getRoot().setOnLongClickListener(view -> {
                if (comment.getmCommentUserId() == mUserData.getUser_id()) {
                    commentDelete.commentDelete(comment);
                    return true;
                } else {
                    return false;
                }
            });
        }

        private void setCommentMenu(Comment comment) {
            view.commentMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof PostDetailActivity) {
                        if (!((PostDetailActivity) mContext).checkUserLogin()) {
                            return;
                        }
                    }
                    bottomSheetMenuClickListener.onBottomSheetMenuClick(comment, comment.getmCommentUserId() == mUserData.getUser_id());
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
                view.commentCardV.setCardBackgroundColor(Color.parseColor("#464646"));
                view.commentTextView.setTextColor(Color.parseColor("#ffffff"));
                view.userNameTextView.setTextColor(Color.parseColor("#ffffff"));
                view.time.setTextColor(Color.parseColor("#ffffff"));
                view.likeCount.setTextColor(Color.parseColor("#ffffff"));
                view.reply.setTextColor(Color.parseColor("#ffffff"));
                view.commentMenu.setImageDrawable(view.getRoot().getContext().getResources().getDrawable(R.drawable.ic_menu_options_white));
            } else {
                view.commentCardV.setCardBackgroundColor(Color.parseColor("#ffffff"));
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
