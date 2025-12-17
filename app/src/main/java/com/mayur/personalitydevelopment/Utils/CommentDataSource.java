package com.mayur.personalitydevelopment.Utils;

import com.mayur.personalitydevelopment.models.Comment;
import com.mayur.personalitydevelopment.models.Reply;

import java.util.ArrayList;

public class CommentDataSource {

    public static Comment createComment(String mCommentText,
                                        int mCommentUserId,
                                        String mCommentUserName,
                                        int mId,
                                        boolean mLikedByMe,
                                        int mLikes,
                                        ArrayList<Reply> mReplies,
                                        String mTime,
                                        String imageUrl,
                                        String timestamp
    ) {
        return new Comment.Builder()
                .withId(mId)
                .withCommentUserId(mCommentUserId)
                .withFirstName(mCommentUserName)
                .withCommentText(mCommentText)
                .withTotalLikes(mLikes + "")
                .withProfilePhotoThumb(imageUrl)
                .withLikedByMe(mLikedByMe)
                .withTime(Long.parseLong(mTime))
                .withReplies(mReplies)
                .withTimestamp(timestamp)
                .build();
    }

    public static Reply createReply(
            int mParentCommentId,
            String mCommentText,
            int mCommentUserId,
            String mCommentUserName,
            int mreplyID,
            boolean mLikedByMe,
            int mLikes,
            String mTime,
            String imageUrl,
            String timestamp
    ) {
        return new Reply.Builder()
                .withParentId(mParentCommentId + "")
                .withCommentUserId(mCommentUserId)
                .withFirstName(mCommentUserName)
                .withCommentText(mCommentText)
                .withTotalLikes(mLikes + "")
                .withProfilePhotoThumb(imageUrl)
                .withTime(Long.parseLong(mTime))
                .withLikedByMe(mLikedByMe).withId(mreplyID)
                .withTimestamp(timestamp)
                .build();
    }

}
