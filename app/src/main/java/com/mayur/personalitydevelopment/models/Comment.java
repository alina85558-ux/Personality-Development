package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Comment implements Comparable<Comment> {

    @SerializedName("subject")
    private String subject;
    @SerializedName("parent_id")
    private String parentId;
    @SerializedName("total_likes")
    private String totalLikes;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastLame;
    @SerializedName("profile_photo_thumb")
    private String profilePhotoThumb;
    @SerializedName("can_delete")
    private boolean canDelete;
    @SerializedName("comment")
    private String mCommentText;
    @SerializedName("user_id")
    private int mCommentUserId;
    @SerializedName("id")
    private int mId;
    @SerializedName("is_like")
    private boolean mLikedByMe;
    @SerializedName("child_comments")
    private ArrayList<Reply> mReplies;
    @SerializedName("created_at")
    private long mCreatedAt;
    @SerializedName("timestamp")
    private String mTimestamp;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(String totalLikes) {
        this.totalLikes = totalLikes;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastLame() {
        return lastLame;
    }

    public void setLastLame(String lastLame) {
        this.lastLame = lastLame;
    }

    public String getProfilePhotoThumb() {
        return profilePhotoThumb;
    }

    public void setProfilePhotoThumb(String profilePhotoThumb) {
        this.profilePhotoThumb = profilePhotoThumb;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public String getmCommentText() {
        return mCommentText;
    }

    public void setmCommentText(String mCommentText) {
        this.mCommentText = mCommentText;
    }

    public int getmCommentUserId() {
        return mCommentUserId;
    }

    public void setmCommentUserId(int mCommentUserId) {
        this.mCommentUserId = mCommentUserId;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public boolean ismLikedByMe() {
        return mLikedByMe;
    }

    public void setmLikedByMe(boolean mLikedByMe) {
        this.mLikedByMe = mLikedByMe;
    }

    public ArrayList<Reply> getmReplies() {
        return mReplies;
    }

    public void setmReplies(ArrayList<Reply> mReplies) {
        this.mReplies = mReplies;
    }

    public long getmCreatedAt() {
        return mCreatedAt;
    }

    public void setmCreatedAt(long mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    public String getmTimestamp() {
        return mTimestamp;
    }

    public void setmTimestamp(String mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    @Override
    public int compareTo(Comment comment) {
        return comment.getmId() == this.mId ? 0 : 1;
    }

    public static class Builder {
        private String subject;
        private String parentId;
        private String totalLikes;
        private String firstName;
        private String lastLame;
        private String profilePhotoThumb;
        private boolean canDelete;
        private String mCommentText;
        private int mCommentUserId;
        private int mId;
        private boolean mLikedByMe;
        private ArrayList<Reply> mReplies;
        private long mCreatedAt;
        private String mTimestamp;

        public Comment.Builder withSubject(String mSubject) {
            subject = mSubject;
            return this;
        }

        public Comment.Builder withParentId(String mParentId) {
            parentId = mParentId;
            return this;
        }

        public Comment.Builder withTotalLikes(String mTotalLikes) {
            totalLikes = mTotalLikes;
            return this;
        }

        public Comment.Builder withFirstName(String mFirstName) {
            firstName = mFirstName;
            return this;
        }

        public Comment.Builder withLastLame(String mLastLame) {
            lastLame = mLastLame;
            return this;
        }

        public Comment.Builder withProfilePhotoThumb(String mProfilePhotoThumb) {
            profilePhotoThumb = mProfilePhotoThumb;
            return this;
        }

        public Comment.Builder withCanDelete(boolean mCanDelete) {
            canDelete = mCanDelete;
            return this;
        }

        public Comment.Builder withCommentText(String commentText) {
            mCommentText = commentText;
            return this;
        }

        public Comment.Builder withCommentUserId(int commentUserId) {
            mCommentUserId = commentUserId;
            return this;
        }

        public Comment.Builder withId(int id) {
            mId = id;
            return this;
        }

        public Comment.Builder withLikedByMe(boolean likedByMe) {
            mLikedByMe = likedByMe;
            return this;
        }

        public Comment.Builder withReplies(ArrayList<Reply> replies) {
            mReplies = replies;
            return this;
        }

        public Comment.Builder withTime(long createdAt) {
            mCreatedAt = createdAt;
            return this;
        }

        public Comment.Builder withTimestamp(String timestamp) {
            mTimestamp = timestamp;
            return this;
        }

        public Comment build() {
            Comment comment = new Comment();
            comment.subject =subject;
            comment.parentId =parentId;
            comment.totalLikes=totalLikes;
            comment.firstName=firstName;
            comment.lastLame=lastLame;
            comment.profilePhotoThumb=profilePhotoThumb;
            comment.canDelete=canDelete;
            comment.mCommentText = mCommentText;
            comment.mCommentUserId = mCommentUserId;
            comment.mId = mId;
            comment.mLikedByMe = mLikedByMe;
            comment.mReplies = mReplies;
            comment.mCreatedAt = mCreatedAt;
            comment.mTimestamp = mTimestamp;
            return comment;
        }
    }
}
