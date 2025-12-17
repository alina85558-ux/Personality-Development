package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Card implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("course_category_id")
    @Expose
    private Integer course_category_id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("notes")
    @Expose
    private ArrayList<Note> notes = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public Integer getCourse_category_id() {
        return course_category_id;
    }

    public void setCourse_category_id(Integer course_category_id) {
        this.course_category_id = course_category_id;
    }
}