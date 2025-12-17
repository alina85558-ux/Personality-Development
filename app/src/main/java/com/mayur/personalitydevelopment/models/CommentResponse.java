package com.mayur.personalitydevelopment.models;

import java.io.Serializable;
import java.util.ArrayList;

public class CommentResponse implements Serializable {

    private ArrayList<Comment> commentList;

    public ArrayList<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(ArrayList<Comment> commentList) {
        this.commentList = commentList;
    }

}



