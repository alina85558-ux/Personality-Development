package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AddNoteListModel {

    @SerializedName("name")
    private String noteHeader;
    @SerializedName("course_category_id")
    private String courseCategoryId;
    @SerializedName("notes")
    private ArrayList<AddNoteList> addNoteList;

    public String getNoteHeader() {
        return noteHeader;
    }

    public void setNoteHeader(String noteHeader) {
        this.noteHeader = noteHeader;
    }

    public String getCourseCategoryId() {
        return courseCategoryId;
    }

    public void setCourseCategoryId(String courseCategoryId) {
        this.courseCategoryId = courseCategoryId;
    }

    public ArrayList<AddNoteList> getAddNoteList() {
        return addNoteList;
    }

    public void setAddNoteList(ArrayList<AddNoteList> addNoteList) {
        this.addNoteList = addNoteList;
    }

    public class AddNoteList {
        @SerializedName("title")
        private String noteTitle;
        @SerializedName("is_checked")
        private boolean isNoteCompleted;

        public String getNoteTitle() {
            return noteTitle;
        }

        public void setNoteTitle(String noteTitle) {
            this.noteTitle = noteTitle;
        }

        public boolean isNoteCompleted() {
            return isNoteCompleted;
        }

        public void setNoteCompleted(boolean noteCompleted) {
            isNoteCompleted = noteCompleted;
        }
    }

}




